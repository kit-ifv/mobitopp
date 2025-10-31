package domain.synthesis

import kotlin.math.abs
import kotlin.math.max

data class IPUOutputLog(
    val description: String,
    val expected: Int,
    val actual: Int,

) {
    val difference: Int = expected - actual

    @Suppress("MagicNumber")
    val quotientDifference: Double = run {

        val exp = if (expected == 0) 1e-9 else expected.toDouble()
        val act = if (actual == 0) 1e-9 else actual.toDouble()
        max(exp / act, act / exp)
    }

    val percentDifference = abs(difference.toDouble()) / max(1, expected)
}
