package domain.data

import Buildable
import Decodable
import Encodable
import Identifiable
import utils.units.Distance
import utils.units.GPSCoordinate
import ID

typealias Attractivity = Map<LegacyActivityType, Double>

/**
 * The data class for a zone in the simulation. Maybe this should be refactored into an interface to allow future
 * patches/addons.
 * @property location the location of the zone. Can be deliberately set to null
 * @property name The name of the zone
 * @property parkingPlaces the number of available parking spaces in the zone
 * @property isDestination Whether the zone can be selected as a destination
 * @property relief Maybe the height of the centroid location of the zone
 * @property attractivity a map of the attractivity of the zone
 */
@Buildable
interface ZoneData: Location, Identifiable<ZoneData> {
    val visumId: Long
    val name: String
    val areaType: AreaType
    val regionType: Int
    val classification: ZoneClassification
    val parkingPlaces: Int
    val centroid: Location
    val isDestination: Boolean
    val relief: Distance

    //val attractivity: Attractivity

}
@Buildable
interface HackyZoneData: ZoneData
enum class AreaType(private val code: Int): Encodable {
    DEFAULT(0),
    RURAL(1),
    PROVINCIAL(2),
    CITYOUTSKIRT(3),
    METROPOLITAN(4),
    CONURBATION(5);

    override fun encode() = this.code

    companion object: Decodable<AreaType> {
        override fun decode(i: Int) = AreaType.values().first { it.code == i }
    }
}

enum class ZoneClassification(private val code: Int): Encodable {
    STUDY_AREA(0),
    EXTENDED_STUDY_AREA(1),
    OUTLYING_AREA(2);

    override fun encode() = this.code

    companion object: Decodable<ZoneClassification> {
        override fun decode(i: Int) = ZoneClassification.values().first { it.code == i }
    }
}


fun main() {

    print("HELLO")
}
