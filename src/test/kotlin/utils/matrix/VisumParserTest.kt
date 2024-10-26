@file:Suppress("MaximumLineLength")

package utils.matrix

import datastructure.matrix.VisumParseError
import datastructure.matrix.VisumParser
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.test.DefaultAsserter.assertTrue

class VisumParserTest {
    @Test
    fun `test parsing valid Visum file`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/good_case_matrix.mtx")
        val parser = VisumParser(path)
        val zoneIds = parser.getZoneIds()

        assertEquals(16, zoneIds.size)
        // Assertion for zoneIds
        val expectedZoneIds = arrayOf(
            10101, 10301, 10302, 10601, 10701, 10702, 11001, 11002, 11003, 11101,
            11102, 11103, 11104, 11201, 11202, 11203
        )
        assertArrayEquals(expectedZoneIds, zoneIds.map { it.id.toInt() }.toTypedArray())

        val array = parser.getArray()

        assertEquals(16 * 16, array.size)
        // Assertion for matrix content
        val expectedMatrix = arrayOf(
            25.988, 0.057, 0.039, 0.014, 0.002, 0.133, 0.036, 0.014, 0.017, 0.099,
            0.382, 0.563, 0.444, 0.094, 0.085, 0.151,
            0.057, 39.806, 0.002, 0.013, 0.029, 0.160, 0.117, 0.048, 0.105, 0.015,
            0.014, 0.015, 0.010, 0.004, 0.004, 0.004,
            0.039, 0.002, 37.890, 0.009, 0.100, 0.025, 0.084, 0.032, 0.077, 0.013,
            0.012, 0.018, 0.010, 0.003, 0.003, 0.003,
            0.014, 0.013, 0.009, 26.517, 0.004, 0.018, 0.051, 0.020, 0.042, 0.015,
            0.050, 0.074, 0.058, 0.003, 0.004, 0.004,
            0.002, 0.029, 0.100, 0.004, 47.402, 0.010, 0.019, 0.008, 0.018, 0.032,
            0.129, 0.246, 0.195, 0.021, 0.037, 0.042,
            0.133, 0.160, 0.025, 0.018, 0.010, 28.139, 0.014, 0.006, 0.013, 0.078,
            0.232, 0.387, 0.525, 0.103, 0.122, 0.118,
            0.036, 0.117, 0.084, 0.051, 0.019, 0.014, 41.605, 0.001, 0.001, 0.084,
            0.122, 0.140, 0.166, 0.048, 0.055, 0.051,
            0.014, 0.048, 0.032, 0.020, 0.008, 0.006, 0.001, 27.492, 0.001, 0.038,
            0.037, 0.062, 0.053, 0.017, 0.019, 0.018,
            0.017, 0.105, 0.077, 0.042, 0.018, 0.013, 0.001, 0.001, 49.549, 0.172,
            0.109, 0.170, 0.131, 0.038, 0.043, 0.040,
            0.099, 0.015, 0.013, 0.015, 0.032, 0.078, 0.084, 0.038, 0.172, 37.590,
            0.008, 0.007, 0.008, 0.029, 0.033, 0.032,
            0.382, 0.014, 0.012, 0.050, 0.129, 0.232, 0.122, 0.037, 0.109, 0.008,
            40.536, 0.003, 0.003, 0.009, 0.010, 0.010,
            0.563, 0.015, 0.018, 0.074, 0.246, 0.387, 0.140, 0.062, 0.170, 0.007,
            0.003, 43.272, 0.004, 0.010, 0.011, 0.012,
            0.444, 0.010, 0.010, 0.058, 0.195, 0.525, 0.166, 0.053, 0.131, 0.008,
            0.003, 0.004, 59.360, 0.011, 0.012, 0.013,
            0.094, 0.004, 0.003, 0.003, 0.021, 0.103, 0.048, 0.017, 0.038, 0.029,
            0.009, 0.010, 0.011, 47.805, 0.001, 0.001,
            0.085, 0.004, 0.003, 0.004, 0.037, 0.122, 0.055, 0.019, 0.043, 0.033,
            0.010, 0.011, 0.012, 0.001, 40.307, 0.001,
            0.151, 0.004, 0.003, 0.004, 0.042, 0.118, 0.051, 0.018, 0.040, 0.032,
            0.010, 0.012, 0.013, 0.001, 0.001, 46.766
        )

