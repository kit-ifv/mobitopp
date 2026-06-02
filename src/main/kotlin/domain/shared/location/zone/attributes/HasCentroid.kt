package domain.shared.location.zone.attributes

import domain.shared.location.zone.Zone
import org.locationtech.jts.geom.Point

interface HasCentroid {
    val centroid: Point
}

val Zone<HasCentroid>.centroid: Point get() = attributes.centroid
