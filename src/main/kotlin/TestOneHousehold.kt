import domain.data.EconomicStatus
import domain.enums.Bbsr17
import modeling.steps.Run
import usecases.LegacyMode
import usecases.legacyChoiceModelModes
import usecases.steps.ProjectContext
import usecases.steps.StationColumns
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

fun main() {
    Run {
        ProjectContext(
            scenarioName = "testSteps",
            areaTypeCodes = Bbsr17,
            demandFolder = rootHamburg,
            economicalStatusCodes = EconomicStatus,
            simulationSeed = 42,
            modes = LegacyMode,
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
        )
        prepareSharingStations(
            errorHandling = ErrorHandling.THROW,
            file = File(
                "${ROOT_FS}\\Input\\transmove\\mobitopp-env\\data\\zone-repository\\bikesharing_stations.csv"
            ),
            providerName = "StadtMobil",
            mode = LegacyMode.BIKESHARING,
            columns = StationColumns(vehicleCountColumn = "bikes"),
        )
        finishSharingStations()
        loadChoiceModels(legacyChoiceModelModes)
        loadTestSet()
        assignHomeLocations()
        assignFixedDestinations(Path("src/test/resources/debughh/fixedDestination.csv").toFile())
        simulate()
    }
}
