package domain.shared.location.zone

import org.locationtech.jts.geom.Point

interface HasCentroid {
    val centroid: Point
}

val Zone<HasCentroid>.centroid: Point get() = attributes.centroid