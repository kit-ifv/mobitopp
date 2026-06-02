@file:Suppress("UnusedPrivateProperty", "MagicNumber")

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
import application.steps.model.assignHouseholdLocation
import application.steps.model.assignMainCarUsers
import application.steps.model.buildSimulationAgents
import application.steps.model.gaussianDurationRandomizer
import application.steps.model.loadBehaviorModels
import application.steps.model.simpleDrtAlgorithm
import application.steps.model.simulate
import application.steps.parser.csv.carCsv
import application.steps.parser.csv.cars
import application.steps.parser.csv.filterFractionOfPopulation
import application.steps.parser.csv.fixedDestinationCsv
import application.steps.parser.csv.fixedDestinationCsvParser
import application.steps.parser.csv.fixedDestinations
import application.steps.parser.csv.householdCsv
import application.steps.parser.csv.householdCsvParser
import application.steps.parser.csv.households
import application.steps.parser.csv.loadActivities
import application.steps.parser.csv.loadAttractivenessModelFromCsv
import application.steps.parser.csv.loadCars
import application.steps.parser.csv.loadHouseholds
import application.steps.parser.csv.loadPersons
import application.steps.parser.csv.loadZones
import application.steps.parser.csv.personCsv
import application.steps.parser.csv.persons
import application.steps.parser.csv.plannedActivities
import application.steps.parser.csv.plannedActivityCsv
import application.steps.parser.csv.zoneCsv
import application.steps.parser.csv.zones
import application.steps.parser.loadImpedance
import application.steps.results.writeTrips
import core.modelsteps.Cloneable
import core.modelsteps.Config
import core.modelsteps.ExecutionMode
import core.modelsteps.Simulation
import core.modelsteps.initReport
import core.modelsteps.resources.MapRepository
import core.modelsteps.resources.MutableRepository
import core.modelsteps.steps.modelStep
import domain.shared.behavior.AttractivenessModel
import domain.shared.datastructure.matrix.ConstantZoneIdMatrix
import domain.shared.datastructure.matrix.KeyBasedMatrixCreation
import domain.shared.datastructure.matrix.MatrixImpedance
import domain.shared.datastructure.matrix.ZoneMatrixCreation
import domain.shared.enums.ActivityType
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.LegacyMode
import domain.shared.enums.MainModes
import domain.shared.enums.Mode
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.shared.enums.legacyChoiceModelModes
import domain.shared.location.Impedance
import domain.shared.location.zone.ZoneId
import domain.shared.location.zone.MaximalZone
import domain.simulation.agent.DrtProviderAgent
import domain.simulation.agent.PersonAgent
import domain.simulation.agent.SharingProviderAgent
import domain.simulation.behavior.legacyDestinationChoice
import domain.simulation.behavior.legacyModeChoice
import domain.simulation.events.PersonBehavior
import domain.simulation.events.drtProviderStateMachine
import domain.simulation.events.personStateMachine
import domain.synthesis.data.CarId
import domain.synthesis.data.CarSegment
import domain.synthesis.data.DrtProvider
import domain.synthesis.data.DrtProviderId
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableDrtProviderData
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.MutablePrivateCar
import domain.synthesis.data.MutableSharingProvider
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import domain.synthesis.data.PrivateCar
import domain.synthesis.data.SharingProvider
import domain.synthesis.data.SharingProviderId
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

// TODO move from main to test
val visum_network = Path("src/test/resources/synthesis/leopoldshafen.net")
val attractivities = Path("data/attractivities.csv")
val dataFolder = Path("src/test/resources/testDemand/demand-data/")

