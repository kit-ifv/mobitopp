package domain.synthesis.behavior

import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.data.EconomicStatus

class SurveyHousehold<out HouseholdInfo : MinimumHouseholdAttributes, out PersonInfo : MinimumPersonAttributes>(
    override val surveyHouseholdId: Long,

    override val members: List<SurveyPerson<PersonInfo>>,

    override val attributes: HouseholdInfo,
) : ISurveyHousehold<HouseholdInfo, PersonInfo> {
    lateinit var economicStatus: EconomicStatus

    override fun toString(): String = "Survey Household($surveyHouseholdId) [${members.joinToString { it.toString() }}"
}
