package domain.location

import domain.enums.Mode
import utils.units.Distance

/**
 * The generic interface for determining travel distance. It might appear that requiring a mode seems obsolete. However,
 * specific implementations might have different distances depending on the chosen mode. (Such as PT having a different
 * network than Car traffic).
 */
fun interface TravelDistance<in Location>  {
    fun calculate(from: Location, to: Location, using: Mode): Distance
}
/*
class DefaultTD: TravelDistance<Location> {
    override fun calculate(from: Location, to: Location, using: Mode): Distance {
        return from.distance(to)
    }
}

class OtherTD: TravelDistance<Position> {
    override fun calculate(from: Position, to: Position, using: Mode): Distance {
        return from.distance(to)
    }
}
*/
