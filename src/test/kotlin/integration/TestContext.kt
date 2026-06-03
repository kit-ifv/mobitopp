package integration

import application.config.subconfigs.BaseCSVFiles
import application.config.subconfigs.CoreCSVConfig
import application.steps.ActivityTypesConfig
import application.steps.AttractivenessFileConfig
import application.steps.CarCodesConfig
import application.steps.DrtModesConfig
import application.steps.DrtSourceFilesConfig
import application.steps.HasCarRepo
import application.steps.HasDrtProviderAgentRepo
import application.steps.HasDrtProviderRepo
import application.steps.HasHouseholdRepo
import application.steps.HasModes
import application.steps.HasMutableAttractivenessModel
import application.steps.HasMutableImpedance
import application.steps.HasMutablePersonBehavior
import application.steps.HasPersonAgentRepo
import application.steps.HasPersonRepo
import application.steps.HasSharingProviderAgentRepo
import application.steps.HasSharingProviderRepo
import application.steps.HasZoneRepo
import application.steps.HouseholdCodesConfig
import application.steps.MatrixConfig
import application.steps.PurposesConfig
import application.steps.RegionCodesConfig
import application.steps.ResultsConfig
import application.steps.SharingModesConfig
import application.steps.SharingSourceFilesConfig
import application.steps.SimulationConfig
import application.steps.SourceFilesConfig
import application.steps.UnitConfig
import attractivities
import core.modelsteps.Cloneable
import core.modelsteps.Config
import core.modelsteps.ExecutionMode
import core.modelsteps.initReport
import core.modelsteps.resources.MapRepository
import core.modelsteps.resources.MutableRepository
import dataFolder
import domain.shared.behavior.AttractivenessModel
import domain.shared.datastructure.matrix.KeyBasedMatrixCreation
import domain.shared.datastructure.matrix.ZoneMatrixCreation
import domain.shared.enums.ActivityType
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.LegacyMode
import domain.shared.enums.Mode
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.shared.location.Impedance
import domain.shared.location.zone.MaximalZone
import domain.shared.location.zone.ZoneId
import domain.simulation.agent.DrtProviderAgent
import domain.simulation.agent.PersonAgent
import domain.simulation.agent.SharingProviderAgent
import domain.simulation.events.PersonBehavior
import domain.synthesis.data.drt.DrtProvider
import domain.synthesis.data.drt.DrtProviderId
import domain.synthesis.data.MutableDrtProviderData
import domain.synthesis.data.MutableSharingProvider
import domain.synthesis.data.SharingProvider
import domain.synthesis.data.SharingProviderId
import domain.synthesis.data.car.CarId
import domain.synthesis.data.car.CarSegment
import domain.synthesis.data.car.MutablePrivateCar
import domain.synthesis.data.car.PrivateCar
import domain.synthesis.data.household.EconomicStatus
import domain.synthesis.data.household.Household
import domain.synthesis.data.household.HouseholdId
import domain.synthesis.data.household.MutableHousehold
import domain.synthesis.data.person.MutablePerson
import domain.synthesis.data.person.Person
import domain.synthesis.data.person.PersonId
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.UnitIntervalValue
import edu.kit.ifv.units.share
import utils.CodePlan
import utils.ErrorHandling
import utils.report.ReportBuilder
import utils.units.AbsoluteTime
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

