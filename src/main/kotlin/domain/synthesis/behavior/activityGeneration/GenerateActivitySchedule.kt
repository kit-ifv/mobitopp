package domain.synthesis.behavior.activityGeneration

import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.SurveyPerson

fun interface GenerateActivitySchedule<in S : MinimumHouseholdAttributes, in T : MinimumPersonAttributes> :
    GenerateHouseholdActivitySchedule<S, T> {
    fun generate(person: SurveyPerson<T>): PreliminaryActivitySchedule
    override fun generate(household: ISurveyHousehold<S, T>): List<PreliminaryActivitySchedule> =
        household.members.map {
            generate(it)
        }
}
