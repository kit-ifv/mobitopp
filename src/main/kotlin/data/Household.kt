package data

import units.Currency

/**
 * The minimal viable information about a household in the simulation.
 *  @property location A household will have a fixed location somewhere in the simulation world.
 *  @property incomePerMonth The household income as required by some utility functions
 *  @property economicStatus The economic status grouping (Might be derived from income)
 *
 */
interface Household {
    val location: Location
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

    override fun encode(): Int {
        return this.code
    }
    companion object : Decodable<EconomicStatus> {
        override fun decode(i: Int): EconomicStatus {
            return EconomicStatus.values().first {it.code == i}
        }

    }

}
