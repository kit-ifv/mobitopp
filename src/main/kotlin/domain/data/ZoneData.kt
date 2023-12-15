package domain.data

import Builder
import ID
import Identifiable
import domain.enums.AreaType
import domain.enums.LegacyActivityType
import domain.enums.ZoneClassification
import domain.location.Location
import newId
import utils.units.Distance

typealias Attractivity = Map<LegacyActivityType, Double> //TODO


/**
 * The data class for a zone in the simulation. Maybe this should be refactored into an interface to allow future
 * patches/addons.
 * @property visumId the zone id in visum
 * @property name The name of the zone
 * @property areaType the area type of the zone
 * @property regionType the region type of the zone
 * @property classification the zone classification in the research context
 * @property parkingPlaces the number of available parking spaces in the zone
 * @property centroid the centroid point of the zone
 * @property isDestination whether the zone can be selected as a destination
 * @property relief height difference in the zone
 */
interface ZoneData: Identifiable<ZoneData> {
    val visumId: Long
    val name: String
    val areaType: AreaType
    val regionType: Int
    val classification: ZoneClassification
    val parkingPlaces: Int
    val centroid: Location //TODO type point
    val isDestination: Boolean
    val relief: Distance

    //val attractivity: Attractivity

}

class ZoneDataBuilder : Builder<ZoneData>, Identifiable<ZoneDataBuilder> {
    override val id: ID<ZoneDataBuilder>
        get() = ID(visumId!!.toULong())

    var visumId: Long? = null
    var name: String? = null
    var areaType: AreaType? = null
    var regionType: Int? = null
    var classification: ZoneClassification? = null
    var parkingPlaces: Int? = null
    var centroid: Location? = null
    var isDestination: Boolean? = null
    var relief: Distance? = null

    override fun build() = object:ZoneData{
        override val visumId: Long = this@ZoneDataBuilder.visumId!!
        override val name: String = this@ZoneDataBuilder.name!!
        override val areaType: AreaType = this@ZoneDataBuilder.areaType!!
        override val regionType: Int = this@ZoneDataBuilder.regionType!!
        override val classification: ZoneClassification = this@ZoneDataBuilder.classification!!
        override val parkingPlaces: Int = this@ZoneDataBuilder.parkingPlaces!!
        override val centroid: Location = this@ZoneDataBuilder.centroid!!
        override val isDestination: Boolean = this@ZoneDataBuilder.isDestination!!
        override val relief: Distance = this@ZoneDataBuilder.relief!!
        override val id: ID<ZoneData> = this.newId()
    }

}

fun main() {

    print("HELLO")
}
