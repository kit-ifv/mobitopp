package utils.random

import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

@Suppress("MagicNumber")
fun Random.getGaussian(mean: Double = 0.0, stdDev: Double = 1.0): Double {
    var u: Double
    var v: Double
    var s: Double

    do {
        u = this.nextDouble(from = -1.0, until = 1.0)
        v = this.nextDouble(from = -1.0, until = 1.0)
        s = u * u + v * v
    } while (s >= 1 || s == 0.0)

    val multiplier = sqrt(-2.0 * ln(s) / s)

    // TODO Box–Muller polar form algorithm would produce cheap next value: v * multiplier;
    // cannot be stored due to extension method :(

    return u * multiplier * stdDev + mean
}
