@file:Suppress("UnusedPrivateProperty")

import application.config.ExampleProjectContext
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
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.MainModes
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.legacyChoiceModelModes
import domain.shared.enums.legacyChoiceModelPurposes
import domain.simulation.behavior.GaussianActivityDurationRandomizer
import domain.simulation.behavior.legacyDestinationChoice
import domain.simulation.behavior.legacyModeChoice
import domain.simulation.config.ShortTermConfig
import domain.simulation.events.personStateMachine
import domain.synthesis.behavior.AssignAroundZoneCentroid
import domain.synthesis.data.EconomicStatus
import domain.synthesis.parser.NoActivityStartShifter
import edu.kit.ifv.units.meters
import edu.kit.ifv.units.share
import utils.ErrorHandling
import utils.csv.Row
import kotlin.io.path.Path

fun main(args: Array<String>) {
    val shortTermConfig: ShortTermConfig =
        args.firstOrNull()?.let { Yaml.readYaml(it) } ?: error("No config argument handed.")

    shortTermConfig.validate()
    Simulation {
        ExampleProjectContext(
            scenarioName = "testSteps",
            regionTypeCodes = RegioStaR17,
            dataFolder = shortTermConfig.dataFolder,
            economicalStatusCodes = EconomicStatus,
            simulationSeed = 42,
            modes = MainModes,
        )
    }.steps {
        loadZones()
        loadVisumNetwork(Path("src/test/resources/rastatt.net"))

        val filter = scaleFilter<Row>(shortTermConfig.fractionOfPopulation.share())

        prepareHouseholds(
            filter = { filter(it) }
        )

        householdHomeLocation(
            AssignAroundZoneCentroid(50.meters)
        )

        finishHouseholds()

        preparePersons()

        preparePrivateCars() // file = File("example/car.csv"))
        assignCarUsers()
        finishPrivateCars()

        prepareActivities(
            errorHandling = ErrorHandling.WARNING,
            shiftActivityStart = NoActivityStartShifter
        )

        finishActivities()
        finishPersons()

        val attractivitiesPath = Path("data/attractivities.csv") // "$ROOT_TRANSMOVE_ENV/attractivities.csv")
        loadAttractivities(
            path = attractivitiesPath,
            purposes = legacyChoiceModelPurposes,
        )

        val costMatrixConfigPath = shortTermConfig.costMatrixConfigPath
        val durationMatrixConfigPath = shortTermConfig.durationMatrixConfigPath
        val distanceMatrixPath = shortTermConfig.distanceMatrixPath
        loadImpedance(
            costMatrixConfig = costMatrixConfigPath,
            durationMatrixConfig = durationMatrixConfigPath,
            distanceMatrix = distanceMatrixPath
        )

        loadBehaviorModels(legacyDestinationChoice, legacyModeChoice, legacyChoiceModelModes)

        assignFixedDestinations(homeActivity = LegacyActivityType.HOME)

        buildAgents(personStateMachine, GaussianActivityDurationRandomizer())

        simulate()
    }
}
