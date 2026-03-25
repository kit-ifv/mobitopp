package domain.synthesis.behavior

import domain.shared.location.StandardLocation
import domain.synthesis.attributes.person.MinimumPersonAttributes

data class SmallestSurveyPerson<T : MinimumPersonAttributes> constructor(
    override val personId: Int,
    override val attributes: T,
) : SurveyPerson<T> {
    override val homeLocation: StandardLocation
        get() = TODO("Not yet implemented")
}