package domain.synthesis.data.car

import utils.Encodable
import utils.EnumDecodable

/**
 * Car segments are a classification seen in https://en.wikipedia.org/wiki/Euro_Car_Segment. If the need arises
 * to implement a different segment encoding it is up to the developer to extract an interface and provide a different
 * encoding.
 */
enum class CarSegment(override val code: Int) : Encodable {
    SMALL(1),
    MIDSIZE(2),
    LARGE(3),
    ;

    override val description: String = name

    companion object : EnumDecodable<CarSegment>(CarSegment::class)
}
