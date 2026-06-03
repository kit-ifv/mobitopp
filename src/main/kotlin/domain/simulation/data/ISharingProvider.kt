package domain.simulation.data

import domain.shared.enums.Mode
import utils.Identifiable

interface ISharingProvider : Identifiable<SharingProviderId> {
    val name: String
    val mode: Mode
    val stations: Set<ISharingStation>
    val operatingHours: IntRange // TODO refine for multiple intervals

    val numberOfVehicles: Int
}