package edu.kit.ifv.domain.shared.location.zone.attributes
import edu.kit.ifv.domain.shared.location.zone.Zone
import org.locationtech.jts.geom.Point

interface HasCentroid {
    val centroid: Point
}

val Zone<HasCentroid>.centroid: Point get() = attributes.centroid
