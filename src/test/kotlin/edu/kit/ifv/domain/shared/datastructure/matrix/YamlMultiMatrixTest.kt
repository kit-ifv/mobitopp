package edu.kit.ifv.domain.shared.datastructure.matrix
import edu.kit.ifv.domain.shared.datastructure.matrix.yaml.YamlInfo
import edu.kit.ifv.domain.shared.datastructure.matrix.yaml.YamlMatrixLookup
import edu.kit.ifv.utils.WithExpiration
import edu.kit.ifv.utils.units.sinceStart
import edu.kit.ifv.utils.units.weeks
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class YamlMultiMatrixTest {

    private val first = Path("src/test/resources/multi_matrix_parser/good_case_matrix_0.mtx")
    private val second = Path("src/test/resources/multi_matrix_parser/good_case_matrix_1.mtx")
    private val third = Path("src/test/resources/multi_matrix_parser/good_case_matrix_2.mtx")
    private val fourth = Path("src/test/resources/multi_matrix_parser/good_case_matrix_3.mtx")
    private val fifth = Path("src/test/resources/multi_matrix_parser/good_case_matrix_4.mtx")

    private val yamlFilePath = Path("src/test/resources/multi_matrix_parser/cost_matrix_configuration.yaml")
    private val yamlLookup = YamlMatrixLookup.default(yamlFilePath, TestModes.Companion)

    private val repetitivePath = Path("src/test/resources/multi_matrix_parser/repetitive_configuration.yaml")
    private val repetitiveYamlLookup = YamlMatrixLookup.default(repetitivePath, TestModes.Companion)

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

    private operator fun YamlMatrixLookup<TestModes>.get(
        abbreviation: String,
        duration: Duration,
    ): WithExpiration<YamlInfo> {
        val dec = when (abbreviation) {
            "bs" -> TestModes.BIKESHARING
            "car" -> TestModes.CAR
            "cs" -> TestModes.CARSHARING
            else -> throw IllegalArgumentException("Unknown abbreviation: $abbreviation")
        }
        return this[dec, duration.sinceStart]
    }

    private fun WithExpiration<YamlInfo>.test(expected: Path, expectedExpiration: Duration) {
        assertEquals(expected, element.path)
        assertEquals(expectedExpiration.sinceStart, expiration)
    }
}
