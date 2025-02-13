import domain.data.EconomicStatus
import domain.enums.Bbsr17
import modeling.steps.Run
import usecases.LegacyMode
import usecases.steps.ProjectContext
import usecases.steps.StationColumns
import usecases.steps.finishActivities
import usecases.steps.finishSharingStations
import usecases.steps.legacyData.loadHouseholds
import usecases.steps.legacyData.loadZones
import usecases.steps.loadPersons
import usecases.steps.prepareActivities
import usecases.steps.prepareSharingStations
import usecases.steps.simulate
import utils.ErrorHandling
import java.io.File
import kotlin.io.path.Path

private val rootHamburg = File(
    "\\\\ifv-fs\\Forschung\\Projekte_intern\\mobitopp\\Output" +
        "\\transmove-synthesis-city-bs\\last-stable"
)

fun main() {
    val input = "\\\\ifv-fs.ifv.kit.edu\\Forschung\\Projekte_intern\\mobitopp\\Input\\transmove\\mobitopp-env\\data"
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
        prepareSharingStations(
            errorHandling = ErrorHandling.THROW,
            file = File(
                "$input\\zone-repository\\bikesharing_stations.csv"
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
    loadHouseholds(Path("src/test/resources/hamburg/household.csv").toFile())
    loadPersons(Path("src/test/resources/hamburg/person.csv").toFile())
    //    preparePrivateCars(file = Path("src/test/resources/hamburg/person.csv").toFile()) // file = File("example/car.csv"))
    //    assignCarUsers()
    //    finishPrivateCars()
    prepareActivities(file = Path("src/test/resources/hamburg/activity.csv").toFile())
    finishActivities()
}
