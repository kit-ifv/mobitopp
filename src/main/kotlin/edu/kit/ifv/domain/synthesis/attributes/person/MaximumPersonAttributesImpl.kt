package edu.kit.ifv.domain.synthesis.attributes.person
import edu.kit.ifv.domain.shared.enums.person.Employment
import edu.kit.ifv.domain.shared.enums.person.Sex
import edu.kit.ifv.units.Distance

data class MaximumPersonAttributesImpl(
    override val age: Int,
    override val sex: Sex,
    override val distanceWork: Distance,
    override val distanceEducation: Distance,
    override var employment: Employment,
    val birthYear: Int,
    val personNumber: Int,
    override val hasBicycle: Boolean,
    override val hasLicence: Boolean,
) : MaximumPersonAttributes
