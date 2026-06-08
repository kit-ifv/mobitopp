package edu.kit.ifv.domain.synthesis.attributes.person
import edu.kit.ifv.domain.shared.enums.person.Employment

/**
 * If the survey data has information about the employment status of the survey person, this interface should be added
 * to the class holding the information block
 */

interface HasEmployment {
    val employment: Employment
}

interface HasMutableEmployment : HasEmployment {
    override var employment: Employment
}
