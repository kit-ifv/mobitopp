package edu.kit.ifv.domain.synthesis.data.household
import edu.kit.ifv.domain.shared.enums.household.HouseholdType
import edu.kit.ifv.domain.synthesis.behavior.ISurveyHousehold

interface HouseholdTypeDetector {
    fun detectHouseholdType(household: ISurveyHousehold<*, *>): HouseholdType
}
