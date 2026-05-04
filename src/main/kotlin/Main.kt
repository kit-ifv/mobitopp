@file:Suppress("UnusedPrivateProperty")

import application.config.ShortTermConfig
import application.config.subconfigs.BaseCSVFiles
import application.config.subconfigs.CoreCSVConfig
import application.steps.ActivityTypesConfig
import application.steps.Config
import application.steps.HasDrtProviderRepo
import application.steps.HasHouseholdRepo
import application.steps.HasPersonRepo
import application.steps.HasSharingProviderRepo
import application.steps.HasZoneRepo
import application.steps.HouseholdCodesConfig
import application.steps.MatrixConfig
import application.steps.RegionCodesConfig
import application.steps.SimulationConfig
import application.steps.SourceFilesConfig
import application.steps.UnitConfig
import application.steps.model.AssignCarUserStep
import application.steps.model.HomeLocationStep
import application.steps.model.addDrtMemberships
import application.steps.model.buildAgents
import application.steps.model.dummyDrtAlgorithm
import application.steps.model.everyoneIsMember
import application.steps.model.finishDrtProviders
import application.steps.model.loadBehaviorModels
import application.steps.model.newDrtProvider
import application.steps.model.scaleFilter
import application.steps.model.simulate
import application.steps.parser.csv.activities
import application.steps.parser.csv.activitiesCsvConfig
import application.steps.parser.csv.assignFixedDestinations
import application.steps.parser.csv.filterFractionOfPopulation
import application.steps.parser.csv.filterHouseholds
import application.steps.parser.csv.finishZones
import application.steps.parser.csv.householdCsv
import application.steps.parser.csv.householdCsvParser
import domain.synthesis.parser.createHouseholdCsvParser
import application.steps.parser.csv.households
import application.steps.parser.csv.householdsFromCsvStep
import application.steps.parser.csv.loadAttractivities
import application.steps.parser.csv.loadHouseholds
import application.steps.parser.csv.loadPersons
import application.steps.parser.csv.loadZones
import application.steps.parser.csv.personCsv
import application.steps.parser.csv.personCsvParser
import application.steps.parser.csv.persons
import application.steps.parser.csv.personsFromCsvStep
import application.steps.parser.csv.prepareZones
import application.steps.parser.csv.privateCars
import application.steps.parser.csv.privateCarsFromCsvStep
import application.steps.parser.csv.zoneCsv
import application.steps.parser.csv.zoneCsvParser
import application.steps.parser.csv.zones
import application.steps.parser.loadImpedance
import application.steps.parser.loadVisumNetwork
import application.steps.parser.loadZonesFromBinary
import core.modelsteps.Cloneable
import core.modelsteps.ExecutionMode
import core.modelsteps.Simulation
import core.modelsteps.initReport
import core.modelsteps.resources.MapRepository
import core.modelsteps.resources.MutableRepository
import core.modelsteps.scopes.filterIdsStep
import domain.shared.config.Yaml
import domain.shared.datastructure.matrix.KeyBasedMatrixCreation
import domain.shared.datastructure.matrix.ZoneMatrixCreation
import domain.shared.datastructure.matrix.optionalCachedMatrixCreator
import domain.shared.enums.ActivityType
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.LegacyMode
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegioStaR4
import domain.shared.enums.areatype.RegionType
import domain.shared.enums.legacyChoiceModelPurposes
import domain.shared.location.MutableZone
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.simulation.behavior.GaussianActivityDurationRandomizer
import domain.simulation.events.drtProviderStateMachine
import domain.simulation.events.personStateMachine
import domain.synthesis.behavior.householdlocation.AssignAroundZoneCentroid
import domain.synthesis.data.DrtProvider
import domain.synthesis.data.DrtProviderId
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableDrtProviderData
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.MutableSharingProvider
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import domain.synthesis.data.SharingProvider
import domain.synthesis.data.SharingProviderId
import domain.synthesis.parser.NoActivityStartShifter
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.UnitIntervalValue
import edu.kit.ifv.units.meters
import edu.kit.ifv.units.share
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.Row
import utils.report.ReportBuilder
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.time.DurationUnit

