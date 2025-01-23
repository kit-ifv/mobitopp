package synthesis.fixedDestinations

import domain.location.Location
import synthesis.domain.SynthesisPerson

/**
 * Adjustable group locators can control the potential valid locations for a collection of agents.
 */
fun interface AdjustableGroupLocator<T> {
    fun match(
        agents: Collection<SynthesisPerson<out T>>,
        potentialLocations: Collection<Location>
    ): List<Pair<SynthesisPerson<out T>, Location>>
}

/**
 * A simple group locator disregards the information of potential valid locations, maybe they are already preallocated.
 */
fun interface SimpleGroupLocator<T> : AdjustableGroupLocator<T> {
    fun match(agents: Collection<SynthesisPerson<out T>>): List<Pair<SynthesisPerson<out T>, Location>>
    override fun match(
        agents: Collection<SynthesisPerson<out T>>,
        potentialLocations: Collection<Location>
    ): List<Pair<SynthesisPerson<out T>, Location>> = match(agents)

}


/**
 * An Adjustable Agent locator is able to adjust the valid locations for each agent individually.
 */
fun interface AdjustableAgentLocator<T> : AdjustableGroupLocator<T> {
    fun locate(agent: SynthesisPerson<out T>, locations: Collection<Location>): Location

    override fun match(
        agents: Collection<SynthesisPerson<out T>>,
        potentialLocations: Collection<Location>
    ): List<Pair<SynthesisPerson<out T>, Location>> {
        return agents.associateWith { locate(it, potentialLocations) }.toList()
    }
}



/**
 * The locations in this class are already set, thus only a locate needs to be implemented. This is useful if you don't
 * want to microscopically manage the potential locations of each agent.
 */
fun interface SimpleLocator<T> : AdjustableAgentLocator<T>, SimpleGroupLocator<T> {
    fun locate(agent: SynthesisPerson<out T>): Location
    override fun locate(agent: SynthesisPerson<out T>, locations: Collection<Location>) = locate(agent)
    override fun match(agents: Collection<SynthesisPerson<out T>>): List<Pair<SynthesisPerson<out T>, Location>> {
        return agents.associateWith { locate(it) }.toList()
    }

    override fun match(
        agents: Collection<SynthesisPerson<out T>>,
        potentialLocations: Collection<Location>
    ): List<Pair<SynthesisPerson<out T>, Location>> {
        return agents.associateWith { locate(it) }.toList()
    }
}



