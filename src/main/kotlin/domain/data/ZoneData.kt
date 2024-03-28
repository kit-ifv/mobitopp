package domain.data

import domain.enums.AreaType
import domain.enums.ZoneClassification
import domain.location.Location
import units.Distance
import utils.Builder
import utils.ID
import utils.Identifiable
import utils.registerId

typealias ZoneId = ID<ZoneData>

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
interface ZoneData : Identifiable<ZoneId> {
    val visumId: Long
    val name: String
    val areaType: AreaType
    val regionType: Int
    val classification: ZoneClassification
    val parkingPlaces: Int
    val centroid: Location // TODO type point
    val isDestination: Boolean
    val relief: Distance
}

interface LegacyZoneData : ZoneData {
    val matrixColumn: Int
}

@SuppressWarnings("LongParameterList")
class ZoneDataBuilder(
    var visumId: Long? = null,
    var matrixColumn: Int? = null,
    var name: String? = null,
    var areaType: AreaType? = null,
    var regionType: Int? = null,
    var classification: ZoneClassification? = null,
    var parkingPlaces: Int? = null,
    var centroid: Location? = null,
    var isDestination: Boolean? = null,
    var relief: Distance? = null
) : Builder<ZoneData> {

    override fun build() = object : ZoneData {
        override val visumId: Long = this@ZoneDataBuilder.visumId!!
        override val name: String = this@ZoneDataBuilder.name!!
        override val areaType: AreaType = this@ZoneDataBuilder.areaType!!
        override val regionType: Int = this@ZoneDataBuilder.regionType!!
        override val classification: ZoneClassification = this@ZoneDataBuilder.classification!!
        override val parkingPlaces: Int = this@ZoneDataBuilder.parkingPlaces!!
        override val centroid: Location = this@ZoneDataBuilder.centroid!!
        override val isDestination: Boolean = this@ZoneDataBuilder.isDestination!!
        override val relief: Distance = this@ZoneDataBuilder.relief!!
        override val id: ZoneId = registerId(visumId)
    }

    override fun toString(): String {
        return "ZoneDataBuilder(" +
            "visumId=$visumId, " +
            "matrixColumn=$matrixColumn, " +
            "name=$name, " +
            "areaType=$areaType, " +
            "regionType=$regionType, " +
            "classification=$classification, " +
            "parkingPlaces=$parkingPlaces, " +
            "centroid=$centroid, " +
            "isDestination=$isDestination, " +
            "relief=$relief)"
    }
}

@SuppressWarnings("LongParameterList")
class LegacyZoneDataBuilder(
    var visumId: Long? = null,
    var matrixColumn: Int? = null,
    var name: String? = null,
    var areaType: AreaType? = null,
    var regionType: Int? = null,
    var classification: ZoneClassification? = null,
    var parkingPlaces: Int? = null,
    var centroid: Location? = null,
    var isDestination: Boolean? = null,
    var relief: Distance? = null
) : Builder<LegacyZoneData> {

    override fun build() = object : LegacyZoneData {
        override val visumId: Long = this@LegacyZoneDataBuilder.visumId!!
        override val matrixColumn: Int = this@LegacyZoneDataBuilder.matrixColumn!!
        override val name: String = this@LegacyZoneDataBuilder.name!!
        override val areaType: AreaType = this@LegacyZoneDataBuilder.areaType!!
        override val regionType: Int = this@LegacyZoneDataBuilder.regionType!!
        override val classification: ZoneClassification = this@LegacyZoneDataBuilder.classification!!
        override val parkingPlaces: Int = this@LegacyZoneDataBuilder.parkingPlaces!!
        override val centroid: Location = this@LegacyZoneDataBuilder.centroid!!
        override val isDestination: Boolean = this@LegacyZoneDataBuilder.isDestination!!
        override val relief: Distance = this@LegacyZoneDataBuilder.relief!!
        override val id: ZoneId = registerId(visumId)
    }
}
