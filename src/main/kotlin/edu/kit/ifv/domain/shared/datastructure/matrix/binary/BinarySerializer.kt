package edu.kit.ifv.domain.shared.datastructure.matrix.binary
import edu.kit.ifv.domain.shared.datastructure.matrix.StandardMatrix
import edu.kit.ifv.utils.files.PathChecksum
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Path

interface BinarySerializer {
    fun serialize(checksum: PathChecksum, matrix: StandardMatrix, path: Path)
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
