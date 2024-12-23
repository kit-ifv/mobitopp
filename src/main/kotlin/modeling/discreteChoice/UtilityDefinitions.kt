package modeling.discreteChoice

import java.util.*
import kotlin.NoSuchElementException

/**
 * A utility function takes in an alternative and a parameter object and returns the utility of said alternative.
 */
fun interface UtilityFunction<SIT, PARAMS> {
    fun calculateUtility(alternative: SIT, parameterObject: PARAMS): Double
}

/**
 * An allocated function knows what options are available
 */
interface AllocatedDistributionFunction<X: Any, SIT : ChoiceSituation<X>, PARAMS> : DistributionFunction<SIT, PARAMS> {
    val name get() = "Unnamed Distribution Function"
    val options: Set<X>
    fun translation(target: SIT): UtilityFunction<SIT, PARAMS>
    fun calculateProbabilities(alternatives: Set<SIT>, parameters: PARAMS): Map<SIT, Double> {

        return calculateProbabilities(
            alternatives.associateWith { translation(it).calculateUtility(it, parameters) },
            parameters
        )
    }


}


interface ParameterizedDistributionFunction<X : Any, SIT : ChoiceSituation<X>, PARAMS> {
    /**
     * Specific implementations of discrete choice models need a layer of abstraction to determine the correct utility
     * function that should be applied to a target situation. As Example take mode choice, where each mode is typically
     * associated with its own utility funciton, whereas destination choice usually has a single function applied to all
     * targets individually. By providing the layer of abstraction any discrete choice scenario should be representable
     */
    fun translation(target: SIT): UtilityFunction<SIT, PARAMS>

    // TODO maybe change return type from X to S, that way X could be dropped from the class generics?
    fun calculateProbabilities(
        alternatives: Set<SIT>,
        parameters: PARAMS,
    ): Map<X, Double>

}

/**
 * A distribution function takes in a collection of situations with their associated utility functions already calculated,
 * a parameter object and returns a map of calculated probabilities from the given alternatives
 */
fun interface DistributionFunction<SIT, PARAMS> {
    fun calculateProbabilities(
        evaluators: Map<SIT, Double>,
        parameters: PARAMS
    ): Map<SIT, Double>
}

fun interface UtilityFunctionAssociation<SIT, PARAMS> {
    fun associateFunction(to: SIT): UtilityFunction<SIT, PARAMS>
}

interface MapBasedAssociation<SIT, PARAMS> : UtilityFunctionAssociation<SIT, PARAMS> {
    val map: Map<SIT, UtilityFunction<SIT, PARAMS>>
    override fun associateFunction(to: SIT): UtilityFunction<SIT, PARAMS> {
        return map[to] ?: throw NoSuchElementException("No utility function located for $to")
    }
}

interface RuleBasedAssociation<X: Any, SIT : ChoiceSituation<X>, PARAMS> : UtilityFunctionAssociation<SIT, PARAMS>,
    AllocatedDistributionFunction<X, SIT, PARAMS> {
    val rules: List<Pair<(SIT) -> Boolean, UtilityFunction<SIT, PARAMS>>>
    override fun associateFunction(to: SIT): UtilityFunction<SIT, PARAMS> {
        val firstMatchingRule = rules.firstOrNull { it.first.invoke(to) }
            ?: throw NoSuchElementException("The choice model: [$name] cannot associate the target element ${to.choice} to a utility function. Is the option defined in the choice model?")
        return firstMatchingRule.second
    }

    override fun translation(target: SIT): UtilityFunction<SIT, PARAMS> {
        return associateFunction(target)
    }
}

