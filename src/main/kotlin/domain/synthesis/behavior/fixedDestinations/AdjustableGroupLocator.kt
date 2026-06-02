package domain.synthesis.behavior.fixedDestinations

import domain.shared.location.StandardLocation
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.SurveyPerson

/**
 * Adjustable group locators can control the potential valid locations for a collection of agents.
 */
fun interface AdjustableGroupLocator<T : MinimumPersonAttributes> {

    fun match(
        agents: Collection<SurveyPerson<T>>,
        potentialLocations: Collection<StandardLocation>,
    ): List<StandardLocation>
}