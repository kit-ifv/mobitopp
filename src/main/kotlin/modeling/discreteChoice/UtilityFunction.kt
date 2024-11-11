package modeling.discreteChoice

import kotlin.math.exp

fun interface UtilityFunction<X, P> {
    // TODO debate whether double is the correct return type.
    fun calculateUtility(alternative: X, parameters: P): Double
}

fun interface DistributionFunction<X, P> {
    fun calculateProbabilities(alternatives: Set<X>, parameters: P, utilityFunction: UtilityFunction<X, P>): Map<X, Double>
}

fun interface SelectionFunction<X> {
    fun calculateSelection(options: Map<X, Double>): X
}


class Logit<X, P> : DistributionFunction<X, P> {
    override fun calculateProbabilities(alternatives: Set<X>, parameters: P, utilityFunction: UtilityFunction<X, P>): Map<X, Double> {


        val currentExp = alternatives.associateWith {
            exp(utilityFunction.calculateUtility(it, parameters))
        }
        val sum = currentExp.values.sum()

        return currentExp.mapValues { it.value / sum }
    }

}

class DiscreteChoiceModel<X, P>(
    private val utilityFunction: UtilityFunction<X, P>,
    private val distributionFunction: DistributionFunction<X, P>,
    private val selectionFunction: SelectionFunction<X>,
) {
    fun select(alternatives: Set<X>, parameters: P): X {
        return selectionFunction.calculateSelection(distributionFunction.calculateProbabilities(alternatives, parameters, utilityFunction))
    }
}

enum class Attempt {
    ONE, TWO, THREE;
}

fun main() {

    val utilityFunction = UtilityFunction { x: Attempt, parameters: Unit -> x.ordinal.toDouble() }
    val dcm =  DiscreteChoiceModel<Attempt, Unit>(
        { x, _ -> x.ordinal.toDouble() },
        Logit()
    ) { it.keys.first() }

    val e = dcm.select(Attempt.entries.toSet(), Unit)
    val result = logit.calculateProbabilities(Attempt.entries.toSet(), Unit){ x: Attempt, parameters: Unit -> x.ordinal.toDouble() }
    println(result)

}