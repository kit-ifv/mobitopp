package edu.kit.ifv.domain.synthesis.behavior.fixeddestinations
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson

/**
 * An Adjustable Agent locator is able to adjust the valid locations for each agent individually.
 */
fun interface AdjustableAgentLocator<T : MinimumPersonAttributes> : AdjustableGroupLocator<T> {
    fun locate(agent: SurveyPerson<T>, locations: Collection<StandardLocation>): StandardLocation
    override fun match(
        agents: Collection<SurveyPerson<T>>,
        potentialLocations: Collection<StandardLocation>,
    ): List<StandardLocation> = agents.map { locate(it, potentialLocations) }
}
