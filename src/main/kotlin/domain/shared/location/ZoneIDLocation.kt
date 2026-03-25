package domain.shared.location

import domain.shared.location.attributes.HasZone
import org.locationtech.jts.geom.Point

data class ZoneIDLocation(
    override val position: Point,
    override val zoneID: ZoneId,
) : HasZone