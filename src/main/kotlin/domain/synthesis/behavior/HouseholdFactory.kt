package domain.synthesis.behavior

import domain.synthesis.SynthesisHousehold
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes

class HouseholdFactory<S : MinimumHouseholdAttributes, T : MinimumPersonAttributes>(
    private val householdAttributeConstructor: (S) -> S,
    private val personAttributeConstructor: (T) -> T,
) {

    fun createFrom(input: ISurveyHousehold<S, T>): SynthesisHousehold<S, T> {
        val household = SynthesisHousehold<S, T>(
            surveyHouseholdId = input.surveyHouseholdId,
            attributes = householdAttributeConstructor(input.attributes),

        )
        household.addMembers(input.members, personAttributeConstructor)
        return household
    }
}
