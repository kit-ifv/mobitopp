package edu.kit.ifv.domain.synthesis.attributes.person
import edu.kit.ifv.domain.shared.enums.person.Employment
import edu.kit.ifv.domain.shared.enums.person.Sex
import edu.kit.ifv.units.Distance

/**
 * Defines the most complete set of person attributes currently required by the core framework.
 *
 * A person attribute class that implements this interface provides all attributes needed by the
 * prefabricated implementations in this code base. In other words, if a project-specific person
 * attribute type fulfills this interface, it can be used with every existing core-framework
 * implementation that depends on person attributes.
 *
 * When new core-framework functionality requires additional person attributes, this interface
 * should be updated accordingly so that it continues to represent the full attribute set expected
 * by the framework.
 */
interface MaximumPersonAttributes :
    HasBicycle,
    HasCommuteDistance,
    HasEducationDistance,
    HasLicence,
    HasMutableEmployment,
    HasMutableTransitPass,
    MinimumPersonAttributes {
    override val age: Int
    override val sex: Sex
    override val distanceWork: Distance
    override val distanceEducation: Distance
    override var employment: Employment
    override val hasBicycle: Boolean
    override val hasLicence: Boolean
    override var hasTransitPass: Boolean
    fun copy(): MaximumPersonAttributes = MaximumPersonAttributesImpl(
        age = this.age,
        sex = this.sex,
        distanceWork = this.distanceWork,
        distanceEducation = this.distanceEducation,
        employment = this.employment,
        birthYear = 0,
        personNumber = -1,
        hasBicycle = this.hasBicycle,
        hasLicence = this.hasLicence,
        hasTransitPass = this.hasTransitPass,
    )
}
