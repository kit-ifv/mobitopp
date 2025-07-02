package domain.shared.enums.areatype

import utils.EnumDecodable

private const val METROPOLE_STR = "Metropole"
private const val URBAN_AREA_STR = "Städtischer Raum"
private const val RURAL_AREA_STR = "Kleinstädtischer / dörflicher Raum"

enum class RegioStaRGem5(override val code: Int, override val description: String) : RegioStaRClassification {
    METROPOLE(1, METROPOLE_STR),
    REGIOPOLE(2, "Regiopole, Großstadt"),
    CENTRAL_CITY(3, "Zentrale Stadt, Mittelstadt"),
    URBAN_AREA(4, URBAN_AREA_STR),
    RURAL_AREA(5, RURAL_AREA_STR);

    companion object : EnumDecodable<RegioStaRGem5>(RegioStaRGem5::class)

    override fun toRegioStaR2() = toRegioStaRGem7().toRegioStaR2()
    override fun toRegioStaR4() = toRegioStaRGem7().toRegioStaR4()
    override fun toRegioStaR7() = toRegioStaRGem7().toRegioStaR7()
    override fun toRegioStaR17() = toRegioStaRGem7().toRegioStaR17()
    override fun toRegioStaR17Plus() = toRegioStaRGem7().toRegioStaR17Plus()

    override fun toRegioStaRGem5() = this
    override fun toRegioStaRGem7() = when (this) {
        METROPOLE -> RegioStaRGem7.METROPOLE
        REGIOPOLE -> warnCast(RegioStaRGem7.LARGE_CITY, RegioStaRGem7.regiopoleLargeCityGem5)
        CENTRAL_CITY -> warnCast(RegioStaRGem7.MEDIUM_CITY, RegioStaRGem7.centralMediumCityGem5)
        URBAN_AREA -> RegioStaRGem7.URBAN_AREA
        RURAL_AREA -> RegioStaRGem7.SMALL_TOWN_RURAL_AREA
    }
}

enum class RegioStaRGem7(override val code: Int, override val description: String) : RegioStaRClassification {
    METROPOLE(1, METROPOLE_STR),
    REGIOPOLE(2, "Regiopole"),
    LARGE_CITY(3, "Großstadt"),
    CENTRAL_CITY(4, "Zentrale Stadt"),
    MEDIUM_CITY(5, "Mittelstadt"),
    URBAN_AREA(6, URBAN_AREA_STR),
    SMALL_TOWN_RURAL_AREA(7, RURAL_AREA_STR);

    companion object : EnumDecodable<RegioStaRGem7>(RegioStaRGem7::class) {
        val regiopoleLargeCityGem5 = setOf(REGIOPOLE, LARGE_CITY)
        val centralMediumCityGem5 = setOf(CENTRAL_CITY, MEDIUM_CITY)
    }

    override fun toRegioStaRGem5() = when (this) {
        METROPOLE -> RegioStaRGem5.METROPOLE
        REGIOPOLE -> RegioStaRGem5.REGIOPOLE
        LARGE_CITY -> RegioStaRGem5.REGIOPOLE
        CENTRAL_CITY -> RegioStaRGem5.CENTRAL_CITY
        MEDIUM_CITY -> RegioStaRGem5.CENTRAL_CITY
        URBAN_AREA -> RegioStaRGem5.URBAN_AREA
        SMALL_TOWN_RURAL_AREA -> RegioStaRGem5.RURAL_AREA
    }

    override fun toRegioStaRGem7() = this
    override fun toRegioStaR2() = when (this) {
        METROPOLE,
        REGIOPOLE,
        LARGE_CITY,
        URBAN_AREA -> RegioStaR2.URBAN
        CENTRAL_CITY,
        MEDIUM_CITY,
        SMALL_TOWN_RURAL_AREA -> RegioStaR2.RURAL
    }

    override fun toRegioStaR4() = toRegioStaR17().toRegioStaR4()
    override fun toRegioStaR7() = when (this) {
        METROPOLE -> RegioStaR7.METROPOLITAN
        REGIOPOLE, LARGE_CITY -> RegioStaR7.REGIOPOLIS_AND_LARGECITIES
        CENTRAL_CITY -> RegioStaR7.CENTRAL_CITIES_IN_RURAL_REGIONS
        MEDIUM_CITY -> warnCast(RegioStaR7.MEDIUM_CITIES_URBAN_AREA, RegioStaR7.mediumGem7)
        URBAN_AREA -> warnCast(RegioStaR7.MEDIUM_CITIES_URBAN_AREA_OF_A_CITY_REGION, RegioStaR7.mediumGem7)
        SMALL_TOWN_RURAL_AREA -> warnCast(RegioStaR7.SMALL_TOWN_RURAL_AREAS_IN_RURAL_REGIONS, RegioStaR7.smallGem7)
    }

    override fun toRegioStaR17() = when (this) {
        METROPOLE -> RegioStaR17.METROPOLE
        REGIOPOLE -> RegioStaR17.REGIOPOLE
        LARGE_CITY -> RegioStaR17.LARGE_CITY_METRO
        CENTRAL_CITY -> warnCast(RegioStaR17.CENTRAL_CITY_RURAL_NEAR_URBAN, RegioStaR17.centralCityGem7)
        MEDIUM_CITY -> warnCast(RegioStaR17.MEDIUM_CITY_RURAL_PERIPHERAL, RegioStaR17.mediumCityGem7)
        URBAN_AREA -> warnCast(RegioStaR17.URBAN_AREA_RURAL_NEAR_URBAN, RegioStaR17.urbanAreaGem7)
        SMALL_TOWN_RURAL_AREA -> warnCast(RegioStaR17.SMALL_RURAL_AREA_PERIPHERAL, RegioStaR17.smallRuralGem7)
    }

    override fun toRegioStaR17Plus() = toRegioStaR17().toRegioStaR17Plus()
}
