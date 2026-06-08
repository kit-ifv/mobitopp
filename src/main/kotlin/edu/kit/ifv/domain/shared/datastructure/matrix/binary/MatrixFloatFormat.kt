package edu.kit.ifv.domain.shared.datastructure.matrix.binary
import java.io.DataOutputStream
import java.nio.ByteBuffer

object MatrixFloatFormat : StandardMatrixBinaryFormat {
    override val fileExtension: String = ".fbin"
    override val elementByteSize: Int = Float.SIZE_BYTES

    override fun readContentFromBuffer(byteBuffer: ByteBuffer, elements: Int): DoubleArray = readLoop(
        byteBuffer,
        elements,
    ) {
        it.float.toDouble()
    }

    override fun writeContentArray(output: DataOutputStream, values: DoubleArray) {
        writeBuffer(output, values, MatrixDoubleFormat.elementByteSize) {
            putFloat(it.toFloat())
        }
    }
}
