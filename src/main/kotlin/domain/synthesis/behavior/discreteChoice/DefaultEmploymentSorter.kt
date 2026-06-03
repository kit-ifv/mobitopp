package domain.synthesis.behavior.discreteChoice

import domain.synthesis.data.person.Employment

/**
 * The standard instantiation for an [EmploymentSorter]. Note that there is a keyword "object" instead of "class".
 * This makes the [DefaultEmploymentSorter] a singleton. The benefit is that you can reference it writing DefaultEmploymentSorter
 * instead of DefaultEmploymentSorter() (no brackets). In this particular scenario an "object" instantiation is adequate as there
 * is no requirement for different object state (actually the DefaultEmploymentSorter has no interactable state at all)
 *
 * The logic behaves as follows:
 * [domain.synthesis.data.person.Employment.PARTTIME] and [domain.synthesis.data.person.Employment.FULLTIME] are considered "Working" Employments.
 * [domain.synthesis.data.person.Employment.STUDENT_TERTIARY] is considered a university student.
 * [domain.synthesis.data.person.Employment.RETIRED] is considered retired
 * [domain.synthesis.data.person.Employment.UNEMPLOYED] is considered unemployed.
 * All other employments fall in neither of these categories.
 */
object DefaultEmploymentSorter : EmploymentSorter {
    /**
     * For performance reasons it is smart to keep a fixed reference to the set of occupations that are considered
     * working employments.
     */
    private val workingOccupation = setOf(Employment.FULLTIME, Employment.PARTTIME)
    override fun isWorking(employment: Employment): Boolean = employment in workingOccupation

    override fun isUniversityStudent(employment: Employment): Boolean = employment == Employment.STUDENT_TERTIARY

    override fun isRetired(employment: Employment): Boolean = employment == Employment.RETIRED

    override fun isUnemployed(employment: Employment): Boolean = employment == Employment.UNEMPLOYED
}
