package domain.synthesis.behavior.cars.ownership

import domain.synthesis.data.person.Employment

/**
 * This interface provides an interaction point to define different behaviours to determine whether a person is considered
 * working, student, retired or unemployed. If you ever need a different behaviour to determine what should be considered
 * either of these attributes for the utility function you can simply pass a different Employment sorter to the creation
 * of the [domain.synthesis.behavior.cars.choicemodels.CarOwnershipFactors] and your logic will be used instead. The idea behind this design decision is to avoid
 * hard coding decisions into the utility function, and rather provide the end user (You) with the option to flexibly
 * change the behaviour without needing to go through the entire code base.
 */
interface EmploymentSorter {
    /**
     * Determines whether an employment should be considered working.
     * @param employment the [Employment] to evaluate.
     */
    fun isWorking(employment: Employment): Boolean

    /**
     * Determines whether an employment should be considered being a student.
     * @param employment the [Employment] to evaluate.
     */
    fun isUniversityStudent(employment: Employment): Boolean

    /**
     * Determines whether an employment should be considered retired.
     * @param employment the [Employment] to evaluate.
     */
    fun isRetired(employment: Employment): Boolean

    /**
     * Determines whether an employment should be considered unemployed.
     * @param employment the [Employment] to evaluate.
     */
    fun isUnemployed(employment: Employment): Boolean
}
