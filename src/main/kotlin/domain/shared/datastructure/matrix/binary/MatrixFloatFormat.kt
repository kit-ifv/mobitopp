package domain.shared.datastructure.matrix.binary

import java.io.DataInputStream
import java.io.DataOutputStream

object MatrixFloatFormat: StandardMatrixBinaryFormat {
    override val fileExtension: String = ".fbin"

    override fun writeContent(output: DataOutputStream, value: Double) {
        output.writeFloat(value.toFloat())
    }

    override fun readContentElement(input: DataInputStream): Double {
        return input.readFloat().toDouble()
    }
}