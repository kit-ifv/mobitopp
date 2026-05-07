package domain.shared.location.attributes

import domain.shared.enums.areatype.RegionType
import domain.shared.location.Location

interface HasRegionType {
    val regionType: RegionType
}
