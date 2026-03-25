package domain.synthesis.behavior

import domain.synthesis.SynthesisHousehold
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes

object HouseholdFactory  {

    fun <S : MinimumHouseholdAttributes, T : MinimumPersonAttributes> createFrom(input: ISurveyHousehold<S, T>): SynthesisHousehold<S, T> {

        val household = SynthesisHousehold<S, T>(
            surveyHouseholdId = input.surveyHouseholdId,
            attributes = input.attributes,

            )
        household.addMembers(input.members)
        return household

    }
}