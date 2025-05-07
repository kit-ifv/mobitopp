package modeling.discreteChoice.selection

import modeling.discreteChoice.SelectionFunction
import utils.collections.select
import kotlin.random.Random

class RandomWeightedSelect<X> : SelectionFunction<X> {
    override fun calculateSelection(options: Map<X, Double>, random: Random): X = options.select(random.nextDouble())
}

class UniformSelect<X> : SelectionFunction<X> {
    override fun calculateSelection(options: Map<X, Double>, random: Random): X {
        val index = random.nextInt(options.size)
        return options.keys.toList()[index]
    }
}
