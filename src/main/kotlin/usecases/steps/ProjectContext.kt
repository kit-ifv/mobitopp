package usecases.steps

import domain.data.ActivityId
import domain.data.CarId
import domain.data.CarSegment
import domain.data.EconomicStatus
import domain.data.Employment
import domain.data.EngineType
import domain.data.Graduation
import domain.data.HouseholdId
import domain.data.LegacyZone
import domain.data.MutableHousehold
import domain.data.MutableLegacyZone
import domain.data.MutablePerson
import domain.data.MutablePrivateCar
import domain.data.MutableSharingProvider
import domain.data.MutableSharingStation
import domain.data.PersonId
import domain.data.PlannedActivity
import domain.data.Sex
import domain.data.SharingProviderId
import domain.data.SharingStationId
import domain.data.ZoneId
import domain.enums.ActivityType
import domain.enums.Mode
import domain.enums.areatype.RegioStaR17
import domain.enums.areatype.RegionType
import domain.events.PersonBehavior
import domain.location.Metrics
import domain.roadnetwork.LocatableGraph
import modeling.steps.Context
import modeling.steps.ExecutionMode
import modeling.steps.LateInit
import modeling.steps.MapRepository
import modeling.steps.MutableRepository
import modeling.steps.SimulationContext
import units.CurrencyUnit
import units.DistanceUnit
import usecases.AttractivenessModel
import usecases.LegacyActivityType
import usecases.LegacyMode
import usecases.steps.legacyData.HomeLocationModelContext
import usecases.steps.legacyData.LoadHouseholdContext
import usecases.steps.legacyData.LoadPrivateCarsContext
import usecases.steps.legacyData.LoadZonesContext
import utils.CodePlan
import utils.units.AbsoluteTime
import utils.units.weeks
import java.nio.file.Path
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

data class ProjectContext(
    override val scenarioName: String,
    override val demandFolder: Path,

    override val regionTypeCodes: CodePlan<RegionType> = RegioStaR17,
    override val economicalStatusCodes: CodePlan<EconomicStatus> = EconomicStatus,
    override val sexCodes: CodePlan<Sex> = Sex,
    override val graduationCodes: CodePlan<Graduation> = Graduation,
    override val employmentCodes: CodePlan<Employment> = Employment,
    override val engineCodes: CodePlan<EngineType> = EngineType,
    override val carSegmentCodes: CodePlan<CarSegment> = CarSegment,
    override val activityTypeCodes: CodePlan<ActivityType> = LegacyActivityType,
    override val modes: CodePlan<Mode> = LegacyMode,

    override val homeActivityType: ActivityType = LegacyActivityType.HOME,

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
    LoadSharingProvidersContext,
    LoadHouseholdContext,
    LoadPersonsContext,
    LoadPrivateCarsContext,
    LoadPlannedActivitiesContext,
    AssignPlannedActivitiesContext,
    LoadFixedDestinationsContext,
    LoadChoiceModelsContext,
    AssignCarsContext,
    RunSimContext,
    WriteTripsCsvContext,
    SimulationContext,
    RoadNetworkContext,
    HomeLocationModelContext {
    override val execMode: ExecutionMode = ExecutionMode()

    override val attractivenessModel = LateInit<AttractivenessModel>("Attractiveness Model")
    override val roadNetwork = LateInit<LocatableGraph>("Road Network Graph")
    override val behavior = LateInit<PersonBehavior>("Person Choice Models")

    override val zoneRepository = MapRepository<MutableLegacyZone, ZoneId>("zones")
    override val householdRepository = MapRepository<MutableHousehold, HouseholdId>("households")
    override val personRepository = MapRepository<MutablePerson, PersonId>("persons")
    override val carRepository = MapRepository<MutablePrivateCar, CarId>("cars")
    override val plannedActivityRepository = MapRepository<PlannedActivity, ActivityId>("planned activities")
    override val sharingProvidersRepository: MutableRepository<MutableSharingProvider, SharingProviderId> =
        MapRepository<MutableSharingStation, SharingStationId>("sharing stations")

    override val zoneColumnIndex: Map<Int, LegacyZone> by lazy {
        require(zoneRepository.sealed) {
            "Expected zone repo to be sealed/finished before using the matrix column > zone mapping"
        }
        zoneRepository.elements.associateBy { it.matrixColumn }
    }

    override val impedance = LateInit<Metrics>("Impedance")
}
