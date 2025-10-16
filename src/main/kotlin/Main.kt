@file:Suppress("UnusedPrivateProperty")

import domain.simulation.behavior.legacyDestinationChoiceBuilder
import application.config.ExampleProjectContext
import application.config.ShortTermConfig
import application.steps.model.assignCarUsers
import application.steps.model.buildAgents
import application.steps.model.householdHomeLocation
import application.steps.model.loadBehaviorModels
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
import application.steps.parser.loadImpedance
import application.steps.parser.loadVisumNetwork
import core.modelsteps.Simulation
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
import domain.simulation.behavior.legacyModeChoiceBuilder
import domain.simulation.events.personStateMachine
import domain.synthesis.behavior.AssignAroundZoneCentroid
import domain.synthesis.parser.NoActivityStartShifter
import edu.kit.ifv.units.meters
import edu.kit.ifv.units.share
import utils.ErrorHandling
import utils.csv.Row
import kotlin.io.path.Path

private const val ROOT_FS = "\\\\ifv-fs/Forschung/Projekte_intern/mobitopp/Output"

private val rootRastattPath = Path("$ROOT_FS/logiktram_rastatt_long-term-module/rastatt")

private val rootKarlsruhePath = Path("$ROOT_FS/logiktram_karlsruhe_long-term-module/karlsruhe")

private val rootHamburgPath = Path("$ROOT_FS/transmove-synthesis-city-bs/last-stable")


private const val ROOT_TRANSMOVE_ENV =
    "\\\\ifv-fs.ifv.kit.edu/Forschung/Projekte_intern/mobitopp/Input" +
            "/transmove/mobitopp-env/data/zone-repository"

val standardConfig = ShortTermConfig(
    visumNetwork = Path("src/test/resources/rastatt.net"),
    fractionOfPopulation = 1.0,
    matrixRepo = Path(ROOT_MTX),
    costMatrixConfig = Path("cost-matrix-configuration_transmove_turbo.yaml"),
    durationMatrixConfig = Path("time-matrix-configuration_transmove_turbo.yaml"),
    distanceMatrix = Path("DIS_Car.mtx.bz2"),
    cachePath = Path("data/data-cache"),
    zoneMatrixCreationMethod = VisumMatrixCreator,
    simulationContext = ExampleProjectContext(
        scenarioName = "MobitoppReengineeringMain",
        dataFolder = rootRastattPath,
        modes = MainModes,
        simulationSeed = 42,
        regionTypeCodes = RegioStaR17
    ),
    errorHandling = ErrorHandling.WARNING,
    resultPath = Path("results"),
    resultName = "mobitopp-main.csv",
    zoneRepo = Path("src/test/resources/testDemand/zone-repository"),
    destinationChoiceParameterSet = DestinationChoiceParameters(),
    modeChoiceParameterSet = ModeChoiceParameters(),
    choiceModelModes = legacyChoiceModelModes,
    sharingProviderName = "",
    vehicleCountColumn = "",
    attractivitiesCSV = Path("data/attractivities.csv")
)

fun main(args: Array<String>) {
    val shortTermConfig: ShortTermConfig<ModeChoiceParameters> =
        args.firstOrNull()?.let { Yaml.readYaml(it) } ?: standardConfig

    shortTermConfig.validate()
    Simulation {
        shortTermConfig.simulationContext
    }.steps {
        loadZones()
        loadVisumNetwork(shortTermConfig.visumNetwork ?: Path("src/test/resources/rastatt.net"))

        val filter = scaleFilter<Row>(shortTermConfig.fractionOfPopulation.share())

        prepareHouseholds(
            path = shortTermConfig.householdCSV ?: defaultHouseholdPath,
            filter = { filter(it) }
        )

        householdHomeLocation(
            AssignAroundZoneCentroid(50.meters)
        )

        finishHouseholds()

        preparePersons(
            path = shortTermConfig.personCSV ?: defaultPersonPath,
        )

        preparePrivateCars(
            path = shortTermConfig.privateCarsCSV ?: defaultCarPath,
        )

        assignCarUsers()
        finishPrivateCars()

        prepareActivities(
            path = shortTermConfig.activityCSV ?: defaultActivityPath,
            errorHandling = ErrorHandling.WARNING,
            shiftActivityStart = NoActivityStartShifter
        )

        finishActivities()
        finishPersons()

        loadAttractivities(
            path = shortTermConfig.attractivitiesCSV ?: Path("data/attractivities.csv"),
            purposes = legacyChoiceModelPurposes,
        )

        loadImpedance(
            costMatrixConfig = if (shortTermConfig.costMatrixConfig.isAbsolute) {
                shortTermConfig.costMatrixConfig
            } else {
                shortTermConfig.matrixRepo.resolve(shortTermConfig.costMatrixConfig)
            },
            durationMatrixConfig = if (shortTermConfig.durationMatrixConfig.isAbsolute) {
                shortTermConfig.durationMatrixConfig
            } else {
                shortTermConfig.matrixRepo.resolve(shortTermConfig.durationMatrixConfig)
            },
            distanceMatrix = if (shortTermConfig.distanceMatrix.isAbsolute) {
                shortTermConfig.distanceMatrix
            } else {
                shortTermConfig.matrixRepo.resolve(shortTermConfig.distanceMatrix)
            },
        )

        loadBehaviorModels(
            legacyDestinationChoiceBuilder.build(shortTermConfig.destinationChoiceParameterSet),
            legacyModeChoiceBuilder.build(shortTermConfig.modeChoiceParameterSet),
            shortTermConfig.choiceModelModes
        )

        assignFixedDestinations(homeActivity = LegacyActivityType.HOME)

        buildAgents(personStateMachine, GaussianActivityDurationRandomizer())

        simulate()
    }
}
