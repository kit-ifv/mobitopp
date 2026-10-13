package edu.kit.ifv.domain.synthesis.behavior.activitygeneration

import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.ISurveyHousehold

class OverridingHouseholdActivityGenerator<
    in S : MinimumHouseholdAttributes,
    in T : MinimumPersonAttributes,
    >(
    private val fallback: GenerateHouseholdActivitySchedule<S, T>,
    private val selector: ActivityGeneratorSelector<S, T>,
) : GenerateHouseholdActivitySchedule<S, T> {

    override fun generate(household: ISurveyHousehold<S, T>): List<PreliminaryActivitySchedule> {
        val originalSchedules = fallback.generate(household)

        require(originalSchedules.size == household.members.size) {
            "Expected one activity schedule per household member"
        }

        return household.members.zip(originalSchedules).map { (person, original) ->
            selector.selectFor(person)?.generate(person) ?: original
        }
    }
}

fun <S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> GenerateHouseholdActivitySchedule<S, T>.withOverride(
    selector: ActivityGeneratorSelector<S, T>,
): GenerateHouseholdActivitySchedule<S, T> = OverridingHouseholdActivityGenerator(this, selector)
