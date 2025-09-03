package domain.synthesis.behavior.householdgeneration

import domain.synthesis.behavior.SurveyHousehold

/**
 * Defines a rule for population synthesis, which describes a condition to be met by the synthetic population.
 * These conditions are often based on observations from external surveys. Example rules include:
 * - "The number of men aged 20–30 in a specific area should be 9001."
 * - "Only 1000 agents should have an income of exactly 42€."
 *
 * The implementation logic of a rule is left as an interface for external implementations. All rules must have a
 * numeric target that specifies the desired outcome.
 *
 * @property target The numeric value representing the desired state or outcome defined by the rule.
 * @property descriptiveText A human-readable name or description of the rule.
 * @param T The shared property type required by a household to be evaluated against this rule.
 */
interface Rule<T> {
    val target: Int
    val description: String

    val logic: NamedCountRule<T>

    /**
     * Evaluates the extent to which a given [surveyHousehold] satisfies the condition defined by the rule.
     * For example:
     * - A rule like "Number of people aged 10–50" could return a value in the range [0, ∞].
     * - A rule like "Household size is exactly 5" could return either 0 or 1.
     *
     * @param surveyHousehold The household to evaluate.
     * @return An integer representing the contribution of the household to the rule's target.
     */
    fun evaluate(surveyHousehold: SurveyHousehold<out T>): Int

    fun evaluate(households: Collection<SurveyHousehold<out T>>): Int = households.sumOf { evaluate(it) }

    /**
     * Determines whether a given [surveyHousehold] contributes to the rule's target.
     *
     * @param surveyHousehold The household to check.
     * @return `true` if the household contributes to the rule's target; `false` otherwise.
     */
    fun appliesTo(surveyHousehold: SurveyHousehold<out T>): Boolean = evaluate(surveyHousehold) != 0

    /**
     * Calculates the difference (offset) between the desired [target] and the aggregate contributions from a
     * collection of households.
     *
     * @param output A collection of households to evaluate.
     * @return The difference between the target and the sum of contributions from the households.
     */
    fun verify(output: Collection<SurveyHousehold<out T>>): Double {
        return target.toDouble() - output.sumOf { evaluate(it) }
    }

    /**
     * Filters a collection of households to include only those that contribute to the rule's target.
     *
     * @param target The collection of households to filter.
     * @return A list of households that contribute to the rule.
     */
    fun filter(target: Collection<SurveyHousehold<out T>>): List<SurveyHousehold<out T>> {
        return target.filter { appliesTo(it) }
    }

    fun descriptiveText() = "[$description] expected = $target"
}

/*
   I really wanted to be able to specify rules as numeric rules and boolean rules. To avoid JVM-overload ambiguity different
   namespaces are required. This is the reason why the four classes below exist.
   Advantages:
        - Being able to write the conditions naturally {it.size == 5} instead of { if(it.size == 5) 1 else 0}
        - Can compile the code. (Quite useful)
   Disadvantages:
        - Always have to instantiate the rule by name: CheckRule for Booleans and CountRule for Ints.
        - Multiple classes with similar logic
    I would really prefer if there would be a way so that a single class could differentiate between an anonymous function
    returning an int and an anonymous function returning a bool. If someone else has a good idea how to implement this
    please do so at your own discretion.
 */

/**
 * Represents a rule that evaluates a household's contribution to a target based on counting a specific attribute.
 * Example: Counting the number of female agents in households.
 */
fun interface CountRule<T> {
    /**
     * Evaluates how much the given [surveyHousehold] contributes to the target based on a specific attribute.
     *
     * @param surveyHousehold The household to evaluate.
     * @return An integer representing the contribution.
     */
    fun matches(surveyHousehold: SurveyHousehold<out T>): Int
}

/**
 * Represents a rule that evaluates a household's contribution to a target using a boolean condition.
 * The contribution is 1 if the condition is met, otherwise 0.
 */
fun interface CheckRule<T> : CountRule<T> {
    /**
     * Checks whether the given [surveyHousehold] meets the condition defined by the rule.
     *
     * @param surveyHousehold The household to evaluate.
     * @return `true` if the condition is met; `false` otherwise.
     */
    fun fits(surveyHousehold: SurveyHousehold<out T>): Boolean
    override fun matches(surveyHousehold: SurveyHousehold<out T>): Int {
        return if (fits(surveyHousehold)) 1 else 0
    }
}

class NamedCheckRule<T>(ruleDescription: String, override val logic: CheckRule<T>) :
    NamedCountRule<T>(
        ruleDescription,
        logic
    ),
    CheckRule<T> by logic {
    override fun matches(surveyHousehold: SurveyHousehold<out T>): Int {
        return super<NamedCountRule>.matches(surveyHousehold)
    }
}

open class NamedCountRule<T>(val ruleDescription: String, open val logic: CountRule<T>) : CountRule<T> by logic

/**
 * A named implementation of the [Rule] interface, using a [CountRule] to calculate a household's contribution to
 * the target. Example: Counting the number of households with a specific attribute.
 *
 * @property description A descriptive name for the rule.
 * @property target The numeric target for the rule.
 * @property logic The [CountRule] implementation defining the logic for evaluating households.
 */
class ZoneRule<T>(
    override val description: String = logic.ruleDescription,
    override val target: Int,
    override val logic: NamedCountRule<T>,
) : Rule<T> {
    constructor(description: String, target: Int, logic: CountRule<T>) : this(
        description,
        target,
        NamedCountRule("Unnamed rule", logic)
    )
    override fun evaluate(surveyHousehold: SurveyHousehold<out T>): Int {
        return logic.matches(surveyHousehold)
    }

    override fun toString(): String {
        return descriptiveText()
    }
}

/**
 * A named implementation of the [Rule] interface, using a [CheckRule] to evaluate a household's contribution to
 * the target. Example: Checking whether households meet a specific condition.
 *
 * @property description A descriptive name for the rule.
 * @property target The numeric target for the rule.
 * @property logic The [CheckRule] implementation defining the logic for evaluating households.
 */
class ZoneCheckRule<T>(
    override val description: String = logic.ruleDescription,
    override val target: Int,
    override val logic: NamedCheckRule<T>,
) : Rule<T> {
    constructor(description: String, target: Int, logic: CheckRule<T>) : this(
        description,
        target,
        NamedCheckRule("Unnamed rule", logic)
    )
    override fun evaluate(surveyHousehold: SurveyHousehold<out T>): Int {
        return logic.matches(surveyHousehold)
    }

    override fun toString(): String {
        return descriptiveText()
    }
}
