@file:Suppress("UnusedPrivateProperty")

import domain.data.EconomicStatus
import domain.enums.areatype.RegioStaR17
import modeling.steps.Run
import synthesis.AssignAroundZoneCentroid
import units.meters
import units.share
import usecases.LegacyMode
import usecases.legacyChoiceModelPurposes
import usecases.steps.GaussianActivityDurationRandomizer
import usecases.steps.NoActivityStartShifter
import usecases.steps.ProjectContext
import usecases.steps.applyHomeLocationsInSchedule
import usecases.steps.assignCarUsers
import usecases.steps.assignFixedDestinations
import usecases.steps.assignPlannedActivities
import usecases.steps.finishActivities
import usecases.steps.finishPersons
import usecases.steps.gaussianDurationRandomizer
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
import usecases.steps.randomizeActivityDurations
import usecases.steps.scaleFilter
import usecases.steps.simulate
import utils.ErrorHandling
import utils.csv.Row
import java.io.File
import kotlin.io.path.Path

private const val ROOT_FS = "\\\\ifv-fs\\Forschung\\Projekte_intern\\mobitopp\\Output"

private val rootRastatt = File("$ROOT_FS\\logiktram_rastatt_long-term-module\\rastatt")

private val rootKarlsruhe = File("$ROOT_FS\\logiktram_karlsruhe_long-term-module\\karlsruhe")

private val rootHamburg = File("$ROOT_FS\\transmove-synthesis-city-bs\\last-stable")

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
    "\\\\ifv-fs.ifv.kit.edu\\Forschung\\Projekte_intern\\mobitopp\\Input" +
        "\\transmove\\mobitopp-env\\data\\zone-repository"

fun main() {
    Run {
        ProjectContext(
            scenarioName = "testSteps",
            regionTypeCodes = RegioStaR17,
            demandFolder = rootRastatt,
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
        assignPlannedActivities()
        randomizeActivityDurations(
            gaussianDurationRandomizer()
        )

        finishActivities()
        finishPersons()

        loadAttractivities(
            file = File("data/attractivities.csv"), // "$ROOT_TRANSMOVE_ENV\\attractivities.csv"),
            purposes = legacyChoiceModelPurposes,
        )

        loadImpedance(
            costMatrixConfig = File(
                "$ROOT_MTX\\cost-matrix-configuration_transmove_turbo.yaml"
            ),
            durationMatrixConfig = File(
                "$ROOT_MTX\\time-matrix-configuration_transmove_turbo.yaml"
            ),
            distanceMatrix = File(
                "$ROOT_MTX\\DIS_Car.mtx.bz2"
            )
        )

        // loadChoiceModels(legacyChoiceModelModes, legacyChoiceModelPurposes)
        applyHomeLocationsInSchedule()
        assignFixedDestinations()
        simulate()
    }
}
