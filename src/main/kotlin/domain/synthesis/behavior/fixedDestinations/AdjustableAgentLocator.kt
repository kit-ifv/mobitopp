package domain.synthesis.behavior.fixedDestinations

import domain.shared.location.StandardLocation
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.SurveyPerson

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