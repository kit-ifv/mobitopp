package domain.shared.enums

import domain.shared.behavior.ChoiceModelPurposes
import utils.Decodable

/**
 * The default activity encoding from legacy mobiTopp
 */
@Deprecated("Avoid using Legacy whenever possible")
enum class LegacyActivityType(override val code: Int) : ActivityType {
    WORK(1),
    BUSINESS(2),
    EDUCATION(3),
    SHOPPING(4),
    LEISURE(5),
    SERVICE(6),
    HOME(7),
    UNDEFINED(8),
    OTHERHOME(9),

    PRIVATE_BUSINESS(11),
    PRIVATE_VISIT(12),
    BUSINESS_OUT(21),
    BUSINESS_TO_WORK(22),
    DELIVER_PARCEL(23),
    BUSINESS_TRAVEL(24),
    SHOPPING_DAILY(41),
    SHOPPING_OTHER(42),
    LEISURE_INDOOR(51),
    LEISURE_OUTDOOR(52),
    LEISURE_OTHER(53),
    LEISURE_SIGHTSEEING(54),
    LEISURE_TRAVEL(55),
    PICK_UP_PARCEL(71),
    LEISURE_WALK(77),

    EDUCATION_PRIMARY(31),
    EDUCATION_SECONDARY(32),
    EDUCATION_TERTIARY(33),
    EDUCATION_OCCUP(34)
    ;

    override val description: String
        get() = this.name

    companion object : Decodable<ActivityType> {
        override fun decode(i: Int) = entries.first { it.code == i }
        override fun decode(s: String) = valueOf(s)
        override fun values(): Set<LegacyActivityType> = LegacyActivityType.entries.toSet()
    }
}
@Deprecated("Avoid legacy references at all cost, only use this when you know what you are doing")
val legacyChoiceModelPurposes = ChoiceModelPurposes(
    home = LegacyActivityType.HOME,
    work = LegacyActivityType.WORK,
    business = LegacyActivityType.BUSINESS,
    shopping = LegacyActivityType.SHOPPING,
    privateBusiness = LegacyActivityType.PRIVATE_BUSINESS,
    service = LegacyActivityType.SERVICE,
    privateVisit = LegacyActivityType.PRIVATE_VISIT,
    leisureTravel = LegacyActivityType.LEISURE_TRAVEL,
    businessTravel = LegacyActivityType.BUSINESS_TRAVEL,
    education = LegacyActivityType.EDUCATION,
    leisure = LegacyActivityType.LEISURE,
    undefined = LegacyActivityType.UNDEFINED,

    allActivityTypes = LegacyActivityType.entries.toSet(),
    typesWithAttractivity = setOf(
        LegacyActivityType.BUSINESS,
        LegacyActivityType.LEISURE_INDOOR,
        LegacyActivityType.LEISURE_OUTDOOR,
        LegacyActivityType.PRIVATE_BUSINESS,
        LegacyActivityType.PRIVATE_VISIT,
        LegacyActivityType.SERVICE,
        LegacyActivityType.SHOPPING_DAILY,
        LegacyActivityType.SHOPPING_OTHER,
        LegacyActivityType.EDUCATION_PRIMARY,
        LegacyActivityType.EDUCATION_SECONDARY,
        LegacyActivityType.EDUCATION_TERTIARY
    ),
    leisureTypes = setOf(
        LegacyActivityType.LEISURE,
        LegacyActivityType.LEISURE_INDOOR,
        LegacyActivityType.LEISURE_OUTDOOR,
        LegacyActivityType.LEISURE_OTHER,
        LegacyActivityType.LEISURE_WALK,
        LegacyActivityType.LEISURE_SIGHTSEEING,
        LegacyActivityType.PRIVATE_VISIT,
    ),
    educationTypes = setOf(
        LegacyActivityType.EDUCATION,
        LegacyActivityType.EDUCATION_PRIMARY,
        LegacyActivityType.EDUCATION_SECONDARY,
        LegacyActivityType.EDUCATION_TERTIARY,
        LegacyActivityType.EDUCATION_OCCUP
    ),
    shoppingTypes = setOf(
        LegacyActivityType.SHOPPING,
        LegacyActivityType.SHOPPING_DAILY,
        LegacyActivityType.SHOPPING_OTHER,
        LegacyActivityType.PRIVATE_BUSINESS,
    ),
    businessTypes = setOf(
        LegacyActivityType.BUSINESS,
        LegacyActivityType.BUSINESS_TRAVEL,
        LegacyActivityType.BUSINESS_OUT,
        LegacyActivityType.BUSINESS_TO_WORK,
    )
)
