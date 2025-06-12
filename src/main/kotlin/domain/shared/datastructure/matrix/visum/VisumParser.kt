@file:Suppress("MaximumLineLength", "TooGenericExceptionCaught", "StringLiteralDuplication")

package domain.shared.datastructure.matrix.visum

import domain.shared.location.ZoneId
import utils.files.decompressedBufferedReader
import java.nio.file.Path

/**
 * Custom exception class for Visum parsing errors.
 *
 * @param message A descriptive message of the error.
 * @param cause The cause of the error, if available.
 */
class VisumParseError(
    message: String,
    path: Path,
    cause: Throwable? = null
) : Exception("Error in visum file ${path.fileName} $message.\nSource: $path", cause)

/**
 * Function to convert a string to a ZoneId.
 */
private val AS_ZONE_ID: (String) -> ZoneId = { ZoneId(it.toLong()) }

/**
 * Extension function to split a string by whitespace block.
 */
private fun String.splitByWhitespaceBlock(): List<String> {
    return this.trim().split(Regex("\\s+"))
}

/**
 * Class responsible for parsing Visum files and extracting matrix data.
 *
 * @param path The path to the Visum file.
 */
class VisumParser(val path: Path) : IVisumParser {
    private var state: MatrixParseState = MatrixParseState.INIT
    private var lines: Iterator<IndexedValue<String>>? =
        path.decompressedBufferedReader().lineSequence().withIndex().iterator()

    //    private val generateLines = {path.toFile().decompressedBufferedReader().lineSequence().withIndex().iterator()}
    private var numberOfNetworkObjects: Int = 0
        get() {
            while (field == 0) {
                parseStep()
            }
            return field
        }

    private lateinit var zoneIds: Array<ZoneId>
    private var zoneIdIndex: Int = 0

    init {
//        val file = path.toFile()
//        val reader = file.decompressedBufferedReader() // properBufferedReader(file) // TODO check
//        val reader = file.decompressedBufferedReader()
//        lines = reader.lineSequence().withIndex().iterator() // TODO maybe? .filter { it.isNotEmpty() }
    }

    // Custom getter are not allowed with lateinit -.- therefore I wrote this. Take that kotlin compiler
    override fun getZoneIds(): Array<ZoneId> {
        while (!(this::zoneIds.isInitialized && zoneIdIndex == numberOfNetworkObjects)) {
            parseStep()
        }

        return zoneIds
    }

    private lateinit var array: DoubleArray
    private var columnIndex: Int = 0
    private var rowIndex: Int = -1

    // Custom getter are not allowed with lateinit -.- therefore I wrote this. Take that kotlin compiler
    // TODO use by lazy { } instead of lateinit
    override fun getArray(): DoubleArray {
        if (isNotFinished()) {
            while (isNotFinished()) {
                parseStep()
            }
            // test that there are no more lines of matrix data at the end of the mtx file
            parseStep()
        }

        return array
    }

    private fun isNotFinished() = !(
        this::array.isInitialized &&
            (rowIndex + 1) == numberOfNetworkObjects &&
            columnIndex == numberOfNetworkObjects
        )

    /**
     * Enumeration representing the different states of the matrix parsing process.
     */
    enum class MatrixParseState {
        INIT {
            override fun nextState(
                line: String,
                lineNumber: Int,
                parser: VisumParser,
            ): MatrixParseState {
                return when (line) {
                    "* Anzahl Netzobjekte" -> READ_NUMBER
                    else -> this
                }
            }
        },

        READ_NUMBER {
            override fun nextState(
                line: String,
                lineNumber: Int,
                parser: VisumParser,
            ): MatrixParseState {
                try {
                    parser.numberOfNetworkObjects = line.toUInt().toInt()
                    parser.array =
                        DoubleArray(parser.numberOfNetworkObjects * parser.numberOfNetworkObjects) { Double.NaN }
                    parser.zoneIds = Array(parser.numberOfNetworkObjects) { ZoneId(-1) }
                } catch (error: Exception) {
                    throw VisumParseError(
                        "A natural number (Uint) was expected in line $lineNumber. But the following string was found: \n\"$line\"",
                        parser.path,
                        error,
                    )
                }
                return PARSE_NET_OBJECT_NUMBER_HEADER
            }
        },

        PARSE_NET_OBJECT_NUMBER_HEADER {
            override fun nextState(
                line: String,
                lineNumber: Int,
                parser: VisumParser,
            ): MatrixParseState {
                return when (line) {
                    "* Netzobjekt-Nummern" -> READ_NET_OBJECT_NUMBERS
                    else -> throw VisumParseError(
                        "Expected \"* Netzobjekt-Nummern\" in line $lineNumber. But the following string was found: \n\"$line\"",
                        parser.path,
                    )
                }
            }
        },

