package modeling.discreteChoice

/**
 * A utility function takes in an alternative and a parameter object and returns the utility of said alternative.
 */
fun interface UtilityFunction<SIT, PARAMS> {
    fun calculateUtility(alternative: SIT, parameterObject: PARAMS): Double
}

/**
 * An allocated function knows what options are available
 */
interface OptionDistributionFunction<X : Any, SIT : ChoiceSituation<X>, PARAMS> :
    ExtractableDistributionFunction<X, SIT, PARAMS> {
    val options: Set<X> get() = translation.keys
    val translation: Map<X, UtilityFunction<SIT, PARAMS>>
    override fun translation(target: SIT): UtilityFunction<SIT, PARAMS> = translation.getOrElse(target.choice) {
        throw NoSuchElementException("There is no utility function for $target in this distribution function")
    }
}

/**
 * If we have this class we have the ability to predetermine the utility function for a given situation SIT
 */
interface ExtractableDistributionFunction<X : Any, SIT : ChoiceSituation<X>, PARAMS> :
    DistributionFunction<SIT, PARAMS> {
    val name get() = "Unnamed Distribution Function"
    fun translation(target: SIT): UtilityFunction<SIT, PARAMS>
    fun calculateProbabilities(alternatives: Set<SIT>, parameters: PARAMS): Map<SIT, Double> {
        return calculateProbabilities(
            alternatives.associateWith { translation(it).calculateUtility(it, parameters) },
            parameters
        )
    }
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

interface RuleBasedAssociation<X : Any, SIT : ChoiceSituation<X>, PARAMS> :
    UtilityFunctionAssociation<SIT, PARAMS>,
    ExtractableDistributionFunction<X, SIT, PARAMS> {
    val rules: List<Pair<(SIT) -> Boolean, UtilityFunction<SIT, PARAMS>>>
    override fun associateFunction(to: SIT): UtilityFunction<SIT, PARAMS> {
        val firstMatchingRule = rules.firstOrNull { it.first.invoke(to) }
            ?: throw NoSuchElementException(
                "The choice model: [$name] cannot associate the target element " +
                    "${to.choice} to a utility function. Is the option defined in the choice model?"
            )
        return firstMatchingRule.second
    }

    override fun translation(target: SIT): UtilityFunction<SIT, PARAMS> {
        return associateFunction(target)
    }
}
