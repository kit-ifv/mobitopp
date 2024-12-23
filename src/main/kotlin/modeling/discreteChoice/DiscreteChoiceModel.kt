package modeling.discreteChoice

import utils.collections.select
import kotlin.random.Random
fun interface SelectionFunction<X> {
    fun calculateSelection(options: Map<X, Double>): X
}
class DiscreteChoiceModel<X: Any, SIT: ChoiceSituation<X>, P>(
    private val distributionFunction: AllocatedDistributionFunction<X, SIT, P>,
    private val selectionFunction: SelectionFunction<SIT> = SelectionFunction { it.select(GlobalRandomizer.nextDouble()) },
) {
    fun select(alternatives: Set<SIT>, parameters: P): X {
        return selectionFunction.calculateSelection(
            distributionFunction.calculateProbabilities(
                alternatives,
                parameters
            )
        ).choice
    }

    fun select(converter: (X) -> SIT, parameters: P) : X {
        return select(distributionFunction.options.map(converter).toSet(), parameters)
    }
    fun selectVerbose(alternatives: Set<SIT>, parameters: P): X {
        return selectionFunction.calculateSelection(
            distributionFunction.calculateProbabilities(
                alternatives,
                parameters
            ).also { println(it) }
        ).choice
    }

}

val GlobalRandomizer = Random(1)