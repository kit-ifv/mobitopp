package edu.kit.ifv.domain.shared.location
import org.locationtech.jts.geom.Point

fun interface PointParser {
    fun parsePoint(string: String): Point
}
