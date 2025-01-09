package domain.enums

import utils.CodePlan
import utils.Decodable
import utils.Encodable
import java.awt.geom.Area

/**
 * Area types can distinguish areas of different purpose: e.g. residential vs. industrial.
 * There are multiple definitions of area types:
 * hence each project can select which area type should be used.
 */
interface AreaType : Encodable {
    val description: String

    fun toRegiostar17(): Regiostar17
}

private const val conversionErrorZoneArea = """The ZoneAreaType DEFAULT cannot be translated to RegioStar17.
In old mobitopp this area type existed to express an unknown type, which cannot be translated to a concrete RegioStar17
instance. To avoid this issue you need to manually convert the Default type to the RegioStar17 instance you require for
your project. We cannot provide a default conversion for you"
"""

private const val conversionErrorBbsr17 = """The BBsr17 type "default" cannot be translated to RegioStar17.
In old mobitopp this area type existed to express an unknown type, which cannot be translated to a concrete RegioStar17
instance. To avoid this issue you need to manually convert the Default type to the RegioStar17 instance you require for
your project. We cannot provide a default conversion for you"
"""

/**
 * ZoneAreaType is a AreaType encoding from legacy mobiTopp
 *
 * @property code integer encoding of the zone area type
 */
enum class ZoneAreaType(private val code: Int) : AreaType {
    DEFAULT(0),
    RURAL(1),
    PROVINCIAL(2),
    CITYOUTSKIRT(3),
    METROPOLITAN(4),
    CONURBATION(5);

    override fun encode() = this.code
    override val description: String
        get() = this.name

    companion object : CodePlan<AreaType> {
        override fun decode(i: Int) = ZoneAreaType.entries.first { it.code == i }
        override fun decode(s: String) = ZoneAreaType.valueOf(s)
        override fun values(): Set<AreaType> = ZoneAreaType.entries.toSet()
    }

    override fun toRegiostar17(): Regiostar17 {
        return when (this) {
            CONURBATION -> Regiostar17.METROPOLE
            METROPOLITAN -> Regiostar17.LARGE_CITY_METRO
            CITYOUTSKIRT -> Regiostar17.URBAN_AREA_RURAL_NEAR_URBAN
            PROVINCIAL -> Regiostar17.MEDIUM_CITY_RURAL_PERIPHERAL
            RURAL -> Regiostar17.SMALL_RURAL_AREA_PERIPHERAL
            DEFAULT -> throw NoSuchElementException(conversionErrorZoneArea)
        }
    }
}

/**
 * AreaType based on RegioStaR 17 defined by:
 * https://bmdv.bund.de/SharedDocs/DE/Artikel/G/regionalstatistische-raumtypologie.html
 *
 * @property code integer code of Bbsr17 area type
 * @property text description of Bbsr17 area type
 */
@Suppress("EnumNaming", "EnumEntryNameCase")
enum class Bbsr17(private val code: Int, private val text: String) : AreaType {
    defaultType(0, "default"),
    largerCentralCitiesInAgglomerationAreas(1, "Larger central cities in agglomeration areas"),
    centralCitiesInAgglomerationAreas(2, "Central cities in agglomeration areas"),
    highOrderCentresInHighlyAgglomeratedCountiesInAgglomerationAreas(
        3,
        "High-order centres in highly agglomerated counties in agglomeration areas"
    ),
    highlyAgglomeratedCountiesInAgglomerationAreas(4, "Highly agglomerated counties in agglomeration areas"),
    highOrderCentresInAgglomeratedCountiesInAgglomerationAreas(
        5,
        "High-order centres in agglomerated counties in agglomeration areas"
    ),
    agglomeratedCountiesInAgglomerationAreas(6, "Agglomerated counties in agglomeration areas"),
    highOrderCentresInRuralCountiesInAgglomerationAreas(
        7,
        "High-order centres in rural Counties in agglomeration areas"
    ),
    ruralCountiesInAgglomerationAreas(8, "Rural counties in agglomeration areas"),
    centralCitiesInUrbanizedAreas(9, "Central cities in urbanized areas"),
    highOrderCentresInAgglomeratedCountiesInUrbanizedAreas(
        10,
        "High-order centres in agglomerated Counties in urbanized areas"
    ),
    agglomeratedCountiesInUrbanizedAreas(11, "Agglomerated counties in urbanized areas"),
    highOrderCentresInRuralCountiesInUrbanizedAreas(12, "High-order centres in rural Counties in urbanized areas"),
    ruralCountiesInUrbanizedAreas(13, "Rural Counties in urbanized areas"),
    highOrderCentresInRuralCountiesWithHigherDensityInRuralAreas(
        14,
        "High-order centres in rural counties with higher density in rural areas"
    ),
    ruralCountiesWithHigherDensityInRuralAreas(15, "Rural counties with higher density in rural areas"),
    highOrderCentresInRuralCountiesWithLowerDensityInRuralAreas(
        16,
        "High-order centres in rural counties with lower density in rural areas"
    ),
    ruralCountiesWithLowerDensityInRuralAreas(17, "Rural counties with lower density in rural areas");


