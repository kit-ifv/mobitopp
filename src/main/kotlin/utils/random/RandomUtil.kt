package utils.random

import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

@Suppress("MagicNumber")
fun Random.randomGaussian(): Double {
    val u1 = nextDouble()
    val u2 = nextDouble()
    return sqrt(-2.0 * ln(u1)) * cos(2.0 * Math.PI * u2)
}

fun Random.getGaussian(mean: Double, stdDev: Double): Double {
    return randomGaussian() * stdDev + mean
}
