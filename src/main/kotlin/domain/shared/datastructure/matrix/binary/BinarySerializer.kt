package domain.shared.datastructure.matrix.binary

import domain.shared.datastructure.matrix.StandardMatrix
import domain.shared.location.zone.ZoneId
import utils.files.PathChecksum
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.inputStream

interface BinarySerializer {
    fun serialize(checksum: PathChecksum, matrix: StandardMatrix, path: Path)
}

interface StandardMatrixBinaryFormat :
    BinaryStandardSerializer,
    BinaryStandardDeserializer {
    val fileExtension: String
}

interface BinaryStandardSerializer : BinarySerializer {
    override fun serialize(checksum: PathChecksum, matrix: StandardMatrix, path: Path) {
        Files.newOutputStream(path).use { fileStream ->
            BufferedOutputStream(fileStream).use { bufferedStream ->
                bufferedStream.writeOutput(checksum, matrix)
            }
        }
    }

    private fun BufferedOutputStream.writeOutput(checksum: PathChecksum, matrix: StandardMatrix) {
        DataOutputStream(this).use { outputStream ->
            // Write the hash code used to identify the original source.
            outputStream.writeLong(checksum.value)
            // Write the size as an Int
            outputStream.writeInt(matrix.size)

            // Write all zoneIds (their corresponding Int values) from the translation map
            matrix.keys.forEach { idInt ->
                outputStream.writeInt(idInt.value.toInt())
            }
            // Write all values from the array
            writeContentArray(outputStream, matrix.values().toDoubleArray())
        }
    }
    fun writeContentArray(output: DataOutputStream, values: DoubleArray)
}

interface BinaryDeserializer {
    fun deserialize(path: Path): StandardMatrix
}

interface BinaryStandardDeserializer : BinaryDeserializer {
    val elementByteSize: Int
    override fun deserialize(path: Path): StandardMatrix = path.inputStream().buffered().use {
        val input = DataInputStream(it)
        input.readLong() // Skip the hashcode found at position 0 in the file as a long.
        val size = input.readInt()
        val zoneIds = Array(size) {
            ZoneId(-1)
        }
        for (i in 0 until size) {
            zoneIds[i] = ZoneId(
                input.readInt().toLong(),
            ) // The established format is writing IDs (represented as long) as int, so we need to read them as int.
        }

        val amountOfElements = size * size
        val byteCount = amountOfElements * elementByteSize
        val buffer = ByteArray(byteCount)
        input.readFully(buffer)
        val bb = ByteBuffer.wrap(buffer).order(ByteOrder.BIG_ENDIAN)
        val doubleArray = readContentFromBuffer(bb, amountOfElements)
        StandardMatrix.fromValues(doubleArray, zoneIds, path)
    }

    /*
    Since this format is the one using hash codes at position0 it should also be the interface that provides easy access
    to the hash code.
     */
    fun checksum(path: Path): PathChecksum = path.inputStream().buffered().use {
        val result = runCatching { PathChecksum.from(DataInputStream(it).readLong()) }
        if (result.isFailure) {
            println("The failure path is $path")
        }
        result.getOrThrow()
    }

    fun readContentFromBuffer(byteBuffer: ByteBuffer, elements: Int): DoubleArray
}

internal inline fun readLoop(bb: ByteBuffer, elements: Int, crossinline decode: (ByteBuffer) -> Double): DoubleArray {
    val array = DoubleArray(elements)
    for (i in 0 until elements) {
        array[i] = decode(bb)
    }
    return array
}

internal inline fun writeBuffer(
    output: DataOutputStream,
    values: DoubleArray,
    bytesPerElement: Int,
    crossinline encode: ByteBuffer.(Double) -> Unit,
) {
    val bb = ByteBuffer.allocate(values.size * bytesPerElement).order(ByteOrder.BIG_ENDIAN)
    for (v in values) {
        encode(bb, v)
    }
    output.write(bb.array())
}