        READ_NET_OBJECT_NUMBERS {
            override fun nextState(
                line: String,
                lineNumber: Int,
                parser: VisumParser,
            ): MatrixParseState {
                if (line == "*") {
                    if (parser.zoneIdIndex != parser.numberOfNetworkObjects) {
                        throw VisumParseError(
                            "Number of ZoneIds (${parser.zoneIdIndex}) does not match the expected number of network objects (${parser.numberOfNetworkObjects})." +
                                "Line $lineNumber: \n\"$line\"",
                            parser.path,
                        )
                    }

                    // Check if the zoneIds are unique
                    val uniqueZoneIds = parser.zoneIds.toSet()
                    if (uniqueZoneIds.size != parser.zoneIds.size) {
                        val duplicateZoneIds = parser.zoneIds.groupingBy { it }.eachCount().filter { it.value > 1 }.keys
                        throw VisumParseError("Duplicate ZoneIds found: $duplicateZoneIds", parser.path)
                    }

                    return PARSE_MATRIX_ROW
                }

                val zoneIds: List<ZoneId>
                try {
                    // Split line at Whitespace-Block and convert the numbers to ZoneIds
                    zoneIds = line.splitByWhitespaceBlock().map(AS_ZONE_ID)
                } catch (error: Exception) {
                    throw VisumParseError(
                        "Line $lineNumber contains a Number that could not be parsed to an ZoneId. Line $lineNumber: \n\"$line\"",
                        parser.path,
                        error
                    )
                }

                zoneIds.forEach { value ->
                    if (parser.zoneIdIndex == parser.zoneIds.size) {
                        throw VisumParseError(
                            "Number of ZoneIds (>${parser.zoneIdIndex}) does not match the expected number of network objects (${parser.numberOfNetworkObjects})." +
                                "Line $lineNumber: \n\"$line\"",
                            parser.path,
                        )
                    }

                    parser.zoneIds[parser.zoneIdIndex] = value
                    parser.zoneIdIndex += 1
                }

                return READ_NET_OBJECT_NUMBERS
            }
        },

