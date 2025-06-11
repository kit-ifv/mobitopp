package domain.shared.datastructure.matrix

import core.datastructure.matrix.YamlMultiMatrix
import domain.shared.datastructure.matrix.visum.VisumMatrixFormat
import domain.synthesis.data.ZoneId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import utils.Decodable
import utils.Encodable
import utils.units.AbsoluteTime
import utils.units.weeks
import kotlin.io.path.Path

class EncodableString(private val s: String) : Encodable, Comparable<String> by s, CharSequence by s {
    override val code: Int
        get() = error("Not implemented")

    override val description: String
        get() = s
}

class YamlMultiMatrixTest {
    @Test
    fun `test YamlMultiMatrix`() {
        // Mocked path to a YAML file
        val yamlFilePath = Path("src/test/resources/multi_matrix_parser/cost_matrix_configuration.yaml")

        // Mocked parser function
        val parser: (Double) -> Double = { it }

        // Mocked mode decoder
        val modeDecoder = object : Decodable<EncodableString> {
            override fun decode(i: Int): EncodableString {
                error("Not implemented")
            }

            override fun decode(s: String): EncodableString {
                return EncodableString(s)
            }

            override fun values(): Set<EncodableString> {
                error("Not implemented")
            }
        }

        // Create an instance of YamlMultiMatrix
        YamlMultiMatrix<EncodableString, ZoneId, Double>(
            yamlFilePath,
            parser,
            modeDecoder,
            AbsoluteTime.Companion.START,
            AbsoluteTime.Companion.START + 4.weeks,
            formats = listOf(VisumMatrixFormat)
        )

        // Test if parsing the YAML file completes without errors
        // If no exceptions are thrown during initialization, the test passes
        Assertions.assertTrue(true, "YAML file parsed successfully")
    }
}
