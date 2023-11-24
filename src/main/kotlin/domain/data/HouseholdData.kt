package domain.data

import Buildable
import Decodable
import Encodable
import Identifiable
import utils.units.Currency

/**
 * The minimal viable information about a household in the simulation.
 *  @property location A household will have a fixed location somewhere in the simulation world.
 *  @property incomePerMonth The household income as required by some utility functions
 *  @property economicStatus The economic status grouping (Might be derived from income)
 *
 */
@Buildable
interface HouseholdData: Identifiable<HouseholdData> {
    val location: Location
    val domCode: Int
    val type: Int
    val incomePerMonth: Currency
    val economicStatus: EconomicStatus
}

/**
 * The economic status as taken from the original mobiTopp codebase
 */
enum class EconomicStatus(val code: Int): Encodable {
    VERY_LOW(1),
    LOW(2),
    MIDDLE(3),
    HIGH(4),
    VERY_HIGH(5);

    override fun encode() = this.code

    companion object : Decodable<EconomicStatus> {
        override fun decode(i: Int) = EconomicStatus.values().first {it.code == i}
    }
}

enum class HouseholdType(private val code: Int) : Encodable {

    SINGLE_HH_WITH_CHILDREN(1),
    SINGLE_HH(2),
    COUPLE_WITH_CHILDREN(3),
    COUPLE_WITHOUT_CHILDREN(4),
    OTHER_MULTI_PERSON_HH(5);

    override fun encode() = this.code

    companion object : Decodable<HouseholdType> {
        override fun decode(i: Int) = HouseholdType.values().first {it.code == i}
    }
}
