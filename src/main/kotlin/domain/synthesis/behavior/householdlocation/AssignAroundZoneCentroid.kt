package domain.synthesis.behavior.householdlocation

import domain.shared.location.BetterLocation
import domain.shared.location.RoadAccess
import domain.shared.location.StandardLocation
import domain.shared.location.attributes.HasRegionType
import domain.shared.location.zone.GeometricZone
import domain.shared.location.zone.HasCentroid
import domain.shared.location.zone.StandardZone
import domain.shared.location.zone.ZoneWithCentroid
import edu.kit.ifv.units.Distance
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Point
import kotlin.random.Random

@Deprecated("Use AssignAroundPoint instead")
@Suppress("UnusedPrivateProperty")
class AssignAroundZoneCentroid<H>(private val radius: Distance) : AssignHouseholdLocations<StandardZone, H> {
    override fun generateLocation(zone: StandardZone, household: H): StandardLocation =
        BetterLocation(zone.centroid, zone, RoadAccess.INVALID)
}

class AssignRandomLocation<AREA : GeometricZone<HasRegionType>, H> : AssignHouseholdLocations<AREA, H> {
    override fun generateLocation(zone: AREA, household: H): StandardLocation =
        BetterLocation(zone.randomPoint(), zone, RoadAccess.INVALID)
}
// TODO the name is confusing.
class AssignAroundPoint<T, AREA : ZoneWithCentroid<T>, H>(distance: Distance,
    private val pointExtractor: (ZoneWithCentroid<T>) -> Point = {it.centroid}) :
    AssignHouseholdLocations<AREA, H> where T : HasRegionType {
    private val scalingFactorWGS = distance.inMeters * 0.00001
    val random: Random = Random(42)
    override fun generateLocation(zone: AREA, household: H): StandardLocation {
        val point = pointExtractor(zone)
        val factory = point.factory

        val newX = point.x + random.nextDouble(-scalingFactorWGS, scalingFactorWGS)
        val newY = point.y + random.nextDouble(-scalingFactorWGS, scalingFactorWGS)
        return BetterLocation(factory.createPoint(Coordinate(newX, newY)), zone, RoadAccess.INVALID)
    }
}