        assertArrayEquals(expectedMatrix.toDoubleArray(), array, 0.001)
    }

    @Test
    fun `test parsing invalid Visum file with non-numeric network object number`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_non_numeric_net_object_number.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"natural number\"",
            exception.message?.contains("natural number") ?: false
        )
        assertTrue("Message did not contain the line number \"10\"", exception.message?.contains("10") ?: false)
        assertTrue("Message did not contain the malformed double \"-16\"", exception.message?.contains("-16") ?: false)
    }

    @Test
    fun `test parsing invalid Visum file with unexpected network object numbers line`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_network_object_numbers_line.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"Expected \\\"* Netzobjekt-Nummern\\\"\"",
            exception.message?.contains("Expected \"* Netzobjekt-Nummern\"") ?: false
        )
        assertTrue("Message did not contain the line number \"11\"", exception.message?.contains("11") ?: false)
        assertTrue(
            "Message did not contain the actual line content \"* Netz-Objekt-Nummern\"",
            exception.message?.contains("* Netz-Objekt-Nummern") ?: false
        )
    }

    @Test
    fun `test parsing invalid Visum file with too few ZoneIds`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_mismatched_number_of_zone_ids.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"Number of ZoneIds\"",
            exception.message?.contains("Number of ZoneIds") ?: false
        )
        assertTrue("Message did not contain the line number \"14\"", exception.message?.contains("14") ?: false)
        assertTrue(
            "Message did not contain the actual number of ZoneIds \"12\"",
            exception.message?.contains("12") ?: false
        )
        assertTrue(
            "Message did not contain the expected number of network objects \"16\"",
            exception.message?.contains("16") ?: false
        )
    }

    @Test
    fun `test parsing invalid Visum file with non-parsable ZoneId`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_non_parsable_zone_id.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"could not be parsed to an ZoneId\"",
            exception.message?.contains("could not be parsed to an ZoneId") ?: false
        )
        assertTrue("Message did not contain the line number \"13\"", exception.message?.contains("13") ?: false)
        assertTrue(
            "Message did not contain the line content \"     11102      11103      11104      11201      11202      112a03\"",
            exception.message?.contains("     11102      11103      11104      11201      11202      112a03") ?: false
        )
    }

    @Test
    fun `test parsing invalid Visum file with too many ZoneIds`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_mismatched_zone_ids.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"Number of ZoneIds\"",
            exception.message?.contains("Number of ZoneIds") ?: false
        )
        assertTrue("Message did not contain the line number \"13\"", exception.message?.contains("13") ?: false)
        assertTrue(
            "Message did not contain the expected number of network objects \"16\"",
            exception.message?.contains("16") ?: false
        )
    }

    @Test
    fun `test parsing invalid Visum file with unexpected line before network object names`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_unexpected_line_before_names.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"* Netzobjektnamen\"",
            exception.message?.contains("* Netzobjektnamen") ?: false
        )
        assertTrue("Message did not contain the line number \"63\"", exception.message?.contains("63") ?: false)
        assertTrue(
            "Message did not contain the actual line content \"* Netz-Objektnamen\"",
            exception.message?.contains("* Netz-Objektnamen") ?: false
        )
    }

    @Test
    fun `test parsing invalid Visum file with unexpected format in matrix row header`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_unexpected_format_in_row_header.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"* Obj <NUMBER> Summe = <NUMBER>(.<NUMBER>)?\"",
            exception.message?.contains("* Obj <NUMBER> Summe = <NUMBER>(.<NUMBER>)?") ?: false
        )
        assertTrue("Message did not contain the line number \"30\"", exception.message?.contains("30") ?: false)
        assertTrue(
            "Message did not contain the actual line content \"* Obj 10702 Summe == 184.458\"",
            exception.message?.contains("* Obj 10702 Summe == 184.458") ?: false
        )
    }

    @Test
    fun `test parsing invalid Visum file with non-parsable ZoneId number`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_non_parsable_zone_id_number.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"could not be parsed to ZoneId\"",
            exception.message?.contains("could not be parsed to ZoneId") ?: false
        )
        assertTrue("Message did not contain the line number \"30\"", exception.message?.contains("30") ?: false)
        assertTrue(
            "Message did not contain the actual number \"10702165468576845645649846541321564896746543216534655846531316534654563132165468456132135416\"",
            exception.message?.contains("10702165468576845645649846541321564896746543216534655846531316534654563132165468456132135416") ?: false
        )
        assertTrue(
            "Message did not contain the actual line content \"* Obj 10702165468576845645649846541321564896746543216534655846531316534654563132165468456132135416 Summe = 184.458\"",
            exception.message?.contains("* Obj 10702165468576845645649846541321564896746543216534655846531316534654563132165468456132135416 Summe = 184.458") ?: false
        )
    }

    @Test
    fun `test parsing invalid Visum file with more values than declared in matrix row`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_more_values_than_declared_in_row.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }
        println(exception)
        assertTrue(
            "Message did not contain the hint \"contains more values than declared\"",
            exception.message?.contains("contains more values than declared") ?: false
        )
        assertTrue("Message did not contain the line number \"32\"", exception.message?.contains("32") ?: false)
        assertTrue("Message did not contain the element number \"7\"", exception.message?.contains("7") ?: false)
        assertTrue(
            "Message did not contain the actual line content \" 0.232  0.387  0.525  0.103  0.122  0.118  0.117\"",
            exception.message?.contains(" 0.232  0.387  0.525  0.103  0.122  0.118  0.117") ?: false
        )
    }

    @Test
    fun `test parsing invalid Visum file with non-parsable Double value in matrix row`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_non_parsable_double_value_in_row.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"could not be parsed as a Double\"",
            exception.message?.contains("could not be parsed as a Double") ?: false
        )
        assertTrue("Message did not contain the line number \"17\"", exception.message?.contains("17") ?: false)
        assertTrue("Message did not contain the element number \"6\"", exception.message?.contains("6") ?: false)
        assertTrue(
            "Message did not contain the actual value \"0.11a8\"",
            exception.message?.contains("0.11a8") ?: false
        )
        assertTrue(
            "Message did not contain the actual line content \" 0.232  0.387  0.525  0.103  0.122  0.11a8\"",
            exception.message?.contains(" 0.232  0.387  0.525  0.103  0.122  0.11a8") ?: false
        )
    }

    @Test
    fun `test parsing invalid Visum file with element unable to be added to matrix row`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_element_unable_to_add_to_row.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"could not be added to the matrix row\"",
            exception.message?.contains("could not be added to the matrix row") ?: false
        )
        assertTrue("Message did not contain the line number \"17\"", exception.message?.contains("17") ?: false)
        assertTrue("Message did not contain the element number \"6\"", exception.message?.contains("6") ?: false)
        assertTrue("Message did not contain the actual element \"NaN\"", exception.message?.contains("NaN") ?: false)
        assertTrue(
            "Message did not contain the actual line content \" 0.232  0.387  0.525  0.103  0.122  NaN\"",
            exception.message?.contains(" 0.232  0.387  0.525  0.103  0.122  NaN") ?: false
        )
    }

    @Test
    fun `test parsing invalid Visum file with NaN value in matrix`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_nan_value_in_matrix.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"Got NaN as a value for a matrix element\"",
            exception.message?.contains("Got NaN as a value for a matrix element") ?: false
        )
    }

    @Test
    fun `test parsing invalid Visum file with too many elements in matrix row`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_too_many_elements_in_row.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"contains more values than declared\"",
            exception.message?.contains("contains more values than declared") ?: false
        )
        assertTrue("Message did not contain the actual row index \"5\"", exception.message?.contains("5") ?: false)
        assertTrue("Message did not contain the actual zone \"10702\"", exception.message?.contains("10702") ?: false)
        assertTrue(
            "Message did not contain the actual number of elements \"16\"",
            exception.message?.contains("16") ?: false
        )
    }

    @Test
    fun `test parsing invalid Visum file with too few elements in matrix row`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_too_few_elements_in_row.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"has too few elements\"",
            exception.message?.contains("has too few elements") ?: false
        )
        assertTrue("Message did not contain the actual row index \"5\"", exception.message?.contains("5") ?: false)
        assertTrue("Message did not contain the actual zone \"10702\"", exception.message?.contains("10702") ?: false)
        assertTrue(
            "Message did not contain the actual number of elements \"15\"",
            exception.message?.contains("15") ?: false
        )
        assertTrue(
            "Message did not contain the expected number of elements \"16\"",
            exception.message?.contains("16") ?: false
        )
    }

    @Test
    fun `test parsing invalid Visum file with unexpected zone ID in matrix row`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_unexpected_zone_id_in_row.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"expected the row for\"",
            exception.message?.contains("expected the row for") ?: false
        )
        assertTrue(
            "Message did not contain the actual expected zone ID \"10702\"",
            exception.message?.contains("10702") ?: false
        )
        assertTrue(
            "Message did not contain the actual received zone ID \"11001\"",
            exception.message?.contains("11001") ?: false
        )
    }

    @Test
    fun `test parsing invalid Visum file with more rows than declared`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_more_rows_than_declared.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"contains more values than declared\"",
            exception.message?.contains("contains more values than declared") ?: false
        )
        assertTrue(
            "Message did not contain the actual expected number of rows \"16\"",
            exception.message?.contains("16") ?: false
        )
        assertTrue(
            "Message did not contain the actual number of rows \"17\"",
            exception.message?.contains("17") ?: false
        )
    }

    @Test
    fun `test parsing invalid Visum file with duplicate zone IDs`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_duplicate_zone_ids.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"Duplicate ZoneIds found\"",
            exception.message?.contains("Duplicate ZoneIds found") ?: false
        )
        assertTrue(
            "Message did not contain the actual duplicate zone IDs \"11202\"",
            exception.message?.contains("11202") ?: false
        )
    }

    @Test
    fun `test parsing invalid short Visum file`() {
        val path: Path = Paths.get("src/test/resources/visum_parser/invalid_short_matrix.mtx")
        val parser = VisumParser(path)

        val exception = assertThrows<VisumParseError> {
            parser.getArray()
        }

        assertTrue(
            "Message did not contain the hint \"Unexpected End of File\"",
            exception.message?.contains("Unexpected End of File") ?: false
        )
    }
}
