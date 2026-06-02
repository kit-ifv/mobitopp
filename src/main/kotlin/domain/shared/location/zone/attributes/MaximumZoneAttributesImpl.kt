package domain.shared.location.zone.attributes

import domain.shared.enums.ZoneClassification
import domain.shared.enums.areatype.RegionType
import edu.kit.ifv.units.Distance
import org.locationtech.jts.geom.Point

data class MaximumZoneAttributesImpl(
    override val visumId: Long,
    override val name: String,
    override val classification: ZoneClassification,
    override val isDestination: Boolean,
    override val relief: Distance,
    override val regionType: RegionType,
    override val parkingPlaces: Int,
    override val centroid: Point,
) : MaximumZoneAttributes
