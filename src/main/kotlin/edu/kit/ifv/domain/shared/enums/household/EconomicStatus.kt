package edu.kit.ifv.domain.shared.enums.household
import edu.kit.ifv.utils.codes.Encodable
import edu.kit.ifv.utils.codes.EnumDecodable

/**
 * The economic status as taken from the original mobiTopp codebase
 */
enum class EconomicStatus(override val code: Int) : Encodable {
    VERY_LOW(1),
    LOW(2),
    MIDDLE(3),
    HIGH(4),
    VERY_HIGH(5),
    ;

    override val description: String = name

    companion object : EnumDecodable<EconomicStatus>(EconomicStatus::class)
}
