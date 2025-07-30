package domain.synthesis.data

import Mutable
import domain.shared.location.Location
import kotlinx.serialization.Serializable
import units.Currency
import utils.Encodable
import utils.EnumDecodable
import utils.Identifiable
import utils.random.StochasticActor
import kotlin.random.Random

@Serializable
@JvmInline
value class HouseholdId(val value: Long) {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    fun compareTo(other: HouseholdId): Int {
        return value.compareTo(other.value)
    }

    /**
     * Robin: I added a method to iterate over ids, I want to use this feature for generating autoincrementing ids
     * in the test cases
     *
     * @return the next higher id.
     */
    fun next(): HouseholdId {
        return HouseholdId(value + 1)
    }
}

interface IHousehold : Identifiable<HouseholdId>, StochasticActor {
    val householdNumber: Long
    val surveyYear: Int
    val location: Location
    val domCode: Int
    val type: Int
    val incomePerMonth: Currency
    val economicStatus: EconomicStatus
    val members: Set<IPerson>
    val cars: Set<IPrivateCar>
}

@Mutable
abstract class Household(
    override val id: HouseholdId,
    seed: Long,
) : IHousehold {

    final override val random: Random by lazy { Random(id.value + seed) }

    abstract override val members: Set<Person>
    abstract override val cars: Set<PrivateCar>
}

/**
 * The economic status as taken from the original mobiTopp codebase
 */
enum class EconomicStatus(override val code: Int) : Encodable {
    VERY_LOW(1),
    LOW(2),
    MIDDLE(3),
    HIGH(4),
    VERY_HIGH(5);

    override val description: String = name

    companion object : EnumDecodable<EconomicStatus>(EconomicStatus::class)
}

enum class HouseholdType(override val code: Int) : Encodable {

    SINGLE_HH_WITH_CHILDREN(1),
    SINGLE_HH(2),
    COUPLE_WITH_CHILDREN(3),
    COUPLE_WITHOUT_CHILDREN(4),
    OTHER_MULTI_PERSON_HH(5);

    override val description: String = name

    companion object : EnumDecodable<HouseholdType>(HouseholdType::class)
}
