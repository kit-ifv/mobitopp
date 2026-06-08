package edu.kit.ifv.domain.synthesis.behavior
import edu.kit.ifv.domain.shared.enums.household.EconomicStatus
import edu.kit.ifv.domain.synthesis.attributes.household.MinimumHouseholdAttributes
import edu.kit.ifv.domain.synthesis.attributes.person.MinimumPersonAttributes

class SurveyHousehold<out HouseholdInfo : MinimumHouseholdAttributes, out PersonInfo : MinimumPersonAttributes>(
    override val surveyHouseholdId: Long,

    override val members: List<SurveyPerson<PersonInfo>>,

    override val attributes: HouseholdInfo,
) : ISurveyHousehold<HouseholdInfo, PersonInfo> {
    lateinit var economicStatus: EconomicStatus

    override fun toString(): String = "Survey Household($surveyHouseholdId) [${members.joinToString { it.toString() }}"
}
