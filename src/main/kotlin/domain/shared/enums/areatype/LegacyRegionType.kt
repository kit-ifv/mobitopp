package domain.shared.enums.areatype

import utils.EnumDecodable

private const val CONVERSION_ERROR_ZONE_AREA = """The ZoneAreaType DEFAULT cannot be translated to RegioStar17.
In old mobitopp this area type existed to express an unknown type, which cannot be translated to a concrete RegioStar17
instance. To avoid this issue you need to manually convert the Default type to the RegioStar17 instance you require for
your project. We cannot provide a default conversion for you"
"""

private const val CONVERSION_ERROR_BBSR17 = """The BBsr17 type "default" cannot be translated to RegioStar17.
In old mobitopp this area type existed to express an unknown type, which cannot be translated to a concrete RegioStar17
instance. To avoid this issue you need to manually convert the Default type to the RegioStar17 instance you require for
your project. We cannot provide a default conversion for you"
"""

/**
 * ZoneAreaType is a AreaType encoding from legacy mobiTopp
 *
 * @property code integer encoding of the zone area type
 */
enum class ZoneRegionType(override val code: Int) : RegionType {
    DEFAULT(0),
    RURAL(1),
    PROVINCIAL(2),
    CITYOUTSKIRT(3),
    METROPOLITAN(4),
    CONURBATION(5);

    override val description: String
        get() = this.name

    companion object : EnumDecodable<ZoneRegionType>(ZoneRegionType::class)

    override fun toRegioStaR17(): RegioStaR17 {
        return when (this) {
            CONURBATION -> RegioStaR17.METROPOLE
            METROPOLITAN -> RegioStaR17.LARGE_CITY_METRO
            CITYOUTSKIRT -> RegioStaR17.URBAN_AREA_RURAL_NEAR_URBAN
            PROVINCIAL -> RegioStaR17.MEDIUM_CITY_RURAL_PERIPHERAL
            RURAL -> RegioStaR17.SMALL_RURAL_AREA_PERIPHERAL
            DEFAULT -> throw NoSuchElementException(CONVERSION_ERROR_ZONE_AREA)
        }
    }
}

/**
 * AreaType based on RegioStaR 17 defined by:
 * https://bmdv.bund.de/SharedDocs/DE/Artikel/G/regionalstatistische-raumtypologie.html
 *
 * @property code integer code of Bbsr17 area type
 * @property description description of Bbsr17 area type
 */
@Suppress("EnumNaming", "EnumEntryNameCase")
enum class Bbsr17(override val code: Int, override val description: String) : RegionType {
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

    @Suppress("CyclomaticComplexMethod")
    override // There are more than 14 enum entries.
    fun toRegioStaR17(): RegioStaR17 {
        return when (this) {
            largerCentralCitiesInAgglomerationAreas -> RegioStaR17.METROPOLE
            centralCitiesInAgglomerationAreas -> RegioStaR17.LARGE_CITY_METRO
            highOrderCentresInHighlyAgglomeratedCountiesInAgglomerationAreas -> RegioStaR17.MEDIUM_CITY_METRO
            highlyAgglomeratedCountiesInAgglomerationAreas -> RegioStaR17.URBAN_AREA_METRO
            highOrderCentresInAgglomeratedCountiesInAgglomerationAreas -> RegioStaR17.SMALL_RURAL_AREA_METRO
            agglomeratedCountiesInAgglomerationAreas -> RegioStaR17.REGIOPOLE
            highOrderCentresInRuralCountiesInAgglomerationAreas -> RegioStaR17.MEDIUM_CITY_REGIOPOLITAN
            ruralCountiesInAgglomerationAreas -> RegioStaR17.URBAN_AREA_REGIOPOLITAN
            centralCitiesInUrbanizedAreas -> RegioStaR17.SMALL_RURAL_AREA_REGIOPOLITAN
            highOrderCentresInAgglomeratedCountiesInUrbanizedAreas -> RegioStaR17.CENTRAL_CITY_RURAL_NEAR_URBAN
            agglomeratedCountiesInUrbanizedAreas -> RegioStaR17.MEDIUM_CITY_RURAL_NEAR_URBAN
            highOrderCentresInRuralCountiesInUrbanizedAreas -> RegioStaR17.URBAN_AREA_RURAL_NEAR_URBAN
            ruralCountiesInUrbanizedAreas -> RegioStaR17.SMALL_RURAL_AREA_NEAR_URBAN
            highOrderCentresInRuralCountiesWithHigherDensityInRuralAreas -> RegioStaR17.CENTRAL_CITY_RURAL_PERIPHERAL
            ruralCountiesWithHigherDensityInRuralAreas -> RegioStaR17.MEDIUM_CITY_RURAL_PERIPHERAL
            highOrderCentresInRuralCountiesWithLowerDensityInRuralAreas -> RegioStaR17.URBAN_AREA_RURAL_PERIPHERAL
            ruralCountiesWithLowerDensityInRuralAreas -> RegioStaR17.SMALL_RURAL_AREA_PERIPHERAL
            defaultType -> throw NoSuchElementException(CONVERSION_ERROR_BBSR17)
        }
    }

    companion object : EnumDecodable<Bbsr17>(Bbsr17::class)
}

/**
 * A representation that I have found in the legacy code that exists without explanation. Possible guesses are that
 * these encodings are based on the underlying settlement size, but expert input is recommended.
 */
enum class SizebasedRegiostarClassification {
    CITY, SMALL_TOWN, URBAN_AREA, RURAL_AREA
}

@Suppress("MagicNumber")
fun RegioStaR17.toSizebasedClassification(): SizebasedRegiostarClassification {
    return when (code) {
        111, 112, 121, 211 -> SizebasedRegiostarClassification.CITY
        113, 123, 213, 223, 221 -> SizebasedRegiostarClassification.SMALL_TOWN
        114, 124, 214 -> SizebasedRegiostarClassification.URBAN_AREA
        115, 125, 215, 224, 225 -> SizebasedRegiostarClassification.RURAL_AREA
        else -> error("RegioStaR17 code $code should not exist!")
    }
}
