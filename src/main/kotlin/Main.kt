@file:Suppress("UnusedPrivateProperty")

import application.config.ExampleProjectContext
import application.config.ShortTermConfig
import application.config.subconfigs.CSVConfig
import application.config.subconfigs.MatrixConfig
import application.steps.model.addDrtMemberships
import application.steps.model.assignCarUsers
import application.steps.model.buildAgents
import application.steps.model.dummyDrtAlgorithm
import application.steps.model.everyoneIsMember
import application.steps.model.householdHomeLocation
import application.steps.model.loadBehaviorModels
import application.steps.model.newDrtProvider
import application.steps.model.scaleFilter
import application.steps.model.simulate
import application.steps.parser.csv.assignFixedDestinations
import application.steps.parser.csv.finishActivities
import application.steps.parser.csv.finishHouseholds
import application.steps.parser.csv.finishPersons
import application.steps.parser.csv.finishPrivateCars
import application.steps.parser.csv.loadAttractivities
import application.steps.parser.csv.loadZones
import application.steps.parser.csv.prepareActivities
import application.steps.parser.csv.prepareHouseholds
import application.steps.parser.csv.preparePersons
import application.steps.parser.csv.preparePrivateCars
import application.steps.parser.csv.prepareZones
import application.steps.parser.loadImpedance
import application.steps.parser.loadVisumNetwork
import application.steps.results.addPlot
import core.modelsteps.Simulation
import core.results.plots.asLinePlot
import core.results.plots.data.Ordering
import core.results.plots.forData
import core.results.plots.modeStringColor
import domain.shared.config.Yaml
import domain.shared.datastructure.matrix.VisumMatrixCreator
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.LegacyMode
import domain.shared.enums.MainModes
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.legacyChoiceModelModes
import domain.shared.enums.legacyChoiceModelPurposes
import domain.simulation.behavior.DestinationChoiceParameters
import domain.simulation.behavior.GaussianActivityDurationRandomizer
import domain.simulation.behavior.ModeChoiceParameters
import domain.simulation.behavior.legacyDestinationChoiceBuilder
import domain.simulation.behavior.legacyModeChoiceBuilder
import domain.simulation.events.drtProviderStateMachine
import domain.simulation.events.personStateMachine
import domain.simulation.results.personLegs
import domain.synthesis.behavior.AssignAroundZoneCentroid
import domain.synthesis.parser.NoActivityStartShifter
import edu.kit.ifv.units.meters
import edu.kit.ifv.units.share
import utils.ErrorHandling
import utils.csv.Row
import kotlin.io.path.Path
import kotlin.time.Duration.Companion.minutes

val visum_network = Path("src/test/resources/rastatt.net")
val attractivities = Path("data/attractivities.csv")
val dataFolder = Path("src/test/resources/testDemand/demand-data/")
val standardConfig = ShortTermConfig(
    visumNetwork = visum_network,
    fractionOfPopulation = 1.0,

    cachePath = Path("data/data-cache"),
    zoneMatrixCreationMethod = VisumMatrixCreator,
    simulationContext = ExampleProjectContext(
        scenarioName = "MobitoppReengineeringMain",
        dataFolder = dataFolder,
        modes = MainModes,
        simulationSeed = 42,
        regionTypeCodes = RegioStaR17
    ),
    errorHandling = ErrorHandling.WARNING,
    resultPath = Path("results"),

    destinationChoiceModel = legacyDestinationChoiceBuilder.build(DestinationChoiceParameters()),
    modeChoiceModel = legacyModeChoiceBuilder.build(ModeChoiceParameters()),

    sharingProviderName = "",
    sourceFiles = CSVConfig(
        dataRepo = dataFolder,
        zoneRepo = Path("src/test/resources/testDemand/zone-repository/"),
        attractivitiesCSV = attractivities,
    ),
).apply {
    matrixConfig = MatrixConfig(matrixRepo = Path(ROOT_MTX))
    resultName = "mobitopp-main.csv"
    vehicleCountColumn = ""
    choiceModelModes = legacyChoiceModelModes
}

@Suppress("LongMethod")
fun main(args: Array<String>) {
    val shortTermConfig: ShortTermConfig =
        args.firstOrNull()?.let { Yaml.readYaml(it) } ?: standardConfig

    shortTermConfig.validate()
    Simulation {
        shortTermConfig.simulationContext
    }.steps {
        prepareZones(shortTermConfig.sourceFiles.zonesCSV)
        loadZones()
        loadVisumNetwork(shortTermConfig.visumNetwork ?: visum_network)

        val filter = scaleFilter<Row>(shortTermConfig.fractionOfPopulation.share())

        prepareHouseholds(
            path = shortTermConfig.sourceFiles.householdCSV,
            filter = { filter(it) }
        )

        householdHomeLocation(
            AssignAroundZoneCentroid(50.meters)
        )

//        scalePopulation(0.1.share())

        newDrtProvider {
            name = "DummyDrt"
            mode = LegacyMode.RIDE_POOLING
        }

        finishHouseholds()

        preparePersons(
            path = shortTermConfig.sourceFiles.personCSV,
        )
        addDrtMemberships(everyoneIsMember)

        preparePrivateCars(
            path = shortTermConfig.sourceFiles.privateCarsCSV
        )

        assignCarUsers()
        finishPrivateCars()

        prepareActivities(
            path = shortTermConfig.sourceFiles.activityCSV,
            errorHandling = ErrorHandling.WARNING,
            shiftActivityStart = NoActivityStartShifter
        )

        finishActivities()
        finishPersons()

        loadAttractivities(
            path = shortTermConfig.sourceFiles.attractivitiesCSV,
            purposes = legacyChoiceModelPurposes,
        )

        loadImpedance(
            costMatrixConfig = shortTermConfig.matrixConfig.costMatrixConfig,
            durationMatrixConfig = shortTermConfig.matrixConfig.durationMatrixConfig,
            distanceMatrix = shortTermConfig.matrixConfig.distanceMatrix,
        )

        loadBehaviorModels(
            shortTermConfig.destinationChoiceModel,
            shortTermConfig.modeChoiceModel,
            shortTermConfig.choiceModelModes
        )

        assignFixedDestinations(homeActivity = LegacyActivityType.HOME)

        buildAgents(
            personStateMachine,
            drtStateMachine = drtProviderStateMachine,
            drtAlgorithm = dummyDrtAlgorithm(
                zoneRepository.elements.filter { it.isDestination }.toList()
            ),
            durationRandomizer = GaussianActivityDurationRandomizer()
        )

        simulate()

        addPlot {
            forData {
                personLegs
            }.groupBy {
                it.leg.transportType
            }.count {
                it.leg.startTime.roundToMultipleOf(5.minutes)
            }.sortX {
                Ordering.Ascending()
            }.asLinePlot {
                name = "timeline by mode"
                xAxisLabel = "time"
                yAxisLabel = "trip count"
                coloring = { modeStringColor(it.description) }
            }
        }
    }
}
