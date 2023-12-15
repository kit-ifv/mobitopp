package domain.location

import domain.enums.Mode
import domain.location.Location
import utils.units.Currency

/**
 * The generic interface for determining travel cost. The cost is dependent on the mode.
 */
fun interface TravelCost {
    fun calculate(from: Location, to: Location, using: Mode): Currency
}

