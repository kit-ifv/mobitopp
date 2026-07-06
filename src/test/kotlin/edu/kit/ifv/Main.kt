@file:Suppress("UnusedPrivateProperty", "MagicNumber")

package edu.kit.ifv

import edu.kit.ifv.application.config.subconfigs.BaseCSVFiles
import edu.kit.ifv.application.config.subconfigs.CoreCSVConfig
import edu.kit.ifv.application.steps.ActivityTypesConfig
import edu.kit.ifv.application.steps.AttractivenessFileConfig
import edu.kit.ifv.application.steps.CarCodesConfig
import edu.kit.ifv.application.steps.DrtModesConfig
import edu.kit.ifv.application.steps.DrtSourceFilesConfig
import edu.kit.ifv.application.steps.HasCarRepo
import edu.kit.ifv.application.steps.HasChoiceModelModes
import edu.kit.ifv.application.steps.HasDrtProviderAgentRepo
import edu.kit.ifv.application.steps.HasDrtProviderRepo
import edu.kit.ifv.application.steps.HasHouseholdRepo
import edu.kit.ifv.application.steps.HasModeChoiceModel
import edu.kit.ifv.application.steps.HasModes
import edu.kit.ifv.application.steps.HasMutableAttractivenessModel
import edu.kit.ifv.application.steps.HasMutableDestinationChoiceModel
import edu.kit.ifv.application.steps.HasMutableImpedance
import edu.kit.ifv.application.steps.HasMutableModeAvailabilityModel
import edu.kit.ifv.application.steps.HasPersonAgentRepo
import edu.kit.ifv.application.steps.HasPersonRepo
import edu.kit.ifv.application.steps.HasReplanningStrategy
import edu.kit.ifv.application.steps.HasSharingProviderAgentRepo
import edu.kit.ifv.application.steps.HasSharingProviderRepo
import edu.kit.ifv.application.steps.HasSpawnDestinationCharacteristics
import edu.kit.ifv.application.steps.HasSpawnModeCharacteristics
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
import edu.kit.ifv.application.steps.model.assignHouseholdLocation
import edu.kit.ifv.application.steps.model.assignMainCarUsers
import edu.kit.ifv.application.steps.model.buildSimulationAgents
import edu.kit.ifv.application.steps.model.gaussianDurationRandomizer
import edu.kit.ifv.application.steps.model.loadAvailabilityModel
import edu.kit.ifv.application.steps.model.loadDestinationChoiceModel
import edu.kit.ifv.application.steps.model.simpleDrtAlgorithm
import edu.kit.ifv.application.steps.model.simulate
import edu.kit.ifv.application.steps.parser.csv.carCsv
import edu.kit.ifv.application.steps.parser.csv.cars
import edu.kit.ifv.application.steps.parser.csv.filterFractionOfPopulation
import edu.kit.ifv.application.steps.parser.csv.fixedDestinationCsv
import edu.kit.ifv.application.steps.parser.csv.fixedDestinationCsvParser
import edu.kit.ifv.application.steps.parser.csv.fixedDestinations
import edu.kit.ifv.application.steps.parser.csv.householdCsv
import edu.kit.ifv.application.steps.parser.csv.householdCsvParser
import edu.kit.ifv.application.steps.parser.csv.households
import edu.kit.ifv.application.steps.parser.csv.loadActivities
import edu.kit.ifv.application.steps.parser.csv.loadAttractivenessModelFromCsv
import edu.kit.ifv.application.steps.parser.csv.loadCars
import edu.kit.ifv.application.steps.parser.csv.loadHouseholds
import edu.kit.ifv.application.steps.parser.csv.loadPersons
import edu.kit.ifv.application.steps.parser.csv.loadZones
import edu.kit.ifv.application.steps.parser.csv.personCsv
import edu.kit.ifv.application.steps.parser.csv.persons
import edu.kit.ifv.application.steps.parser.csv.plannedActivities
import edu.kit.ifv.application.steps.parser.csv.plannedActivityCsv
import edu.kit.ifv.application.steps.parser.csv.zoneCsv
import edu.kit.ifv.application.steps.parser.csv.zones
import edu.kit.ifv.application.steps.parser.loadImpedance
import edu.kit.ifv.application.steps.results.createHtmlReport
import edu.kit.ifv.application.steps.results.writeTrips
import edu.kit.ifv.core.modelsteps.Cloneable
import edu.kit.ifv.core.modelsteps.Config
import edu.kit.ifv.core.modelsteps.ExecutionMode
import edu.kit.ifv.core.modelsteps.Simulation
import edu.kit.ifv.core.modelsteps.initReport
import edu.kit.ifv.core.modelsteps.resources.MapRepository
import edu.kit.ifv.core.modelsteps.resources.MutableRepository
import edu.kit.ifv.core.modelsteps.steps.modelStep
import edu.kit.ifv.domain.shared.behavior.AttractivenessModel
import edu.kit.ifv.domain.shared.behavior.ChoiceModelModes
import edu.kit.ifv.domain.shared.car.CarId
import edu.kit.ifv.domain.shared.car.CarSegment
import edu.kit.ifv.domain.shared.data.household.HouseholdId
import edu.kit.ifv.domain.shared.data.person.PersonId
import edu.kit.ifv.domain.shared.datastructure.matrix.ConstantZoneIdMatrix
import edu.kit.ifv.domain.shared.datastructure.matrix.KeyBasedMatrixCreation
import edu.kit.ifv.domain.shared.datastructure.matrix.MatrixImpedance
import edu.kit.ifv.domain.shared.datastructure.matrix.ZoneMatrixCreation
import edu.kit.ifv.domain.shared.datastructure.schedule.replanning.ReplanningStrategy
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.enums.LegacyActivityType
import edu.kit.ifv.domain.shared.enums.LegacyMode
import edu.kit.ifv.domain.shared.enums.MainModes
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.enums.areatype.RegioStaR17
import edu.kit.ifv.domain.shared.enums.areatype.RegionType
import edu.kit.ifv.domain.shared.enums.household.EconomicStatus
import edu.kit.ifv.domain.shared.enums.legacyChoiceModelModes
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.zone.MaximalZone
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.domain.simulation.agent.DrtProviderAgent
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.agent.SharingProviderAgent
import edu.kit.ifv.domain.simulation.behavior.AvailabilityModelWithSharing
import edu.kit.ifv.domain.simulation.behavior.DestinationChoiceCharacteristics
import edu.kit.ifv.domain.simulation.behavior.ModeChoiceCharacteristics
import edu.kit.ifv.domain.simulation.behavior.legacyModeChoice
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
import edu.kit.ifv.domain.simulation.events.GenerateDestinationCharacteristics
import edu.kit.ifv.domain.simulation.events.GenerateModeCharacteristics
import edu.kit.ifv.domain.simulation.events.StandardDestinationImplementation
import edu.kit.ifv.domain.simulation.events.StandardModeImplementation
import edu.kit.ifv.domain.simulation.events.drtProviderStateMachine
import edu.kit.ifv.domain.simulation.events.personStateMachine
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
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

