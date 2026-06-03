package domain.synthesis.data.person

import utils.codes.Encodable
import utils.codes.EnumDecodable

enum class ChargingInfluence(override val code: Int) : Encodable {
    ALWAYS(0),
    ONLY_WHEN_BATTERY_LOW(1),
    NEVER(2),
    ;

    override val description: String = name

    companion object : EnumDecodable<ChargingInfluence>(ChargingInfluence::class)
}
