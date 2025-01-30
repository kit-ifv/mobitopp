package modeling.discreteChoice

import utils.collections.select
import kotlin.random.Random

fun interface SelectionFunction<X> {
    fun calculateSelection(options: Map<X, Double>): X
}

open class DiscreteChoiceModel<X : Any, SIT : ChoiceSituation<X>, P>(
    protected open val distributionFunction: ExtractableDistributionFunction<X, SIT, P>,
    protected val selectionFunction: SelectionFunction<SIT> = SelectionFunction {
        it.select(
            GlobalRandomizer.nextDouble()
        )
    },
) {
    fun select(alternatives: Set<SIT>, parameters: P): X {
        return selectionFunction.calculateSelection(
            distributionFunction.calculateProbabilities(
                alternatives,
                parameters
            )
        ).choice
    }

    fun select(singularOption: SIT, parameters: P): X = select(setOf(singularOption), parameters)

    fun probabilities(alternatives: Set<SIT>, parameters: P) = distributionFunction.calculateProbabilities(
        alternatives,
        parameters
    )
    fun utility(
        alternative: SIT,
        parameters: P
    ) = distributionFunction.translation(alternative).calculateUtility(alternative, parameters)
    fun changeSelectionFunction(selectionFunction: SelectionFunction<SIT>): DiscreteChoiceModel<X, SIT, P> {
        return DiscreteChoiceModel(distributionFunction, selectionFunction)
    }
}

class KnownDiscreteChoiceModel<X : Any, SIT : ChoiceSituation<X>, P>(
    override val distributionFunction: OptionDistributionFunction<X, SIT, P>
) : DiscreteChoiceModel<X, SIT, P>(
    distributionFunction
) {
    fun select(converter: (X) -> SIT, parameters: P): X {
        return select(distributionFunction.options.map(converter).toSet(), parameters)
    }
}

val GlobalRandomizer = Random(1)
