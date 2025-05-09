package modeling.discreteChoice.structure

import modeling.discreteChoice.UtilityAssignment
import modeling.discreteChoice.UtilityFunction
import modeling.discreteChoice.utility.MapBasedUtilityEnumeration
import modeling.discreteChoice.utility.Rule
import modeling.discreteChoice.utility.RuleBasedUtilityAssignment
import modeling.discreteChoice.utility.UtilityEnumeration
import modeling.models.ChoiceAlternative

class DiscreteStructure<R : Any, A, P>(
    lambda: DiscreteStructure<R, A, P>.() -> Unit,
) : EnumeratedStructureBuilder<R, A, P>, UtilityEnumerationBuilder<R, A, P> where A : ChoiceAlternative<R> {

    private val options = mutableMapOf<R, UtilityFunction<A, P>>()

    init {
        this.lambda()
    }

    override fun build(): UtilityEnumeration<R, A, P> = MapBasedUtilityEnumeration(map = options)

    override fun addUtilityFunctionByIdentifier(option: R, utilityFunction: UtilityFunction<A, P>) {
        require(!options.containsKey(option)) {
            "Duplicate: A utility function for $option has already been defined in this structure. " +
                "Current elements ${options.keys} already have a utility function associated. "
        }
        options[option] = utilityFunction
    }
}

class RuleBasedStructure<R : Any, A, P>(
    lambda: RuleBasedStructure<R, A, P>.() -> Unit,
) : RuleBasedStructureBuilder<R, A, P>, UtilityAssignmentBuilder<R, A, P> where A : ChoiceAlternative<R> {

    val rules = mutableListOf<Rule<A, P>>()

    init {
        this.lambda()
    }

    override fun addUtilityFunctionByRule(
        rule: (A) -> Boolean,
        utilityFunction: UtilityFunction<A, P>
    ) {
        rules.add(Rule(rule, utilityFunction))
    }

    override fun build(): UtilityAssignment<R, A, P> = RuleBasedUtilityAssignment(rules)
}
