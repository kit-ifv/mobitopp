package domain.shared.enums.person

import utils.codes.Encodable
import utils.codes.EnumDecodable

enum class Graduation(override val code: Int) : Encodable {
    // TODO split into school and higher education
    UNDEFINED(-1),
    OTHER(0),
    NOT_HIGH_SCHOOL(1),
    HIGH_SCHOOL_GRADUATE(2),
    SOME_COLLEGE_CREDIT_NO_DEGREE(3),
    ASSOCIATE_TECHNICAL_SCHOOL_DEGREE(4),
    BACHELOR_DEGREE(5),
    MASTER_DEGREE(6),
    ;

    override val description: String = name

    companion object : EnumDecodable<Graduation>(Graduation::class)
}
