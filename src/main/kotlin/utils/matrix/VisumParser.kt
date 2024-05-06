@file:Suppress("MaximumLineLength", "TooGenericExceptionCaught")

package utils.matrix

import data.ZoneId
import java.nio.file.Path

/**
 * Custom exception class for Visum parsing errors.
 *
 * @param message A descriptive message of the error.
 * @param cause The cause of the error, if available.
 */
class VisumParseError(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

/**
 * Function to convert a string to a ZoneId.
 */
private val AS_ZONE_ID: (String) -> ZoneId = { ZoneId(it.toInt()) }

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
class VisumParser(path: Path) {
    private var state: MatrixParseState = MatrixParseState.INIT
    private val lines: Iterator<IndexedValue<String>>
    private var numberOfNetworkObjects: Int = 0
        get() {
            while (field == 0) {
                parseStep()
            }
            return field
        }

    private lateinit var zoneIds: Array<ZoneId>

    // Custom getter are not allowed with lateinit -.- therefore I wrote this. Take that kotlin compiler
    fun getZoneIds(): Array<ZoneId> {
        while (!(this::zoneIds.isInitialized && zoneIds.isNotEmpty())) {
            parseStep()
        }
        return zoneIds
    }

    private lateinit var array: Array<Double>
    private var columnIndex: Int = 0
    private var rowIndex: Int = -1

    //todo vvv

    // Custom getter are not allowed with lateinit -.- therefore I wrote this. Take that kotlin compiler
    fun getArray(): Array<Double> {
        while (!(this::array.isInitialized && (rowIndex + 1) == numberOfNetworkObjects && columnIndex == numberOfNetworkObjects)) {
            parseStep()
        }
        return array
    }

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
                    parser.array = Array(parser.numberOfNetworkObjects) { Double.NaN }
                } catch (error: Exception) {
                    throw VisumParseError(
                        "A natural number (Uint) was expected in line $lineNumber. But the following string was found: \n\"$line\"",
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
                    return PARSE_MATRIX_ROW
                }

                val zoneIds: List<ZoneId>
                try {
                    // Split line at Whitespace-Block and convert the numbers to ZoneIds
                    zoneIds = line.splitByWhitespaceBlock().map(AS_ZONE_ID)
                } catch (error: Exception) {
                    throw VisumParseError(
                        "Line $lineNumber contains a Number that could not be parsed to an ZoneId. Line $lineNumber: \n\"$line\"",
                        error
                    )
                }

                if (zoneIds.size != parser.numberOfNetworkObjects) {
                    throw VisumParseError(
                        "Number of ZoneIds (${zoneIds.size}) does not match the expected number of network objects (${parser.numberOfNetworkObjects})." +
                                "Line $lineNumber: \n\"$line\""
                    )
                }
                parser.zoneIds = zoneIds.toTypedArray()
                assert(parser.numberOfNetworkObjects == parser.zoneIds.size) {
                    "The number of network objects (${parser.numberOfNetworkObjects}) does not match the number of parsed ZoneIds (${parser.zoneIds.size})." +
                            "This should have been intercepted by the error detection "
                }
                return READ_NET_OBJECT_NUMBERS
            }
        },

        PARSE_MATRIX_ROW {
            private fun tryParseRowHeader(
                line: String,
                lineNumber: Int,
                parser: VisumParser,
            ): MatrixParseState {
                val regex = Regex("""^\* Obj (\d+) Summe = (\d+\.\d+)""")
                val matchResult =
                    regex.matchEntire(line)
                        ?: throw VisumParseError(
                            "Line $lineNumber could not be parsed. It did not match the pattern: " +
                                    "\"* Obj <NUMBER> Summe = <NUMBER>.<NUMEBER>\". " +
                                    "More precisely, it did not match this regex: " +
                                    "\"\"\"^\\* Obj (\\d+) Summe = (\\d+\\.\\d+)\"\"\". Line $lineNumber: \n\"$line\"",
                        )

                val (objNum, _) = matchResult.destructured

                val objectZoneId: ZoneId
                try {
                    objectZoneId = AS_ZONE_ID(objNum)
                } catch (error: Exception) {
                    throw VisumParseError(
                        "Line $lineNumber contains a Number \"$objNum\" that could not be parsed to ZoneId. Line $lineNumber: \n\"$line\"",
                        error,
                    )
                }

                parser.startRow(objectZoneId)
                return PARSE_MATRIX_ROW
            }

            private fun tryParseRowContent(
                line: String,
                lineNumber: Int,
                parser: VisumParser
            ): MatrixParseState {
                line.split(Regex("""\s+""")).forEachIndexed { index, number ->
                    val elementNumber = index + 1
                    try {
                        parser.addElementToRow(number.toDouble())
                    } catch (error: IndexOutOfBoundsException) {
                        throw VisumParseError(
                            "The file contains more values than declared. Error occurred at element no. $elementNumber while parsing line $lineNumber: \n\"$line\"",
                            error
                        )
                    } catch (error: NumberFormatException) {
                        throw VisumParseError(
                            "Line $lineNumber contains a value \"$number\" that could not be parsed as a Double. Error occurred at element no. $elementNumber: \n\"$line\"",
                            error
                        )
                    } catch (error: VisumParseError) {
                        throw VisumParseError(
                            "Line $lineNumber contains an element \"$number\" that could not be added to the matrix row. Error occurred at element no. $elementNumber: \n\"$line\"",
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
                return try {
                    tryParseRowHeader(line, lineNumber, parser)
                } catch (headerError: Exception) {
                    try {
                        tryParseRowContent(line, lineNumber, parser)
                    } catch (contentError: Exception) {
                        val headerMsg = headerError.stackTraceToString().prependIndent("\t")
                        val contentMsg = contentError.stackTraceToString().prependIndent("\t")

                        throw VisumParseError(
                            "Could not Parse Line because it was neither a Header because: \n$headerMsg\nNor could it be parsed as a data line because: \n$contentMsg",
                        )
                    }
                }
            }
        };

        abstract fun nextState(
            line: String,
            lineNumber: Int,
            parser: VisumParser,
        ): MatrixParseState
    }

    init {
        val file = path.toFile()
        lines = file.useLines { it.iterator().withIndex() }
    }

    private fun parseStep() {
        val indexedLine = lines.next()
        state.nextState(indexedLine.value, indexedLine.index, this)
    }

    private fun addElementToRow(value: Double) {
        if (value.isNaN()) {
            throw VisumParseError("Got NaN as a value for a matrix element")
        }

        if (columnIndex >= numberOfNetworkObjects) {
            val zoneId = zoneIds[rowIndex]
            throw VisumParseError(
                "The row with the $rowIndex for zone $zoneId has to many elements (>$numberOfNetworkObjects)."
            )
        }
        val arrayIndex = rowIndex * numberOfNetworkObjects + columnIndex
        array[arrayIndex] = value
        columnIndex += 1
    }

    private fun startRow(zoneId: ZoneId) {
        // check if the row was finished or if it is the first row
        if (columnIndex < numberOfNetworkObjects && rowIndex != -1) {
            throw VisumParseError(
                "The row with the $rowIndex for zone ${zoneIds[rowIndex]} has to few elements ($columnIndex / $numberOfNetworkObjects)."
            )
        }

        rowIndex += 1
        columnIndex = 0

        if (zoneIds[rowIndex] != zoneId) {
            throw VisumParseError("expected the row for ${zoneIds[rowIndex]} but got the row for $zoneId.")
        }
    }
}