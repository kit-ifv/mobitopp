package domain.synthesis.behavior.fixedDestinations

import domain.shared.location.StandardLocation
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.SurveyPerson
import utils.collections.addProgressBar

/**
 * The locations in this class are already set, thus only a locate needs to be implemented. This is useful if you don't
 * want to microscopically manage the potential locations of each agent.
 */
fun interface SimpleLocator<T : MinimumPersonAttributes> :
    AdjustableAgentLocator<T>,
    SimpleGroupLocator<T> {

    fun locate(agent: SurveyPerson<T>): StandardLocation

    override fun locate(agent: SurveyPerson<T>, locations: Collection<StandardLocation>) = locate(agent)
    override fun match(agents: Collection<SurveyPerson<T>>): List<StandardLocation> =
        agents.addProgressBar("Running Fixed Destination Locator").map {
            locate(it)
        }
    override fun match(
        agents: Collection<SurveyPerson<T>>,
        potentialLocations: Collection<StandardLocation>,
    ): List<StandardLocation> = agents.map { locate(it) }
}