        PARSE_MATRIX_ROW {
            private fun tryParseRowEnd(
                line: String,
                lineNumber: Int,
                parser: VisumParser,
            ): MatrixParseState {
                return when (line) {
                    "* Netzobjektnamen" -> END
                    else -> throw VisumParseError(
                        "Line $lineNumber could not be parsed. It did not match " +
                            "\"* Netzobjektnamen\". Line $lineNumber: \n\"$line\"",
                        parser.path
                    )
                }
            }

            private fun tryParseRowHeader(
                line: String,
                lineNumber: Int,
                parser: VisumParser,
            ): MatrixParseState {
                val regex = Regex("""^\* Obj (\d+) Summe = (-?\d+(\.\d+)?)""")
                val matchResult =
                    regex.matchEntire(line)
                        ?: throw VisumParseError(
                            "Line $lineNumber could not be parsed. " +
                                "It did not match the pattern: " +
                                "\"* Obj <NUMBER> Summe = <NUMBER>(.<NUMBER>)?\".\n" +
                                "More precisely, it did not match this regex: " +
                                "\"\"\"^\\* Obj (\\d+) Summe = (-?\\d+(\\.\\d+)?)\"\"\". Line $lineNumber: \n\"$line\"",
                            parser.path
                        )

                val (objNum, _) = matchResult.destructured

                val objectZoneId: ZoneId
                try {
                    objectZoneId = AS_ZONE_ID(objNum)
                } catch (error: Exception) {
                    throw VisumParseError(
                        "Line $lineNumber contains a Number \"$objNum\" that could not be parsed to ZoneId. Line $lineNumber: \n\"$line\"",
                        parser.path,
                        error,
                    )
                }

                try {
                    parser.startRow(objectZoneId)
                } catch (error: IndexOutOfBoundsException) {
                    throw VisumParseError(
                        "The file contains more values than declared. " +
                            "Expected ${parser.numberOfNetworkObjects} Rows but got at least ${parser.numberOfNetworkObjects + 1}. " +
                            "Error occurred while parsing line $lineNumber: \n\"$line\"",
                        parser.path,
                        error
                    )
                }

                return PARSE_MATRIX_ROW
            }

            private fun tryParseRowContent(
                line: String,
                lineNumber: Int,
                parser: VisumParser
            ): MatrixParseState {
                line.trim().split(Regex("""\s+""")).forEachIndexed { index, number ->
                    val elementNumber = index + 1
                    try {
                        parser.addElementToRow(number.toDouble())
                    } catch (error: IndexOutOfBoundsException) {
                        if (parser.rowIndex == parser.numberOfNetworkObjects) {
                            throw VisumParseError(
                                "The file contains more values than declared. " +
                                    "Expected ${parser.numberOfNetworkObjects} Rows but got at least ${parser.numberOfNetworkObjects + 1}. " +
                                    "Error occurred while parsing line $lineNumber: \n\"$line\"",
                                parser.path,
                                error
                            )
                        } else {
                            throw VisumParseError(
                                "The file contains more values than declared. " +
                                    "Expected ${parser.numberOfNetworkObjects} in this Row (Index: ${parser.rowIndex}, ZoneId: ${parser.zoneIds[parser.rowIndex]}). " +
                                    "Error occurred at element no. $elementNumber while parsing line $lineNumber: \n\"$line\"",
                                parser.path,
                                error
                            )
                        }
                    } catch (error: NumberFormatException) {
                        throw VisumParseError(
                            "Line $lineNumber contains a value \"$number\" that could not be parsed as a Double. " +
                                "Error occurred at element no. $elementNumber while parsing line $lineNumber: \n\"$line\"",
                            parser.path,
                            error
                        )
                    } catch (error: VisumParseError) {
                        throw VisumParseError(
                            "Line $lineNumber contains an element \"$number\" that could not be added to the matrix row. Error occurred at element no. $elementNumber: \n\"$line\"",
                            parser.path,
                            error
                        )
                    }
                }

                return PARSE_MATRIX_ROW
            }

            override fun nextState(
                line: String,
                lineNumber: Int,
                parser: VisumParser,
            ): MatrixParseState {
                val savedHeaderError: VisumParseError
                try {
                    return tryParseRowHeader(line, lineNumber, parser)
                } catch (headerError: VisumParseError) {
                    savedHeaderError = headerError
                }

                val savedContentError: VisumParseError
                try {
                    return tryParseRowContent(line, lineNumber, parser)
                } catch (contentError: VisumParseError) {
                    savedContentError = contentError
                }

                val savedEndError: VisumParseError
                try {
                    return tryParseRowEnd(line, lineNumber, parser)
                } catch (endError: VisumParseError) {
                    savedEndError = endError
                }

                val headerMsg = savedHeaderError.stackTraceToString().prependIndent("\t")
                val contentMsg = savedContentError.stackTraceToString().prependIndent("\t")
                val endMsg = savedEndError.stackTraceToString().prependIndent("\t")
                throw VisumParseError(
                    "Could not Parse Line because it was neither a Header because: \n$headerMsg\n" +
                        "Nor could it be parsed as a data line because: \n$contentMsg\n" +
                        "Nor could it be parsed as data end because:\n$endMsg",
                    parser.path
                )
            }
        },

        END {
            override fun nextState(
                line: String,
                lineNumber: Int,
                parser: VisumParser,
            ): MatrixParseState {
                parser.lines = null
                return END
            }
        };

        abstract fun nextState(
            line: String,
            lineNumber: Int,
            parser: VisumParser,
        ): MatrixParseState
    }

    private fun parseStep() {
        // TODO the lines.hasNext() call takes >50% of the method execution time.
        if (!lines!!.hasNext()) {
            throw VisumParseError("Unexpected End of File at: $path", path)
        }
        val indexedLine = lines!!.next()
        state = state.nextState(indexedLine.value, indexedLine.index + 1, this)
    }

    private fun addElementToRow(value: Double) {
        if (value.isNaN()) {
            throw VisumParseError(
                "Got NaN as a value for a matrix element row: $rowIndex, col: $columnIndex",
                path
            )
        }

        if (columnIndex >= numberOfNetworkObjects) {
            throw IndexOutOfBoundsException("ColumnIndex is $columnIndex but len is $numberOfNetworkObjects")
        } else if (rowIndex >= numberOfNetworkObjects) {
            throw IndexOutOfBoundsException("RowIndex is $rowIndex but len is $numberOfNetworkObjects")
        }

        val arrayIndex = rowIndex * numberOfNetworkObjects + columnIndex
        array[arrayIndex] = value
        columnIndex += 1
    }

    private fun startRow(zoneId: ZoneId) {
        // check if the row was finished or if it is the first row
        if (columnIndex < numberOfNetworkObjects && rowIndex != -1) {
            throw VisumParseError(
                "The row with the $rowIndex for zone ${zoneIds[rowIndex]} has too few elements ($columnIndex / $numberOfNetworkObjects).",
                path
            )
        }

        rowIndex += 1
        columnIndex = 0

        if (zoneIds[rowIndex] != zoneId) {
            throw VisumParseError("expected the row for ${zoneIds[rowIndex]} but got the row for $zoneId.", path)
        }
    }
}
