package domain.synthesis

import domain.synthesis.attributes.person.HasAge
import domain.synthesis.attributes.person.HasBiologicalSex
import domain.synthesis.behavior.MinimalistHousehold
import domain.synthesis.data.HouseholdType

class StandardHouseholdTypeDetector<P> : HouseholdTypeDetector<P> where P : HasAge, P : HasBiologicalSex {
    override fun detectHouseholdType(household: MinimalistHousehold<*, P>): HouseholdType {
        if(household.size == 1) return HouseholdType.SINGLE_HH

        val (adults, children) = household.members.partition { it.attributes.age > 18 }
        if(adults.size == 1 && children.isNotEmpty()) return HouseholdType.SINGLE_HH_WITH_CHILDREN
        if(adults.size == 2 && adults[0].attributes.sex != adults[1].attributes.sex) {
            if(children.isEmpty()) return HouseholdType.COUPLE_WITHOUT_CHILDREN
            return HouseholdType.COUPLE_WITH_CHILDREN
        }
        return HouseholdType.OTHER_MULTI_PERSON_HH

    }

}