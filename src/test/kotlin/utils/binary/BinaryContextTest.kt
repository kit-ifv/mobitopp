package utils.binary

import domain.data.EconomicStatus
import domain.enums.Regiostar17
import modeling.steps.Run
import usecases.LegacyMode
import usecases.steps.ProjectContext
import usecases.steps.legacyData.loadHouseholds
import usecases.steps.legacyData.loadPrivateCars
import usecases.steps.legacyData.loadZones
import usecases.steps.loadActivities
import usecases.steps.loadPersons
import java.io.File
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertContentEquals


private val ROOT_FS = "\\\\ifv-fs\\Forschung\\Projekte_intern\\mobitopp\\Output"

private val rootRastatt = File("$ROOT_FS\\logiktram_rastatt_long-term-module\\rastatt")

class BinaryContextTest {
    @Test
    fun householdTest() {
        Run {
            ProjectContext(
                scenarioName = "testSteps",
                areaTypeCodes = Regiostar17,
                demandFolder = rootRastatt,
                economicalStatusCodes = EconomicStatus,
                simulationSeed = 42,
                modes = LegacyMode,
            )
        }.steps {
            loadZones()
            loadHouseholds()
            loadPersons()
            loadPrivateCars()
            loadActivities()
            writeZonesBinary(Path("src/test/resources/binaryZone.bin"))
            writeHouseholdBinary(Path("src/test/resources/binaryHousehold.bin"))
            writePersonsBinary(Path("src/test/resources/binaryPerson.bin"))
            writeActivitiesBinary(Path("src/test/resources/binaryPlannedActivities.bin"))
            writeCarsBinary(Path("src/test/resources/binaryCars.bin"))
        }
    }

    @Test
    fun readBinaryTest() {
//        Run {
//            ProjectContext(
//                scenarioName = "testSteps",
//                areaTypeCodes = Regiostar17,
//                demandFolder = rootRastatt,
//                economicalStatusCodes = EconomicStatus,
//                simulationSeed = 42,
//                modes = LegacyMode,
//            )
//        }.steps {
//            loadZonesFromBinary(Path("src/test/resources/binaryZone.bin"))
//            loadHouseholdFromBinary(Path("src/test/resources/binaryHousehold.bin"))
//            loadPersonsFromBinary(Path("src/test/resources/binaryPerson.bin"))
//            loadActivitiesFromBinary(Path("src/test/resources/binaryPlannedActivities.bin"))
//            loadCarsFromBinary(Path("src/test/resources/binaryCars.bin"))
//
//            println("Wololo")
//        }

        val x = ProjectContext(
            scenarioName = "testSteps",
            areaTypeCodes = Regiostar17,
            demandFolder = rootRastatt,
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
            demandFolder = rootRastatt,
            economicalStatusCodes = EconomicStatus,
            simulationSeed = 42,
            modes = LegacyMode,
        )
        y.loadZones()
        y.loadHouseholds()
        y.loadPersons()
        y.loadPrivateCars()
        y.loadActivities()

        val expected = x.zoneRepository.elements.toList()
        val actual = y.zoneRepository.elements.toList()
        assertContentEquals(expected, actual)
    }
}
