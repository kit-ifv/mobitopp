package domain.location

import domain.enums.Mode
import domain.location.Location
import kotlin.time.Duration

/**
 * The generic interface for calculating travel time. A request will always contain an origin, a destination and a mode
 */
fun interface TravelTime {

    fun calculate(from: Location, to: Location, using: Mode): Duration
}

