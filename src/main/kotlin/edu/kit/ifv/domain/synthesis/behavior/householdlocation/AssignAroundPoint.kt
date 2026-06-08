package edu.kit.ifv.domain.synthesis.behavior.householdlocation
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.StandardLocationImpl
import edu.kit.ifv.domain.shared.location.road.RoadAccess
import edu.kit.ifv.domain.shared.location.zone.Zone
import edu.kit.ifv.domain.shared.location.zone.attributes.HasCentroid
import edu.kit.ifv.domain.shared.location.zone.attributes.HasRegionType
import edu.kit.ifv.units.Distance
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Point
import kotlin.random.Random

// TODO the name is confusing.
@Suppress("MagicNumber")
class AssignAroundPoint<Z, AREA : Zone<Z>, H>(
    distance: Distance,
    val random: Random = Random(42),
    private val pointExtractor: (Zone<Z>) -> Point = { it.attributes.centroid },

) : AssignHouseholdLocations<AREA, H> where Z : HasRegionType, Z : HasCentroid {
    private val scalingFactorWGS = distance.inMeters * 0.00001

    override fun generateLocation(zone: AREA, household: H): StandardLocation {
        val point = pointExtractor(zone)
        val factory = point.factory

        val newX = point.x + random.nextDouble(-scalingFactorWGS, scalingFactorWGS)
        val newY = point.y + random.nextDouble(-scalingFactorWGS, scalingFactorWGS)
        return StandardLocationImpl(factory.createPoint(Coordinate(newX, newY)), zone, RoadAccess.Companion.INVALID)
    }
}
