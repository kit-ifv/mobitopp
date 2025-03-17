package datastructure.matrix

import domain.data.ZoneId
import usecases.steps.binary.operateOnMemoryFile
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.nio.file.Files
import java.nio.file.Path

/**
 * An abstract class representing a matrix.
 *
 * @param I The type of index used for rows and columns.
 * @param O The type of elements stored in the matrix.
 */
interface Matrix<I, O> {

    /**
     * Retrieves the element at the specified row and column.
     *
     * @param row The index of the row.
     * @param column The index of the column.
     * @return The element at the specified row and column.
     */
    operator fun get(row: I, column: I): O
}

interface IntoMatrix<I, O> {
    fun into()
}

class ConstantMatrix<O>(val value: O) : Matrix<Any, O> {
    override fun get(row: Any, column: Any): O = value
}

@Suppress("MagicNumber") // 4 is not magic, it is the size of an int/float respectively
fun Path.getSize(): Triple<Int, Map<ZoneId, Int>, FloatArray> {
    return operateOnMemoryFile {
        val size = this.getInt(0)
        val mapper = HashMap<ZoneId, Int>()
        val translation = IntArray(size)
        for (i in 0 until size) {
            val fileContent = this.getInt((i + 1) * 4)
            mapper[ZoneId(fileContent.toLong())] = i
            translation[i] = this.getInt((i + 1) * 4)
        }
        val floatArray = FloatArray(size * size)
        for (i in floatArray.indices) {
            floatArray[i] = this.getFloat((i + 1 + size) * 4)
        }
        Triple(size, mapper, floatArray)
    }
}

// TODO maybe apply the converter to the elements of the matrix directly, unless this would waste storage space when <O> is complex
class FloatMatrix<O>(
    val size: Int,
    private val translation: Map<ZoneId, Int>,
    private val floatArray: FloatArray,
    val converter: (Double) -> O
) : Matrix<ZoneId, O> {
    @Suppress("NestedBlockDepth")
    fun writeToBinary(path: Path) {
        Files.newOutputStream(path).use { fileStream ->
            BufferedOutputStream(fileStream).use { bufferedStream ->
                DataOutputStream(bufferedStream).use { outputStream ->
                    // Write the size as an Int
                    outputStream.writeInt(size)

                    // Write all zoneIds (their corresponding Int values) from the translation map
                    translation.keys.forEach { zoneIdInt ->
                        outputStream.writeInt(zoneIdInt.value.toInt())
                    }

                    // Write all floats from the floatArray
                    floatArray.forEach { floatValue ->
                        outputStream.writeFloat(floatValue)
                    }
                }
            }
        }
    }

    override fun get(row: ZoneId, column: ZoneId): O {
        val rowIndex =
            translation[row] ?: throw IllegalArgumentException("Row $row not found in index lookup")
        val columnIndex =
            translation[column] ?: throw IllegalArgumentException("Column $column not found in index lookup")

        val index = rowIndex * size + columnIndex

        return converter(floatArray[index].toDouble())
    }

    companion object {
        fun <O> fromPath(path: Path, converter: (Double) -> O): FloatMatrix<O> {
            val (size, translation, floatArray) = path.getSize()
            return FloatMatrix(size, translation, floatArray, converter)
        }
    }
}
