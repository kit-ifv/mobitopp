package domain.shared.datastructure.matrix.binary

import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.experimental.and
import kotlin.experimental.or

/* Definitions */

/* half float bit representations */
private const val halfFloatNaN: Short = 0xFFFF.toShort()
private const val halfFloatInfinity: Short = 0xFFFE.toShort()
private const val halfFloatNegativeInfinity: Short = 0xFFFD.toShort()
private const val halfFloatMaxValue = 261888.0

/* bit counts */
private const val halfFloatBias = 14 // bias is the implicit exponent offset that allows for negative exponents
private const val halfFloatMantissaLength = 11
private const val halfFloatExponentLength = 5
private const val doubleBias = 1023
private const val doubleMantissaLength = 52
private const val doubleExponentLength = 11

/* bit masks */
const val halfFloatExponentMask = 0xF800.toShort()
const val halfFloatMantissaMask = 0x07FF.toShort()
const val doubleExponentMask = 0x7FF0000000000000
const val doubleMantissaMask = 0x000FFFFFFFFFFFFF


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
        return input.readShort().fromHalfFloat()
    }

    fun Short.fromHalfFloat(): Double {
        when (this) {
            halfFloatNegativeInfinity -> return underflowValue
            halfFloatInfinity -> return overflowValue
            halfFloatNaN -> return Double.NaN
        }

        val bits = this.toDoubleExponent() or this.toDoubleMantissa()
        return Double.fromBits(bits)
    }

    fun Double.toHalfFloat(): Short {
        if (this < 0) return halfFloatNegativeInfinity
        if (this > halfFloatMaxValue) return halfFloatInfinity
        if (this.isNaN()) return halfFloatNaN

        val bits = toBits()

        val outBits: Short = bits.toHalfFloatExponent() or bits.toHalfFloatMantissa()
        return outBits
    }

    /**
     * Takes a HalfFloat in the form of a short and returns a long which has the bits set for an IEEE double float
     * exponent.
     */
    private fun Short.toDoubleExponent(): Long {
        val exponent = (this and halfFloatExponentMask).rotateRight(11)
        if (exponent == 0.toShort()) return 0 //todo denormalized handling?
        return (exponent - halfFloatBias + doubleBias).toLong().shl(doubleMantissaLength)
    }

    /**
     * Takes a HalfFloat in the form of a short and returns a long which has the bits set for an IEEE double float
     * mantissa.
     */
    private fun Short.toDoubleMantissa(): Long {
        val mantissa = this and halfFloatMantissaMask
        return mantissa.toLong().shl(doubleMantissaLength - halfFloatMantissaLength)
    }

    /**
     * Takes a bit representation of an IEEE double and returns the correct bits of a half float exponent.
     * @param this must be in the range of representable half float values. I.e. this functions doesn't regard edge
     * cases.
     */
    private fun Long.toHalfFloatExponent(): Short {
        val doubleExponent = (this and doubleExponentMask).rotateRight(doubleMantissaLength)
        if (doubleExponent == 0L) return 0.toShort()
        val halfFloatExponent = doubleExponent - doubleBias + halfFloatBias //todo proper ranging somehow. don't know the correct way atm
        return halfFloatExponent.shl(halfFloatMantissaLength).toShort()
    }

    /**
     * Takes a bit representation of an IEEE double and returns the correct bits of a half float mantissa.
     * @param this must be in the range of representable half float values. I.e. this functions doesn't regard edge
     * cases.
     */
    private fun Long.toHalfFloatMantissa(): Short {
        val doubleMantissa = this and doubleMantissaMask
        return doubleMantissa.shr(doubleMantissaLength - halfFloatMantissaLength).toShort()
    }
}