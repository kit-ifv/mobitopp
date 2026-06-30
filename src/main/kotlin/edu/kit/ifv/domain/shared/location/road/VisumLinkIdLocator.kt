package edu.kit.ifv.domain.shared.location.road
import edu.kit.ifv.domain.shared.location.Location

fun interface VisumLinkIdLocator {
    fun linkIdFor(location: Location<*>): Long
}
