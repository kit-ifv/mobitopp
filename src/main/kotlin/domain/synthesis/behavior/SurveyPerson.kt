package domain.synthesis.behavior

import domain.shared.location.StandardLocation
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.data.person.Sex

interface SurveyPerson<out T> : MinimalistPerson<T> where T : MinimumPersonAttributes {
    val personId: Int
    override val attributes: T
    val age: Int get() = attributes.age
    val sex: Sex get() = attributes.sex

    val homeLocation: StandardLocation
}
