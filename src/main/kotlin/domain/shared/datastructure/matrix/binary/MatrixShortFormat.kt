package domain.shared.datastructure.matrix.binary

import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.math.roundToInt

object MatrixShortFormat : StandardMatrixBinaryFormat {
    override val fileExtension: String = ".sbin"
    override fun writeContent(output: DataOutputStream, value: Double) {
        output.writeShort(value.toConvertedShort())
    }

    override fun readContentElement(input: DataInputStream): Double {
        return input.readUnsignedShort().convertedDouble()
    }

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
}
