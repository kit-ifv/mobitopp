package application.config.subconfigs

import application.config.Yaml
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.test.Test
import kotlin.test.assertEquals

class MatrixConfigTest {
    val expected = listOf(
        MatrixConfig(
            matrixRepo = Path("a"),
        ),
        MatrixConfig(
            matrixRepo = Path("b"),
        ),
        MatrixConfig(
            matrixRepo = Path("c"),
            durationMatrixConfig = Path("b"),
        ),
        MatrixConfig(
            costMatrixConfig = Path("e"),
            durationMatrixConfig = Path("f"),
            distanceMatrix = Path("g"),
        ),
    )

    @Test
    fun readTest() {
        val testConfig = Path("src/test/resources/yamlParsing/MatrixConfigTest.yaml")
        val parsed: List<MatrixConfig> = Yaml.readYaml(testConfig)
        assertEquals(expected, parsed)
    }

    @Test
    fun writeTest() {
        val tempFile = Path("src/test/resources/tempOutput/readWriteMatrixConfigTest.yaml")
        tempFile.createParentDirectories()
        if (!tempFile.exists()) tempFile.createFile()
        for (testConfig in expected) {
            Yaml.writeYaml(tempFile, testConfig)
            val parsed = Yaml.readYaml<MatrixConfig>(tempFile)
            assertEquals(testConfig, parsed)
            tempFile.deleteIfExists()
        }
    }
}
