package domain.shared.datastructure.matrix.binary

import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.experimental.and
import kotlin.experimental.or


object MatrixHalfFloatFormat : StandardMatrixBinaryFormat {
    override val fileExtension: String = ".hfbin"

    override fun writeContent(output: DataOutputStream, value: Double) {
        output.writeShort(value.toHalfFloat())
    }

    override fun readContentElement(input: DataInputStream): Double {
        return input.readShort().fromHalfFloat()
    }
}


fun Double.toHalfFloat(): Int {
    if (this < 0) return halfFloatNaN.toInt()
    val bits = toBits()


    return 0
}

fun Short.fromHalfFloat(): Double {
    if (this == halfFloatInfinity) return Double.POSITIVE_INFINITY

    val characteristic = (this and 0xF800.toShort()).toInt().shr(12)
    val exponent = (this and 0xF800.toShort()) - exponentOffset
    val mantissa = this and 0x07FF
    val normalizedMantissa = mantissa or 0x800
    if(characteristic == 0x1F && mantissa != 0.toShort()) return Double.NaN

    val bits = 0L
    return Double.fromBits(bits)
}

/* Definitions */
const val halfFloatNaN: Short = 0xF
const val halfFloatInfinity: Short = 0xF800.toShort()
const val exponentOffset: Byte = 14