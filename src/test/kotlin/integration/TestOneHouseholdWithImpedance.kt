package integration

import ROOT_MTX_ROBIN
import application.config.subconfigs.CoreCSVConfig
import application.steps.model.loadBehaviorModels
import application.steps.model.simulate
import application.steps.parser.csv.loadAttractivenessModelFromCsv
import application.steps.parser.csv.loadZones
import application.steps.parser.csv.zoneCsv
import application.steps.parser.csv.zones
import application.steps.parser.loadImpedance
import application.steps.results.writeTrips
import core.modelsteps.Simulation
import domain.shared.enums.LegacyMode
import domain.shared.enums.areatype.Bbsr17
import domain.shared.enums.legacyChoiceModelModes
import domain.simulation.behavior.legacyDestinationChoice
import domain.simulation.behavior.legacyModeChoice
import domain.synthesis.data.EconomicStatus
import kotlin.io.path.Path

private const val ROOT_FS = "\\\\ifv-fs/Forschung/Projekte_intern/mobitopp"

private val rootHamburg = Path("$ROOT_FS/Output/transmove-synthesis-city-bs/last-stable")

fun main() {
    val config = TestConfig(
        regionTypeCodes = Bbsr17,
        sourceFiles = CoreCSVConfig(
            dataRepo = rootHamburg.resolve("demand-data"),
            zoneRepo = rootHamburg.resolve("zone-repository")
        ),
        costMatrixConfig = Path("$ROOT_MTX_ROBIN/simplified-cost-matrix.yaml"),
        durationMatrixConfig = Path("$ROOT_MTX_ROBIN/simplified-time-matrix.yaml"),
        distanceMatrix = Path("$ROOT_MTX_ROBIN/DIS_Car.mtx.bz2"),
        economicStatusCodes = EconomicStatus,
        seed = 42,
    )

    Simulation(config) {
        TestContext(
            scenarioName = "testSteps",
            modes = LegacyMode,
        )
    }.steps {
        zones {
            loadZones(zoneCsv())
        }

        loadImpedance()

        val attractivenessPath = Path("data/attractivities.csv")
        loadAttractivenessModelFromCsv(attractivenessPath)

//        FILE AT PATH NO LONGER EXISTS!
//        val bikesharingStationsPath = Path(
//            "${ROOT_FS}/Input/transmove/mobitopp-env/data/zone-repository/bikesharing_stations.csv"
//        )
//        prepareSharingStations(
//            errorHandling = ErrorHandling.THROW,
//            path = bikesharingStationsPath,
//            providerName = "StadtMobil",
//            mode = LegacyMode.BIKESHARING,
//            columns = StationColumns(vehicleCountColumn = "bikes"),
//        )
//        finishSharingStations()

        loadBehaviorModels(
            legacyDestinationChoice,
            legacyModeChoice,
            legacyChoiceModelModes
        )

        loadTestSet()
        simulate()
        writeTrips()
    }
}
