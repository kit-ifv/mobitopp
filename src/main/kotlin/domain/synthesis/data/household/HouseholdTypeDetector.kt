package domain.synthesis.data.household

import domain.synthesis.behavior.ISurveyHousehold

interface HouseholdTypeDetector {
    fun detectHouseholdType(household: ISurveyHousehold<*, *>): HouseholdType
}
