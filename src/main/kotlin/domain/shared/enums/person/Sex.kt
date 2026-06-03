package domain.shared.enums.person

import utils.Encodable
import utils.EnumDecodable

/**
 * An enum for the sex of a person. As this class is only applicable to a person the enum resides in the same source
 * code file as the person. (Refactor Idea maybe make an inner class)
 */
enum class Sex(override val code: Int) : Encodable {
    MALE(1),
    FEMALE(2),
    UNKNOWN(9),
    ;

    fun isFemale(): Boolean = this == FEMALE

    fun isMale(): Boolean = this == MALE

    override val description: String = name

    companion object : EnumDecodable<Sex>(Sex::class)
}
