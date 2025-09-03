package domain.shared.datastructure.matrix.binary

import domain.shared.datastructure.matrix.StandardMatrix
import domain.shared.location.ZoneId
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.inputStream

interface BinarySerializer {
    fun serialize(hashCode: Long, matrix: StandardMatrix, path: Path)
}

interface StandardMatrixBinaryFormat : BinaryStandardSerializer, BinaryStandardDeserializer {
    val fileExtension: String
}

interface BinaryStandardSerializer : BinarySerializer {
    override fun serialize(hashCode: Long, matrix: StandardMatrix, path: Path) {

        Files.newOutputStream(path).use { fileStream ->
            BufferedOutputStream(fileStream).use { bufferedStream ->
                DataOutputStream(bufferedStream).use { outputStream ->
                    // Write the hash code used to identify the original source.
                    outputStream.writeLong(hashCode)
                    // Write the size as an Int
                    outputStream.writeInt(matrix.size)

                    // Write all zoneIds (their corresponding Int values) from the translation map
                    matrix.keys.forEach { idInt ->
                        outputStream.writeInt(idInt.value.toInt())
                    }

                    // Write all values from the array
                    matrix.values().forEach { value ->
                        writeContent(outputStream, value)
                    }
                }
            }
        }

    }

    fun writeContent(output: DataOutputStream, value: Double)
}

interface BinaryDeserializer {
    fun deserialize(path: Path): StandardMatrix
}

interface BinaryStandardDeserializer : BinaryDeserializer {
    override fun deserialize(path: Path): StandardMatrix {
        return path.inputStream().buffered().use {
            val input = DataInputStream(it)
            val hashCode = input.readLong() // Skip the hashcode found at position 0 in the file as a long.
            val size = input.readInt()
            val zoneIds = Array(size) {
                ZoneId(-1)
            }
            for (i in 0 until size) {
                zoneIds[i] = ZoneId(
                    input.readInt().toLong()
                ) // The established format is writing IDs (represented as long) as int, so we need to read them as int.
            }
            val doubleArray = DoubleArray(size * size)
            for (i in doubleArray.indices) {
                doubleArray[i] = readContentElement(input)
            }
            StandardMatrix.fromValues(doubleArray, zoneIds)
        }
    }

    /*
    Since this format is the one using hash codes at position0 it should also be the interface that provides easy access
    to the hash code.
     */
    fun hashCode(path: Path): Long {
        return path.inputStream().buffered().use {
            DataInputStream(it).readLong()
        }
    }

    fun readContentElement(input: DataInputStream): Double
}