// val standardConfig = ShortTermConfig(
//    visumNetwork = visum_network,
//    fractionOfPopulation = 0.2,
//
//    zoneMatrixCreationMethod = VisumMatrixCreator,
// //    simulationContext = ExampleProjectContext(
// //        scenarioName = "MobitoppReengineeringMain",
// //        dataFolder = dataFolder,
// //        modes = MainModes,
// //        simulationSeed = 42,
// //        regionTypeCodes = RegioStaR17
// //    ),
//    errorHandling = ErrorHandling.WARNING,
//    resultPath = Path("results"),
//
//    destinationChoiceModel = legacyDestinationChoiceBuilder.build(DestinationChoiceParameters()),
//    modeChoiceModel = legacyModeChoiceBuilder.build(ModeChoiceParameters()),
//
//    sourceFiles = CoreCSVConfig(
//        dataRepo = dataFolder,
//        zoneRepo = Path("src/test/resources/testDemand/zone-repository/"),
//        attractivitiesCSV = attractivities,
//    ),
// ).apply {
//    matrixConfig = MatrixConfig(matrixRepo = Path("src/test/resources/test_matrix"))
//    resultName = "mobitopp-integration.main.csv"
//    choiceModelModes = legacyChoiceModelModes
// }

val exampleChoiceModelModes = legacyChoiceModelModes.copy(options = MainModes.values())

class MyContext :
    HasZoneRepo<MaximalZone, MaximalZone>,
    HasHouseholdRepo<MutableHousehold, Household>,
    HasCarRepo<MutablePrivateCar, PrivateCar>,
    HasPersonRepo<MutablePerson, Person>,
    HasSharingProviderRepo<MutableSharingProvider, SharingProvider>,
    HasDrtProviderRepo<MutableDrtProviderData, DrtProvider>,
    Cloneable<MyContext>,
    HasMutableAttractivenessModel,
    HasPersonAgentRepo<PersonAgent, PersonAgent>,
    HasSharingProviderAgentRepo<SharingProviderAgent, SharingProviderAgent>,
    HasDrtProviderAgentRepo<DrtProviderAgent, DrtProviderAgent>,
    HasMutableImpedance,
    HasModes, // TODO discuss whether modes are context or config
    HasMutablePersonBehavior {
    override val scenarioName: String = "regression test short term scenario"
    override val modes: CodePlan<Mode> = LegacyMode
    override lateinit var impedance: Impedance
    override lateinit var attractiveness: AttractivenessModel
    override lateinit var personBehavior: PersonBehavior
    override val execMode: ExecutionMode = ExecutionMode()
    override val report: ReportBuilder = initReport()
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

    override fun clone(): MyContext = MyContext() // TODO doppelt zu context factory

    override var currentStep: String = ""
}

class MyConfig :
    Config,
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
    ResultsConfig {

    override val seed: Long = 42L
    override val fractionOfPopulation: UnitIntervalValue = 0.2.share()
    override val simulationStart: AbsoluteTime = AbsoluteTime.START
    override val simulationEnd: AbsoluteTime = simulationStart + 7.days
    override val timeStep: Duration = 1.minutes

    private val matrixBasePath = Path("src/test/resources/test_matrix")
    override val costMatrixConfig: Path = matrixBasePath.resolve("cost-matrix-config_constant.yaml")
    override val durationMatrixConfig: Path = matrixBasePath.resolve("time-matrix-config_constant.yaml")
    override val distanceMatrix: Path = matrixBasePath.resolve("DIS_Car.mtx.bz2")
    override val matrixCreation: ZoneMatrixCreation = KeyBasedMatrixCreation

    override val resultDir: Path = Path("results/test")

    override val cachePath: Path = Path("cache")
    override val sourceFiles: BaseCSVFiles = CoreCSVConfig(
        dataRepo = dataFolder,
        zoneRepo = Path("src/test/resources/testDemand/zone-repository/"),
        attractivitiesCSV = attractivities,
    )

    override val bikeSharingStations: Path = Path("")
    override val carSharingStations: Path = Path("")
    override val carSharingFloatingArea: Path = Path("")
    override val ridePoolingServiceAreas: Path = Path("")

    override val attractivenessFile: Path = sourceFiles.attractivitiesCSV

    override val distanceUnit: DistanceUnit = DistanceUnit.METERS
    override val durationUnit: DurationUnit = DurationUnit.MINUTES
    override val currencyUnit: CurrencyUnit = CurrencyUnit.EUROS

    override val carSegmentCodes: CodePlan<CarSegment> = CarSegment
    override val regionTypeCodes: CodePlan<RegionType> = RegioStaR17
    override val activityTypes: CodePlan<ActivityType> = LegacyActivityType
    override val economicStatusCodes: CodePlan<EconomicStatus> = EconomicStatus
    override val errorHandling: ErrorHandling = ErrorHandling.THROW

    override val bikeSharingMode: Mode = LegacyMode.BIKESHARING
    override val carSharingStationMode: Mode = LegacyMode.CARSHARING_STATION
    override val carSharingFloatingMode: Mode = LegacyMode.CARSHARING_FREE

    override val ridePoolingMode: Mode = LegacyMode.RIDE_POOLING
    override val work: ActivityType = LegacyActivityType.WORK
    override val privateVisit: ActivityType = LegacyActivityType.PRIVATE_VISIT
}

