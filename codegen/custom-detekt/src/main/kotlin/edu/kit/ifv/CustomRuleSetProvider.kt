package edu.kit.ifv

import dev.detekt.api.RuleSet
import dev.detekt.api.RuleSetId
import dev.detekt.api.RuleSetProvider

class CustomRuleSetProvider : RuleSetProvider {
    override val ruleSetId: RuleSetId = RuleSetId("custom-rules")

    override fun instance(): RuleSet {
        return RuleSet(ruleSetId,
            listOf(
                ::LayeredArchitecture
            )
        )
    }
}