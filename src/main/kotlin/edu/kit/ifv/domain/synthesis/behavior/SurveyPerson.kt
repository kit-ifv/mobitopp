package edu.kit.ifv.domain.synthesis.behavior
import edu.kit.ifv.domain.shared.enums.person.Sex
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes

interface SurveyPerson<out T> : MinimalistPerson<T> where T : MinimumPersonAttributes {
    val personId: Int
    override val attributes: T
    val age: Int get() = attributes.age
    val sex: Sex get() = attributes.sex

    val homeLocation: StandardLocation
}
