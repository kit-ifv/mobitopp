package modeling.steps

import domain.data.CarId
import domain.data.CarSegment
import domain.data.EMobilityPersonData
import domain.data.EMobilityPersonDataBuilder
import domain.data.EconomicStatus
import domain.data.Employment
import domain.data.EngineType
import domain.data.Graduation
import domain.data.HouseholdData
import domain.data.HouseholdDataBuilder
import domain.data.HouseholdId
import domain.data.LegacyZoneData
import domain.data.LegacyZoneDataBuilder
import domain.data.PersonData
import domain.data.PersonId
import domain.data.PrivateCarBuilder
import domain.data.PrivateCarData
import domain.data.Sex
import domain.data.ZoneData
import domain.data.ZoneDataBuilder
import domain.data.ZoneId
import domain.enums.AreaType
import domain.enums.Bbsr17
import units.CurrencyUnit
import usecases.assignCarUsers
import usecases.legacyData.finishPrivateCars
import usecases.legacyData.loadHouseholds
import usecases.legacyData.loadZones
import usecases.legacyData.preparePrivateCars
import usecases.loadPersons
import utils.Builder
import utils.CodePlan
import java.io.File

interface Context {
    val scenarioName: String
    val demandFolder: File
    fun reset()
}

interface HouseholdContext {
    val householdRepository: RepositoryBuilder<HouseholdDataBuilder, HouseholdData, HouseholdId>
    val economicalStatusCodes: CodePlan<EconomicStatus>
    val currencyUnit: CurrencyUnit
}

interface PersonContext<B, E> where B : Builder<E>, E : PersonData {
    val personRepository: RepositoryBuilder<B, E, PersonId>
    val employmentCodes: CodePlan<Employment>
    val graduationCodes: CodePlan<Graduation>
    val sexCodes: CodePlan<Sex>
}

interface EMobilityPersonContext : PersonContext<EMobilityPersonDataBuilder, EMobilityPersonData>

interface ZoneContext<B, E> where B : Builder<E>, E : ZoneData {
    val zoneRepository: RepositoryBuilder<B, E, ZoneId>
    val areaTypeCodes: CodePlan<AreaType>
}

interface BaseZoneContext : ZoneContext<ZoneDataBuilder, ZoneData>

interface LegacyZonesContext : ZoneContext<LegacyZoneDataBuilder, LegacyZoneData> {
    val zoneColumnIndex: Map<Int, LegacyZoneData>
}

interface PrivateCarContext<B, E> where B : Builder<E>, E : PrivateCarData {
    val engineCodes: CodePlan<EngineType>
    val carSegmentCodes: CodePlan<CarSegment>
    val carRepository: RepositoryBuilder<B, E, CarId>
}

interface BasePrivateCarContext : PrivateCarContext<PrivateCarBuilder, PrivateCarData>

data class BaseContext(
    override val scenarioName: String,
    override val demandFolder: File,
    override val areaTypeCodes: CodePlan<AreaType> = Bbsr17,
    override val economicalStatusCodes: CodePlan<EconomicStatus> = EconomicStatus,
    override val sexCodes: CodePlan<Sex> = Sex,
    override val graduationCodes: CodePlan<Graduation> = Graduation,
    override val employmentCodes: CodePlan<Employment> = Employment,
    override val currencyUnit: CurrencyUnit = CurrencyUnit.EUROS,
) : Context, BaseZoneContext, HouseholdContext, EMobilityPersonContext {

    override val zoneRepository = RepositoryBuilder<ZoneDataBuilder, ZoneData, ZoneId>()
    override val householdRepository = RepositoryBuilder<HouseholdDataBuilder, HouseholdData, HouseholdId>()
    override val personRepository = RepositoryBuilder<EMobilityPersonDataBuilder, EMobilityPersonData, PersonId>()

    override fun reset() {
        zoneRepository.reset()
        householdRepository.reset()
        personRepository.reset()
    }
}

data class LegacyContext(
    override val scenarioName: String,
    override val demandFolder: File,
    override val areaTypeCodes: CodePlan<AreaType> = Bbsr17,
    override val economicalStatusCodes: CodePlan<EconomicStatus> = EconomicStatus,
    override val sexCodes: CodePlan<Sex> = Sex,
    override val graduationCodes: CodePlan<Graduation> = Graduation,
    override val employmentCodes: CodePlan<Employment> = Employment,
    override val currencyUnit: CurrencyUnit = CurrencyUnit.EUROS,
    override val engineCodes: CodePlan<EngineType> = EngineType,
    override val carSegmentCodes: CodePlan<CarSegment> = CarSegment,
) : Context, LegacyZonesContext, HouseholdContext, EMobilityPersonContext, BasePrivateCarContext {

    override val zoneRepository = RepositoryBuilder<LegacyZoneDataBuilder, LegacyZoneData, ZoneId>()
    override val householdRepository = RepositoryBuilder<HouseholdDataBuilder, HouseholdData, HouseholdId>()
    override val personRepository = RepositoryBuilder<EMobilityPersonDataBuilder, EMobilityPersonData, PersonId>()
    override val carRepository = RepositoryBuilder<PrivateCarBuilder, PrivateCarData, CarId>()

    private var index: Map<Int, LegacyZoneData>? = null
    override val zoneColumnIndex: Map<Int, LegacyZoneData>
        get() {
            check(zoneRepository.state == RepositoryState.FINISHED) {
                "Cannot access zoneColumnIndex as the zoneRepository has not been built yet!"
            }

            return index ?: zoneRepository.elements.associateBy { it.matrixColumn }.also { index = it }
        }

    override fun reset() { // TODO unhappy with reset -> rethink validation / context state
        zoneRepository.reset()
        householdRepository.reset()
        personRepository.reset()
        carRepository.reset()
        index = null
    }
}

fun main() {
    val context = LegacyContext(
        scenarioName = "testSteps",
        areaTypeCodes = Bbsr17,
        demandFolder = File(
            "\\\\ifv-fs\\Forschung\\Projekte_intern\\mobitopp\\Output\\logiktram_karlsruhe_long-term-module\\karlsruhe"
        ),
        economicalStatusCodes = EconomicStatus
    )

    context.synthesis {
        loadZones()
        loadHouseholds()
        loadPersons()
        preparePrivateCars()
        assignCarUsers()
        finishPrivateCars()
    }

    println(
        "zones: " +
            context.zoneRepository.elements.count()
    )

    println(
        "households: " +
            context.householdRepository.elements.count()
    )

    println(
        "persons: " +
            context.personRepository.elements.count()
    )
}
