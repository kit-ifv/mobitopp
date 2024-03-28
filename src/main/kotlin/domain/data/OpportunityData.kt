package domain.data

import Buildable
import domain.enums.LegacyActivityType
import domain.location.Location
import utils.Identifiable

@Buildable
interface OpportunityData : Identifiable<OpportunityData> {

    val location: Location
    val attractivity: Int
    val activityType: LegacyActivityType
}
