package modeling.steps

import CodePlan
import domain.data.EMobilityPersonDataBuilder
import domain.data.EconomicStatus
import domain.data.Employment
import domain.data.Graduation
import domain.data.HouseholdData
import domain.data.HouseholdDataBuilder
import domain.data.LegacyZoneData
import domain.data.LegacyZoneDataBuilder
import domain.data.PersonData
import domain.data.Sex
import domain.data.ZoneData
import domain.data.ZoneDataBuilder
import domain.enums.AreaType
import domain.enums.Bbsr17
import units.CurrencyUnit
import usecases.finishPersons
import usecases.loadHouseholds
import usecases.loadZones
import usecases.prepareEmobilityPersons
import utils.ErrorHandling
import java.io.File

interface Context {
    val scenarioName: String
    val demandFolder: File

    val currencyUnit: CurrencyUnit
    // other default units

    val areaTypeCodes: CodePlan<AreaType>
    val economicalStatusCodes: CodePlan<EconomicStatus>
    val employmentCodes: CodePlan<Employment>
    val graduationCodes: CodePlan<Graduation>
    val sexCodes: CodePlan<Sex>

    val zoneRepository: BuilderRepository<ZoneDataBuilder, ZoneData>
    val householdRepository: BuilderRepository<HouseholdDataBuilder, HouseholdData>
    val personRepository: BuilderRepository<EMobilityPersonDataBuilder, PersonData>

    fun reset() {
        zoneRepository.reset()
        householdRepository.reset()
        personRepository.reset()
    }
}

interface LegacyZones {
    val zoneRepository: BuilderRepository<LegacyZoneDataBuilder, LegacyZoneData>
}

class ModelExecution<C>(
    val context: C
) : MultiStep(context.scenarioName) where C : Context

fun <C> C.synthesis(lambda: ModelExecution<C>.() -> Unit): C where C : Context {
    println("Validate before run!")
    val dummy = ModelExecution(this)
    dummy.lambda()
    val isValid = dummy.validate()

    if (isValid) {
        println("Execute")
        this.reset()
        val synth = ModelExecution(this)
        synth.lambda()
        synth.execute()
        return this
    } else {
        error("validation failed")
    }
}

data class BaseContext(
    override val scenarioName: String,
    override val demandFolder: File,
    override val areaTypeCodes: CodePlan<AreaType> = Bbsr17,
    override val economicalStatusCodes: CodePlan<EconomicStatus> = EconomicStatus,
    override val sexCodes: CodePlan<Sex> = Sex,
    override val graduationCodes: CodePlan<Graduation> = Graduation,
    override val employmentCodes: CodePlan<Employment> = Employment,
    override val currencyUnit: CurrencyUnit = CurrencyUnit.EUROS,
) : Context {

    override val zoneRepository: BuilderRepository<ZoneDataBuilder, ZoneData> =
        BuilderRepository()

    override val householdRepository: BuilderRepository<HouseholdDataBuilder, HouseholdData> =
        BuilderRepository()

    override val personRepository: BuilderRepository<EMobilityPersonDataBuilder, PersonData> =
        BuilderRepository()
}

fun main() {
    val context = BaseContext(
        scenarioName = "testSteps",
        areaTypeCodes = Bbsr17,
        demandFolder = File(
            "\\\\ifv-fs\\Forschung\\Projekte_intern\\mobitopp\\Output\\logiktram_rastatt_long-term-module\\rastatt"
        ),
        economicalStatusCodes = EconomicStatus
    )

    context.synthesis {
        loadZones()
        loadHouseholds()
        prepareEmobilityPersons(errorHandling = ErrorHandling.THROW)
        finishPersons()
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
