package application.config

import application.config.subconfigs.CoreCSVConfig
import domain.shared.config.Yaml
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.test.Test
import kotlin.test.assertEquals

class CoreCSVConfigParsingTest {
    val instantiatedWithRepos = CoreCSVConfig(
        dataRepo = Path("testD"),
        zoneRepo = Path("testZ"),
        householdCSV = Path("testH")
    )
    val instatiatedWithSingleParam = CoreCSVConfig(
        personCSV = Path("a"),
        householdCSV = Path("b"),
        activityCSV = Path("c"),
        privateCarsCSV = Path("d"),
        fixedDestinationCSV = Path("e"),
        attractivitiesCSV = Path("f"),
        zonesCSV = Path("h"),
    )
    val anotherConfig = CoreCSVConfig(
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
        val path = Path("src/test/resources/yamlParsing/CSVConfigTest.yaml")
        val parsed: List<CoreCSVConfig> = Yaml.readYaml(path)
        assertEquals(expected, parsed)
    }

    @Test
    fun readWriteTest() {
        val tempFile = Path("src/test/resources/tempOutput/readWriteCSVConfigTest.yaml")
        tempFile.createParentDirectories()
        if (!tempFile.exists()) tempFile.createFile()
        for (testConfig in expected) {
            Yaml.writeYaml(tempFile, testConfig)
            val parsed = Yaml.readYaml<CoreCSVConfig>(tempFile)
            assertEquals(testConfig, parsed)
            tempFile.deleteIfExists()
        }
    }
}
