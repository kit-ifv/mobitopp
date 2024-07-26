package usecases.steps

import domain.data.ActivityId
import domain.data.CarId
import domain.data.CarSegment
import domain.data.EconomicStatus
import domain.data.Employment
import domain.data.EngineType
import domain.data.Graduation
import domain.data.Household
import domain.data.HouseholdBuilder
import domain.data.HouseholdId
import domain.data.LegacyZone
import domain.data.LegacyZoneBuilder
import domain.data.Person
import domain.data.PersonBuilder
import domain.data.PersonId
import domain.data.PlannedActivity
import domain.data.PlannedActivityBuilder
import domain.data.PrivateCar
import domain.data.PrivateCarBuilder
import domain.data.Sex
import domain.data.SharingStation
import domain.data.SharingStationId
import domain.data.Zone
import domain.data.ZoneBuilder
import domain.data.ZoneId
import domain.enums.ActivityType
import domain.enums.AreaType
import domain.enums.Bbsr17
import domain.enums.LegacyActivityType
import domain.enums.Mode
import domain.enums.StandardMode
import domain.events.PersonBehavior
import domain.location.Metrics
import modeling.steps.Context
import modeling.steps.LateInit
import modeling.steps.RepositoryBuilder
import modeling.steps.RepositoryState
import modeling.steps.SimulationContext
import units.CurrencyUnit
import units.DistanceUnit
import usecases.AttractivenessModel
import utils.Builder
import utils.CodePlan
import utils.units.AbsoluteTime
import utils.units.weeks
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

interface HouseholdContext {
    val householdRepository: RepositoryBuilder<HouseholdBuilder, Household, HouseholdId>
    val economicalStatusCodes: CodePlan<EconomicStatus>
}

interface PersonContext {
    val personRepository: RepositoryBuilder<PersonBuilder, Person, PersonId>
    val employmentCodes: CodePlan<Employment>
    val graduationCodes: CodePlan<Graduation>
    val sexCodes: CodePlan<Sex>
}

interface ZoneContext<B, E> where B : Builder<E>, E : Zone {
    val zoneRepository: RepositoryBuilder<B, E, ZoneId>
    val areaTypeCodes: CodePlan<AreaType>
}

interface BaseZoneContext : ZoneContext<ZoneBuilder, Zone>

interface LegacyZonesContext : ZoneContext<LegacyZoneBuilder, LegacyZone> {
    val zoneColumnIndex: Map<Int, LegacyZone>
    val attractivenessModel: LateInit<AttractivenessModel>
}

interface PrivateCarContext<B, E> where B : Builder<E>, E : PrivateCar {
    val engineCodes: CodePlan<EngineType>
    val carSegmentCodes: CodePlan<CarSegment>
    val carRepository: RepositoryBuilder<B, E, CarId>
}

interface BasePrivateCarContext : PrivateCarContext<PrivateCarBuilder, PrivateCar>

interface ActivityContext {
    val activityTypeCodes: CodePlan<ActivityType>

    val plannedActivityRepository: RepositoryBuilder<PlannedActivityBuilder, PlannedActivity, ActivityId>
}

interface SharingStationsContext {
    val sharingStationsRepository: RepositoryBuilder<Builder<SharingStation>, SharingStation, SharingStationId>
}

data class LegacyContext(
    override val scenarioName: String,
    override val demandFolder: File,

    override val areaTypeCodes: CodePlan<AreaType> = Bbsr17,
    override val economicalStatusCodes: CodePlan<EconomicStatus> = EconomicStatus,
    override val sexCodes: CodePlan<Sex> = Sex,
    override val graduationCodes: CodePlan<Graduation> = Graduation,
    override val employmentCodes: CodePlan<Employment> = Employment,
    override val engineCodes: CodePlan<EngineType> = EngineType,
    override val carSegmentCodes: CodePlan<CarSegment> = CarSegment,
    override val activityTypeCodes: CodePlan<ActivityType> = LegacyActivityType,
    override val modes: CodePlan<Mode> = StandardMode,

    override val costUnit: CurrencyUnit = CurrencyUnit.EUROS,
    override val distanceUnit: DistanceUnit = DistanceUnit.METERS,
    override val timeUnit: DurationUnit = DurationUnit.MINUTES,

    override val simulationSeed: Long = 42,
    override val simulationStart: AbsoluteTime = AbsoluteTime.START,
    override val simulationEnd: AbsoluteTime = AbsoluteTime.START + 1.weeks,
    override val timeStep: Duration = 1.minutes,

) : Context,
    LegacyZonesContext,
    HouseholdContext,
    PersonContext,
    BasePrivateCarContext,
    ActivityContext,
    SharingStationsContext,
    SimulationContext {

    override val attractivenessModel = LateInit<AttractivenessModel>("Attractiveness Model")
    override val behavior = LateInit<PersonBehavior>("Person Choice Models")

    override val zoneRepository = RepositoryBuilder<LegacyZoneBuilder, LegacyZone, ZoneId>()
    override val householdRepository = RepositoryBuilder<HouseholdBuilder, Household, HouseholdId>()
    override val personRepository = RepositoryBuilder<PersonBuilder, Person, PersonId>()
    override val carRepository = RepositoryBuilder<PrivateCarBuilder, PrivateCar, CarId>()
    override val plannedActivityRepository = RepositoryBuilder<PlannedActivityBuilder, PlannedActivity, ActivityId>()

    private var index: Map<Int, LegacyZone>? = null
    override val zoneColumnIndex: Map<Int, LegacyZone>
        get() {
            check(zoneRepository.state == RepositoryState.FINISHED) {
                "Cannot access zoneColumnIndex as the zoneRepository has not been built yet!"
            }

            return index ?: zoneRepository.elements.associateBy { it.matrixColumn }.also { index = it }
        }

    override val impedance = LateInit<Metrics>("Impedance")

    override val sharingStationsRepository =
        RepositoryBuilder<Builder<SharingStation>, SharingStation, SharingStationId>()
}
