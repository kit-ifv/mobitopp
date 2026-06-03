package domain.shared.location.attributes

import domain.shared.location.zone.attributes.HasRegionType
import domain.shared.location.zone.attributes.HasRoadAccess
import domain.shared.location.zone.attributes.HasSizebasedClassification
import domain.shared.location.zone.attributes.HasZoneId

interface StandardLocationAttributes :
    HasRegionType,
    HasSizebasedClassification,
    HasZoneId,
    HasRoadAccess
