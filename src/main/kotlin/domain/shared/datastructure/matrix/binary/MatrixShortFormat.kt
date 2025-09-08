package domain.shared.datastructure.matrix.binary

import java.io.DataOutputStream
import java.nio.ByteBuffer
import kotlin.math.roundToInt
@Suppress("MagicNumber")
object MatrixShortFormat : StandardMatrixBinaryFormat {
    override val fileExtension: String = ".sbin"
    override val elementByteSize: Int = Short.SIZE_BYTES

    fun Int.convertedDouble(): Double {
        val unsigned = this and 0xFFFF // interpret bits as unsigned
        return if (unsigned == 0xFFFF) {
            999_999.0 // sentinel
        } else {
            unsigned / 100.0
        }
    }

    fun Double.toConvertedShort(): Int {
        return if (this >= 655.35) {
            0xFFFF
        } else {
            (this * 100).roundToInt()
        }
    }
    override fun writeContentArray(output: DataOutputStream, values: DoubleArray) {
        writeBuffer(output, values, MatrixDoubleFormat.elementByteSize) {
            putShort(it.toConvertedShort().toShort())
        }
    }

    override fun readContentFromBuffer(byteBuffer: ByteBuffer, elements: Int): DoubleArray {
        return readLoop(byteBuffer, elements) {
            it.ushort.convertedDouble()
        }
    }

    inline val ByteBuffer.ushort get(): Int {
        return short.toInt() and 0xFFFF
    }
}