val visum_network = Path("src/test/resources/synthesis/leopoldshafen.net")
val attractivities = Path("data/attractivities.csv")
val dataFolder = Path("src/test/resources/testDemand/demand-data/")
//val standardConfig = ShortTermConfig(
//    visumNetwork = visum_network,
//    fractionOfPopulation = 0.2,
//
//    zoneMatrixCreationMethod = VisumMatrixCreator,
////    simulationContext = ExampleProjectContext(
////        scenarioName = "MobitoppReengineeringMain",
////        dataFolder = dataFolder,
////        modes = MainModes,
////        simulationSeed = 42,
////        regionTypeCodes = RegioStaR17
////    ),
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
//).apply {
//    matrixConfig = MatrixConfig(matrixRepo = Path("src/test/resources/test_matrix"))
//    resultName = "mobitopp-integration.main.csv"
//    choiceModelModes = legacyChoiceModelModes
//}

class MyContext:
    HasZoneRepo<MutableZone, Zone>,
    HasHouseholdRepo<MutableHousehold, Household>,
    HasPersonRepo<MutablePerson, Person>,
    HasSharingProviderRepo<MutableSharingProvider, SharingProvider>,
    HasDrtProviderRepo<MutableDrtProviderData, DrtProvider>,
    Cloneable<MyContext>
{
    override val execMode: ExecutionMode = ExecutionMode()
    override val report: ReportBuilder = initReport()
    override val scenarioName: String = "regression test short term scenario"
    override val mutableZoneRepository: MutableRepository<MutableZone, ZoneId> = MapRepository("zone")
    override val mutablePersonRepository: MutableRepository<MutablePerson, PersonId> = MapRepository("person")
    override val mutableHouseholdRepository: MutableRepository<MutableHousehold, HouseholdId> = MapRepository("household")
    override val mutableSharingProviderRepository: MutableRepository<MutableSharingProvider, SharingProviderId> = MapRepository("sharingProvider")
    override val mutableDrtProviderRepository: MutableRepository<MutableDrtProviderData, DrtProviderId> = MapRepository("drtProvider")
    override fun clone(): MyContext = MyContext() //TODO doppelt zu context factory
}


class MyConfig :
    Config,
    SimulationConfig,
    MatrixConfig,
    UnitConfig,
    RegionCodesConfig,
    HouseholdCodesConfig,
    ActivityTypesConfig,
    SourceFilesConfig
{
    override val seed: Long = 42L
    override val costMatrixConfig: Path = Path("")
    override val durationMatrixConfig: Path = Path("")
    override val distanceMatrix: Path = Path("")
    override val matrixCreation: ZoneMatrixCreation = KeyBasedMatrixCreation
    override val distanceUnit: DistanceUnit = DistanceUnit.METERS
    override val durationUnit: DurationUnit = DurationUnit.MINUTES
    override val currencyUnit: CurrencyUnit = CurrencyUnit.EUROS
    override val regionTypeCodes: CodePlan<RegionType> = RegioStaR17
    override val activityTypes: CodePlan<ActivityType> = LegacyActivityType
    override val sourceFiles: BaseCSVFiles = CoreCSVConfig(
        dataRepo = dataFolder,
        zoneRepo = Path("src/test/resources/testDemand/zone-repository/"),
        attractivitiesCSV = attractivities,
    )
    override val errorHandling: ErrorHandling = ErrorHandling.THROW
    override val cachePath: Path = Path("cache")
    override val economicStatusCodes: CodePlan<EconomicStatus> = EconomicStatus
    override val fractionOfPopulation: UnitIntervalValue = 0.5.share()

}

