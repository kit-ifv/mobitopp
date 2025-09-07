package domain.shared.datastructure.matrix.visum

import domain.shared.location.ZoneId
import utils.files.decompressedBufferedReader
import java.io.BufferedReader
import java.nio.file.Path

// Detekt complained, so I extracted the number line pattern into its own variable
const val NUMBER_LINE_PATTERN = "[\\d|\\s.-]+"

// Another complaint by detekt
const val OBJ = "* Obj"

/**
 * The `MatrixParser` class is responsible for parsing a matrix from a file and converting it into a `Matrix` object.
 *
 * @param thoth lambda providing a buffered reader.
 */
class VisumMatrixParser(thoth: () -> BufferedReader) : IVisumParser {
    private var state: MatrixParseState = MatrixParseState.LOCATE_NUMBER
    private lateinit var values: List<MutableList<Double>>
    private val mutableList: MutableList<ZoneId> = ArrayList()
    private var index = -1

    constructor(file: Path) : this({
        file.decompressedBufferedReader()
        // properBufferedReader(file.toFile())
    })

    init {

        val reader = thoth.invoke()
        reader.forEachLine {
            state = state.nextState(it)
            state.handle(it, this)
        }
    }

    enum class MatrixParseState {
        LOCATE_NUMBER {

            override fun nextState(s: String): MatrixParseState {
                return when (s) {
                    "* Anzahl Netzobjekte" -> FOUND_HEADER
                    else -> this
                }
            }
        },
        FOUND_HEADER {

            override fun nextState(s: String): MatrixParseState {
                return when (s.toIntOrNull()) {
                    is Int -> READ_NUMBER
                    else -> this
                }
            }
        },
        READ_NUMBER {

            override fun handle(s: String, m: VisumMatrixParser) {
                m.setInitialSize(s.toInt())
            }

            override fun nextState(s: String): MatrixParseState {
                return when (s) {
                    "* Netzobjekt-Nummern" -> LOCATE_ZONE_IDS
                    else -> this
                }
            }
        },

        LOCATE_ZONE_IDS {

            override fun nextState(s: String): MatrixParseState {
                return when {
                    s.matches(Regex(NUMBER_LINE_PATTERN)) -> READ_ZONE_IDS
                    else -> this
                }
            }
        },
        READ_ZONE_IDS {
            override fun handle(s: String, m: VisumMatrixParser) {
                m.addZones(s)
            }

            override fun nextState(s: String): MatrixParseState {
                return when {
                    s.matches(Regex(NUMBER_LINE_PATTERN)) -> this
                    else -> LOCATE_CONTENT_HEADER
                }
            }
        },
        LOCATE_CONTENT_HEADER {

            override fun nextState(s: String): MatrixParseState {
                return when {
                    s.startsWith(OBJ) -> READ_CONTENT_HEADER
                    else -> this
                }
            }
        },
        READ_CONTENT_HEADER {
            override fun handle(s: String, m: VisumMatrixParser) {
                m.increaseIndex()
            }

            override fun nextState(s: String): MatrixParseState {
                return READ_CONTENT
            }
        },
        READ_CONTENT {
            override fun handle(s: String, m: VisumMatrixParser) {
                m.addContent(s)
            }

            override fun nextState(s: String): MatrixParseState {
                return when {
                    s.matches(Regex(NUMBER_LINE_PATTERN)) -> this
                    s.startsWith(OBJ) -> READ_CONTENT_HEADER
                    else -> LOCATE_CONTENT_HEADER
                }
            }
        };

        open fun handle(s: String, m: VisumMatrixParser) {
        }

        abstract fun nextState(s: String): MatrixParseState
    }

    private fun setInitialSize(i: Int) {
        values = List(i) {
            ArrayList()
        }
    }

    private fun addZones(s: String) {
        mutableList.addAll(s.splitByWhitespace().map(AS_ZONE_ID))
    }

    private fun increaseIndex() {
        index++
    }

    private fun addContent(s: String) {
        values[index].addAll(s.splitByWhitespace().map(AS_DOUBLE))
    }

//    fun get(): Matrix {
//        return Matrix(mutableList.withIndex().associate { it.value to it.index }, values)
//    }

    override fun getZoneIds(): Array<ZoneId> {
        return mutableList.toTypedArray()
    }

    override fun getArray(): DoubleArray {
        return values.flatten().toDoubleArray()
    }
}

private fun String.splitByWhitespace(): List<String> {
    return this.trim().split(Regex("\\s+"))
}

private val AS_DOUBLE: (String) -> Double = { it.toDouble() }
private val AS_ZONE_ID: (String) -> ZoneId = { ZoneId(it.toLong()) }

// fun properBufferedReader(item: File): BufferedReader {
//    return when (item.extension) {
//        "bz2" -> BufferedReader(InputStreamReader(uncompressBZip2From(FileInputStream(item))))
//        else -> item.bufferedReader(charset("ISO-8859-1"))
//    }
// }
//
// // TODO can be deleted, included buffering in FileDecompression.kt
// @Suppress("MagicNumber") // 1024 is just the buffer size
// private fun uncompressBZip2From(fin: FileInputStream): InputStream {
//    val inputStream = BufferedInputStream(fin)
//    val bzIn = BZip2CompressorInputStream(inputStream)
//
//    val out = ByteArrayOutputStream()
//
//    // TODO -> simplify to bzIn.copyTo(out)
//    val buffer = ByteArray(1024)
//    var n = 0
//    while (-1 != (bzIn.read(buffer).also { n = it })) {
//        out.write(buffer, 0, n)
//    }
//    out.close()
//
//    bzIn.close()
//
//    return ByteArrayInputStream(out.toByteArray())
// }
