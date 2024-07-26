
import domain.data.EconomicStatus
import domain.enums.Bbsr17
import domain.enums.LegacyActivityType
import domain.enums.StandardMode
import modeling.steps.Run
import usecases.steps.LegacyContext
import usecases.steps.assignFixedDestinations
import usecases.steps.assignHomeLocations
import usecases.steps.dummyImpedance
import usecases.steps.finishSharingStations
import usecases.steps.legacyData.loadZones
import usecases.steps.loadAttractivities
import usecases.steps.loadChoiceModels
import usecases.steps.prepareSharingStations
import usecases.steps.simulate
import utils.ErrorHandling
import java.io.File
import kotlin.io.path.Path

private const val ROOT_FS = "\\\\ifv-fs\\Forschung\\Projekte_intern\\mobitopp"

private val rootHamburg = File(
    "$ROOT_FS\\Output\\transmove-synthesis-city-bs\\last-stable"
)
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
fun main() {
    Run {
        LegacyContext(
            scenarioName = "testSteps",
            areaTypeCodes = Bbsr17,
            demandFolder = rootHamburg,
            economicalStatusCodes = EconomicStatus,
            simulationSeed = 42
        )
    }.steps {
        loadZones()
        context.impedance.value = dummyImpedance
//        loadImpedance(
//            costMatrixConfig = File(
//                "$ROOT_MTX\\cost-matrix-configuration_transmove_turbo.yaml"
//            ),
//            durationMatrixConfig = File(
//                "$ROOT_MTX\\time-matrix-configuration_transmove_turbo.yaml"
//            ),
//            distanceMatrix = File(
//                "$ROOT_MTX\\DIS_Car.mtx.bz2"
//            )
//        )

        loadAttractivities(
            file = File("data/attractivities.csv"),
            activityTypes = attractivenessTypes
        )
        prepareSharingStations(
            errorHandling = ErrorHandling.THROW,
            file = File(
                "${ROOT_FS}\\Input\\transmove\\mobitopp-env\\data\\zone-repository\\bikesharing_stations.csv"
            ),
            providerName = "StadtMobil",
            mode = StandardMode.BIKESHARING,
            vehicleCountColumn = "bikes",
        )
        finishSharingStations()
        loadChoiceModels()
        loadTestSet()
        assignHomeLocations()
        assignFixedDestinations(Path("src/test/resources/debughh/fixedDestination.csv").toFile())
        simulate()
    }
}
