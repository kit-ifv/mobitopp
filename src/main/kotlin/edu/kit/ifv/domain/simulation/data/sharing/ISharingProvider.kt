package edu.kit.ifv.domain.simulation.data.sharing
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.utils.Identifiable

interface ISharingProvider : Identifiable<SharingProviderId> {
    val name: String
    val mode: Mode
    val stations: Set<ISharingStation>
    val operatingHours: IntRange // TODO refine for multiple intervals

    val numberOfVehicles: Int
}
