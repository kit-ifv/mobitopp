package domain.shared.location.jts

import edu.kit.ifv.JTSConverter
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.meters
import edu.kit.ifv.units.toDistance
import org.geotools.api.referencing.cs.CoordinateSystem
import org.geotools.referencing.GeodeticCalculator
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.operation.distance.DistanceOp

object JTSDistanceCalculator {

    private val crsLookup: MutableMap<Int, DistanceUnit?> = mutableMapOf()

    private fun isProjectable(srid: Int): DistanceUnit? {
        val crs = JTSConverter.epsg(srid)

        val axisUnits = crs.coordinateSystem.axisUnits()
        if (axisUnits.size == 1) {
            return when (axisUnits.first()) {
                "Metre" -> DistanceUnit.METERS
                "Degree Angle" -> null
                else -> null.also { println("Cannot decode axis Unit [${axisUnits.first()}]") }
            }
        }
        return null
    }

    private val geodeticCalculator by lazy {
        GeodeticCalculator()
    }

    operator fun get(srid: Int) = crsLookup.getOrPut(srid) { isProjectable(srid) }

    @Suppress("MagicNumber")
    fun distance(geom1: Geometry, geom2: Geometry): Distance {
        val distanceUnit = get(geom1.srid)
        if (geom1.srid == geom2.srid && distanceUnit != null) {
            return geom1.distance(geom2).toDistance(distanceUnit)
        }

        val wgsGeom1 = JTSConverter.convertGeometry(geom1, 4326)
        val wgsGeom2 = JTSConverter.convertGeometry(geom2, 4326)

        val distanceOp = DistanceOp(wgsGeom1, wgsGeom2)
        val (p1, p2) = distanceOp.nearestPoints()

        geodeticCalculator.setStartingGeographicPoint(p1.x, p1.y)
        geodeticCalculator.setDestinationGeographicPoint(p2.x, p2.y)

        return geodeticCalculator.orthodromicDistance.meters
    }

    private fun CoordinateSystem.axisUnits(): Set<String> =
        (0 until dimension).map { this.getAxis(it).unit.name }.toSet()
}
