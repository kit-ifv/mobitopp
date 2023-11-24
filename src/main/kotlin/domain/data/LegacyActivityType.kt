package domain.data

import Decodable
import Encodable


/**
 * The default activity encoding from legacy MobiTopp
 */
enum class LegacyActivityType(val code: Int): Encodable {
    WORK      (1),
    BUSINESS  (2),
    EDUCATION (3),
    SHOPPING  (4),
    LEISURE   (5),
    SERVICE   (6),
    HOME      (7),
    UNDEFINED (8),
    OTHERHOME (9),

    PRIVATE_BUSINESS    (11),
    PRIVATE_VISIT       (12),
    BUSINESS_OUT        (21),
    BUSINESS_TO_WORK    (22),
    DELIVER_PARCEL      (23),
    BUSINESS_TRAVEL  	(24),
    SHOPPING_DAILY      (41),
    SHOPPING_OTHER      (42),
    LEISURE_INDOOR      (51),
    LEISURE_OUTDOOR     (52),
    LEISURE_OTHER       (53),
    LEISURE_SIGHTSEEING (54),
    LEISURE_TRAVEL		(55),
    PICK_UP_PARCEL      (71),
    LEISURE_WALK        (77),

    EDUCATION_PRIMARY    (31),
    EDUCATION_SECONDARY  (32),
    EDUCATION_TERTIARY   (33),
    EDUCATION_OCCUP 		 (34)
    ;

    override fun encode(): Int {
        return this.code
    }
    companion object : Decodable<LegacyActivityType> {
        override fun decode(i: Int) = LegacyActivityType.values().first {it.code == i}
    }

}

