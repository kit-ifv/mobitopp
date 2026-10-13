package edu.kit.ifv.domain.synthesis.behavior.activitygeneration

import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson

fun interface ActivityGeneratorSelector<in S : MinimumHouseholdAttributes, in T : MinimumPersonAttributes> {
    fun selectFor(person: SurveyPerson<T>): GenerateActivitySchedule<S, T>?
}