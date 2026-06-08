package edu.kit.ifv.domain.shared.location.attributes
import edu.kit.ifv.domain.shared.location.zone.attributes.HasRegionType
import edu.kit.ifv.domain.shared.location.zone.attributes.HasRoadAccess
import edu.kit.ifv.domain.shared.location.zone.attributes.HasSizebasedClassification
import edu.kit.ifv.domain.shared.location.zone.attributes.HasZoneId

interface StandardLocationAttributes :
    HasRegionType,
    HasSizebasedClassification,
    HasZoneId,
    HasRoadAccess