    override fun toRegiostar17(): Regiostar17 {
        return when(this) {

            largerCentralCitiesInAgglomerationAreas ->Regiostar17.METROPOLE
            centralCitiesInAgglomerationAreas ->Regiostar17.LARGE_CITY_METRO
            highOrderCentresInHighlyAgglomeratedCountiesInAgglomerationAreas -> Regiostar17.MEDIUM_CITY_METRO
            highlyAgglomeratedCountiesInAgglomerationAreas -> Regiostar17.URBAN_AREA_METRO
            highOrderCentresInAgglomeratedCountiesInAgglomerationAreas -> Regiostar17.SMALL_RURAL_AREA_METRO
            agglomeratedCountiesInAgglomerationAreas -> Regiostar17.REGIOPOLE
            highOrderCentresInRuralCountiesInAgglomerationAreas -> Regiostar17.MEDIUM_CITY_REGIOPOLITAN
            ruralCountiesInAgglomerationAreas -> Regiostar17.URBAN_AREA_REGIOPOLITAN
            centralCitiesInUrbanizedAreas -> Regiostar17.SMALL_RURAL_AREA_REGIOPOLITAN
            highOrderCentresInAgglomeratedCountiesInUrbanizedAreas -> Regiostar17.CENTRAL_CITY_RURAL_NEAR_URBAN
            agglomeratedCountiesInUrbanizedAreas -> Regiostar17.MEDIUM_CITY_RURAL_NEAR_URBAN
            highOrderCentresInRuralCountiesInUrbanizedAreas -> Regiostar17.URBAN_AREA_RURAL_NEAR_URBAN
            ruralCountiesInUrbanizedAreas -> Regiostar17.SMALL_RURAL_AREA_NEAR_URBAN
            highOrderCentresInRuralCountiesWithHigherDensityInRuralAreas -> Regiostar17.CENTRAL_CITY_RURAL_PERIPHERAL
            ruralCountiesWithHigherDensityInRuralAreas -> Regiostar17.MEDIUM_CITY_RURAL_PERIPHERAL
            highOrderCentresInRuralCountiesWithLowerDensityInRuralAreas -> Regiostar17.URBAN_AREA_RURAL_PERIPHERAL
            ruralCountiesWithLowerDensityInRuralAreas -> Regiostar17.SMALL_RURAL_AREA_PERIPHERAL
                defaultType-> throw NoSuchElementException(conversionErrorBbsr17)
        }
    }
    override fun encode() = this.code

    override val description: String
        get() = this.text

    companion object : CodePlan<AreaType> {
        override fun decode(i: Int) = Bbsr17.entries.first { it.code == i }
        override fun decode(s: String) = Bbsr17.valueOf(s)
        override fun values(): Set<AreaType> = Bbsr17.entries.toSet()
    }
}



enum class Regiostar17(val code: Int, val text: String) : Encodable, AreaType {
    METROPOLE(111, "Metropole"),
    LARGE_CITY_METRO(112, "Großstadt einer Metropolitanen Stadtregion"),
    MEDIUM_CITY_METRO(113, "Mittelstadt einer Metropolitanen Stadtregion"),
    URBAN_AREA_METRO(114, "Städtischer Raum einer Metropolitanen Stadtregion"),
    SMALL_RURAL_AREA_METRO(115, "Kleinstädtischer, dörflicher Raum einer Metropolitanen Stadtregion"),
    REGIOPOLE(121, "Regiopole"),
    MEDIUM_CITY_REGIOPOLITAN(123, "Mittelstadt einer Regiopolitanen Stadtregion"),
    URBAN_AREA_REGIOPOLITAN(124, "Städtischer Raum einer Regiopolitanen Stadtregion"),
    SMALL_RURAL_AREA_REGIOPOLITAN(125, "Kleinstädtischer, dörflicher Raum einer Regiopolitanen Stadtregion"),
    CENTRAL_CITY_RURAL_NEAR_URBAN(211, "Zentrale Stadt einer Stadtregionsnahen ländlichen Region"),
    MEDIUM_CITY_RURAL_NEAR_URBAN(213, "Mittelstadt einer Stadtregionsnahen ländlichen Region"),
    URBAN_AREA_RURAL_NEAR_URBAN(214, "Städtischer Raum einer Stadtregionsnahen ländlichen Region"),
    SMALL_RURAL_AREA_NEAR_URBAN(215, "Kleinstädtischer, dörflicher Raum einer Stadtregionsnahen ländlichen Region"),
    CENTRAL_CITY_RURAL_PERIPHERAL(221, "Zentrale Stadt einer Peripheren ländlichen Region"),
    MEDIUM_CITY_RURAL_PERIPHERAL(223, "Mittelstadt einer Peripheren ländlichen Region"),
    URBAN_AREA_RURAL_PERIPHERAL(224, "Städtischer Raum einer Peripheren ländlichen Region"),
    SMALL_RURAL_AREA_PERIPHERAL(225, "Kleinstädtischer, dörflicher Raum einer Peripheren ländlichen Region");

