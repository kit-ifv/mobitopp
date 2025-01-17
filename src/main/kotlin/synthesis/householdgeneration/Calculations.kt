package synthesis.householdgeneration

import synthesis.SurveyHousehold
import kotlin.math.abs

/**
 * The Observer class is responsible for monitoring a set of households encoded as vectors (represented by [ScalableVector])
 * and tracking the deviation from an expected value based on the application of rules. After the vectorization of households,
 * the encoded information is stored in a matrix where each column represents a household, and each row corresponds to a rule
 * or target condition. The observer tracks which households, encoded as vectors, need to be monitored to ensure that the rules
 * are matched and updated accordingly.
 *
 * @param name The name of the rule or observer, typically the description of the rule.
 * @param observedIndex The index of the target in the rule, which corresponds to the row in the vectorized matrix.
 * @param vectors A list of [ScalableVector] objects representing the household data in vectorized form.
 * @param expected The expected value that the household sum should match after optimization.
 */
class Observer(val name: String, private val observedIndex: Int, val vectors: List<ScalableVector>, val expected: Int) {
    /**
     * Sums the current values for the given [observedIndex] across all the vectors.
     *
     * @return The sum of the values for the [observedIndex] across all vectors.
     */
    fun sum(): Double {
        return vectors.sumOf { it.currentValueForIndex(observedIndex) }
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
    val relativeDifference get() = absoluteDifference / expected

    /**
     * The absolute difference between the expected value and the current sum.
     */
    val absoluteDifference get() = abs(expected - sum())

    /**
     * Optimizes the vectors by scaling all of them proportionally, ensuring that the sum matches the expected value.
     */
    fun optimize() {
        this.timesAssign((expected / sum()))
    }

    override fun toString() = "[$name] difference = $relativeDifference"

    companion object {
        /**
         * Creates an [Observer] from a given [Rule] and a collection of all encoded household vectors.
         * Filters the vectors to include only those that apply to the given rule at the specified [observedIndex].
         *
         * @param rule The rule that describes the condition or target to observe.
         * @param observedIndex The index of the rule defined in the external logic.
         * @param allHouseholdsEncoded The collection of all [ScalableVector]s representing encoded households.
         * @return A new [Observer] instance.
         */
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


/**
 * A [ScalableVector] represents a vectorized encoding of household attributes, where each element of the vector
 * corresponds to a specific attribute, and the `scalar` factor indicates how many instances of the household encoding
 * are currently present. The vector is immutable, but the scalar factor is mutable, allowing for optimizations and adjustments
 * during processing.
 *
 * The vector itself is represented as an integer array, where each element typically represents an integer encoding of a
 * household's attribute (e.g., [0, 1, 2] could represent a household with the ruleset
 * ["Household Size == 1", "Household Size == 2", "Number of LicenceHolders"])
 *
 * This class provides a read-only view of the vector's content and exposes operations to manipulate its scalar,
 * evaluate its values, and check if it applies to a specific rule.
 *
 * @param vector The integer array representing the attribute values of the household encoding.
 * @param scalar The scaling factor applied to the vector, representing how many instances of the household are desired (default is 1.0).
 */
class ScalableVector(private val vector: Collection<Int>, var scalar: Double = 1.0) {
    private val array: IntArray = vector.toIntArray()

    /**
     * A read-only property that provides a list view of the [array] for external access.
     * The underlying array is unfortunately mutable, but the list provides an immutable view to prevent external modification.
     */
    val content: List<Int> = array.toList()

    /**
     * Returns the value of the vector at the specified [index], adjusted by the current [scalar].
     *
     * @param index The index in the vector for which the value should be retrieved.
     * @return The value at the specified [index] in the vector, multiplied by the [scalar].
     */
    fun currentValueForIndex(index: Int): Double = array[index] * scalar

    /**
     * Determines whether this vector applies to a given rule based on the value at the [ruleIndex].
     * A value other than 0 at the [ruleIndex] indicates that the vector applies to the rule.
     *
     * @param ruleIndex The index of the rule to check against in the vector.
     * @return True if the vector applies to the rule (i.e., the value at the [ruleIndex] is non-zero), otherwise false.
     */
    fun appliesToRule(ruleIndex: Int): Boolean {
        return array[ruleIndex] != 0
    }

    /**
     * Multiplies the [scalar] of the vector by the given [times] value.
     *
     * @param times The number by which to multiply the current scalar.
     */
    operator fun timesAssign(times: Number) {
        scalar *= times.toDouble()
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ScalableVector) {
            return false
        }
        return array.contentEquals(other.array)

    }

    override fun hashCode(): Int {
        return array.contentHashCode()
    }

    private fun IntArray.contentHashCode(): Int {
        var result = 1
        for (element in this) {
            result = 31 * result + element
        }
        return result
    }

    companion object {
        /**
         * creates a Scalable Vector for a target [surveyHousehold] based on the ruleset defined in [rules]
         */
        fun <T> createFrom(surveyHousehold: SurveyHousehold<out T>, rules: List<Rule<in T>>): ScalableVector {
            return ScalableVector(rules.map { it.evaluate(surveyHousehold) })
        }
    }
}


