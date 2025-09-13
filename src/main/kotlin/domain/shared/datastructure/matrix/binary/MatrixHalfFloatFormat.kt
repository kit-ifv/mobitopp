package domain.shared.datastructure.matrix.binary

import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * @param overflowValue when a value exceeds the HalfFloat range it gets projected to this value.
 * @param underflowValue when a double < 0 is read, this is the value it should be translated to. Default is NaN, but
 * 0.0 could also be a reasonable choice.
 */
class MatrixHalfFloatFormat(val overflowValue: Double = Double.MAX_VALUE, val underflowValue: Double = Double.NaN) : StandardMatrixBinaryFormat {
    override val fileExtension: String = ".hfbin"

    override fun writeContent(output: DataOutputStream, value: Double) {
        output.writeShort(value.toHalfFloat().toInt())
    }

    override fun readContentElement(input: DataInputStream): Double {
        return input.readShort().fromHalfFloat(underflowValue, overflowValue)
    }
}


fun Short.fromHalfFloat(underflowValue: Double = Double.NaN, overflowValue: Double = Double.POSITIVE_INFINITY): Double {
    when (this) {
        halfFloatNegativeInfinity -> return underflowValue
        halfFloatInfinity -> return overflowValue
        halfFloatNaN -> return Double.NaN
    }

    val exponent = (this and halfFloatExponentMask).rotateRight(11)
    val mantissa = this and halfFloatMantissaMask

    val bits = exponent.toDoubleExponent() or mantissa.toDoubleMantissa()
    return Double.fromBits(bits)
}

fun Double.toHalfFloat(): Short {
    if (this < 0) return halfFloatNegativeInfinity
    if (this > 64444) return halfFloatInfinity //TODO insert real highest half float value
    if (this.isNaN()) return halfFloatNaN

    val bits = toBits()
    val doubleExponent = (bits and doubleExponentMask).shr(doubleMantissaLength)
    val doubleMantissa = bits and doubleMantissaMask
    val outBits: Short = doubleExponent.toHalfFloatExponent() or doubleMantissa.toHalfFloatMantissa()
    return outBits
}

fun Short.toDoubleExponent(): Long {
    return (this - halfFloatBias + doubleBias).toLong().shl(doubleMantissaLength)
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
const val halfFloatNaN: Short = 0xF800.toShort()
const val halfFloatInfinity: Short = 0xF801.toShort()
const val halfFloatNegativeInfinity: Short = 0xF802.toShort()
const val halfFloatBias = 14
const val halfFloatExponentMask = 0xF800.toShort()
const val halfFloatMantissaMask = 0x07FF.toShort()
const val halfFloatMantissaLength = 11
const val halfFloatExponentLength = 5
const val doubleBias = 1023
const val doubleMantissaLength = 52
const val doubleExponentLength = 11
const val doubleExponentMask = 0x7FF0000000000000
const val doubleMantissaMask = 0x000FFFFFFFFFFFFF
const val doubleSignMask = 0x800000000000000
