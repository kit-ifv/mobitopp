package data

import units.Currency
import domain.enums.Mode
import domain.location.Location

/**
 * The generic interface for determining travel cost. The cost is dependent on the mode.
 */
fun interface TravelCost {
    fun calculate(from: Location, to: Location, using: Mode): Currency
}

