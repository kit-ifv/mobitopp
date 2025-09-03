package domain.shared.datastructure.matrix.binary

import java.io.DataInputStream
import java.io.DataOutputStream

object MatrixDoubleFormat: StandardMatrixBinaryFormat {
    override val fileExtension: String = ".dbin"
    override fun writeContent(output: DataOutputStream, value: Double) {
        output.writeDouble(value)
    }



    override fun readContentElement(input: DataInputStream): Double {
        return input.readDouble()
    }
}