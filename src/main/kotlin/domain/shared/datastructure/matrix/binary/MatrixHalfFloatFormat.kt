package domain.shared.datastructure.matrix.binary

import java.io.DataOutputStream
import java.nio.ByteBuffer
import kotlin.experimental.and
import kotlin.experimental.or

/* Definitions */

/* half float bit representations */
private const val HALF_FLOAT_NAN: Short = 0xFFFF.toShort()
private const val HALF_FLOAT_INFINITY: Short = 0xFFFE.toShort()
private const val HALF_FLOAT_NEGATIVE_INFINITY: Short = 0xFFFD.toShort()

/* bit counts */
private const val HALF_FLOAT_BIAS = 14 // bias is the implicit exponent offset that allows for negative exponents
private const val HALF_FLOAT_MANTISSA_LENGTH = 11

@Suppress("unused")
private const val HALF_FLOAT_EXPONENT_LENGTH = 5

private const val DOUBLE_BIAS = 1023
private const val DOUBLE_MANTISSA_LENGTH = 52

@Suppress("unused")
private const val DOUBLE_EXPONENT_LENGTH = 11

/* bit masks */
const val HALF_FLOAT_EXPONENT_MASK = 0xF800.toShort()
const val HALF_FLOAT_MANTISSA_MASK = 0x07FF.toShort()
const val DOUBLE_EXPONENT_MASK = 0x7FF0000000000000
const val DOUBLE_MANTISSA_MASK = 0x000FFFFFFFFFFFFF

/* values */
private const val HALF_FLOAT_MAX_EXPONENT: Int = 0x1F - HALF_FLOAT_BIAS
private const val HALF_FLOAT_MIN_EXPONENT: Int = -HALF_FLOAT_BIAS
private const val HALF_FLOAT_MAX_VALUE: Double = 261888.0 // == 0xFFFC.fromHalfFloat()
private const val HALF_FLOAT_MIN_POSITIVE_VALUE: Double = 6.10649585723877E-5 // == 0x0001.fromHalfFloat()

/**
 * HalfFloats have roughly `log_10(2^12) = 3,6` decimal places of precision. They can represent numbers in the range of
 * `[0.0, 261888.0]`.
 * @param overflowValue when a value exceeds the HalfFloat range it gets projected to this value.Default is
 * `Double.POSITIVE_INFINITY`.
 * @param underflowValue when a double < 0 is read, this is the value it should be translated to. Default is
 * `Double.NaN`, but 0.0 could also be a reasonable choice.
 */
class MatrixHalfFloatFormat(
    val overflowValue: Double = Double.POSITIVE_INFINITY,
    val underflowValue: Double = Double.NaN,
    override val elementByteSize: Int = Short.SIZE_BYTES
) : StandardMatrixBinaryFormat {
    override val fileExtension: String = ".hfbin"

    override fun writeContentArray(output: DataOutputStream, values: DoubleArray) {
        writeBuffer(output, values, MatrixDoubleFormat.elementByteSize) {
            putShort(it.toHalfFloat())
        }
    }

    override fun readContentFromBuffer(byteBuffer: ByteBuffer, elements: Int): DoubleArray {
        return readLoop(byteBuffer, elements) {
            it.getShort().fromHalfFloat()
        }
    }

    fun Short.fromHalfFloat(): Double {
        // half float has these three special values, everything else is a valid half float
        return when (this) {
            HALF_FLOAT_NEGATIVE_INFINITY -> underflowValue
            HALF_FLOAT_INFINITY -> overflowValue
            HALF_FLOAT_NAN -> Double.NaN
            else -> {
                val bits = this.toDoubleExponent() or this.toDoubleMantissa()
                Double.fromBits(bits)
            }
        }
    }

    @Suppress("ReturnCount")
    fun Double.toHalfFloat(): Short {
        if (this < 0) return HALF_FLOAT_NEGATIVE_INFINITY
        if (this > HALF_FLOAT_MAX_VALUE) return HALF_FLOAT_INFINITY
        if (this < HALF_FLOAT_MIN_POSITIVE_VALUE) return 0 // clamping to 0 if less than half float min positive value
        if (this.isNaN()) return HALF_FLOAT_NAN

        val bits = toBits()
        val outBits: Short = bits.toHalfFloatExponent() or bits.toHalfFloatMantissa()
        return outBits
    }

    /**
     * Takes a HalfFloat in the form of a short and returns a long which has the bits set for an IEEE double float
     * exponent.
     */
    private fun Short.toDoubleExponent(): Long {
        if (this == 0.toShort()) return 0
        val exponent = (this and HALF_FLOAT_EXPONENT_MASK).rotateRight(HALF_FLOAT_MANTISSA_LENGTH)
        return (exponent - HALF_FLOAT_BIAS + DOUBLE_BIAS).toLong().shl(DOUBLE_MANTISSA_LENGTH)
    }

    /**
     * Takes a HalfFloat in the form of a short and returns a long which has the bits set for an IEEE double float
     * mantissa.
     */
    private fun Short.toDoubleMantissa(): Long {
        val mantissa = this and HALF_FLOAT_MANTISSA_MASK
        return mantissa.toLong().shl(DOUBLE_MANTISSA_LENGTH - HALF_FLOAT_MANTISSA_LENGTH)
    }

    /**
     * Takes a bit representation of an IEEE double and returns the correct bits of a half float exponent.
     * @param this must be in the range of representable half float values. I.e. this functions doesn't regard edge
     * cases.
     */
    private fun Long.toHalfFloatExponent(): Short {
        val doubleExponent = (this and DOUBLE_EXPONENT_MASK).rotateRight(DOUBLE_MANTISSA_LENGTH)
        val halfFloatExponent = (doubleExponent - DOUBLE_BIAS)
            .coerceIn(HALF_FLOAT_MIN_EXPONENT.toLong(), HALF_FLOAT_MAX_EXPONENT.toLong()) + HALF_FLOAT_BIAS
        return halfFloatExponent.shl(HALF_FLOAT_MANTISSA_LENGTH).toShort()
    }

    /**
     * Takes a bit representation of an IEEE double and returns the correct bits of a half float mantissa.
     * @param this must be in the range of representable half float values. I.e. this functions doesn't regard edge
     * cases.
     */
    private fun Long.toHalfFloatMantissa(): Short {
        // Just takes the upper bits of the double mantissa. Special cases need to be handled somewhere else.
        val doubleMantissa = this and DOUBLE_MANTISSA_MASK
        return doubleMantissa.shr(DOUBLE_MANTISSA_LENGTH - HALF_FLOAT_MANTISSA_LENGTH).toShort()
    }
}
