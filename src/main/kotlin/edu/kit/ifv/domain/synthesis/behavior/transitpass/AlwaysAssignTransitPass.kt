package edu.kit.ifv.domain.synthesis.behavior.transitpass
import edu.kit.ifv.domain.synthesis.SynthesisHousehold
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes
import edu.kit.ifv.domain.synthesis.behavior.ISurveyHousehold
import edu.kit.ifv.domain.synthesis.behavior.SurveyPerson

@Suppress("SpacingAroundColon")
object AlwaysAssignTransitPass : AssignTransitCardOwnership<MinimumHouseholdAttributes, MinimumPersonAttributes> {

    context(household: ISurveyHousehold<MinimumHouseholdAttributes, MinimumPersonAttributes>)
    override fun assignForPerson(person: SurveyPerson< MinimumPersonAttributes>): Boolean = true
}
