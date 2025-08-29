package domain.shared.datastructure.matrix

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import utils.Decodable
import utils.Encodable
import utils.WithExpiration
import utils.units.sinceStart
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class EncodableString(private val s: String) : Encodable, Comparable<String> by s, CharSequence by s {
    override val code: Int
        get() = error("Not implemented")

    override val description: String
        get() = s
}

private enum class PotentialModes(override val code: Int, override val description: String) :
    Encodable {
    BIKESHARING(0, "bikesharing"), CAR(1, "car"), CARSHARING(2, "carsharing_free_floating");

    companion object : Decodable<PotentialModes> {
        override fun values(): Set<PotentialModes> {
            return PotentialModes.entries.toSet()
        }
    }
}

class YamlMultiMatrixTest {

    private val first = Path("src/test/resources/multi_matrix_parser/good_case_matrix_0.mtx")
    private val second = Path("src/test/resources/multi_matrix_parser/good_case_matrix_1.mtx")
    private val third = Path("src/test/resources/multi_matrix_parser/good_case_matrix_2.mtx")
    private val fourth = Path("src/test/resources/multi_matrix_parser/good_case_matrix_3.mtx")
    private val fifth = Path("src/test/resources/multi_matrix_parser/good_case_matrix_4.mtx")

    private val yamlFilePath = Path("src/test/resources/multi_matrix_parser/cost_matrix_configuration.yaml")
    private val yamlLookup = YamlMatrixLookup(yamlFilePath, PotentialModes.Companion)

    @Test
    fun testWeekZero() {
        // path to a YAML file

        yamlLookup["bs", 4.hours].test(fourth, 5.days)
        yamlLookup["bs", 0.hours].test(fourth, 5.days)
        yamlLookup["bs", 13.hours].test(fourth, 5.days)
        yamlLookup["bs", 25.hours].test(fourth, 5.days)


    }

    private operator fun MatrixLookup<PotentialModes>.get(
        abbreviation: String,
        duration: Duration,
    ): WithExpiration<YamlInfo> {
        val dec = when (abbreviation) {
            "bs" -> PotentialModes.BIKESHARING
            "car" -> PotentialModes.CAR
            "cs" -> PotentialModes.CARSHARING
            else -> throw IllegalArgumentException("Unknown abbreviation: $abbreviation")
        }
        return this[dec, duration.sinceStart]
    }

    private fun WithExpiration<YamlInfo>.test(expected: Path, expectedExpiration: Duration) {
        assertEquals(expected, element.path)
        assertEquals(expectedExpiration.sinceStart, expiration)
    }
}
