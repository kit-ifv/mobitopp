package modeling.discreteChoice

import modeling.models.ChoiceAlternative
import modeling.models.ChoiceFilter
import modeling.models.ChoiceModel
import modeling.models.FixedChoicesModel
import utils.collections.associateWithNotNull
import kotlin.collections.get
import kotlin.random.Random

/**
 * A utility function takes in an alternative and a parameter object and returns the utility of said alternative.
 */
fun interface UtilityFunction<A, P> {
    fun calculateUtility(alternative: A, parameterObject: P): Double
}

fun interface UtilityAssignment<R : Any, A, P> where A : ChoiceAlternative<R> {
    fun getUtilityFunctionFor(alternative: A): UtilityFunction<A, P>?
    operator fun get(alternative: A) = getUtilityFunctionFor(alternative)
}

/**
 * A distribution function takes in a collection of situations with their associated utility functions already calculated,
 * a parameter object and returns a map of calculated probabilities from the given alternatives
 */
fun interface DistributionFunction<A, P> {
    fun calculateProbabilities(utilities: Map<A, Double>, parameters: P): Map<A, Double>
}

fun interface SelectionFunction<X> {
    fun calculateSelection(options: Map<X, Double>, random: Random): X
}

data class DiscreteChoiceModel<R : Any, A, P>(
    override val name: String,
    override val choiceFilter: ChoiceFilter<A>,
    val utilityAssignment: UtilityAssignment<R, A, P>,
    val distributionFunction: DistributionFunction<A, P>,
    val selectionFunction: SelectionFunction<A>,
    val parameters: P,
) : ChoiceModel<A, R> where A : ChoiceAlternative<R> {

    override fun select(choices: Set<A>, random: Random): R =
        selectionFunction.calculateSelection(probabilities(choices), random).choice

    fun probabilities(alternatives: Set<A>) =
        distributionFunction.calculateProbabilities(utilities(alternatives), parameters)

    fun utilities(alternatives: Set<A>): Map<A, Double> =
        alternatives.associateWithNotNull { utility(it) }

    fun utility(alternative: A): Double = requireNotNull(
        utilityAssignment[alternative]?.calculateUtility(alternative, parameters)
    ) {
        "Error in model $name: \n" +
            "No utility function was designed for ${alternative.choice}"
    }

    fun with(choices: Set<R>) = EnumeratedDiscreteChoiceModel<R, A, P>(this, choices)
}

data class EnumeratedDiscreteChoiceModel<R : Any, A, P>(
    val model: DiscreteChoiceModel<R, A, P>,
    override val choices: Set<R>,
) : ChoiceModel<A, R> by model, FixedChoicesModel<A, R> where A : ChoiceAlternative<R> {

    override fun select(choices: Set<A>, random: Random): R = model.select(choices, random)
}
