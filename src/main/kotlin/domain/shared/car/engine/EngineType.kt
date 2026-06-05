package domain.shared.car.engine

import utils.codes.Encodable
import utils.codes.EnumDecodable
private const val CONV = "conventional"
private const val BEV = "bev"
private const val EREV = "erev"

enum class EngineType(override val code: Int) : Encodable {
    COMBUSTION(1) {
        override val asText: String = CONV
    },
    ELECTRIC(2) {
        override val asText: String = BEV
    },
    HYBRID(3) {
        override val asText: String = EREV
    }, ;

    override val description: String = name
    abstract val asText: String
    companion object : EnumDecodable<EngineType>(EngineType::class) {

        fun parseEngineType(string: String): EngineType = when (string) {
            CONV -> COMBUSTION

            BEV -> ELECTRIC

            EREV -> HYBRID

            else -> throw IllegalArgumentException(
                "Cannot parse string $string to EngineType: expected 'conventional', 'bev' or 'erev'!",
            )
        }
    }
}
