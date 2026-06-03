package domain.shared.location.road

import domain.shared.location.Location

fun interface VisumLinkIdLocator {
    fun linkIdFor(location: Location<*>): Long
}