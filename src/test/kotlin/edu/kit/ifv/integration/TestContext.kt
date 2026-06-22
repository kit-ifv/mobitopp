package edu.kit.ifv.integration
import attractivities
import dataFolder
import edu.kit.ifv.application.config.subconfigs.BaseCSVFiles
import edu.kit.ifv.application.config.subconfigs.CoreCSVConfig
import edu.kit.ifv.application.steps.ActivityTypesConfig
import edu.kit.ifv.application.steps.AttractivenessFileConfig
import edu.kit.ifv.application.steps.CarCodesConfig
import edu.kit.ifv.application.steps.DrtModesConfig
import edu.kit.ifv.application.steps.DrtSourceFilesConfig
import edu.kit.ifv.application.steps.HasCarRepo
import edu.kit.ifv.application.steps.HasDrtProviderAgentRepo
import edu.kit.ifv.application.steps.HasDrtProviderRepo
import edu.kit.ifv.application.steps.HasHouseholdRepo
import edu.kit.ifv.application.steps.HasModes
import edu.kit.ifv.application.steps.HasMutableAttractivenessModel
import edu.kit.ifv.application.steps.HasMutableImpedance
import edu.kit.ifv.application.steps.HasMutablePersonBehavior
import edu.kit.ifv.application.steps.HasPersonAgentRepo
import edu.kit.ifv.application.steps.HasPersonRepo
import edu.kit.ifv.application.steps.HasSharingProviderAgentRepo
import edu.kit.ifv.application.steps.HasSharingProviderRepo
import edu.kit.ifv.application.steps.HasZoneRepo
import edu.kit.ifv.application.steps.HouseholdCodesConfig
import edu.kit.ifv.application.steps.MatrixConfig
import edu.kit.ifv.application.steps.PurposesConfig
import edu.kit.ifv.application.steps.RegionCodesConfig
import edu.kit.ifv.application.steps.ResultsConfig
import edu.kit.ifv.application.steps.SharingModesConfig
import edu.kit.ifv.application.steps.SharingSourceFilesConfig
import edu.kit.ifv.application.steps.SimulationConfig
import edu.kit.ifv.application.steps.SourceFilesConfig
import edu.kit.ifv.application.steps.UnitConfig
import edu.kit.ifv.core.modelsteps.Cloneable
import edu.kit.ifv.core.modelsteps.Config
import edu.kit.ifv.core.modelsteps.ExecutionMode
import edu.kit.ifv.core.modelsteps.initReport
import edu.kit.ifv.core.modelsteps.resources.MapRepository
import edu.kit.ifv.core.modelsteps.resources.MutableRepository
import edu.kit.ifv.domain.shared.behavior.AttractivenessModel
import edu.kit.ifv.domain.shared.car.CarId
import edu.kit.ifv.domain.shared.car.CarSegment
import edu.kit.ifv.domain.shared.data.household.HouseholdId
import edu.kit.ifv.domain.shared.data.person.PersonId
import edu.kit.ifv.domain.shared.datastructure.matrix.KeyBasedMatrixCreation
import edu.kit.ifv.domain.shared.datastructure.matrix.ZoneMatrixCreation
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.enums.LegacyActivityType
import edu.kit.ifv.domain.shared.enums.LegacyMode
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.enums.areatype.RegioStaR17
import edu.kit.ifv.domain.shared.enums.areatype.RegionType
import edu.kit.ifv.domain.shared.enums.household.EconomicStatus
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.shared.location.zone.MaximalZone
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.domain.simulation.agent.DrtProviderAgent
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.agent.SharingProviderAgent
import edu.kit.ifv.domain.simulation.data.car.MutablePrivateCar
import edu.kit.ifv.domain.simulation.data.car.PrivateCar
import edu.kit.ifv.domain.simulation.data.drt.DrtProvider
import edu.kit.ifv.domain.simulation.data.drt.DrtProviderId
import edu.kit.ifv.domain.simulation.data.drt.MutableDrtProviderData
import edu.kit.ifv.domain.simulation.data.household.Household
import edu.kit.ifv.domain.simulation.data.household.MutableHousehold
import edu.kit.ifv.domain.simulation.data.person.MutablePerson
import edu.kit.ifv.domain.simulation.data.person.Person
import edu.kit.ifv.domain.simulation.data.sharing.MutableSharingProvider
import edu.kit.ifv.domain.simulation.data.sharing.SharingProvider
import edu.kit.ifv.domain.simulation.data.sharing.SharingProviderId
import edu.kit.ifv.domain.simulation.events.PersonBehavior
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.UnitIntervalValue
import edu.kit.ifv.units.share
import edu.kit.ifv.utils.CodePlan
import edu.kit.ifv.utils.ErrorHandling
import edu.kit.ifv.utils.report.ReportBuilder
import edu.kit.ifv.utils.units.AbsoluteTime
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
