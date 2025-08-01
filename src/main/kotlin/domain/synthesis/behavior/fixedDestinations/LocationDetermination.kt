package domain.synthesis.behavior.fixedDestinations

import domain.shared.location.Location
import domain.synthesis.behavior.domain.SynthesisPerson
import utils.collections.addProgressBar

data class AssignedLocation<T>(
    val targetPerson: SynthesisPerson<out T>,
    val assignedLocation: Location
)

/**
 * Adjustable group locators can control the potential valid locations for a collection of agents.
 */
fun interface AdjustableGroupLocator<T> {
    fun match(
        agents: Collection<SynthesisPerson<out T>>,
        potentialLocations: Collection<Location>
    ): List<AssignedLocation<T>>
}

/**
 * A simple group locator disregards the information of potential valid locations, maybe they are already preallocated.
 */
fun interface SimpleGroupLocator<T> : AdjustableGroupLocator<T> {
    fun match(agents: Collection<SynthesisPerson<out T>>): List<AssignedLocation<T>>

    /**
     * Convenience function, to avoid having to write listOf(...) every time this method is called.
     */
    fun match(vararg agents: SynthesisPerson<out T>) = match(agents.toList())
    override fun match(
        agents: Collection<SynthesisPerson<out T>>,
        potentialLocations: Collection<Location>
    ): List<AssignedLocation<T>> = match(agents)
}

/**
 * An Adjustable Agent locator is able to adjust the valid locations for each agent individually.
 */
fun interface AdjustableAgentLocator<T> : AdjustableGroupLocator<T> {
    fun locate(agent: SynthesisPerson<out T>, locations: Collection<Location>): Location

    override fun match(
        agents: Collection<SynthesisPerson<out T>>,
        potentialLocations: Collection<Location>
    ): List<AssignedLocation<T>> {
        return agents.map { AssignedLocation(it, locate(it, potentialLocations)) }
    }
}

/**
 * The locations in this class are already set, thus only a locate needs to be implemented. This is useful if you don't
 * want to microscopically manage the potential locations of each agent.
 */
fun interface SimpleLocator<T> : AdjustableAgentLocator<T>, SimpleGroupLocator<T> {
    fun locate(agent: SynthesisPerson<out T>): Location
    override fun locate(agent: SynthesisPerson<out T>, locations: Collection<Location>) = locate(agent)
    override fun match(agents: Collection<SynthesisPerson<out T>>): List<AssignedLocation<T>> {
        return agents.addProgressBar("Running Fixed Destination Locator").map { AssignedLocation(it, locate(it)) }
    }
    override fun match(
        agents: Collection<SynthesisPerson<out T>>,
        potentialLocations: Collection<Location>
    ): List<AssignedLocation<T>> {
        return agents.map { AssignedLocation(it, locate(it)) }
    }
}
