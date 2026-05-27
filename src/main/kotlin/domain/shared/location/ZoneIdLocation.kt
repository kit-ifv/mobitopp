package domain.shared.location

import domain.shared.location.attributes.HasZoneId
import org.locationtech.jts.geom.Point

data class ZoneIdLocation(
    override val position: Point,
    override val zoneId: ZoneId,
) : LocationWithZoneId

interface LocationWithZoneId: HasZoneId, Location