package edu.kit.ifv.domain.synthesis.behavior.fixeddestinations
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson

/**
 * Adjustable group locators can control the potential valid locations for a collection of agents.
 */
fun interface AdjustableGroupLocator<T : MinimumPersonAttributes> {

    fun match(
        agents: Collection<SurveyPerson<T>>,
        potentialLocations: Collection<StandardLocation>,
    ): List<StandardLocation>
}