@Suppress("LongMethod")
fun main(args: Array<String>) {
    Simulation(MyConfig()) {
        MyContext()
    }.steps {
//        loadVisumNetwork(
//            shortTermConfig.visumNetwork ?: visum_network
//        ) {
//            connector = VisumLocale.ConnectorLocale(travelTimeCar = "T0_TSYS(CS)")
//        }

        zones(sealed = true) {
            loadZones(zoneCsv())
        }

        loadImpedance()

        // hacky fix of distance matrix due to faulty input data
        modelStep("fix distance matrix in impedance") {
            impedance = (impedance as MatrixImpedance).copy(
                travelDistance = ConstantZoneIdMatrix(5.0),
            )
        }

        loadAttractivenessModelFromCsv()

//            sharingProviders {
//                loadSharingProviders(bikeSharingProviderCsv())
//                loadSharingProviders(carSharingStationProviderCsv(
//                    parser = carSharingStationProvidersByStationParser {
//                        errorHandling = ErrorHandling.WARNING
//                    }
//                ))
//                loadSharingProviders(carSharingFloatingProviderCsv())
//
//                updateEachStep("Set operation hours for all sharing providers") {
//                    it.operatingHours = 0..20
//                }
//            }
//
//            drtProviders {
//                loadDrtProviders(ridePoolingProviderCsv())
//
//                newDrtProvider {
//                    name = "MyCustomNewDrtProvider"
//                    mode = LegacyMode.RIDE_HAILING
//                    operatingHours = 6..23
//                    serviceArea.addAll(zoneRepository.elements.map { it.id })
//                }
//            }
        val zoneByIndex: (ZoneId) -> MaximalZone = { zoneRepository.elements.elementAt(it.value.toInt()) }

        households {
            loadHouseholds(
                householdCsv(
                    parser = householdCsvParser {
                        getZone = zoneByIndex
                    },
                ),
            )

            filterFractionOfPopulation()

            assignHouseholdLocation()
        }

// TODO discuss: persons are added automatically as members to referenced household,
//    this bypasses the seps mechanic which usually log all modifications made to the repository content, should we change that?
        persons {
            loadPersons(personCsv())

            plannedActivities {
                loadActivities(plannedActivityCsv())

                fixedDestinations(
                    homeActivity = LegacyActivityType.HOME,
                    fixedDestinationCsv(
                        fixedDestinationCsvParser {
                            zoneConverter = zoneByIndex
                        },
                    ),
                )
            }
//
//                addDrtMembershipsIf { person, provider ->
//                    person.age > 16
//                }
        }

        cars {
            loadCars(carCsv())

            assignMainCarUsers()
        }

        loadBehaviorModels(
            legacyDestinationChoice,
            legacyModeChoice,
            exampleChoiceModelModes,
        )

        buildSimulationAgents( // TODO maybe create individual model steps to set up the state machines
            personStateMachine,
            drtStateMachine = drtProviderStateMachine,
            drtAlgorithm = { _ ->
                simpleDrtAlgorithm(
                    impedance,
                    zoneRepository.elements.filter { it.isDestination }.toList(),
                )
            },
            durationRandomizer = gaussianDurationRandomizer(),
        )

        simulate()

        writeTrips()
    }
}
