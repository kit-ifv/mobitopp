package domain.shared.datastructure.matrix.binary

import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.experimental.and
import kotlin.experimental.or


object MatrixHalfFloatFormat : StandardMatrixBinaryFormat {
    override val fileExtension: String = ".hfbin"

    override fun writeContent(output: DataOutputStream, value: Double) {
        output.writeShort(value.toHalfFloat().toInt())
    }

    override fun readContentElement(input: DataInputStream): Double {
        return input.readShort().fromHalfFloat()
    }
}


fun Double.toHalfFloat(): Short {
    if (this < 0 || this.isNaN()) return halfFloatNaN
    if(this > halfFloatMaxVal) return halfFloatInfinity
    val bits = toBits()
    val doubleExponent = (bits and doubleExponentMask).shr(doubleMantissaLength)
    val doubleMantissa = bits and doubleMantissaMask
    val outBits: Short = doubleExponent.toHalfFloatExponent() or doubleMantissa.toHalfFloatMantissa()
    return outBits
}

fun Short.fromHalfFloat(): Double {
    if (this == halfFloatInfinity) return Double.POSITIVE_INFINITY
    if (this == halfFloatNaN) return Double.NaN

    val exponent = (this and halfFloatExponentMask).rotateRight(11)
    val mantissa = this and halfFloatMantissaMask

    val bits = exponent.toDoubleExponent() or mantissa.toDoubleMantissa()
    return Double.fromBits(bits)
}


fun Short.toDoubleExponent(): Long {
    return (this - halfFloatBias + doubleBias).toLong().shl(doubleMantissaLength + 1)
}

fun Short.toDoubleMantissa(): Long {
    return this.toLong().shl(doubleMantissaLength - halfFloatMantissaLength)
}

fun Long.toHalfFloatExponent(): Short {
    return this.shr(doubleExponentLength - halfFloatExponentLength).shl(halfFloatMantissaLength).toShort()
}

fun Long.toHalfFloatMantissa(): Short {
    return this.shr(doubleMantissaLength - halfFloatMantissaLength).toShort()
}

/* Definitions
* normalized -> first 1 is implicit
*
* */
const val halfFloatNaN: Short = 0xF801.toShort()
const val halfFloatInfinity: Short = 0xF800.toShort()
const val halfFloatMaxVal = 65504
const val halfFloatBias = 14
const val halfFloatExponentMask = 0xF800.toShort()
const val halfFloatMantissaMask = 0x07FF.toShort()
const val halfFloatMantissaLength = 11
const val halfFloatExponentLength = 5
const val doubleBias = 1023
const val doubleMantissaLength = 52
const val doubleExponentLength = 11
const val doubleExponentMask = 0x3FF0000000000000
const val doubleMantissaMask = 0x000FFFFFFFFFFFFF
const val doubleSignMask = 0x800000000000000
