@file:Suppress("UnusedPrivateProperty")

import application.config.ExampleProjectContext
import application.config.ShortTermConfig
import application.config.subconfigs.CoreCSVConfig
import application.config.subconfigs.MatrixConfig
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
import application.steps.parser.csv.finishZones
import application.steps.parser.csv.households
import application.steps.parser.csv.householdsFromCsvStep
import application.steps.parser.csv.loadAttractivities
import application.steps.parser.csv.persons
import application.steps.parser.csv.personsFromCsvStep
import application.steps.parser.csv.prepareZones
import application.steps.parser.csv.privateCars
import application.steps.parser.csv.privateCarsFromCsvStep
import application.steps.parser.loadImpedance
import application.steps.parser.loadVisumNetwork
import core.modelsteps.Simulation
import domain.shared.config.Yaml
import domain.shared.datastructure.matrix.VisumMatrixCreator
import domain.shared.datastructure.matrix.optionalCachedMatrixCreator
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
import domain.synthesis.behavior.householdlocation.AssignAroundZoneCentroid
import domain.synthesis.parser.NoActivityStartShifter
import edu.kit.ifv.units.meters
import edu.kit.ifv.units.share
import utils.ErrorHandling
import utils.csv.Row
import kotlin.io.path.Path

val visum_network = Path("src/test/resources/synthesis/leopoldshafen.net")
val attractivities = Path("data/attractivities.csv")
val dataFolder = Path("src/test/resources/testDemand/demand-data/")
val standardConfig = ShortTermConfig(
    visumNetwork = visum_network,
    fractionOfPopulation = 0.2,

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

    sourceFiles = CoreCSVConfig(
        dataRepo = dataFolder,
        zoneRepo = Path("src/test/resources/testDemand/zone-repository/"),
        attractivitiesCSV = attractivities,
    ),
).apply {
    matrixConfig = MatrixConfig(matrixRepo = Path("src/test/resources/test_matrix"))
    resultName = "mobitopp-integration.main.csv"
    choiceModelModes = legacyChoiceModelModes
}

@Suppress("LongMethod")
fun main(args: Array<String>) {
    val shortTermConfig: ShortTermConfig<CoreCSVConfig> =
        args.firstOrNull()?.let { Yaml.readYaml(it) } ?: standardConfig

    shortTermConfig.validate()
    Simulation {
        shortTermConfig.simulationContext
    }.steps {
        loadVisumNetwork(
            shortTermConfig.visumNetwork ?: visum_network
        ) {
            connector = VisumLocale.ConnectorLocale(travelTimeCar = "T0_TSYS(CS)")
        }

        val filter = scaleFilter<Row>(shortTermConfig.fractionOfPopulation.share())

        prepareZones(shortTermConfig.sourceFiles.zonesCSV, errorHandling = shortTermConfig.errorHandling)
        finishZones()
        households {
            source = householdsFromCsvStep(path = shortTermConfig.sourceFiles.householdCSV
            ) {
                errorHandling = shortTermConfig.errorHandling
                this.filter = { filter(it) }
            }.optionalCache(shortTermConfig.cachePath)
            +HomeLocationStep(
                this@steps,
                AssignAroundZoneCentroid(50.meters)
            )
        }

        newDrtProvider {
            name = "DummyDrt"
            mode = LegacyMode.RIDE_POOLING
        }
        addDrtMemberships(everyoneIsMember)
        finishDrtProviders()

        persons {
            source = personsFromCsvStep(path = shortTermConfig.sourceFiles.personCSV) {
                errorHandling = shortTermConfig.errorHandling
            }.optionalCache(shortTermConfig.cachePath)
        }

        privateCars {
            source = privateCarsFromCsvStep(path = shortTermConfig.sourceFiles.privateCarsCSV) {
                errorHandling = shortTermConfig.errorHandling
            }.optionalCache(shortTermConfig.cachePath)
            AssignCarUserStep(this@steps)
        }

        activities {
            source = activitiesCsvConfig(path = shortTermConfig.sourceFiles.activityCSV) {
                errorHandling = shortTermConfig.errorHandling
                shiftActivityStart = NoActivityStartShifter
            }.optionalCache(shortTermConfig.cachePath)
        }

        loadAttractivities(
            path = shortTermConfig.sourceFiles.attractivitiesCSV,
            purposes = legacyChoiceModelPurposes,
        )

        loadImpedance(
            costMatrixConfig = shortTermConfig.matrixConfig.costMatrixConfig,
            durationMatrixConfig = shortTermConfig.matrixConfig.durationMatrixConfig,
            distanceMatrix = shortTermConfig.matrixConfig.distanceMatrix,
            matrixCreator = optionalCachedMatrixCreator(
                shortTermConfig.cachePath,
                shortTermConfig.zoneMatrixCreationMethod
            )
        )

        loadBehaviorModels(
            shortTermConfig.destinationChoiceModel,
            shortTermConfig.modeChoiceModel,
            shortTermConfig.choiceModelModes
        )

        assignFixedDestinations(
            path = shortTermConfig.sourceFiles.fixedDestinationCSV,
            homeActivity = LegacyActivityType.HOME
        )

        buildAgents(
            personStateMachine,
            drtStateMachine = drtProviderStateMachine,
            drtAlgorithm = { _ ->
                dummyDrtAlgorithm(
                    zoneRepository.elements.filter { it.isDestination }.toList()
                )
            },
            durationRandomizer = GaussianActivityDurationRandomizer()
        )

        simulate()
    }
}
