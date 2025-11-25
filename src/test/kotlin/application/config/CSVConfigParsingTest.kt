package application.config

import application.config.subconfigs.CSVConfig
import domain.shared.config.Yaml
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import kotlin.test.Test
import kotlin.test.assertEquals

class CSVConfigParsingTest {
    val instantiatedWithRepos = CSVConfig(
        dataRepo = Path("testD"),
        zoneRepo = Path("testZ"),
        householdCSV = Path("testH")
    )
    val instatiatedWithSingleParam = CSVConfig(
        personCSV = Path("a"),
        householdCSV = Path("b"),
        activityCSV = Path("c"),
        privateCarsCSV = Path("d"),
        fixedDestinationCSV = Path("e"),
        attractivitiesCSV = Path("f"),
        bikeSharingStationsCSV = Path("g"),
        zonesCSV = Path("h"),
    )
    val anotherConfig = CSVConfig(
        dataRepo = Path("x1"),
        zoneRepo = Path("x2"),
        householdCSV = Path("x3"),
        attractivitiesCSV = Path("x4"),
    )

    val expected = listOf(
        instantiatedWithRepos,
        instatiatedWithSingleParam,
        instatiatedWithSingleParam,
        anotherConfig,
    )

    @Test
    fun basicParsingTest() {
        val path = "src/test/resources/yamlParsing/CSVConfigTest.yaml"
        val parsed: List<CSVConfig> = Yaml.readYaml(path)
        assertEquals(expected, parsed)
    }

    @Test
    fun readWriteTest() {
        val tempFile = Path("src/test/resources/tempOutput/readWriteCSVConfigTest.yaml")
        for (testConfig in expected) {
            Yaml.writeYaml(tempFile, testConfig)
            val parsed = Yaml.readYaml<CSVConfig>(tempFile)
            assertEquals(testConfig, parsed)
            tempFile.deleteIfExists()
        }
    }
}
