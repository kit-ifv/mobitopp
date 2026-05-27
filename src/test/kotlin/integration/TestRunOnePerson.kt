package integration

import application.config.subconfigs.CoreCSVConfig
import application.steps.model.simulate
import application.steps.parser.csv.fixedDestinationCsv
import application.steps.parser.csv.fixedDestinations
import application.steps.parser.csv.householdCsv
import application.steps.parser.csv.households
import application.steps.parser.csv.loadActivities
import application.steps.parser.csv.loadHouseholds
import application.steps.parser.csv.loadPersons
import application.steps.parser.csv.loadZones
import application.steps.parser.csv.personCsv
import application.steps.parser.csv.persons
import application.steps.parser.csv.plannedActivities
import application.steps.parser.csv.plannedActivityCsv
import application.steps.parser.csv.zoneCsv
import application.steps.parser.csv.zones
import core.modelsteps.Simulation
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.LegacyMode
import domain.shared.enums.areatype.Bbsr17
import domain.synthesis.data.EconomicStatus
import kotlin.io.path.Path

private val rootHamburg = Path(
    "\\\\ifv-fs/Forschung/Projekte_intern/mobitopp/Output" +
        "/transmove-synthesis-city-bs/last-stable"
)

fun main() {
    val input = "\\\\ifv-fs.ifv.kit.edu/Forschung/Projekte_intern/mobitopp/Input/transmove/mobitopp-env/data"
    val config = TestConfig(
        regionTypeCodes = Bbsr17,
        sourceFiles = CoreCSVConfig(
            dataRepo = rootHamburg.resolve("demand-data"),
            zoneRepo = rootHamburg.resolve("zone-repository")
        ),
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
//        prepareSharingStations(
//            errorHandling = ErrorHandling.THROW,
//            path = Path(
//                "$input/zone-repository/bikesharing_stations.csv"
//            ),
//            providerName = "StadtMobil",
//            mode = LegacyMode.BIKESHARING,
//            columns = StationColumns(vehicleCountColumn = "bikes"),
//        )
//        finishSharingStations()
        loadTestSet()

        simulate()
    }
}

context(config: TestConfig)
fun TestContext.loadTestSet() {

    households {
        loadHouseholds(householdCsv(path = Path("src/test/resources/hamburg/household.csv")))
    }

    persons {
        loadPersons(personCsv(path = Path("src/test/resources/hamburg/person.csv")))

        plannedActivities {
            loadActivities(plannedActivityCsv(path = Path("src/test/resources/hamburg/activity.csv")))

            fixedDestinations( //Moved fixed destinations to loadTestSet
                LegacyActivityType.HOME,
                fixedDestinationCsv(path= Path("src/test/resources/debughh/fixedDestination.csv"))
            )
        }
    }

    //    preparePrivateCars(file = Path("src/test/resources/hamburg/person.csv").toFile()) // file = File("example/car.csv"))
    //    assignCarUsers()
    //    finishPrivateCars()
}
