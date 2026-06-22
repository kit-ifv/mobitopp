package edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.communitybased
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson

/**
 * Find the best location for a target agent based on the demand and available locations.
 */
fun interface BestLocationFromDemand<T : MinimumPersonAttributes> {
    fun bestLocation(
        agent: SurveyPerson<T>,
        demand: CommunityDemand,
        locations: Collection<StandardLocation>,
    ): StandardLocation
}
