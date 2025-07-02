package domain.synthesis.parser

import domain.shared.enums.ActivityType
import domain.shared.location.Location
import domain.synthesis.data.Person

data class ActivityLocation(val person: Person, val activityType: ActivityType, val location: Location)
