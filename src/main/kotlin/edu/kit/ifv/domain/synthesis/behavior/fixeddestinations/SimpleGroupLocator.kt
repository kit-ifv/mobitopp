package edu.kit.ifv.domain.synthesis.behavior.fixeddestinations
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson

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
