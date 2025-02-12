package domain.enums.areatype

import utils.EnumDecodable

private const val METROPOLE_STR = "Metropole"
private const val URBAN_AREA_STR = "Städtischer Raum"
private const val RURAL_AREA_STR = "Kleinstädtischer / dörflicher Raum"

enum class RegiostarGem5(override val code: Int, override val description: String) : RegioStaRClassification {
    METROPOLE(1, METROPOLE_STR),
    REGIOPOLE(2, "Regiopole, Großstadt"),
    CENTRAL_CITY(3, "Zentrale Stadt, Mittelstadt"),
    URBAN_AREA(4, URBAN_AREA_STR),
    RURAL_AREA(5, RURAL_AREA_STR);

    companion object : EnumDecodable<RegiostarGem5>(RegiostarGem5::class)

    override fun toRegioStaR2() = toRegioStaRGem7().toRegioStaR2()
    override fun toRegioStaR4() = toRegioStaRGem7().toRegioStaR4()
    override fun toRegioStaR7() = toRegioStaRGem7().toRegioStaR7()
    override fun toRegioStaR17() = toRegioStaRGem7().toRegioStaR17()
    override fun toRegioStaR17Plus() = toRegioStaRGem7().toRegioStaR17Plus()

    override fun toRegioStaRGem5() = this
    override fun toRegioStaRGem7() = when (this) {
        METROPOLE -> RegiostarGem7.METROPOLE
        REGIOPOLE -> warnCast(RegiostarGem7.LARGE_CITY, RegiostarGem7.regiopoleLargeCityGem5)
        CENTRAL_CITY -> warnCast(RegiostarGem7.MEDIUM_CITY, RegiostarGem7.centralMediumCityGem5)
        URBAN_AREA -> RegiostarGem7.URBAN_AREA
        RURAL_AREA -> RegiostarGem7.SMALL_TOWN_RURAL_AREA
    }
}

enum class RegiostarGem7(override val code: Int, override val description: String) : RegioStaRClassification {
    METROPOLE(1, METROPOLE_STR),
    REGIOPOLE(2, "Regiopole"),
    LARGE_CITY(3, "Großstadt"),
    CENTRAL_CITY(4, "Zentrale Stadt"),
    MEDIUM_CITY(5, "Mittelstadt"),
    URBAN_AREA(6, URBAN_AREA_STR),
    SMALL_TOWN_RURAL_AREA(7, RURAL_AREA_STR);

    companion object : EnumDecodable<RegiostarGem7>(RegiostarGem7::class) {
        val regiopoleLargeCityGem5 = setOf(REGIOPOLE, LARGE_CITY)
        val centralMediumCityGem5 = setOf(CENTRAL_CITY, MEDIUM_CITY)
    }

    override fun toRegioStaRGem5() = when (this) {
        METROPOLE -> RegiostarGem5.METROPOLE
        REGIOPOLE -> RegiostarGem5.REGIOPOLE
        LARGE_CITY -> RegiostarGem5.REGIOPOLE
        CENTRAL_CITY -> RegiostarGem5.CENTRAL_CITY
        MEDIUM_CITY -> RegiostarGem5.CENTRAL_CITY
        URBAN_AREA -> RegiostarGem5.URBAN_AREA
        SMALL_TOWN_RURAL_AREA -> RegiostarGem5.RURAL_AREA
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
        METROPOLE -> Regiostar7.METROPOLITAN
        REGIOPOLE, LARGE_CITY -> Regiostar7.REGIOPOLIS_AND_LARGECITIES
        CENTRAL_CITY -> Regiostar7.CENTRAL_CITIES_IN_RURAL_REGIONS
        MEDIUM_CITY -> warnCast(Regiostar7.MEDIUM_CITIES_URBAN_AREA, Regiostar7.mediumGem7)
        URBAN_AREA -> warnCast(Regiostar7.MEDIUM_CITIES_URBAN_AREA_OF_A_CITY_REGION, Regiostar7.mediumGem7)
        SMALL_TOWN_RURAL_AREA -> warnCast(Regiostar7.SMALL_TOWN_RURAL_AREAS_IN_RURAL_REGIONS, Regiostar7.smallGem7)
    }

    override fun toRegioStaR17() = when (this) {
        METROPOLE -> Regiostar17.METROPOLE
        REGIOPOLE -> Regiostar17.REGIOPOLE
        LARGE_CITY -> Regiostar17.LARGE_CITY_METRO
        CENTRAL_CITY -> warnCast(Regiostar17.CENTRAL_CITY_RURAL_NEAR_URBAN, Regiostar17.centralCityGem7)
        MEDIUM_CITY -> warnCast(Regiostar17.MEDIUM_CITY_RURAL_PERIPHERAL, Regiostar17.mediumCityGem7)
        URBAN_AREA -> warnCast(Regiostar17.URBAN_AREA_RURAL_NEAR_URBAN, Regiostar17.urbanAreaGem7)
        SMALL_TOWN_RURAL_AREA -> warnCast(Regiostar17.SMALL_RURAL_AREA_PERIPHERAL, Regiostar17.smallRuralGem7)
    }

    override fun toRegioStaR17Plus() = toRegioStaR17().toRegioStaR17Plus()
}
