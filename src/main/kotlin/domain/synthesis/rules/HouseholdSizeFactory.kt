package domain.synthesis.rules

import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.rules.measurements.HouseholdSizeDefinition
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.covered.FullCoverageGroup
import edu.kit.ifv.populationsynthesis.rules.toRuleSet

abstract class HouseholdSizeFactory<Input>(
    val equalTargetExtractor: (Input, Int) -> Number?,
    val greaterEqualTargetExtractor: (Input, Int) -> Number?,
) {
    fun buildRuleSet(lastExplicitSize: Int, input: Input): CoverageGroup<MinimalistHousehold<*, *>> {
        val equalityRules = (1..lastExplicitSize).map { optionalEqualityRule(it, input) }
        val greaterEqualsRule = optionalGreaterEqualsRule(lastExplicitSize + 1, input)
        val ruleset = (equalityRules + greaterEqualsRule).filterNotNull().toRuleSet()
        return FullCoverageGroup(ruleset)
    }

    private fun optionalGreaterEqualsRule(target: Int, input: Input): Rule<MinimalistHousehold<*, *>>? =
        getGreaterEqualDefinition(target).makeOptionalRule(greaterEqualTargetExtractor(input, target))

    private fun optionalEqualityRule(
        i: Int,
        input: Input
    ): Rule<MinimalistHousehold<*, *>>? = getEqualDefinition(i).makeOptionalRule(equalTargetExtractor(input, i))

    private fun getEqualDefinition(size: Int): HouseholdSizeDefinition {
        return HouseholdSizeDefinition(size, HouseholdSizeDefinition.EqualityOp.EQUALS)
    }

    private fun getGreaterEqualDefinition(size: Int): HouseholdSizeDefinition {
        return HouseholdSizeDefinition(size, HouseholdSizeDefinition.EqualityOp.GREATER_OR_EQUAL)
    }
}
