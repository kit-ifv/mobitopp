package domain.synthesis

import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.data.household.HouseholdType

interface HouseholdTypeDetector {
    fun detectHouseholdType(household: ISurveyHousehold<*, *>): HouseholdType
}
