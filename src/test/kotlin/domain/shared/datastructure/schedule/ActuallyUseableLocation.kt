package domain.shared.datastructure.schedule

import domain.shared.enums.areatype.RegionType
import domain.shared.location.LocationOld
import domain.shared.location.RoadAccess
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import edu.kit.ifv.units.KCoordinate
import edu.kit.ifv.units.WGS84Coordinate

/**
 * I hate the definiton loop of: A Location needs a zone. A zone needs a centroid location
 * So I use another definition
 */
class ActuallyUseableLocation(override val coordinate: KCoordinate, val zoneId: ZoneId) : LocationOld {

    constructor(zoneId: Number) : this(zoneId, zoneId, zoneId)

    constructor(x: Number, y: Number, zoneid: Number) : this(
        WGS84Coordinate.Companion.decimalDegree(x.toDouble(), y.toDouble()),
        ZoneId(zoneid.toLong())
    )

    override val zone: Zone?
        get() = null
    override val roadAccess: RoadAccess?
        get() = null

    override fun zoneID(): ZoneId {
        return zoneId
    }

    override fun inSameZone(other: LocationOld): Boolean {
        return this.zoneID() == other.zoneID()
    }

    override fun regionType(): RegionType {
        return error("Detekt wants error")
    }
}
