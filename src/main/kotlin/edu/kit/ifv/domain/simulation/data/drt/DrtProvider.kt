package edu.kit.ifv.domain.simulation.data.drt
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.utils.Identifiable

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
