package edu.kit.ifv.domain.shared.location.zone.attributes
import edu.kit.ifv.domain.shared.enums.ZoneClassification
import edu.kit.ifv.units.Distance

/**
 * The maximum information a zone can hold in the simulation framework, even with the most asinine information available.
 */
interface MaximumZoneAttributes :
    HasRegionType,
    HasNumberParkingPlaces,
    HasCentroid {
    val visumId: Long
    val name: String
    val classification: ZoneClassification
    val isDestination: Boolean
    val relief: Distance
}
