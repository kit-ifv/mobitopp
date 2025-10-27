package application.config

import application.steps.model.AssignCarsContext
import application.steps.model.BuildAgentsContext
import application.steps.model.HomeLocationModelContext
import application.steps.model.LoadBehaviorModelsContext
import application.steps.model.RunSimContext
import application.steps.parser.RoadNetworkContext
import application.steps.parser.csv.LoadAttractivenessDataContext
import application.steps.parser.csv.LoadFixedDestinationsContext
import application.steps.parser.csv.LoadHouseholdContext
import application.steps.parser.csv.LoadPersonsContext
import application.steps.parser.csv.LoadPlannedActivitiesContext
import application.steps.parser.csv.LoadPrivateCarsContext
import application.steps.parser.csv.LoadSharingProvidersContext
import application.steps.parser.csv.LoadZonesContext
import application.steps.results.WriteTripsCsvContext
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import core.modelsteps.ExecutionMode
import core.modelsteps.LateInit
import core.modelsteps.MapRepository
import domain.shared.behavior.AttractivenessModel
import domain.shared.datastructure.LocatableGraph
import domain.shared.enums.ActivityType
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.LegacyMode
import domain.shared.enums.Mode
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.shared.location.LegacyZone
import domain.shared.location.Metrics
import domain.shared.location.MutableLegacyZone
import domain.shared.location.ZoneId
import domain.simulation.agent.PersonAgent
import domain.simulation.agent.SharingProviderAgent
import domain.simulation.config.DemandSimContext
import domain.simulation.events.PersonBehavior
import domain.simulation.results.AgentResultsContext
import domain.synthesis.data.ActivityId
import domain.synthesis.data.CarId
import domain.synthesis.data.CarSegment
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.Employment
import domain.synthesis.data.EngineType
import domain.synthesis.data.Graduation
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.MutablePlannedActivity
import domain.synthesis.data.MutablePrivateCar
import domain.synthesis.data.MutableSharingProvider
import domain.synthesis.data.PersonId
import domain.synthesis.data.Sex
import domain.synthesis.data.SharingProviderId
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.DistanceUnit
import utils.CodePlan
import utils.units.AbsoluteTime
import utils.units.weeks
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

interface StandardContext :
    LoadAttractivenessDataContext,
    LoadZonesContext,
    LoadSharingProvidersContext,
    LoadHouseholdContext,
    LoadPersonsContext,
    LoadPrivateCarsContext,
    LoadPlannedActivitiesContext,
    LoadFixedDestinationsContext,
    LoadBehaviorModelsContext,
    AssignCarsContext,
    WriteTripsCsvContext,
    RunSimContext,
    RoadNetworkContext,
    BuildAgentsContext

@JsonIgnoreProperties(
    value = ["execMode", "zoneColumnIndex", "personAgents", "sharingProviderAgents",
        "regionTypeCodes", "defaultZonePath", "defaultSharingStationPath", "defaultHouseholdPath", "defaultPersonPath",
        "defaultCarPath", "defaultActivityPath", "defaultFixedDestinationsPath"]
)
data class ExampleProjectContext(
    override val scenarioName: String,
    override val dataFolder: Path,

    override val resultDir: Path = Path("results"),

    override val regionTypeCodes: CodePlan<RegionType> = RegioStaR17.Companion,
    @JsonIgnore
    override val economicalStatusCodes: CodePlan<EconomicStatus> = EconomicStatus.Companion,
    @JsonIgnore
    override val sexCodes: CodePlan<Sex> = Sex.Companion,
    @JsonIgnore
    override val graduationCodes: CodePlan<Graduation> = Graduation.Companion,
    @JsonIgnore
    override val employmentCodes: CodePlan<Employment> = Employment.Companion,
    @JsonIgnore
    override val engineCodes: CodePlan<EngineType> = EngineType.Companion,
    @JsonIgnore
    override val carSegmentCodes: CodePlan<CarSegment> = CarSegment.Companion,
    @JsonIgnore
    override val activityTypes: CodePlan<ActivityType> = LegacyActivityType.Companion,
    override val modes: CodePlan<Mode> = LegacyMode.Companion,

    override val costUnit: CurrencyUnit = CurrencyUnit.EUROS,
    override val distanceUnit: DistanceUnit = DistanceUnit.METERS,
    override val timeUnit: DurationUnit = DurationUnit.MINUTES,

    override val simulationSeed: Long = 42,
    override val simulationStart: AbsoluteTime = AbsoluteTime.Companion.START,
    override val simulationEnd: AbsoluteTime = AbsoluteTime.Companion.START + 1.weeks,
    override val timeStep: Duration = 1.minutes,
) : DemandSimContext,
    StandardContext,
    HomeLocationModelContext,
    LoadBehaviorModelsContext,
    AgentResultsContext {
    override val execMode: ExecutionMode = ExecutionMode()

    @JsonIgnore
    override val attractivenessModel = LateInit<AttractivenessModel>("Attractiveness Model")

    @JsonIgnore
    override val roadNetwork = LateInit<LocatableGraph>("Road Network Graph")

    @JsonIgnore
    override val behavior = LateInit<PersonBehavior>("Person Choice Models")

    @JsonIgnore
    override val zoneRepository = MapRepository<MutableLegacyZone, ZoneId>("zones")

    @JsonIgnore
    override val householdRepository = MapRepository<MutableHousehold, HouseholdId>("households")

    @JsonIgnore
    override val sharingProviderRepository = MapRepository<MutableSharingProvider, SharingProviderId>(
        "sharing providers"
    )

    @JsonIgnore
    override val personRepository = MapRepository<MutablePerson, PersonId>("persons")

    @JsonIgnore
    override val carRepository = MapRepository<MutablePrivateCar, CarId>("cars")

    @JsonIgnore
    override val plannedActivityRepository =
        MapRepository<MutablePlannedActivity, ActivityId>("planned activities")

    override val zoneColumnIndex: Map<Int, LegacyZone> by lazy {
        require(zoneRepository.sealed) {
            "Expected zone repo to be sealed/finished before using the matrix column > zone mapping"
        }
        zoneRepository.elements.associateBy { it.matrixColumn }
    }

    @JsonIgnore
    override val impedance = LateInit<Metrics>("Impedance")

    @JsonIgnore
    override val personAgents = MapRepository<PersonAgent, PersonId>("person agents")

    @JsonIgnore
    override val sharingProviderAgents = MapRepository<SharingProviderAgent, SharingProviderId>(
        "sharing providers agents"
    )
}
