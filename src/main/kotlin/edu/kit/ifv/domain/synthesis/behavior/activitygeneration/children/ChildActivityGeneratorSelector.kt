package edu.kit.ifv.domain.synthesis.behavior.activitygeneration.children

import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson
import edu.kit.ifv.domain.synthesis.behavior.activitygeneration.ActivityGeneratorSelector
import edu.kit.ifv.domain.synthesis.behavior.activitygeneration.DayComposingScheduleGenerator
import edu.kit.ifv.domain.synthesis.behavior.activitygeneration.GenerateActivitySchedule

class ChildActivityGeneratorSelector(
    val zeroToTwoYears: DayComposingScheduleGenerator,
    val threeToFiveYears: DayComposingScheduleGenerator,
    val sixToNineYears: DayComposingScheduleGenerator,
) : ActivityGeneratorSelector<MinimumHouseholdAttributes, MinimumPersonAttributes> {
    override fun selectFor(
        person: SurveyPerson<MinimumPersonAttributes>,
    ): GenerateActivitySchedule<MinimumHouseholdAttributes, MinimumPersonAttributes>? =
        when (AgeGroup.fromAge(person.age)) {
            AgeGroup.ZERO_TO_TWO -> zeroToTwoYears
            AgeGroup.THREE_TO_FIVE -> threeToFiveYears
            AgeGroup.SIX_TO_NINE -> sixToNineYears
            null -> null
        }
}
