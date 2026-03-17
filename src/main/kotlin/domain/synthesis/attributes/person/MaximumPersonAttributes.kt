package domain.synthesis.attributes.person

import domain.synthesis.data.Employment
import domain.synthesis.data.Sex
import edu.kit.ifv.units.Distance

data class MaximumPersonAttributes(
    override val age: Int,
    override val sex: Sex,
    override val distanceWork: Distance,
    override val distanceEducation: Distance,
    override val employment: Employment,
    val birthYear: Int,
    val personNumber: Int,
    val hasBicycle: Boolean,
    override val hasLicence: Boolean,

    ):  HasCommuteDistance, HasEducationDistance, HasLicence, HasEmployment, MinimumPersonAttributes