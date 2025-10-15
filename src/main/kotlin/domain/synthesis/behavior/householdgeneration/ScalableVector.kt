package domain.synthesis.behavior.householdgeneration

import domain.synthesis.Signature
import domain.synthesis.behavior.SurveyHousehold

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
    val signature: Signature = array.withIndex().filter { it.value != 0 }.associate { (i, value) -> i to value }

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

    fun attributeForIndex(index: Int): Int = array[index]

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

    @Suppress("MagicNumber")
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
