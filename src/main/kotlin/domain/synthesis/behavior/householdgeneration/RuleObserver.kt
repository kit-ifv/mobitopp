package domain.synthesis.behavior.householdgeneration

import kotlin.math.abs
import kotlin.math.max

/**
 * The RuleObserver class is responsible for monitoring a set of households encoded as vectors (represented by [ScalableVector])
 * and tracking the deviation from an expected value based on the application of rules. After the vectorization of households,
 * the encoded information is stored in a matrix where each column represents a household, and each row corresponds to a rule
 * or target condition. The observer tracks which households, encoded as vectors, need to be monitored to ensure that the rules
 * are matched and updated accordingly.
 *
 * @param name The name of the rule or observer, typically the description of the rule.
 * @param observedIndex The index of the target in the rule, which corresponds to the row in the vectorized matrix.
 * @param vectors A list of [ScalableVector] objects representing the household data in vectorized form.
 */
abstract class RuleObserver(
    val name: String,
    private val observedIndex: Int,
    val vectors: List<ScalableVector>,
) {
    /**
     * Sums the current values for the given [observedIndex] across all the vectors.
     *
     * @return The sum of the values for the [observedIndex] across all vectors.
     */
    fun sum(): Double {
        return vectors.sumOf { it.currentValueForIndex(observedIndex) }
    }

    fun fallbackSize(): Double {
        return vectors.sumOf { it.attributeForIndex(observedIndex).toDouble() }
    }

    /**
     * Performs a sanity check to ensure that no vector has a zero value at the [observedIndex]. An Observer should never
     * track a household which is irrelevant for the underlying rule, which is equivalent to having a 0 as the encoding
     * of the observed attribute.
     *
     * @return True if all vectors have a non-zero value at the [observedIndex], otherwise false.
     */
    fun sanityCheck(): Boolean {
        return vectors.all { it.content[observedIndex] != 0 }
    }

    /**
     * Multiplies the scalar value of all vectors by the given [factor]. Convenience function for the example algorithms
     * of [HouseholdSynthesis]
     *
     * @param factor The number by which the scalar value of each vector is multiplied.
     */
    operator fun timesAssign(factor: Number) {
        vectors.forEach { it.scalar *= factor.toDouble() }
    }

    /**
     * The relative difference between the expected value and the current sum.
     * Calculated as the absolute difference divided by the expected value.
     */
    abstract val relativeDifference: Double

    /**
     * The absolute difference between the expected value and the current sum.
     */
    abstract val absoluteDifference: Double

    abstract val quotientDifference: Double

    /**
     * Optimizes the vectors by scaling all of them proportionally, ensuring that the sum matches the expected value.
     */
    abstract fun optimize()

    override fun toString() = "[$name] difference = $relativeDifference"

    companion object {
        /**
         * Creates an [RuleObserver] from a given [Rule] and a collection of all encoded household vectors.
         * Filters the vectors to include only those that apply to the given rule at the specified [observedIndex].
         *
         * @param rule The rule that describes the condition or target to observe.
         * @param observedIndex The index of the rule defined in the external logic.
         * @param allHouseholdsEncoded The collection of all [ScalableVector]s representing encoded households.
         * @return A new [RuleObserver] instance.
         */
        fun <T> fromRule(
            rule: Rule<T>,
            observedIndex: Int,
            allHouseholdsEncoded: Collection<ScalableVector>,
        ): TargetNumberObserver {
            return TargetNumberObserver(
                rule.description,
                observedIndex,
                allHouseholdsEncoded.filter { it.appliesToRule(observedIndex) },
                rule.target
            )
        }
    }
}

class TargetNumberObserver(
    name: String,
    observedIndex: Int,
    vectors: List<ScalableVector>,
    val expected: Int,

) : RuleObserver(
    name,
    observedIndex,
    vectors,
) {
    override val absoluteDifference: Double get() = abs(expected - sum())
    override val relativeDifference: Double get() = absoluteDifference / expected

    @Suppress("MagicNumber")
    override val quotientDifference: Double
        get() {
            val fallback = 1e-9
            val exp = if (expected == 0) fallback else expected.toDouble()
            val act = if (sum() == 0.0) fallback else sum()
            return max(exp / act, act / exp)
        }
    override fun optimize() {
        val sum = sum()
//        if(sum == 0.0) return // There is no remaining vector with a scalar > 0.0. This observer can no longer be optimized.
        val currentSum = if(sum == 0.0) fallbackSize() else sum

        // TODO fallback calculation if expected != 0.0 and sum is 0.0
        this.timesAssign((expected / currentSum))
    }
}
