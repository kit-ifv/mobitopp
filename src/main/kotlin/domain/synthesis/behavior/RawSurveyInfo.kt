package domain.synthesis.behavior

import domain.synthesis.data.household.HouseholdType
import domain.synthesis.data.person.Employment
import domain.synthesis.data.person.Sex
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance

/**
 * All the information from the survey file, including all irrelevant information
 */
data class RawSurveyInfo(
    val householdId: Long,
    val year: Int,
    val areaType: Int, // TODO what is this?
    val householdSize: Int, // TODO remove?. If I determine household size over the household object, this is useless
    val personNumber: Int,
    val sex: Sex,
    val birthyear: Int,
    val employment: Employment,
    val hasCommuterTicket: Boolean,
    val householdIncome: Currency,
    val householdIncomeClass: Int, // TODO what is this? it is in a range between 0-8 ???
    val typeCode: Int,
    val cars: Int,
    val hasBicycle: Boolean,
    val hasLicence: Boolean,
    val distanceWork: Distance,
    val distanceEducation: Distance,
) {
    val age = year - birthyear

    val type: HouseholdType = HouseholdType.Companion.decodeOrNull(typeCode) ?: HouseholdType.UNDEFINED
}
