package edu.kit.ifv.domain.shared.location
import org.locationtech.jts.geom.Point

data class LocationImpl(override val position: Point) : Location<Any?> {
    override val attributes: Any? = Unit
}
