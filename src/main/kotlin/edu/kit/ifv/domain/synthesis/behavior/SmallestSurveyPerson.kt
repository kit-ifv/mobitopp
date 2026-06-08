package edu.kit.ifv.domain.synthesis.behavior
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes

data class SmallestSurveyPerson<T : MinimumPersonAttributes> constructor(
    override val personId: Int,
    override val attributes: T,
) : SurveyPerson<T> {
    override val homeLocation: StandardLocation by lazy {
        throw IllegalStateException("A Smallest survey person should never be used to determine location")
    }
}
