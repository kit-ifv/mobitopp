package domain.shared.enums.person

import utils.codes.Encodable
import utils.codes.EnumDecodable

/**
 * An enum for the employment of a person. As this class is only applicable to a person the enum resides in the same
 * source code file as the person. (Refactor Idea maybe make an inner class)
 */
enum class Employment(override val code: Int) : Encodable {
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
    NONE(9),
    ;

    override val description: String = name

    fun isStudent() = this.code in studentCodes

    companion object : EnumDecodable<Employment>(Employment::class) {
        private val studentCodes = setOf(4, 40, 41, 42)
    }
}
