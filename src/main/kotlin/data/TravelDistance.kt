package data

import units.Distance

/**
 * The generic interface for determining travel distance. It might appear that requiring a mode seems obsolete. However,
 * specific implementations might have different distances depending on the chosen mode. (Such as PT having a different
 * network than Car traffic).
 */
fun interface TravelDistance {
    fun calculate(from: Location, to: Location, using: Mode): Distance
}
