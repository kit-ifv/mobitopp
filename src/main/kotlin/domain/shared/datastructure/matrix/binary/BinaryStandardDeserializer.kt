package domain.shared.datastructure.matrix.binary

import domain.shared.datastructure.matrix.StandardMatrix
import domain.shared.location.zone.ZoneId
import utils.files.PathChecksum
import java.io.DataInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Path
import kotlin.io.path.inputStream

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
        StandardMatrix.Companion.fromValues(doubleArray, zoneIds, path)
    }

    /*
    Since this format is the one using hash codes at position0 it should also be the interface that provides easy access
    to the hash code.
     */
    fun checksum(path: Path): PathChecksum = path.inputStream().buffered().use {
        val result = runCatching { PathChecksum.Companion.from(DataInputStream(it).readLong()) }
        if (result.isFailure) {
            println("The failure path is $path")
        }
        result.getOrThrow()
    }

    fun readContentFromBuffer(byteBuffer: ByteBuffer, elements: Int): DoubleArray
}