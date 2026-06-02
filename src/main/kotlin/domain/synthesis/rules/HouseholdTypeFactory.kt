package domain.synthesis.rules

import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.data.HouseholdType
import domain.synthesis.rules.measurements.HouseholdTypeDefinition
import edu.kit.ifv.populationsynthesis.rules.Rule
import edu.kit.ifv.populationsynthesis.rules.covered.CoverageGroup
import edu.kit.ifv.populationsynthesis.rules.covered.FullCoverageGroup
import edu.kit.ifv.populationsynthesis.rules.toRuleSet

abstract class HouseholdTypeFactory<Input>(
    private val expectedTypes: Set<HouseholdType> = HouseholdType.validTypes,
    val targetExtractor: (Input, HouseholdType) -> Number?,
) {
    fun buildRuleSet(input: Input): CoverageGroup<ISurveyHousehold<*, *>> {
        val ruleset = expectedTypes.mapNotNull {
            optionalTypeRule(it, input)
        }.toRuleSet()
        return FullCoverageGroup(ruleset)
    }

    private fun optionalTypeRule(type: HouseholdType, input: Input): Rule<ISurveyHousehold<*, *>>? = getTypeDefinition(
        type,
    ).makeOptionalRule(targetExtractor(input, type))

    private fun getTypeDefinition(type: HouseholdType) = HouseholdTypeDefinition(type)
}
