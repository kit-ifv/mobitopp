package domain.synthesis.attributes.person

import domain.synthesis.data.Employment
import domain.synthesis.data.Sex
import edu.kit.ifv.units.Distance

interface MaximumPersonAttributes:  HasBicycle, HasCommuteDistance, HasEducationDistance, HasLicence, HasMutableEmployment, MinimumPersonAttributes {
    override val age: Int
    override val sex: Sex
    override val distanceWork: Distance
    override val distanceEducation: Distance
    override var employment: Employment
//    val birthYear: Int
//    val personNumber: Int
    override val hasBicycle: Boolean
    override val hasLicence: Boolean
    }


