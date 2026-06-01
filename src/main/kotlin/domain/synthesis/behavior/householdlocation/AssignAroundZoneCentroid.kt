package domain.synthesis.behavior.householdlocation

import domain.shared.location.BetterLocation
import domain.shared.location.RoadAccess
import domain.shared.location.StandardLocation
import domain.shared.location.attributes.HasRegionType
import domain.shared.location.zone.GeometricZone
import domain.shared.location.zone.HasCentroid
import domain.shared.location.zone.StandardZone
import domain.shared.location.zone.Zone
import edu.kit.ifv.units.Distance
import org.locationtech.jts.geom.Coordinate
import kotlin.random.Random

@Suppress("UnusedPrivateProperty")
class AssignAroundZoneCentroid<H>(private val radius: Distance) : AssignHouseholdLocations<StandardZone, H> {
    override fun generateLocation(zone: StandardZone, household: H): StandardLocation =
        BetterLocation(zone.centroid, zone, RoadAccess.INVALID)
}

class AssignRandomLocation<AREA : GeometricZone<HasRegionType>, H> : AssignHouseholdLocations<AREA, H> {
    override fun generateLocation(zone: AREA, household: H): StandardLocation =
        BetterLocation(zone.randomPoint(), zone, RoadAccess.INVALID)
}

class AssignAroundPoint<T, AREA : Zone<T>, H>(distance: Distance) :
    AssignHouseholdLocations<AREA, H> where T : HasCentroid, T : HasRegionType {
    private val scalingFactorWGS = distance.inMeters * 0.00001
    val random: Random = Random(42)
    override fun generateLocation(zone: AREA, household: H): StandardLocation {
        val centroid = zone.attributes.centroid
        val factory = centroid.factory

        val newX = centroid.x + random.nextDouble(-scalingFactorWGS, scalingFactorWGS)
        val newY = centroid.y + random.nextDouble(-scalingFactorWGS, scalingFactorWGS)
        return BetterLocation(factory.createPoint(Coordinate(newX, newY)), zone, RoadAccess.INVALID)
    }
}
