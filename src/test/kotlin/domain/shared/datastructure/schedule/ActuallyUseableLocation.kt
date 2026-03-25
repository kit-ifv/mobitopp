package domain.shared.datastructure.schedule

import domain.shared.location.attributes.HasZone
import domain.shared.location.ZoneId
import domain.shared.location.toPoint
import edu.kit.ifv.units.WGS84Coordinate
import org.locationtech.jts.geom.Point

/**
 * I hate the definiton loop of: A Location needs a zone. A zone needs a centroid location
 * So I use another definition
 */
class ActuallyUseableLocation(
    override val position: Point,
    override val zoneID: ZoneId,

    ) : HasZone {

    constructor(zoneId: Number) : this(
        WGS84Coordinate.decimalDegree(zoneId.toDouble(), zoneId.toDouble()).toPoint(),
        ZoneId(zoneId.toLong())
    )

    constructor(x: Number, y: Number, zoneid: Number) : this(
        WGS84Coordinate.decimalDegree(x.toDouble(), y.toDouble()).toPoint(),
        ZoneId(zoneid.toLong())
    )

}
