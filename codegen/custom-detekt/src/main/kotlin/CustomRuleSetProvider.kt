import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class CustomRuleSetProvider : RuleSetProvider {
    override val ruleSetId: String = "custom-rules"

    override fun instance(config: Config): RuleSet {
        return RuleSet(ruleSetId,
            listOf(
                //LayeredArchitecture(config.subConfig(LAYERED_ARCHITECTURE))
                        LayeredArchitecture(config)
            )
        )
    }
}