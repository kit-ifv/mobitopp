package domain.simulation.data.household

import domain.shared.enums.household.HouseholdType
import domain.synthesis.behavior.ISurveyHousehold

class StandardHouseholdTypeDetector : HouseholdTypeDetector {
    @Suppress("ReturnCount", "MagicNumber")
    override fun detectHouseholdType(household: ISurveyHousehold<*, *>): HouseholdType {
        if (household.size == 1) return HouseholdType.SINGLE_HH

        val (adults, children) = household.members.partition { it.age > 18 }
        if (adults.size == 1 && children.isNotEmpty()) return HouseholdType.SINGLE_HH_WITH_CHILDREN
        if (adults.size == 2 && adults[0].sex != adults[1].sex) {
            if (children.isEmpty()) return HouseholdType.COUPLE_WITHOUT_CHILDREN
            return HouseholdType.COUPLE_WITH_CHILDREN
        }
        return HouseholdType.OTHER_MULTI_PERSON_HH
    }
}
