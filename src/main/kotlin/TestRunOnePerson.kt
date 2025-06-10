import application.steps.model.simulate
import application.steps.parser.csv.StationColumns
import application.steps.parser.csv.finishActivities
import application.steps.parser.csv.finishSharingStations
import application.steps.parser.csv.loadHouseholds
import application.steps.parser.csv.loadPersons
import application.steps.parser.csv.loadZones
import application.steps.parser.csv.prepareActivities
import application.steps.parser.csv.prepareSharingStations
import core.modelsteps.Run
import domain.shared.enums.areatype.Bbsr17
import domain.simulation.behavior.LegacyMode
import domain.synthesis.data.EconomicStatus
import usecases.steps.ProjectContext
import utils.ErrorHandling
import kotlin.io.path.Path

private val rootHamburg = Path(
    "\\\\ifv-fs/Forschung/Projekte_intern/mobitopp/Output" +
        "/transmove-synthesis-city-bs/last-stable"
)

fun main() {
    val input = "\\\\ifv-fs.ifv.kit.edu/Forschung/Projekte_intern/mobitopp/Input/transmove/mobitopp-env/data"
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
        prepareSharingStations(
            errorHandling = ErrorHandling.THROW,
            path = Path(
                "$input/zone-repository/bikesharing_stations.csv"
            ),
            providerName = "StadtMobil",
            mode = LegacyMode.BIKESHARING,
            columns = StationColumns(vehicleCountColumn = "bikes"),
        )
        finishSharingStations()
        loadTestSet()

        simulate()
    }
}

fun ProjectContext.loadTestSet() {
    loadHouseholds(Path("src/test/resources/hamburg/household.csv"))
    loadPersons(Path("src/test/resources/hamburg/person.csv"))
    //    preparePrivateCars(file = Path("src/test/resources/hamburg/person.csv").toFile()) // file = File("example/car.csv"))
    //    assignCarUsers()
    //    finishPrivateCars()
    prepareActivities(path = Path("src/test/resources/hamburg/activity.csv"))
    finishActivities()
}
