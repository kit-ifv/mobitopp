package application.config.subconfigs

import domain.shared.config.Yaml
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.test.Test
import kotlin.test.assertEquals

class BikeSharingConfigTest {

    val instantiatedWithRepos = BikeSharingConfig(

        dataRepo = Path("testD"),
        zoneRepo = Path("testZ"),
        householdCSV = Path("testH"),
        sharingProviderName = "name",
        vehicleCountColumn = "column",
    )

    val instatiatedWithSingleParam: BikeSharingConfig = BikeSharingConfig(
        personCSV = Path("a"),
        householdCSV = Path("b"),
        activityCSV = Path("c"),
        privateCarsCSV = Path("d"),
        fixedDestinationCSV = Path("e"),
        attractivitiesCSV = Path("f"),
        bikeSharingStationsCSV = Path("g"),
        zonesCSV = Path("h"),
        sharingProviderName = "name",
        vehicleCountColumn = "column",
    )

    val anotherConfig = BikeSharingConfig(
        dataRepo = Path("x1"),
        zoneRepo = Path("x2"),
        householdCSV = Path("x3"),
        bikeSharingStationsCSV = Path("x5"),
        attractivitiesCSV = Path("x4"),
        sharingProviderName = "name",
        vehicleCountColumn = "column",
    )

    val expected = listOf(
        instantiatedWithRepos,
        instatiatedWithSingleParam,
        instatiatedWithSingleParam,
        anotherConfig,
    )

    @Test
    fun bikeSharingParsingTest() {
        val path = Path("src/test/resources/yamlParsing/BikeSharingConfigTest.yaml")
        val parsed: List<BikeSharingConfig> = Yaml.readYaml(path)
        assertEquals(expected, parsed)
    }

    @Test
    fun readWriteTest() {
        val tempFile = Path("src/test/resources/tempOutput/readWriteCSVConfigTest.yaml")
        tempFile.createParentDirectories()
        if (!tempFile.exists()) tempFile.createFile()
        for (testConfig in expected) {
            Yaml.writeYaml(tempFile, testConfig)
            val parsed = Yaml.readYaml<BikeSharingConfig>(tempFile)
            assertEquals(testConfig, parsed)
            tempFile.deleteIfExists()
        }
    }
}
