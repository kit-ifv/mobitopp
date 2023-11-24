package domain.data

import Buildable
import Identifiable

@Buildable
interface OpportunityData: Identifiable<OpportunityData> {

    val location: Location
    val attractivity: Int
    val activityType: LegacyActivityType

}
