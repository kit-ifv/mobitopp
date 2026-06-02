package domain.synthesis.behavior

import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.data.household.HouseholdType
import edu.kit.ifv.units.Currency

interface ISurveyHousehold<
    out S : MinimumHouseholdAttributes,
    out T : MinimumPersonAttributes,
    > :
    MinimalistHousehold<S, T> {
    val surveyHouseholdId: Long
    val income: Currency get() = attributes.income
    override val members: List<SurveyPerson<T>>
    val type: HouseholdType get() = attributes.type
    fun count(condition: (SurveyPerson<T>) -> Boolean): Int = members.count(condition)
}
