package domain.shared.datastructure.matrix.binary

import java.io.DataInputStream
import java.io.DataOutputStream

class BinaryIntegerFormat(private val scalingFactor: Int): StandardMatrixBinaryFormat{
    override fun writeContent(output: DataOutputStream, value: Double) {
        output.writeInt((value * scalingFactor).toInt())
    }

    override fun readContentElement(input: DataInputStream): Double {
        return input.readInt() / scalingFactor.toDouble()
    }

    override val fileExtension: String = ".ibin"
}