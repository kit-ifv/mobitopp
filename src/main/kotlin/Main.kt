@file:Suppress("UnusedPrivateProperty")

import domain.data.EconomicStatus
import domain.enums.areatype.RegioStaR17
import modeling.steps.Run
import synthesis.AssignAroundZoneCentroid
import units.meters
import units.share
import usecases.LegacyActivityType
import usecases.LegacyMode
import usecases.legacyChoiceModelPurposes
import usecases.models.GaussianActivityDurationRandomizer
import usecases.steps.NoActivityStartShifter
import usecases.steps.ProjectContext
import usecases.steps.assignCarUsers
import usecases.steps.assignFixedDestinations
import usecases.steps.buildAgents
import usecases.steps.finishActivities
import usecases.steps.finishPersons
import usecases.steps.legacyData.finishHouseholds
import usecases.steps.legacyData.finishPrivateCars
import usecases.steps.legacyData.householdHomeLocation
import usecases.steps.legacyData.loadZones
import usecases.steps.legacyData.prepareHouseholds
import usecases.steps.legacyData.preparePrivateCars
import usecases.steps.loadAttractivities
import usecases.steps.loadImpedance
import usecases.steps.loadVisumNetwork
import usecases.steps.prepareActivities
import usecases.steps.preparePersons
import usecases.steps.scaleFilter
import usecases.steps.simulate
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
    Run {
        ProjectContext(
            scenarioName = "testSteps",
            regionTypeCodes = RegioStaR17,
            demandFolder = rootRastattPath,
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

        buildAgents(GaussianActivityDurationRandomizer())

        simulate()
    }
}