class TestContext(
    override val scenarioName: String = "integration test",
    override val modes: CodePlan<Mode> = LegacyMode,
) : HasZoneRepo<MaximalZone, MaximalZone>,
    HasHouseholdRepo<MutableHousehold, Household>,
    HasCarRepo<MutablePrivateCar, PrivateCar>,
    HasPersonRepo<MutablePerson, Person>,
    HasSharingProviderRepo<MutableSharingProvider, SharingProvider>,
    HasDrtProviderRepo<MutableDrtProviderData, DrtProvider>,
    Cloneable<TestContext>,
    HasMutableAttractivenessModel,
    HasPersonAgentRepo<PersonAgent, PersonAgent>,
    HasSharingProviderAgentRepo<SharingProviderAgent, SharingProviderAgent>,
    HasDrtProviderAgentRepo<DrtProviderAgent, DrtProviderAgent>,
    HasMutableImpedance,
    HasModes, // TODO discuss whether modes are context or config
    HasMutablePersonBehavior {
    override val execMode: ExecutionMode = ExecutionMode()
    override val report: ReportBuilder = initReport()

    override lateinit var impedance: Impedance
    override lateinit var attractiveness: AttractivenessModel
    override lateinit var personBehavior: PersonBehavior

    override val mutableZoneRepository: MutableRepository<MaximalZone, ZoneId> = MapRepository("zone")
    override val mutableHouseholdRepository: MutableRepository<MutableHousehold, HouseholdId> =
        MapRepository("household")
    override val mutableCarRepository: MutableRepository<MutablePrivateCar, CarId> = MapRepository("car")
    override val mutablePersonRepository: MutableRepository<MutablePerson, PersonId> = MapRepository("person")
    override val mutableSharingProviderRepository: MutableRepository<MutableSharingProvider, SharingProviderId> =
        MapRepository("sharingProvider")
    override val mutableDrtProviderRepository: MutableRepository<MutableDrtProviderData, DrtProviderId> =
        MapRepository("drtProvider")
    override val mutablePersonAgentRepository: MutableRepository<PersonAgent, PersonId> = MapRepository("PersonAgents")
    override val mutableSharingProviderAgentRepository: MutableRepository<SharingProviderAgent, SharingProviderId> =
        MapRepository("SharingProviderAgents")
    override val mutableDrtProviderAgentRepository: MutableRepository<DrtProviderAgent, DrtProviderId> =
        MapRepository("DrtProviderAgents")

    override fun clone(): TestContext = TestContext() // TODO doppelt zu context factory

    override var currentStep: String = ""
}

@Suppress("LongParameterList")
class TestConfig(
    override val seed: Long = 42L,
    override val fractionOfPopulation: UnitIntervalValue = 0.2.share(),
    override val simulationStart: AbsoluteTime = AbsoluteTime.START,
    override val simulationEnd: AbsoluteTime = simulationStart + 7.days,
    override val timeStep: Duration = 1.minutes,

    private val matrixBasePath: Path = Path("src/test/resources/test_matrix"),
    override val costMatrixConfig: Path = matrixBasePath.resolve("cost-matrix-config_constant.yaml"),
    override val durationMatrixConfig: Path = matrixBasePath.resolve("time-matrix-config_constant.yaml"),
    override val distanceMatrix: Path = matrixBasePath.resolve("DIS_Car.mtx.bz2"),
    override val matrixCreation: ZoneMatrixCreation = KeyBasedMatrixCreation,

    override val resultDir: Path = Path("results/test"),

    override val cachePath: Path = Path("cache"),
    override val sourceFiles: BaseCSVFiles = CoreCSVConfig(
        dataRepo = dataFolder,
        zoneRepo = Path("src/test/resources/testDemand/zone-repository/"),
        attractivitiesCSV = attractivities,
    ),

    override val bikeSharingStations: Path = Path(""),
    override val carSharingStations: Path = Path(""),
    override val carSharingFloatingArea: Path = Path(""),
    override val ridePoolingServiceAreas: Path = Path(""),

    override val attractivenessFile: Path = sourceFiles.attractivitiesCSV,

    override val distanceUnit: DistanceUnit = DistanceUnit.METERS,
    override val durationUnit: DurationUnit = DurationUnit.MINUTES,
    override val currencyUnit: CurrencyUnit = CurrencyUnit.EUROS,

    override val carSegmentCodes: CodePlan<CarSegment> = CarSegment,
    override val regionTypeCodes: CodePlan<RegionType> = RegioStaR17,
    override val activityTypes: CodePlan<ActivityType> = LegacyActivityType,
    override val economicStatusCodes: CodePlan<EconomicStatus> = EconomicStatus,
    override val errorHandling: ErrorHandling = ErrorHandling.THROW,

    override val bikeSharingMode: Mode = LegacyMode.BIKESHARING,
    override val carSharingStationMode: Mode = LegacyMode.CARSHARING_STATION,
    override val carSharingFloatingMode: Mode = LegacyMode.CARSHARING_FREE,

    override val ridePoolingMode: Mode = LegacyMode.RIDE_POOLING,
    override val work: ActivityType = LegacyActivityType.WORK,
    override val privateVisit: ActivityType = LegacyActivityType.PRIVATE_VISIT,
) : Config,
    SimulationConfig,
    MatrixConfig,
    UnitConfig,
    RegionCodesConfig,
    HouseholdCodesConfig,
    CarCodesConfig,
    ActivityTypesConfig,
    SourceFilesConfig,
    SharingSourceFilesConfig,
    SharingModesConfig,
    DrtSourceFilesConfig,
    DrtModesConfig,
    AttractivenessFileConfig,
    PurposesConfig,
    ResultsConfig
