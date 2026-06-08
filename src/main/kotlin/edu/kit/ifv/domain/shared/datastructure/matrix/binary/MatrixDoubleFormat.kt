package edu.kit.ifv.domain.shared.datastructure.matrix.binary
import java.io.DataOutputStream
import java.nio.ByteBuffer

object MatrixDoubleFormat : StandardMatrixBinaryFormat {
    override val fileExtension: String = ".dbin"
    override val elementByteSize: Int = Double.SIZE_BYTES
    override fun writeContentArray(output: DataOutputStream, values: DoubleArray) {
        writeBuffer(output, values, elementByteSize) {
            putDouble(it)
        }
    }

    override fun readContentFromBuffer(byteBuffer: ByteBuffer, elements: Int): DoubleArray = readLoop(
        byteBuffer,
        elements,
    ) {
        it.double
    }
}
