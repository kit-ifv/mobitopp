@file:Suppress("UnusedPrivateProperty")

import domain.data.EconomicStatus
import domain.enums.Bbsr17
import domain.enums.LegacyActivityType
import domain.roadnetwork.LocatableGraph
import modeling.steps.LateInit
import modeling.steps.Run
import units.share
import usecases.LegacyMode
import usecases.legacyChoiceModelModes
import usecases.steps.DefaultContext
import usecases.steps.LegacyContext
import usecases.steps.RoadNetworkContext
import usecases.steps.assignCarUsers
import usecases.steps.assignFixedDestinations
import usecases.steps.assignHomeLocations
import usecases.steps.finishActivities
import usecases.steps.legacyData.finishHouseholds
import usecases.steps.legacyData.finishPrivateCars
import usecases.steps.legacyData.loadZones
import usecases.steps.legacyData.prepareHouseholds
import usecases.steps.legacyData.preparePrivateCars
import usecases.steps.loadAttractivities
import usecases.steps.loadChoiceModels
import usecases.steps.loadImpedance
import usecases.steps.loadPersons
import usecases.steps.loadVisumNetwork
import usecases.steps.prepareActivities
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

private val attractivenessTypes = setOf(
    LegacyActivityType.BUSINESS,
    LegacyActivityType.LEISURE_INDOOR,
    LegacyActivityType.LEISURE_OUTDOOR,
    LegacyActivityType.PRIVATE_BUSINESS,
    LegacyActivityType.PRIVATE_VISIT,
    LegacyActivityType.SERVICE,
    LegacyActivityType.SHOPPING_DAILY,
    LegacyActivityType.SHOPPING_OTHER,
    LegacyActivityType.SHOPPING,
    LegacyActivityType.EDUCATION_PRIMARY,
    LegacyActivityType.EDUCATION_SECONDARY,
    LegacyActivityType.EDUCATION_TERTIARY,
    // TODO Sightseeing?
)

private const val ROOT_TRANSMOVE_ENV =
    "\\\\ifv-fs.ifv.kit.edu\\Forschung\\Projekte_intern\\mobitopp\\Input" +
        "\\transmove\\mobitopp-env\\data\\zone-repository"

private class RoadbasedContext(val original: DefaultContext) :
    DefaultContext by original,
    RoadNetworkContext {
    override val roadNetwork: LateInit<LocatableGraph> = LateInit("Visum RoadNetwork")
}

fun main() {
    Run {
        RoadbasedContext(
            LegacyContext(
                scenarioName = "testSteps",
                areaTypeCodes = Bbsr17,
                demandFolder = rootRastatt,
                economicalStatusCodes = EconomicStatus,
                simulationSeed = 42,
                modes = LegacyMode,
            )
        )
    }.steps {
        loadZones()
        loadVisumNetwork(Path("src/test/resources/rastatt.net"))
//        prepareSharingStations(
//            errorHandling = ErrorHandling.THROW,
//            file = File(
//        "\\\\ifv-fs.ifv.kit.edu\\Forschung\\Projekte_intern\\mobitopp\\Input\\transmove\\mobitopp-env\\data\\zone-repository\\bikesharing_stations.csv"
//                "$ROOT_FS\\Input\\transmove\\mobitopp-env\\data\\zone-repository\\bikesharing_stations.csv"
//            ),
//            providerName = "StadtMobil",
//            mode = StandardMode.BIKESHARING,
//            vehicleCountColumn = "bikes",
//        )
//        finishSharingStations()
//        loadTestSet()

        val filter = scaleFilter<Row>(0.1.share())
        prepareHouseholds(
            filter = { filter(it) }
        )

//        scalePopulation(0.1.share())

        finishHouseholds()

        loadPersons()
        preparePrivateCars() // file = File("example/car.csv"))
        assignCarUsers()
        finishPrivateCars()
        prepareActivities(errorHandling = ErrorHandling.WARNING)
        finishActivities()

        loadAttractivities(
            file = File("$ROOT_TRANSMOVE_ENV\\attractivities.csv"),
            activityTypes = attractivenessTypes
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

        loadChoiceModels(legacyChoiceModelModes)
        assignHomeLocations()
        assignFixedDestinations()
        simulate()
    }
}
