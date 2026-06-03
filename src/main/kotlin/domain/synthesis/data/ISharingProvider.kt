package domain.synthesis.data

import domain.shared.enums.Mode
import utils.Identifiable

interface ISharingProvider : Identifiable<SharingProviderId> {
    val name: String
    val mode: Mode // TODO assign proper mode
    val stations: Set<ISharingStation>
    val operatingHours: IntRange // TODO refine for multiple intervals

//    val ownedVehicles: Set<ISharingVehicle>
    val numberOfVehicles: Int
}
