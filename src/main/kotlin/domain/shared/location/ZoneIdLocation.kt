package domain.shared.location

import domain.shared.location.zone.attributes.HasZoneId

interface LocationWithZoneId : Location<HasZoneId>