    override fun encode(): Int {
        return code
    }

    override val description: String = text
    override fun toRegiostar17(): Regiostar17 = this

    fun toRegiostar7(): Regiostar7 {
        return when(code) {
            111 -> Regiostar7.METROPOLITAN
            112, 121 -> Regiostar7.REGIOPOLIS_AND_LARGECITIES
            113,114,123,124 -> Regiostar7.MEDIUM_CITIES_URBAN_AREA_OF_A_CITY_REGION
            115,125 -> Regiostar7.SMALL_TOWN_RURAL_AREA_OF_A_CITY_REGION
            211, 221 -> Regiostar7.CENTRAL_CITIES_IN_RURAL_REGIONS
            213, 214, 223, 224 -> Regiostar7.MEDIUM_CITIES_URBAN_AREA
            215, 225 -> Regiostar7.SMALL_TOWN_RURAL_AREAS_IN_RURAL_REGIONS
            else -> throw IllegalStateException("There should never be a code $code. That is not a valid Regiostar17 encoding.")
        }
    }

    fun toRegiostar4(): Regiostar4 {
        return when(code) {
            111, 112, 113, 114, 115 -> Regiostar4.CITY_METROPOLITAN
            121, 123, 124, 125 -> Regiostar4.CITY_REGIOPOLITAN
            211, 213, 214, 215 -> Regiostar4.RURAL_URBAN_AREA
            221, 223, 224, 225 -> Regiostar4.RURAL_PERIPHERAL
            else -> throw IllegalStateException("There should never be a code $code. That is not a valid Regiostar17 encoding.")
        }
    }

    companion object : Decodable<Regiostar17> {
        override fun decode(i: Int) = Regiostar17.entries.first { it.code == i }
        override fun decode(s: String) = Regiostar17.valueOf(s)
        override fun values(): Set<Regiostar17> = Regiostar17.entries.toSet()
    }
}

enum class Regiostar7(val code: Int, val text: String) {
    METROPOLITAN(71, "Metropolen"),
    REGIOPOLIS_AND_LARGECITIES(72, "Regiopolen und Großstädte"),
    MEDIUM_CITIES_URBAN_AREA_OF_A_CITY_REGION(73, "Mittelstädte, städtischer Raum einer Stadtregion"),
    SMALL_TOWN_RURAL_AREA_OF_A_CITY_REGION(74, "Kleinstädtischer dörflicher Raum einer Stadtregion"),
    CENTRAL_CITIES_IN_RURAL_REGIONS(75, "Zentrale Städte einer Ländlichen Region"),
    MEDIUM_CITIES_URBAN_AREA(76, "Mittelstädte, städtischer Raum"),
    SMALL_TOWN_RURAL_AREAS_IN_RURAL_REGIONS(77, "Kleinstädtischer, dörflicher Raum einer Ländlichen Region");

}
enum class Regiostar4(val code: Int, val text: String) {
    CITY_METROPOLITAN(11, "Metropolitane Stadtregion"),
    CITY_REGIOPOLITAN(12, "Regiopolitane Stadtregion"),
    RURAL_URBAN_AREA(21, "Stadtregionsnahe ländliche Region"),
    RURAL_PERIPHERAL(22, "Periphere ländliche Region");
}
enum class RegiostarGem5(val code: Int) {
    METROPOLE(1), REGIOPOLE(2), CENTRAL_CITY(3), CITY_AREA(4), RURAL_AREA(5)
}

enum class RegiostarGem7(val code: Int) {
    METROPOLE(1), REGIOPOLE(2), LARGE_CITY(3), CENTRAL_CITY(4), MEDIUM_CITY(5), URBAN_AREA(6), SMALL_TOWN_RURAL_AREA(7)
}


