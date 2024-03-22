package domain.data

import Buildable
import Identifiable
import domain.enums.LegacyActivityType
import domain.location.Location

@Buildable
interface OpportunityData: Identifiable<OpportunityData> {

    val location: Location
    val attractivity: Int
    val activityType: LegacyActivityType

}
