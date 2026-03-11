package domain.shared.location

import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.meters
import edu.kit.ifv.units.toDistance
import org.geotools.api.referencing.crs.ProjectedCRS
import org.geotools.referencing.CRS
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import transformTo

class JTSDistanceCalculatorTest {
    @Test
    fun evaluateCoordinateSystem() {
        val crs = CRS.decode("EPSG:25832")
        val good = crs is ProjectedCRS
        val out = crs.coordinateSystem
        println(out)
    }
    @Test
    fun testProperDistanceCalculation() {
        val gf = GeometryFactory(PrecisionModel(), 4326)
        val p1 = gf.createPoint(Coordinate(20.0, .0))
        val p2 = gf.createPoint(Coordinate(21.0, .0))

        val distance = JTSDistanceCalculator.distance(p1, p2)
        assertEquals(distance, 60.toDistance(DistanceUnit.SEA_MILE))

    }

    @Test
    fun testProperUTMDistanceCalculation() {
        val gf = GeometryFactory(PrecisionModel(), 25832)
        val p1 = gf.createPoint(Coordinate(570168.862, 5650300.787))

        val p2 = GeometryFactory(PrecisionModel(), 4326).createPoint(Coordinate(10.0, 51.0))

        val distance = JTSDistanceCalculator.distance(p1, p2)
        assertTrue(distance <= 1.meters)

    }
}