import application.config.ExampleProjectContext
import application.steps.model.loadBehaviorModels
import application.steps.model.simulate
import application.steps.parser.csv.StationColumns
import application.steps.parser.csv.assignFixedDestinations
import application.steps.parser.csv.finishSharingStations
import application.steps.parser.csv.loadAttractivities
import application.steps.parser.csv.loadZones
import application.steps.parser.csv.prepareSharingStations
import application.steps.parser.dummyImpedance
import core.modelsteps.Simulation
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.LegacyMode
import domain.shared.enums.areatype.Bbsr17
import domain.shared.enums.legacyChoiceModelModes
import domain.shared.enums.legacyChoiceModelPurposes
import domain.simulation.behavior.legacyDestinationChoice
import domain.simulation.behavior.legacyModeChoice
import domain.synthesis.data.EconomicStatus
import utils.ErrorHandling
import kotlin.io.path.Path

private const val ROOT_FS = "\\\\ifv-fs/Forschung/Projekte_intern/mobitopp"

private val rootHamburg = Path("$ROOT_FS/Output/transmove-synthesis-city-bs/last-stable")

fun main() {
    Simulation {
        ExampleProjectContext(
            scenarioName = "testSteps",
            regionTypeCodes = Bbsr17,
            dataFolder = rootHamburg,
            economicalStatusCodes = EconomicStatus,
            simulationSeed = 42,
            modes = LegacyMode,
        )
    }.steps {
        loadZones()
        impedance.value = dummyImpedance
//        loadImpedance(
//            costMatrixConfig = File(
//                "$ROOT_MTX/cost-matrix-configuration_transmove_turbo.yaml"
//            ),
//            durationMatrixConfig = File(
//                "$ROOT_MTX/time-matrix-configuration_transmove_turbo.yaml"
//            ),
//            distanceMatrix = File(
//                "$ROOT_MTX/DIS_Car.mtx.bz2"
//            )
//        )

        val attractivitiesPath = Path("data/attractivities.csv")
        loadAttractivities(
            path = attractivitiesPath,
            legacyChoiceModelPurposes,
        )
        val bikesharingStationsPath =
            Path("${ROOT_FS}/Input/transmove/mobitopp-env/data/zone-repository/bikesharing_stations.csv")
        prepareSharingStations(
            errorHandling = ErrorHandling.THROW,
            path = bikesharingStationsPath,
            providerName = "StadtMobil",
            mode = LegacyMode.BIKESHARING,
            columns = StationColumns(vehicleCountColumn = "bikes"),
        )
        finishSharingStations()
        loadBehaviorModels(legacyDestinationChoice, legacyModeChoice, legacyChoiceModelModes)
        loadTestSet()
        assignFixedDestinations(LegacyActivityType.HOME, Path("src/test/resources/debughh/fixedDestination.csv"))
        simulate()
    }
}
