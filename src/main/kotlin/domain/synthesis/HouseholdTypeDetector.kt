package domain.synthesis

import domain.shared.enums.household.HouseholdType
import domain.synthesis.behavior.ISurveyHousehold

interface HouseholdTypeDetector {
    fun detectHouseholdType(household: ISurveyHousehold<*, *>): HouseholdType
}
