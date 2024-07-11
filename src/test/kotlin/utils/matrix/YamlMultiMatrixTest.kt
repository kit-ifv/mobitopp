package utils.matrix

import Decodable
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class YamlMultiMatrixTest {
    @Test
    fun `test YamlMultiMatrix`() {
        // Mocked path to a YAML file
        val yamlFilePath = Path("src/test/resources/multi_matrix_parser/cost_matrix_configuration.yaml")

        // Mocked parser function
        val parser: (Double) -> Double = { it }

        // Mocked mode decoder
        val modeDecoder = object : Decodable<String> {
            override fun decode(value: String): String {
                return value
            }
        }

        // Create an instance of YamlMultiMatrix
        val yamlMultiMatrix = YamlMultiMatrix<String, String, Double>(yamlFilePath, parser, modeDecoder)

        // Test if parsing the YAML file completes without errors
        // If no exceptions are thrown during initialization, the test passes
        assertTrue(true, "YAML file parsed successfully")
    }
}
