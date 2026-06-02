package domain.synthesis.behavior.fixedDestinations

import domain.shared.location.StandardLocation
import domain.synthesis.SynthesisPerson
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.SurveyPerson
import utils.collections.addProgressBar

data class AssignedLocation<T : MinimumPersonAttributes>(
    val targetPerson: SynthesisPerson<*, T>,
    val assignedLocation: StandardLocation,
)

/**
 * Adjustable group locators can control the potential valid locations for a collection of agents.
 */
fun interface AdjustableGroupLocator<T : MinimumPersonAttributes> {

    fun match(
        agents: Collection<SurveyPerson<T>>,
        potentialLocations: Collection<StandardLocation>,
    ): List<StandardLocation>
}

/**
 * A simple group locator disregards the information of potential valid locations, maybe they are already preallocated.
 */
fun interface SimpleGroupLocator<T : MinimumPersonAttributes> : AdjustableGroupLocator<T> {

    fun match(agents: Collection<SurveyPerson<T>>): List<StandardLocation>

    /**
     * Convenience function, to avoid having to write listOf(...) every time this method is called.
     */
    fun match(vararg agents: SurveyPerson<T>) = match(agents.toList())

    override fun match(
        agents: Collection<SurveyPerson<T>>,
        potentialLocations: Collection<StandardLocation>,
    ): List<StandardLocation> = match(
        agents,
    )
}

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
