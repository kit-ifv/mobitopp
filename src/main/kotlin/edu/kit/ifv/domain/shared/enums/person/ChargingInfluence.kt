package edu.kit.ifv.domain.shared.enums.person
import edu.kit.ifv.utils.codes.Encodable
import edu.kit.ifv.utils.codes.EnumDecodable

enum class ChargingInfluence(override val code: Int) : Encodable {
    ALWAYS(0),
    ONLY_WHEN_BATTERY_LOW(1),
    NEVER(2),
    ;

    override val description: String = name

    companion object : EnumDecodable<ChargingInfluence>(ChargingInfluence::class)
}
