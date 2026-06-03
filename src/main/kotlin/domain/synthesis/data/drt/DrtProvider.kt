package domain.synthesis.data.drt

import domain.shared.enums.Mode
import domain.shared.location.zone.ZoneId
import utils.Identifiable

// TODO remove I from all domain entity interfaces, use pattern as in this file:
// interface = entity name
// mutable long term data class = entity name + "Data"
// dynamic agent class = entity name + "Agent"
interface DrtProvider : Identifiable<DrtProviderId> {
    val name: String
    val mode: Mode
    val serviceArea: List<ZoneId>
    val operatingHours: IntRange // TODO more detailed hours
}

