package domain.shared.datastructure.matrix

import domain.shared.datastructure.matrix.binary.BinaryIntegerFormat
import domain.shared.datastructure.matrix.binary.MatrixDoubleFormat
import domain.shared.datastructure.matrix.binary.MatrixHalfFloatFormat
import domain.shared.datastructure.matrix.binary.MatrixShortFormat
import domain.shared.location.zone.ZoneId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import utils.files.PathChecksum
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import kotlin.math.log
import kotlin.math.pow

class StandardMatrixTest {
    private val matrixPath = Path("src/test/resources/multi_matrix_parser/good_case_matrix_0.mtx")

    private val targetPath = Path("src/test/resources/tempOutput/tempMatrix.bin")
    private val targetPathF = Path("src/test/resources/tempOutput/tempMatrixF.bin")
    private val targetPathHF = Path("src/test/resources/tempOutput/tempMatrixHF.bin")
    private val targetPathS = Path("src/test/resources/tempOutput/tempMatrixS.bin")

    @Test
    fun doubleConversionTest() {
        val standardMatrix = StandardMatrix.parseAsVisumMatrix(matrixPath)
        val format = MatrixDoubleFormat
        format.serialize(PathChecksum.from(1L), standardMatrix, targetPath)

        val output = format.deserialize(targetPath)
        assertEquals(standardMatrix, output)
        targetPath.deleteIfExists()
    }

    @Test
    fun floatConversionTest() {
        val standardMatrix = StandardMatrix.parseAsVisumMatrix(matrixPath)
        val format = BinaryIntegerFormat(1000)
        format.serialize(PathChecksum.from(1L), standardMatrix, targetPathF)
        val output = format.deserialize(targetPathF)
        assertEquals(standardMatrix, output)
        targetPathF.deleteIfExists()
    }

    @Test
    fun halfFloatConversionTest() {
        val standardMatrix = StandardMatrix.parseAsVisumMatrix(matrixPath)
        val format = MatrixHalfFloatFormat()
        format.serialize(PathChecksum.from(1), standardMatrix, targetPathHF)
        val output = format.deserialize(targetPathHF).matrix
        val expectedDouble = standardMatrix.matrix
        val rowLength = expectedDouble.numColumns
        val columnLength = expectedDouble.size / rowLength

        for (x in 0 until columnLength) {
            for (y in 0 until rowLength) {
                val expected = expectedDouble[x, y]
                val actual = output[x, y]
                // essentially checking for the output to be accurate on the first 3 decimal digits.
                var mostSignificantDecimalPlace = log(expected, 10.0)
                // toInt is rounding towards 0 which we don't want in the negative range
                if (mostSignificantDecimalPlace < 0) mostSignificantDecimalPlace--
                val accuracyDecimalPlace = mostSignificantDecimalPlace.toInt() - 3
                val expectedRange = (expected - (5 * 10.0.pow(accuracyDecimalPlace)))
                    .rangeTo(expected + (5 * 10.0.pow(accuracyDecimalPlace)))
                println("expected $expected actual $actual")
                println(accuracyDecimalPlace)
                println(expectedRange)
                assert(actual in expectedRange)
            }
        }
        targetPathHF.deleteIfExists()
    }

    @Test
    fun shortConversionTest() {
        val a = ZoneId(1)
        val b = ZoneId(2)
        val c = ZoneId(9001)
        val standardMatrix = StandardMatrix.fromValues(
            listOf(0.0, 1.0, 0.01, 655.34, 999999.0, 9001.0, 100.15, 2.2, 3.3),
            listOf(a, b, c),
        )
        val format = MatrixShortFormat
        format.serialize(PathChecksum.from(1L), standardMatrix, targetPathS)
        val output = format.deserialize(targetPathS)
        assertEquals(output[a, a], 0.0)
        assertEquals(output[a, b], 1.0)
        assertEquals(output[a, c], 0.01)
        assertEquals(output[b, a], 655.34)
        assertEquals(output[b, b], 999999.0)
        assertEquals(output[b, c], 999999.0)
        assertEquals(output[c, a], 100.15)
        assertEquals(output[c, b], 2.2)
        assertEquals(output[c, c], 3.3)
        targetPathS.deleteIfExists()
    }
}
