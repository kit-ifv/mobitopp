package data

/**
 * A person in for the simulation. Certain properties can be assumed to be known during the simulation
 * @property age The age in years
 * @property gender the Gender of the person
 * @property employment the employment state
 * @property hasLicense whether the individual is allowed to operate motor vehicles (maybe refactor if different licence
 * types become interesting)
 * @property hasCommuterTicket whether a PT ticket is present
 */
interface Person {
    // These values can reasonably be expected for any Person to be present in the simulation
    val age: Int
    val gender: Gender
    val employment: Employment
    val hasLicense: Boolean
    val hasCommuterTicket: Boolean

//    fun household(): Household
}

data class DefaultPerson(
    override val age: Int = 0,
    override val gender: Gender = Gender.FEMALE,
    override val employment: Employment = Employment.INFANT,
    override val hasLicense: Boolean = false,
    override val hasCommuterTicket: Boolean = false
) : Person


/**
 * An enum for the gender of a person. As this class is only applicable to a person the enum resides in the same source
 * code file as the person. (Refactor Idea maybe make an inner class)
 */
enum class Gender(private val code: Int) :Encodable {
    MALE(1),
    FEMALE(2);

    fun isFemale(): Boolean {
        return this == FEMALE
    }

    fun isMale(): Boolean {
        return this == MALE
    }

    override fun encode(): Int {
        return this.code
    }
    companion object: Decodable<Gender> {
        override fun decode(i: Int): Gender {
            return Gender.values().first {it.code == i}
        }
    }

}

/**
 * An enum for the employment of a person. As this class is only applicable to a person the enum resides in the same
 * source code file as the person. (Refactor Idea maybe make an inner class)
 */
enum class Employment(private val code: Int): Encodable {
    UNKNOWN(-1),
    FULLTIME(1),
    PARTTIME(2),
    MARGINAL(22),
    UNEMPLOYED(3),
    STUDENT(4),
    STUDENT_PRIMARY(40),
    STUDENT_SECONDARY(41),
    STUDENT_TERTIARY(42),
    EDUCATION(5),
    HOMEKEEPER(6),
    RETIRED(7),
    INFANT(8),
    NONE(9);

    override fun encode(): Int {
        return this.code
    }

    companion object: Decodable<Employment> {
        override fun decode(i: Int): Employment {
            return Employment.values().first {it.code == i}
        }
    }

}






