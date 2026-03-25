package domain.shared.location

import domain.shared.location.attributes.HasZoneID
import org.locationtech.jts.geom.Point

data class ZoneIDLocation(
    override val position: Point,
    override val zoneID: ZoneId,
) : HasZoneID
