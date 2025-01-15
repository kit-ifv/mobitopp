package synthesis.householdgeneration

import synthesis.SurveyHousehold
import kotlin.math.abs

/**
 * After vectorization of households we have a matrix of household information encoded in integers. Each household is
 * encoded in a column of the matrix, whereas the designated target of a rule is encoded via a row. Since the goal
 * of IPU is to update so that the rules are best matched, the observer needs to track which particular households -
 * encoded as ScalableVector - it needs to track.
 */
class Observer(val name: String, val observedIndex: Int, val vectors: List<ScalableVector>, val expected: Int) {


    fun sum(): Double {
        return vectors.sumOf { it.currentValueForIndex(observedIndex) }
    }

    fun sanityCheck(): Boolean {
        return vectors.all { it.content[observedIndex] != 0 }
    }

    operator fun timesAssign(factor: Number) {
        vectors.forEach { it.scalar *= factor.toDouble() }
    }

    val relativeDifference get() = absoluteDifference / expected
    val absoluteDifference get() = abs(expected - sum())
    fun optimize() {
        this.timesAssign((expected / sum()))
    }

    override fun toString() = "[$name] difference = $relativeDifference"

    companion object {
        fun <T> fromRule(
            rule: Rule<T>,
            observedIndex: Int,
            allHouseholdsEncoded: Collection<ScalableVector>
        ): Observer {
            return Observer(
                rule.description,
                observedIndex,
                allHouseholdsEncoded.filter { it.appliesToRule(observedIndex) },
                rule.target
            )
        }
    }
}

fun Rule<*>.createObserver(index: Int, encodedHouseholds: Collection<ScalableVector>): Observer {
    return Observer.fromRule(this, index, encodedHouseholds)
}

fun List<Rule<Any>>.vectorized(surveyHousehold: SurveyHousehold<out Any>): ScalableVector {
    return ScalableVector(map { it.evaluate(surveyHousehold) }.toIntArray())
}

/**
 * A Scalable vector holds the information of the attributes as a readonly property, but the scale factor is flexible
 * to allow optimizations and alterations.
 * The representation is quite simple: Vector is the attributes of the household encoding i.e. (1, 0, 0)
 * Whereas scalar is the factor how many of this household encoding are currently present.
 */
class ScalableVector(private val vector: IntArray, var scalar: Double = 1.0) {
    /**
     * The intArray representation of the vector should not be visible to the outside, since arrays are mutable
     * the [content] provides a readonly access.
     */
    val content: List<Int> = vector.toList()

    /**
     * Determine the value of this vector for a target index.
     */
    fun currentValueForIndex(index: Int): Double = vector[index] * scalar

    fun appliesToRule(ruleIndex: Int): Boolean {
        return vector[ruleIndex] != 0
    }
    operator fun timesAssign(times: Number) {
        scalar *= times.toDouble()
    }
}