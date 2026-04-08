package domain.synthesis

import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.data.HouseholdType

fun interface HouseholdTypeDetector<P> {
    fun  detectHouseholdType(household: MinimalistHousehold<*, P>): HouseholdType
}

