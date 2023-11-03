package data

import utils.units.Currency

/**
 * The minimal viable information about a household in the simulation.
 *  @property residents A household contains a collection of agents currently residing in said household
 *  @property cars A household has a set of cars.
 *  @property location A household will have a fixed location somewhere in the simulation world.
 *  @property incomePerMonth The household income as required by some utility functions
 *  @property economicStatus The economic status grouping (Might be derived from income)
 *
 */
interface Household {
    val residents: Collection<Person>
    val cars: Collection<Car>
    val location: Location
    val incomePerMonth: Currency
    val economicStatus: EconomicStatus

    /**
     *  Below are derived properties that can be calculated from existing properties. If the performance hit is large
     *  an implementing class should provide backing fields.
     */
    val numberOfCars: Int
        get() = cars.size
    @Suppress("MagicNumber")
    val numberOfMinors: Int
        get() = residents.filter { it.age < 18 }.size
    val numberOfResidents: Int
        get() = residents.size

}

val DEFAULT_ECONOMIC_PARSER = Decodable { i -> EconomicStatus.values().first {it.code == i} }
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

}
