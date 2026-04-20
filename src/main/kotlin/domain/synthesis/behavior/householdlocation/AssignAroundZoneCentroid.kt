package domain.synthesis.behavior.householdlocation

import domain.shared.location.StandardLocation
import domain.shared.location.Zone
import edu.kit.ifv.units.Distance
@Suppress("UnusedPrivateProperty")
class AssignAroundZoneCentroid<AREA : Zone, H>(private val radius: Distance) : AssignHouseholdLocations<AREA, H> {
    override fun generateLocation(zone: AREA, household: H): StandardLocation {
        return zone.centroid
//        return LocationOld(zone.centroid.coordinate.randomCoordinate(radius, zone.random), zone, null)
    }
}
