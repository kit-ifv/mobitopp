package domain.shared.datastructure.matrix

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import utils.Decodable
import utils.Encodable
import utils.WithExpiration
import utils.units.sinceStart
import utils.units.weeks
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours


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

    private val repetitivePath = Path("src/test/resources/multi_matrix_parser/repetitive_configuration.yaml")
    private val repetitiveYamlLookup = YamlMatrixLookup(repetitivePath, PotentialModes.Companion)
    @Test
    fun testWeekZero() {
        // path to a YAML file

        yamlLookup["bs", 4.hours].test(second, 12.hours)
        yamlLookup["bs", 0.hours].test(second, 12.hours)
        yamlLookup["bs", 13.hours].test(fourth, 5.days)
        yamlLookup["bs", 25.hours].test(fourth, 5.days)
        yamlLookup["bs", 5.days].test(third, 6.days)
        yamlLookup["bs", 6.days].test(first, 7.days)


    }
    @Test
    fun testWeekOne() {
        yamlLookup["bs", 1.weeks + 4.hours].test(second, 1.weeks + 12.hours)
        yamlLookup["bs", 1.weeks + 6.days].test(fifth, 2.weeks)
    }

    @TestFactory
    fun testRepetitiveness(): List<DynamicTest> {
        val days = 7
        val intervals = 4
        return (0..<days * intervals).map {
            DynamicTest.dynamicTest("duplicates don't expire $it") {
                repetitiveYamlLookup["bs", it.days / intervals].test(first, Duration.INFINITE)
                repetitiveYamlLookup["car", it.days / intervals].test(second, Duration.INFINITE)
                repetitiveYamlLookup["cs", it.days / intervals].test(third, Duration.INFINITE)
            }
        }
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
