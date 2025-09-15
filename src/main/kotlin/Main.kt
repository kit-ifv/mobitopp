@file:Suppress("UnusedPrivateProperty")

import application.config.ExampleProjectContext
import application.steps.model.assignCarUsers
import application.steps.model.buildAgents
import application.steps.model.householdHomeLocation
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
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.LegacyMode
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.legacyChoiceModelPurposes
import domain.simulation.behavior.GaussianActivityDurationRandomizer
import domain.simulation.events.personStateMachine
import domain.synthesis.behavior.AssignAroundZoneCentroid
import domain.synthesis.data.EconomicStatus
import domain.synthesis.parser.NoActivityStartShifter
import units.meters
import units.share
import utils.ErrorHandling
import utils.csv.Row
import kotlin.io.path.Path

private const val ROOT_FS = "\\\\ifv-fs/Forschung/Projekte_intern/mobitopp/Output"

private val rootRastattPath = Path("$ROOT_FS/logiktram_rastatt_long-term-module/rastatt")

private val rootKarlsruhePath = Path("$ROOT_FS/logiktram_karlsruhe_long-term-module/karlsruhe")

private val rootHamburgPath = Path("$ROOT_FS/transmove-synthesis-city-bs/last-stable")

// private val attractivenessTypes = setOf(
//    LegacyActivityType.BUSINESS,x
//    LegacyActivityType.LEISURE_INDOOR,
//    LegacyActivityType.LEISURE_OUTDOOR,
//    LegacyActivityType.PRIVATE_BUSINESS,x
//    LegacyActivityType.PRIVATE_VISIT,
//    LegacyActivityType.SERVICE,x
//    LegacyActivityType.SHOPPING_DAILY,x
//    LegacyActivityType.SHOPPING_OTHER,x
//    LegacyActivityType.SHOPPING,x
//    LegacyActivityType.EDUCATION_PRIMARY,
//    LegacyActivityType.EDUCATION_SECONDARY,
//    LegacyActivityType.EDUCATION_TERTIARY,
//    // TODO Sightseeing?
// )

private const val ROOT_TRANSMOVE_ENV =
    "\\\\ifv-fs.ifv.kit.edu/Forschung/Projekte_intern/mobitopp/Input" +
        "/transmove/mobitopp-env/data/zone-repository"

fun main() {
    Simulation {
        ExampleProjectContext(
            scenarioName = "testSteps",
            regionTypeCodes = RegioStaR17,
            dataFolder = rootRastattPath,
            economicalStatusCodes = EconomicStatus,
            simulationSeed = 42,
            modes = LegacyMode,
        )
    }.steps {
        loadZones()
        loadVisumNetwork(Path("src/test/resources/rastatt.net"))

        val filter = scaleFilter<Row>(0.1.share())
        prepareHouseholds(
            filter = { filter(it) }
        )

        householdHomeLocation(
            AssignAroundZoneCentroid(50.meters)
        )

//        scalePopulation(0.1.share())
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

        val costMatrixConfigPath = Path("$ROOT_MTX/cost-matrix-configuration_transmove_turbo.yaml")
        val durationMatrixConfigPath = Path("$ROOT_MTX/time-matrix-configuration_transmove_turbo.yaml")
        val distanceMatrixPath = Path("$ROOT_MTX/DIS_Car.mtx.bz2")
        loadImpedance(
            costMatrixConfig = costMatrixConfigPath,
            durationMatrixConfig = durationMatrixConfigPath,
            distanceMatrix = distanceMatrixPath
        )

        // loadChoiceModels(legacyChoiceModelModes, legacyChoiceModelPurposes)

        assignFixedDestinations(homeActivity = LegacyActivityType.HOME)

        buildAgents(personStateMachine, GaussianActivityDurationRandomizer())

        simulate()
    }
}
