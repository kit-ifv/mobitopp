package domain.synthesis.results

@JvmInline
value class Attractiveness(val value: Double) {
    companion object {
        val DEFAULT = Attractiveness(1.0)
    }
}

fun Number.asAttractiveness(): Attractiveness = Attractiveness(this.toDouble())