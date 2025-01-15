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

class AllocatedLogit<X : Any, SIT : ChoiceSituation<X>, P>(
    override val options: Set<X>,
    override val rules: List<Pair<(SIT) -> Boolean, UtilityFunction<SIT, P>>>,
    override val name: String = "Unnamed allocated logit",

) : RuleBasedAssociation<X, SIT, P>, OptionDistributionFunction<X, SIT, P> {
    override val translation: Map<X, UtilityFunction<SIT, P>> = emptyMap()

    override fun calculateProbabilities(evaluators: Map<SIT, Double>, parameters: P): Map<SIT, Double> {
        return Logit<SIT, P>().calculateProbabilities(evaluators, parameters)
    }

    override fun translation(target: SIT): UtilityFunction<SIT, P> {
        return super<RuleBasedAssociation>.translation(target)
    }

    companion object {
        class LogitBuilder<X : Any, SIT : ChoiceSituation<X>, PARAMS>(preknownOptions: Collection<X>) :
            OptionBasedSituationBuilder<X, SIT, PARAMS>, RuleBasedSituationBuilder<X, SIT, PARAMS> {
            val rules: MutableList<Pair<(SIT) -> Boolean, UtilityFunction<SIT, PARAMS>>> = mutableListOf()
            val options: MutableSet<X> = preknownOptions.toMutableSet()
            override fun addUtilityFunctionByIdentifier(x: X, utilityFunction: UtilityFunction<SIT, PARAMS>) {
                rules.add({ sit: SIT -> sit.choice == x } to utilityFunction)
                options.add(x)
            }

            override fun addUtilityFunctionByRule(
                rule: (SIT) -> Boolean,
                utilityFunction: UtilityFunction<SIT, PARAMS>
            ) {
                rules.add(rule to utilityFunction)
            }
        }

        fun <X : Any, SIT : ChoiceSituation<X>, PARAMS> create(
            options: Collection<X>,
            name: String = "Unnamed MNL model",
            lambda: LogitBuilder<X, SIT, PARAMS>.() -> Unit
        ): AllocatedLogit<X, SIT, PARAMS> {
            val builder = LogitBuilder<X, SIT, PARAMS>(options)
            builder.apply(lambda)

            return AllocatedLogit(builder.options, builder.rules, name = name)
        }

        fun <X : Any, SIT : ChoiceSituation<X>, PARAMS> create(
            name: String = "Unnamed MNL model",
            lambda: LogitBuilder<X, SIT, PARAMS>.() -> Unit
        ): AllocatedLogit<X, SIT, PARAMS> = create(emptySet(), name, lambda)
    }
}
