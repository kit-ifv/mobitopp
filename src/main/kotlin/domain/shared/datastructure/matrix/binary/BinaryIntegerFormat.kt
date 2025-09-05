package domain.shared.datastructure.matrix.binary

import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class BinaryIntegerFormat(private val scalingFactor: Int) : StandardMatrixBinaryFormat {

    override val fileExtension: String = ".ibin"
    override val elementByteSize: Int = Int.SIZE_BYTES

    override fun readContentFromBuffer(byteBuffer: ByteBuffer, elements: Int): DoubleArray {
        return readLoop(byteBuffer, elements) {it.int.toDouble() / scalingFactor}
    }

    override fun writeContentArray(output: DataOutputStream, values: DoubleArray) {
        writeBuffer(output, values, elementByteSize) {
            putInt((it * scalingFactor).toInt())
        }
    }



}
