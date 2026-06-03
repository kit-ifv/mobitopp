package domain.synthesis.behavior.transitpass

import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.SurveyPerson

@Suppress("SpacingAroundColon")
object AlwaysAssignTransitPass : AssignTransitCardOwnership<MinimumHouseholdAttributes, MinimumPersonAttributes> {

    context(household: ISurveyHousehold<MinimumHouseholdAttributes, MinimumPersonAttributes>)
    override fun assignForPerson(person: SurveyPerson<MinimumPersonAttributes>): Boolean = true
}
