package domain.simulation.data
//
//import Mutable
//import domain.shared.enums.Mode
//import domain.shared.location.StandardLocation
//import domain.shared.location.zone.Zone
//import domain.shared.location.zone.attributes.HasRegionType
//import kotlinx.serialization.Serializable
//import utils.Identifiable
//
//@Serializable
//@JvmInline
//value class SharingProviderId(val value: Long) : Comparable<SharingProviderId> {
//    /**
//     * Compares this object with the specified object for order. Returns zero if this object is equal
//     * to the specified [other] object, a negative number if it's less than [other], or a positive number
//     * if it's greater than [other].
//     */
//    override fun compareTo(other: SharingProviderId): Int = value.compareTo(other.value)
//
//    /**
//     * Robin: I added a method to iterate over ids, I want to use this feature for generating autoincrementing ids
//     * in the test cases
//     *
//     * @return the next higher id.
//     */
//    fun next(): SharingProviderId = SharingProviderId(value + 1)
//}
//
//interface ISharingProvider : Identifiable<SharingProviderId> {
//    val name: String
//    val mode: Mode
//    val stations: Set<ISharingStation>
//    val operatingHours: IntRange // TODO refine for multiple intervals
//
////    val ownedVehicles: Set<ISharingVehicle>
//    val numberOfVehicles: Int
//}
//
//@Mutable
//abstract class SharingProvider(final override val id: SharingProviderId) : ISharingProvider {
//    abstract override val stations: Set<SharingStation>
//
//    override val numberOfVehicles: Int
//        get() = stations.sumOf { it.initialVehicleCount }
//}
//
//@Serializable
//@JvmInline
//value class SharingStationId(val value: Long) : Comparable<SharingStationId> {
//    /**
//     * Compares this object with the specified object for order. Returns zero if this object is equal
//     * to the specified [other] object, a negative number if it's less than [other], or a positive number
//     * if it's greater than [other].
//     */
//    override fun compareTo(other: SharingStationId): Int = value.compareTo(other.value)
//
//    /**
//     * @return the next higher id.
//     */
//    fun next(): SharingStationId = SharingStationId(value + 1)
//}
//
//interface ISharingStation : Identifiable<SharingStationId> {
//    val uid: String
//    val name: String
//    val location: StandardLocation
//    val zonesByFoot: Set<Zone<HasRegionType>>
//    val owner: ISharingProvider
//
//    fun isReachableFrom(origin: StandardLocation) = zonesByFoot.any { origin in it }
//    fun isReachableFrom(zone: Zone<*>) = zone in zonesByFoot
//}
//
//@Mutable
//abstract class SharingStation(
//    final override val id: SharingStationId,
//    final override val owner: MutableSharingProvider,
//) : ISharingStation {
//
//    abstract val initialVehicleCount: Int
//
//    init {
//        registerOwner()
//    }
//
//    private fun registerOwner() {
//        owner.stations.add(this)
//    }
//}
