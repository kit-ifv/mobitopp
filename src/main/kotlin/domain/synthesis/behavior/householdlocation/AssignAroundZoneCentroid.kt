package domain.synthesis.behavior.householdlocation

import domain.shared.location.BetterLocation
import domain.shared.location.RoadAccess
import domain.shared.location.StandardLocation
import domain.shared.location.attributes.HasRegionType
import domain.shared.location.zone.GeometricZone
import domain.shared.location.zone.StandardZone
import edu.kit.ifv.units.Distance

@Suppress("UnusedPrivateProperty")
class AssignAroundZoneCentroid<H>(
    private val radius: Distance
) : AssignHouseholdLocations<StandardZone, H>  {
    override fun generateLocation(zone: StandardZone, household: H): StandardLocation {
        return BetterLocation(zone.centroid, zone, RoadAccess.INVALID)
    }
}

class AssignRandomLocation<AREA: GeometricZone<HasRegionType>, H> : AssignHouseholdLocations<AREA, H>{
    override fun generateLocation(zone: AREA, household: H): StandardLocation {

        return BetterLocation( zone.randomPoint(), zone, RoadAccess.INVALID)
    }
}