package modeling.discreteChoice


import kotlin.math.exp

class Logit<X, P> : DistributionFunction<X, P> {

    override fun calculateProbabilities(evaluators: Map<X, Double>, parameters: P): Map<X, Double> {
        val currentExp = evaluators.entries.associate {
            it.key to
                    exp(it.value)
        }
        val sum = currentExp.values.sum()

        return currentExp.mapValues { it.value / sum }
    }
}

class AllocatedLogit<X: Any, SIT : ChoiceSituation<X>, P>(override val options: Set<X>, override val rules: List<Pair<(SIT) -> Boolean, UtilityFunction<SIT, P>>>) : RuleBasedAssociation<X, SIT, P> {
    override val name = "Unnamed allocated logit"


    override fun calculateProbabilities(evaluators: Map<SIT, Double>, parameters: P): Map<SIT, Double> {
        return Logit<SIT, P>().calculateProbabilities(evaluators, parameters)
    }


    companion object {
        class LogitBuilder<X: Any, SIT: ChoiceSituation<X>, PARAMS>: ChoiceSituationBuilder<X, SIT, PARAMS> {
            val rules: MutableList<Pair<(SIT) -> Boolean, UtilityFunction<SIT, PARAMS>>> = mutableListOf()
            val options: MutableSet<X> = mutableSetOf()
            override fun addUtilityFunction(x: X, utilityFunction: UtilityFunction<SIT, PARAMS>) {
                rules.add({ sit: SIT -> sit.choice == x } to utilityFunction)
                options.add(x)
            }
        }
            fun <X : Any, SIT : ChoiceSituation<X>, PARAMS> create(name: String = "Unnamed MNL model" , lambda: LogitBuilder<X, SIT, PARAMS>.() -> Unit): AllocatedLogit<X, SIT, PARAMS> {
                val builder = LogitBuilder<X, SIT, PARAMS>()
                builder.apply(lambda)


                return AllocatedLogit(builder.options, builder.rules)
        }

        }

}