val visum_network = Path("src/test/resources/synthesis/leopoldshafen.net")
val attractivities = Path("data/attractivities.csv")
val dataFolder = Path("src/test/resources/testDemand/demand-data/")

val exampleChoiceModelModes = legacyChoiceModelModes.copy(options = MainModes.values())

// TODO discuss whether modes are context or config
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
    HasMutableModeAvailabilityModel,
    HasChoiceModelModes,
    HasModeChoiceModel,
    HasReplanningStrategy,
    HasSpawnModeCharacteristics,
    HasSpawnDestinationCharacteristics,
    HasMutableDestinationChoiceModel,
    HasModes {
    override val scenarioName: String = "regression test short term scenario"
    override val modes: CodePlan<Mode> = LegacyMode
    override lateinit var impedance: Impedance
    override lateinit var attractiveness: AttractivenessModel
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
    override lateinit var modeAvailability: AvailabilityModelWithSharing
    override val choiceModelModes: ChoiceModelModes = exampleChoiceModelModes
    override val modeChoice: FixedChoiceModel<Mode, ModeChoiceCharacteristics> = context(impedance) {
        legacyModeChoice
    } // TODO: Load later
    override lateinit var destinationChoiceModel: FixedChoiceModel<StandardLocation, DestinationChoiceCharacteristics>
    override val spawnModeCharacteristics: GenerateModeCharacteristics<ModeChoiceCharacteristics> =
        StandardModeImplementation
    override val spawnDestinationCharacteristics: GenerateDestinationCharacteristics<DestinationChoiceCharacteristics> =
        StandardDestinationImplementation
    override val replanningStrategy: ReplanningStrategy = ReplanningStrategy.SHIFT

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
    override val fractionOfPopulation: UnitIntervalValue = 1.0.share()
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

        loadAvailabilityModel()

        loadDestinationChoiceModel()

        buildSimulationAgents( // TODO maybe create individual model steps to set up the state machines
            personStateMachine(),
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

        createHtmlReport()
    }
}
