package domain.shared.datastructure.matrix

import domain.shared.datastructure.matrix.yaml.YamlMatrixLookup
import org.junit.jupiter.api.Assertions.assertTrue
import utils.units.sinceStart
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.nameWithoutExtension
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.time.Duration.Companion.hours

class BinaryMatrixCacheTest {

    val fileCacheLocation = Path("src/test/resources/tempOutput")
    val configYaml = Path("src/test/resources/multi_matrix_parser/cost_matrix_configuration.yaml")
    val readerCache = BinaryMatrixFileLookup(fileCacheLocation)

    @BeforeTest
    fun setup() {
        fileCacheLocation.createDirectories()
    }

    @AfterTest
    fun teardown() {
        readerCache.deleteDirectory()
    }

    @Test
    fun cacheConverterToBinary() {
        readerCache.clearDirectory()
        val yamlLookup = YamlMatrixLookup.default(
            configYaml,
            TestModes,
        )
        val fileCacheLookup = yamlLookup.cached(readerCache)
        assertFalse(readerCache.listCachedFiles().any { it.nameWithoutExtension == "good_case_matrix_1" })
        val outputMatrix = fileCacheLookup[TestModes.BIKESHARING, 1.hours.sinceStart]
        assertTrue(readerCache.listCachedFiles().any { it.nameWithoutExtension == "good_case_matrix_1" })
        val matrix = readerCache.format.deserialize(
            Path("src/test/resources/tempOutput/binary-cache/good_case_matrix_1.dbin"),
        )

        assertEquals(outputMatrix, matrix)
    }
}
