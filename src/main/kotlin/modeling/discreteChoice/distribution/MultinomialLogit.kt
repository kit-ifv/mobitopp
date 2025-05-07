package modeling.discreteChoice.distribution

import modeling.discreteChoice.DistributionFunction
import kotlin.math.exp

class MultinomialLogit<X, P> : DistributionFunction<X, P> {

    override fun calculateProbabilities(utilities: Map<X, Double>, parameters: P): Map<X, Double> {
        val currentExp = utilities.entries.associate {
            it.key to exp(it.value)
        }
        val sum = currentExp.values.sum()

        return currentExp.mapValues { it.value / sum }
    }
}

// class AllocatedLogit<X : Any, SIT : ChoiceAlternative<X>, P>(
//    override val options: Set<X>,
//    override val rules: List<Pair<(SIT) -> Boolean, UtilityFunction<SIT, P>>>,
//    override val name: String = "Unnamed allocated logit",
//
//    ) : RuleBasedUtilityEnumeration<X, SIT, P>, OptionDistributionFunction<X, SIT, P> {
//    override val translation: Map<X, UtilityFunction<SIT, P>> = emptyMap()
//
//    override fun calculateProbabilities(utilities: Map<SIT, Double>, parameters: P): Map<SIT, Double> {
//        return Logit<SIT, P>().calculateProbabilities(utilities, parameters)
//    }
//
//    override fun translation(target: SIT): UtilityFunction<SIT, P> {
//        return super<RuleBasedUtilityEnumeration>.translation(target)
//    }
//
//    companion object {
//
//        private const val DEFAULT_NAME = "Unnamed MNL model"
//
//        class LogitBuilder<X : Any, SIT : ChoiceAlternative<X>, PARAMS>(preknownOptions: Collection<X>) :
//            EnumeratedStructureBuilder<X, SIT, PARAMS>, RuleBasedStructureBuilder<X, SIT, PARAMS> {
//
//            val rules: MutableList<Pair<(SIT) -> Boolean, UtilityFunction<SIT, PARAMS>>> = mutableListOf()
//            val options: MutableSet<X> = preknownOptions.toMutableSet()
//            override fun addUtilityFunctionByIdentifier(option: X, utilityFunction: UtilityFunction<SIT, PARAMS>) {
//                rules.add({ sit: SIT -> sit.choice == option } to utilityFunction)
//                options.add(option)
//            }
//
//            override fun addUtilityFunctionByRule(
//                rule: (SIT) -> Boolean,
//                utilityFunction: UtilityFunction<SIT, PARAMS>
//            ) {
//                rules.add(rule to utilityFunction)
//            }
//        }
//
//        fun <X : Any, SIT : ChoiceAlternative<X>, PARAMS> create(
//            options: Collection<X>,
//            name: String = DEFAULT_NAME,
//            lambda: LogitBuilder<X, SIT, PARAMS>.() -> Unit
//        ): AllocatedLogit<X, SIT, PARAMS> {
//            val builder = LogitBuilder<X, SIT, PARAMS>(options)
//            builder.apply(lambda)
//
//            return AllocatedLogit(builder.options, builder.rules, name = name)
//        }
//
//        fun <X : Any, SIT : ChoiceAlternative<X>, PARAMS> create(
//            name: String = DEFAULT_NAME,
//            lambda: LogitBuilder<X, SIT, PARAMS>.() -> Unit
//        ): AllocatedLogit<X, SIT, PARAMS> = create(emptySet(), name, lambda)
//    }
// }
