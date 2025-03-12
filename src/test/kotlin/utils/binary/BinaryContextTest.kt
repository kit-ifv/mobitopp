package utils.binary

import domain.data.EconomicStatus
import domain.enums.Regiostar17
import modeling.steps.Repository
import org.junit.jupiter.api.BeforeAll
import usecases.LegacyMode
import usecases.steps.ProjectContext
import usecases.steps.legacyData.loadHouseholds
import usecases.steps.legacyData.loadPrivateCars
import usecases.steps.legacyData.loadZones
import usecases.steps.loadActivities
import usecases.steps.loadPersons
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertContentEquals

private val demandFolder = Path("src/test/resources/testDemand").toFile()
class BinaryContextTest {

    @Test
    fun readBinaryTest() {
        val x = ProjectContext(
            scenarioName = "testSteps",
            areaTypeCodes = Regiostar17,
            demandFolder = demandFolder,
            economicalStatusCodes = EconomicStatus,
            simulationSeed = 42,
            modes = LegacyMode,
        )
        x.loadZonesFromBinary(Path("src/test/resources/binaryZone.bin"))
        x.loadHouseholdFromBinary(Path("src/test/resources/binaryHousehold.bin"))
        x.loadPersonsFromBinary(Path("src/test/resources/binaryPerson.bin"))
        x.loadActivitiesFromBinary(Path("src/test/resources/binaryPlannedActivities.bin"))
        x.loadCarsFromBinary(Path("src/test/resources/binaryCars.bin"))

        val y = ProjectContext(
            scenarioName = "testSteps",
            areaTypeCodes = Regiostar17,
            demandFolder = demandFolder,
            economicalStatusCodes = EconomicStatus,
            simulationSeed = 42,
            modes = LegacyMode,
        )
        y.loadZones()
        y.loadHouseholds()
        y.loadPersons()
        y.loadPrivateCars()
        y.loadActivities()
        repoEquals(x.zoneRepository, y.zoneRepository)
        repoEquals(x.householdRepository, y.householdRepository)
        repoEquals(x.personRepository, y.personRepository)
        repoEquals(x.carRepository, y.carRepository)
        repoEquals(x.plannedActivityRepository, y.plannedActivityRepository)
    }
    private fun <E : utils.Identifiable<I>, I> repoEquals(x: Repository<E, I>, y: Repository<E, I>) {
        val expected = x.elements.toList()
        val actual = y.elements.toList()
        assertContentEquals(expected, actual)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setup() {
            /*TODO find a way to only create these files when necessary, also the pipeline may be unhappy about this
               test because the paths may not be visible to the gitlab runner */
            createBinaryFiles()
        }
        private fun createBinaryFiles() {
            val x = ProjectContext(
                scenarioName = "testSteps",
                areaTypeCodes = Regiostar17,
                demandFolder = demandFolder,
                economicalStatusCodes = EconomicStatus,
                simulationSeed = 42,
                modes = LegacyMode,
            )

            x.loadZones()
            x.loadHouseholds()
            x.loadPersons()
            x.loadPrivateCars()
            x.loadActivities()
            x.writeZonesBinary(Path("src/test/resources/binaryZone.bin"))
            x.writeHouseholdBinary(Path("src/test/resources/binaryHousehold.bin"))
            x.writePersonsBinary(Path("src/test/resources/binaryPerson.bin"))
            x.writeActivitiesBinary(Path("src/test/resources/binaryPlannedActivities.bin"))
            x.writeCarsBinary(Path("src/test/resources/binaryCars.bin"))
        }
    }
}
