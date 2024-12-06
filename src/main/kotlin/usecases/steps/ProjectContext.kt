package usecases.steps

import domain.data.ActivityId
import domain.data.CarId
import domain.data.CarSegment
import domain.data.EconomicStatus
import domain.data.Employment
import domain.data.EngineType
import domain.data.Graduation
import domain.data.Household
import domain.data.HouseholdId
import domain.data.LegacyZone
import domain.data.Person
import domain.data.PersonId
import domain.data.PlannedActivity
import domain.data.PrivateCar
import domain.data.Sex
import domain.data.SharingStation
import domain.data.SharingStationId
import domain.data.ZoneId
import domain.enums.ActivityType
import domain.enums.AreaType
import domain.enums.Bbsr17
import domain.enums.LegacyActivityType
import domain.enums.Mode
import domain.events.PersonBehavior
import domain.location.Metrics
import modeling.steps.Context
import modeling.steps.LateInit
import modeling.steps.MapRepository
import modeling.steps.SimulationContext
import units.CurrencyUnit
import units.DistanceUnit
import usecases.AttractivenessModel
import usecases.steps.legacyData.LoadHouseholdContext
import usecases.steps.legacyData.LoadPrivateCarsContext
import usecases.steps.legacyData.LoadZonesContext
import utils.CodePlan
import utils.units.AbsoluteTime
import utils.units.weeks
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

data class ProjectContext(
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
    override val modes: CodePlan<Mode>,

    override val costUnit: CurrencyUnit = CurrencyUnit.EUROS,
    override val distanceUnit: DistanceUnit = DistanceUnit.METERS,
    override val timeUnit: DurationUnit = DurationUnit.MINUTES,

    override val simulationSeed: Long = 42,
    override val simulationStart: AbsoluteTime = AbsoluteTime.START,
    override val simulationEnd: AbsoluteTime = AbsoluteTime.START + 1.weeks,
    override val timeStep: Duration = 1.minutes,

) : Context,
    LoadAttractivenessDataContext,
    LoadZonesContext,
    LoadSharingStationsContext,
    LoadHouseholdContext,
    LoadPersonsContext,
    LoadPrivateCarsContext,
    LoadPlannedActivitiesContext,
    LoadFixedDestinationsContext,
    LoadChoiceModelsContext,
    AssignCarsContext,
    RunSimContext,
    WriteTripsCsvContext,
    SimulationContext {

    override val attractivenessModel = LateInit<AttractivenessModel>("Attractiveness Model")
    override val behavior = LateInit<PersonBehavior>("Person Choice Models")

    override val zoneRepository = MapRepository<LegacyZone, ZoneId>("")
    override val householdRepository = MapRepository<Household, HouseholdId>("")
    override val personRepository = MapRepository<Person, PersonId>("")
    override val carRepository = MapRepository<PrivateCar, CarId>("")
    override val plannedActivityRepository = MapRepository<PlannedActivity, ActivityId>("")

    override val zoneColumnIndex: Map<Int, LegacyZone> by lazy {
        require(zoneRepository.sealed) {
            "Expected zone repo to be sealed/finished before using the matrix column > zone mapping"
        }
        zoneRepository.elements.associateBy { it.matrixColumn }
    }

    override val impedance = LateInit<Metrics>("Impedance")

    override val sharingStationsRepository =
        MapRepository<SharingStation, SharingStationId>("")
}
