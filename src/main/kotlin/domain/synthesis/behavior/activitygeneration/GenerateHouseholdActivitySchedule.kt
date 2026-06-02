package domain.synthesis.behavior.activitygeneration

import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.ISurveyHousehold

fun interface GenerateHouseholdActivitySchedule<in S : MinimumHouseholdAttributes, in T : MinimumPersonAttributes> {
    fun generate(household: ISurveyHousehold<S, T>): List<PreliminaryActivitySchedule>
}
