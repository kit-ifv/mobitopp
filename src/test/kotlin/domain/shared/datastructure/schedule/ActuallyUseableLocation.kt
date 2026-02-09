package domain.shared.datastructure.schedule

import domain.shared.enums.areatype.RegionType
import domain.shared.location.Location
import domain.shared.location.RoadAccess
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import edu.kit.ifv.units.Coordinate
import edu.kit.ifv.units.GPSCoordinate

/**
 * I hate the definiton loop of: A Location needs a zone. A zone needs a centroid location
 * So I use another definition
 */
class ActuallyUseableLocation(override val coordinate: Coordinate, val zoneId: ZoneId) : Location {

    constructor(zoneId: Number) : this(zoneId, zoneId, zoneId)

    constructor(x: Number, y: Number, zoneid: Number) : this(
        GPSCoordinate.Companion.decimalDegree(x.toDouble(), y.toDouble()),
        ZoneId(zoneid.toLong())
    )

    override val zone: Zone?
        get() = null
    override val roadAccess: RoadAccess?
        get() = null

    override fun zoneID(): ZoneId {
        return zoneId
    }

    override fun inSameZone(other: Location): Boolean {
        return this.zoneID() == other.zoneID()
    }

    override fun regionType(): RegionType {
        return TODO()
    }
}
