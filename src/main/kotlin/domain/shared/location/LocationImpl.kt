package domain.shared.location

import org.locationtech.jts.geom.Point

data class LocationImpl(override val position: Point) : Location