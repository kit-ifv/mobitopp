package synthesis.householdgeneration

import synthesis.SurveyHousehold
import kotlin.math.abs

class Observer(val name: String, val observedIndex: Int, val vectors: List<ScalableVector>, val expected: Int) {
    fun sum(): Double {
        return vectors.sumOf { it.vector[observedIndex] * it.scalar }
    }

    operator fun times(factor: Number) {
        vectors.forEach { it.scalar *= factor.toDouble() }
    }

    val difference get() = abs(expected - sum()) / expected
    fun optimize() {
        this * (expected / sum())
    }

    override fun toString() = "[$name] difference = $difference"
}

fun List<Rule<Any>>.vectorize(surveyHousehold: SurveyHousehold<out Any>): ScalableVector {
    return ScalableVector(map { it.evaluate(surveyHousehold) }.toIntArray())
}

class ScalableVector(val vector: IntArray, var scalar: Double = 1.0)