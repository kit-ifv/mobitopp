package domain.synthesis.results

@JvmInline
value class Attractiveness(val value: Double) {
    companion object {
        val DEFAULT = Attractiveness(1.0)
        val ZERO = Attractiveness(0.0)
    }

    operator fun plus(other: Number): Double = value + other.toDouble()
}

fun Number.asAttractiveness(): Attractiveness = Attractiveness(this.toDouble())