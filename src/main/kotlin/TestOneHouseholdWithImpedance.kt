import domain.data.EconomicStatus
import domain.enums.areatype.Bbsr17
import modeling.steps.Run
import usecases.LegacyMode
import usecases.legacyChoiceModelModes
import usecases.legacyChoiceModelPurposes
import usecases.models.legacyDestinationChoice
import usecases.models.legacyModeChoice
import usecases.steps.ProjectContext
import usecases.steps.StationColumns
import usecases.steps.applyHomeLocationsInSchedule
import usecases.steps.assignFixedDestinations
import usecases.steps.finishSharingStations
import usecases.steps.legacyData.loadZones
import usecases.steps.loadAttractivities
import usecases.steps.loadChoiceModels
import usecases.steps.loadImpedance
import usecases.steps.output
import usecases.steps.prepareSharingStations
import usecases.steps.simulate
import utils.ErrorHandling
import kotlin.io.path.Path

private const val ROOT_FS = "\\\\ifv-fs/Forschung/Projekte_intern/mobitopp"

private val rootHamburg = Path("$ROOT_FS/Output/transmove-synthesis-city-bs/last-stable")

fun main() {
    Run {
        ProjectContext(
            scenarioName = "testSteps",
            regionTypeCodes = Bbsr17,
            demandFolder = rootHamburg,
            economicalStatusCodes = EconomicStatus,
            simulationSeed = 42,
            modes = LegacyMode,
        )
    }.steps {
        loadZones()

        val costMatrixConfigPath = Path("$ROOT_MTX_ROBIN/simplified-cost-matrix.yaml")
        val durationMatrixConfigPath = Path("$ROOT_MTX_ROBIN/simplified-time-matrix.yaml")
        val distanceMatrixPath = Path("$ROOT_MTX_ROBIN/DIS_Car.mtx.bz2")
        loadImpedance(
            costMatrixConfig = costMatrixConfigPath,
            durationMatrixConfig = durationMatrixConfigPath,
            distanceMatrix = distanceMatrixPath
        )

        val attractivitiesPath = Path("data/attractivities.csv")
        loadAttractivities(
            path = attractivitiesPath,
            purposes = legacyChoiceModelPurposes
        )
        val bikesharingStationsPath = Path(
            "${ROOT_FS}/Input/transmove/mobitopp-env/data/zone-repository/bikesharing_stations.csv"
        )
        prepareSharingStations(
            errorHandling = ErrorHandling.THROW,
            path = bikesharingStationsPath,
            providerName = "StadtMobil",
            mode = LegacyMode.BIKESHARING,
            columns = StationColumns(vehicleCountColumn = "bikes"),
        )
        finishSharingStations()
        loadChoiceModels(legacyDestinationChoice, legacyModeChoice, legacyChoiceModelModes)
        loadTestSet()
        applyHomeLocationsInSchedule()
        assignFixedDestinations(Path("src/test/resources/debughh/fixedDestination.csv"))
        simulate()
        output()
    }
}
