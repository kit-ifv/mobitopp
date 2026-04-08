package domain.synthesis

import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.data.HouseholdType

interface HouseholdTypeDetector {
    fun detectHouseholdType(household: ISurveyHousehold<*, *>): HouseholdType
}