@Suppress("LongMethod")
fun main(args: Array<String>) {


    Simulation {
        MyContext()
    }.steps {

        context(MyConfig()) {

            zones {
                loadZones(
                    zoneCsv(
                        delimiter = "|",
                        parser = zoneCsvParser {
                            errorHandling = ErrorHandling.WARNING
                            regionTypeCodes = RegioStaR4
                        }
                    )
                )

                loadZonesFromBinary(Path("rest.bin"))

                filterIdsStep("filter ids < 200") { it.value < 200 }
            }

            households {
                loadHouseholds(householdCsv(
                    parser = householdCsvParser {
                        errorHandling = ErrorHandling.WARNING
                    }
                ))

                filterFractionOfPopulation()

                filterHouseholds(listOf(1, 2, 3, 4, 5).map { HouseholdId(it.toLong()) })


            }

            persons {
                loadPersons(personCsv(
                    personCsvParser {
                        errorHandling = ErrorHandling.WARNING
                    }
                ))
            }


        }

    }


//
//
//    val shortTermConfig: ShortTermConfig<CoreCSVConfig> =
//        args.firstOrNull()?.let { Yaml.readYaml(it) } ?: standardConfig
//
//    shortTermConfig.validate()
//    Simulation {
//        shortTermConfig.simulationContext
//    }.steps {
//        loadVisumNetwork(
//            shortTermConfig.visumNetwork ?: visum_network
//        ) {
//            connector = VisumLocale.ConnectorLocale(travelTimeCar = "T0_TSYS(CS)")
//        }
//
//        val filter = scaleFilter<Row>(shortTermConfig.fractionOfPopulation.share())
//
//        prepareZones(shortTermConfig.sourceFiles.zonesCSV, errorHandling = shortTermConfig.errorHandling)
//        finishZones()
//        households {
//            source = householdsFromCsvStep(path = shortTermConfig.sourceFiles.householdCSV
//            ) {
//                errorHandling = shortTermConfig.errorHandling
//                this.filter = { filter(it) }
//            }.optionalCache(shortTermConfig.cachePath)
//            +HomeLocationStep(
//                this@steps,
//                AssignAroundZoneCentroid(50.meters)
//            )
//        }
//
//        newDrtProvider {
//            name = "DummyDrt"
//            mode = LegacyMode.RIDE_POOLING
//        }
//        addDrtMemberships(everyoneIsMember)
//        finishDrtProviders()
//
//        persons {
//            source = personsFromCsvStep(path = shortTermConfig.sourceFiles.personCSV) {
//                errorHandling = shortTermConfig.errorHandling
//            }.optionalCache(shortTermConfig.cachePath)
//        }
//
//        privateCars {
//            source = privateCarsFromCsvStep(path = shortTermConfig.sourceFiles.privateCarsCSV) {
//                errorHandling = shortTermConfig.errorHandling
//            }.optionalCache(shortTermConfig.cachePath)
//            AssignCarUserStep(this@steps)
//        }
//
//        activities {
//            source = activitiesCsvConfig(path = shortTermConfig.sourceFiles.activityCSV) {
//                errorHandling = shortTermConfig.errorHandling
//                shiftActivityStart = NoActivityStartShifter
//            }.optionalCache(shortTermConfig.cachePath)
//        }
//
//        loadAttractivities(
//            path = shortTermConfig.sourceFiles.attractivitiesCSV,
//            purposes = legacyChoiceModelPurposes,
//        )
//
//        loadImpedance(
//            costMatrixConfig = shortTermConfig.matrixConfig.costMatrixConfig,
//            durationMatrixConfig = shortTermConfig.matrixConfig.durationMatrixConfig,
//            distanceMatrix = shortTermConfig.matrixConfig.distanceMatrix,
//            matrixCreator = optionalCachedMatrixCreator(
//                shortTermConfig.cachePath,
//                shortTermConfig.zoneMatrixCreationMethod
//            )
//        )
//
//        loadBehaviorModels(
//            shortTermConfig.destinationChoiceModel,
//            shortTermConfig.modeChoiceModel,
//            shortTermConfig.choiceModelModes
//        )
//
//        assignFixedDestinations(
//            path = shortTermConfig.sourceFiles.fixedDestinationCSV,
//            homeActivity = LegacyActivityType.HOME
//        )
//
//        buildAgents(
//            personStateMachine,
//            drtStateMachine = drtProviderStateMachine,
//            drtAlgorithm = { _ ->
//                dummyDrtAlgorithm(
//                    zoneRepository.elements.filter { it.isDestination }.toList()
//                )
//            },
//            durationRandomizer = GaussianActivityDurationRandomizer()
//        )
//
//        simulate()
//    }
}
