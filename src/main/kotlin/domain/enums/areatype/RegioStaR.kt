package domain.enums.areatype

import utils.Encodable
import utils.EnumDecodable

private const val METROPOLE_STR = "Metropole"
private const val LARGE_CITY_STR = "Großstadt einer Metropolitanen Stadtregion"
private const val MEDIUM_CITY_METRO_STR = "Mittelstadt einer Metropolitanen Stadtregion"
private const val URBAN_AREA_METRO_STR = "Städtischer Raum einer Metropolitanen Stadtregion"
private const val RURAL_AREA_METRO_STR = "Kleinstädtischer, dörflicher Raum einer Metropolitanen Stadtregion"
private const val REGIOPOLE_STR = "Regiopole"
private const val MEDIUM_CITY_REGIOPOLE_STR = "Mittelstadt einer Regiopolitanen Stadtregion"
private const val URBAN_AREA_REGIOPOLE_STR = "Städtischer Raum einer Regiopolitanen Stadtregion"
private const val RURAL_AREA_REGIOPOLE_STR = "Kleinstädtischer, dörflicher Raum einer Regiopolitanen Stadtregion"
private const val CENTRAL_CITY_URBAN_STR = "Zentrale Stadt einer Stadtregionsnahen ländlichen Region"
private const val MEDIUM_CITY_URBAN_STR = "Mittelstadt einer Stadtregionsnahen ländlichen Region"
private const val URBAN_AREA_URBAN_STR = "Städtischer Raum einer Stadtregionsnahen ländlichen Region"
private const val RURAL_AREA_URBAN_STR = "Kleinstädtischer, dörflicher Raum einer Stadtregionsnahen ländlichen Region"
private const val CENTRAL_CITY_PERIPHERAL_STR = "Zentrale Stadt einer Peripheren ländlichen Region"
private const val MEDIUM_CITY_PERIPHERAL_STR = "Mittelstadt einer Peripheren ländlichen Region"
private const val URBAN_AREA_PERIPHERAL_STR = "Städtischer Raum einer Peripheren ländlichen Region"
private const val RURAL_AREA_PERIPHERAL_STR = "Kleinstädtischer, dörflicher Raum einer Peripheren ländlichen Region"

interface RegioStaRClassification : Encodable, AreaType {

    fun toRegioStaR2(): RegioStaR2
    fun toRegioStaR4(): Regiostar4
    fun toRegioStaR7(): Regiostar7
    fun toRegioStaR17(): Regiostar17
    fun toRegioStaR17Plus(): Regiostar17Plus

    fun toRegioStaRGem5(): RegiostarGem5
    fun toRegioStaRGem7(): RegiostarGem7

    fun <R : RegioStaRClassification> warnCast(selected: R, possible: Set<R>): R {
        print(
            "The cast from ${this::class}#$description to ${selected::class} is ambiguous!\n" +
                "Possible target values for casting are: ${possible.joinToString(", ")}!\n" +
                "Using type '$selected' as representative of these options, " +
                "as most common type weighted by area, data based on RegioStaR 2020 in Germany!\n"
        )
        return selected
    }

    fun invalidCodeError() = "There should never be a code $code. That is not a valid ${this::class} encoding."
}

enum class RegioStaR2(override val code: Int, override val description: String) : RegioStaRClassification {
    URBAN(1, "Stadtregion"),
    RURAL(2, "Ländliche Region");

    companion object : EnumDecodable<RegioStaR2>(RegioStaR2::class)

    override fun toRegioStaR2() = this
    override fun toRegioStaR4() = toRegioStaR17().toRegioStaR4()
    override fun toRegioStaR7() = toRegioStaR4().toRegioStaR7()

    override fun toRegioStaR17() = when (this) {
        URBAN -> warnCast(Regiostar17.SMALL_RURAL_AREA_REGIOPOLITAN, Regiostar17.urban2)
        RURAL -> warnCast(Regiostar17.SMALL_RURAL_AREA_PERIPHERAL, Regiostar17.rural2)
    }

    override fun toRegioStaR17Plus() = toRegioStaR17().toRegioStaR17Plus()
    override fun toRegioStaRGem5() = toRegioStaR17().toRegioStaRGem5()
    override fun toRegioStaRGem7() = toRegioStaR17().toRegioStaRGem7()
}

enum class Regiostar4(override val code: Int, override val description: String) : RegioStaRClassification {
    CITY_METROPOLITAN(11, "Metropolitane Stadtregion"),
    CITY_REGIOPOLITAN(12, "Regiopolitane Stadtregion"),
    RURAL_URBAN_AREA(21, "Stadtregionsnahe ländliche Region"),
    RURAL_PERIPHERAL(22, "Periphere ländliche Region");

    companion object : EnumDecodable<Regiostar4>(Regiostar4::class) {
        val city = setOf(CITY_METROPOLITAN, CITY_REGIOPOLITAN)
        val rural = setOf(RURAL_URBAN_AREA, RURAL_PERIPHERAL)
    }

    override fun toRegioStaR2(): RegioStaR2 = when (this) {
        CITY_METROPOLITAN, CITY_REGIOPOLITAN -> RegioStaR2.URBAN
        RURAL_URBAN_AREA, RURAL_PERIPHERAL -> RegioStaR2.RURAL
    }

    override fun toRegioStaR4(): Regiostar4 = this
    override fun toRegioStaR7(): Regiostar7 = toRegioStaR17().toRegioStaR7()

    override fun toRegioStaR17(): Regiostar17 = when (this) {
        CITY_METROPOLITAN -> warnCast(Regiostar17.URBAN_AREA_METRO, Regiostar17.metropolitan4)
        CITY_REGIOPOLITAN -> warnCast(Regiostar17.SMALL_RURAL_AREA_REGIOPOLITAN, Regiostar17.regiopolitan4)
        RURAL_URBAN_AREA -> warnCast(Regiostar17.SMALL_RURAL_AREA_NEAR_URBAN, Regiostar17.ruralNearUrban4)
        RURAL_PERIPHERAL -> warnCast(Regiostar17.SMALL_RURAL_AREA_PERIPHERAL, Regiostar17.ruralPeripheral4)
    }

    override fun toRegioStaR17Plus(): Regiostar17Plus = toRegioStaR17().toRegioStaR17Plus()
    override fun toRegioStaRGem5(): RegiostarGem5 = toRegioStaR17().toRegioStaRGem5()
    override fun toRegioStaRGem7(): RegiostarGem7 = toRegioStaR17().toRegioStaRGem7()
}

enum class Regiostar7(override val code: Int, override val description: String) : RegioStaRClassification {
    METROPOLITAN(71, "Metropolen"),
    REGIOPOLIS_AND_LARGECITIES(72, "Regiopolen und Großstädte"),
    MEDIUM_CITIES_URBAN_AREA_OF_A_CITY_REGION(73, "Mittelstädte, städtischer Raum einer Stadtregion"),
    SMALL_TOWN_RURAL_AREA_OF_A_CITY_REGION(74, "Kleinstädtischer dörflicher Raum einer Stadtregion"),
    CENTRAL_CITIES_IN_RURAL_REGIONS(75, "Zentrale Städte einer Ländlichen Region"),
    MEDIUM_CITIES_URBAN_AREA(76, "Mittelstädte, städtischer Raum"),
    SMALL_TOWN_RURAL_AREAS_IN_RURAL_REGIONS(77, "Kleinstädtischer, dörflicher Raum einer Ländlichen Region");

    companion object : EnumDecodable<Regiostar7>(Regiostar7::class) {
        val mediumGem7 = setOf(MEDIUM_CITIES_URBAN_AREA_OF_A_CITY_REGION, MEDIUM_CITIES_URBAN_AREA)
        val smallGem7 = setOf(SMALL_TOWN_RURAL_AREA_OF_A_CITY_REGION, SMALL_TOWN_RURAL_AREAS_IN_RURAL_REGIONS)
    }

    override fun toRegioStaR2() = when (this) {
        METROPOLITAN, REGIOPOLIS_AND_LARGECITIES,
        MEDIUM_CITIES_URBAN_AREA_OF_A_CITY_REGION, SMALL_TOWN_RURAL_AREA_OF_A_CITY_REGION -> RegioStaR2.URBAN

        CENTRAL_CITIES_IN_RURAL_REGIONS, MEDIUM_CITIES_URBAN_AREA,
        SMALL_TOWN_RURAL_AREAS_IN_RURAL_REGIONS -> RegioStaR2.RURAL
    }

    override fun toRegioStaR4(): Regiostar4 = when (this) {
        METROPOLITAN -> Regiostar4.CITY_METROPOLITAN
        REGIOPOLIS_AND_LARGECITIES, SMALL_TOWN_RURAL_AREA_OF_A_CITY_REGION ->
            warnCast(Regiostar4.CITY_REGIOPOLITAN, Regiostar4.city)
        MEDIUM_CITIES_URBAN_AREA_OF_A_CITY_REGION -> warnCast(Regiostar4.CITY_METROPOLITAN, Regiostar4.city)

        CENTRAL_CITIES_IN_RURAL_REGIONS, MEDIUM_CITIES_URBAN_AREA ->
            warnCast(Regiostar4.RURAL_URBAN_AREA, Regiostar4.rural)
        SMALL_TOWN_RURAL_AREAS_IN_RURAL_REGIONS -> warnCast(Regiostar4.RURAL_PERIPHERAL, Regiostar4.rural)
    }

    override fun toRegioStaR7() = this

    override fun toRegioStaR17() = when (this) {
        METROPOLITAN -> Regiostar17.METROPOLE
        REGIOPOLIS_AND_LARGECITIES -> warnCast(Regiostar17.REGIOPOLE, Regiostar17.regiopoleLargeCities7)
        MEDIUM_CITIES_URBAN_AREA_OF_A_CITY_REGION -> warnCast(
            Regiostar17.URBAN_AREA_METRO,
            Regiostar17.mediumCityUrban7
        )
        SMALL_TOWN_RURAL_AREA_OF_A_CITY_REGION -> warnCast(
            Regiostar17.SMALL_RURAL_AREA_REGIOPOLITAN,
            Regiostar17.smallNearUrban7
        )
        CENTRAL_CITIES_IN_RURAL_REGIONS -> warnCast(
            Regiostar17.CENTRAL_CITY_RURAL_NEAR_URBAN,
            Regiostar17.centralCityRural7
        )
        MEDIUM_CITIES_URBAN_AREA -> warnCast(Regiostar17.URBAN_AREA_RURAL_NEAR_URBAN, Regiostar17.mediumCityRural7)
        SMALL_TOWN_RURAL_AREAS_IN_RURAL_REGIONS -> warnCast(
            Regiostar17.SMALL_RURAL_AREA_PERIPHERAL,
            Regiostar17.smallRural7
        )
    }

    override fun toRegioStaR17Plus() = toRegioStaR17().toRegioStaR17Plus()
    override fun toRegioStaRGem5(): RegiostarGem5 = toRegioStaR17().toRegioStaRGem5()
    override fun toRegioStaRGem7(): RegiostarGem7 = toRegioStaR17().toRegioStaRGem7()
}

@Suppress("MagicNumber") // These magic numbers are ok
enum class Regiostar17(override val code: Int, override val description: String) : RegioStaRClassification {
    METROPOLE(111, METROPOLE_STR),
    LARGE_CITY_METRO(112, LARGE_CITY_STR),
    MEDIUM_CITY_METRO(113, MEDIUM_CITY_METRO_STR),
    URBAN_AREA_METRO(114, URBAN_AREA_METRO_STR),
    SMALL_RURAL_AREA_METRO(115, RURAL_AREA_METRO_STR),
    REGIOPOLE(121, REGIOPOLE_STR),
    MEDIUM_CITY_REGIOPOLITAN(123, MEDIUM_CITY_REGIOPOLE_STR),
    URBAN_AREA_REGIOPOLITAN(124, URBAN_AREA_REGIOPOLE_STR),
    SMALL_RURAL_AREA_REGIOPOLITAN(125, RURAL_AREA_REGIOPOLE_STR),
    CENTRAL_CITY_RURAL_NEAR_URBAN(211, CENTRAL_CITY_URBAN_STR),
    MEDIUM_CITY_RURAL_NEAR_URBAN(213, MEDIUM_CITY_URBAN_STR),
    URBAN_AREA_RURAL_NEAR_URBAN(214, URBAN_AREA_URBAN_STR),
    SMALL_RURAL_AREA_NEAR_URBAN(215, RURAL_AREA_URBAN_STR),
    CENTRAL_CITY_RURAL_PERIPHERAL(221, CENTRAL_CITY_PERIPHERAL_STR),
    MEDIUM_CITY_RURAL_PERIPHERAL(223, MEDIUM_CITY_PERIPHERAL_STR),
    URBAN_AREA_RURAL_PERIPHERAL(224, URBAN_AREA_PERIPHERAL_STR),
    SMALL_RURAL_AREA_PERIPHERAL(225, RURAL_AREA_PERIPHERAL_STR);

    companion object : EnumDecodable<Regiostar17>(Regiostar17::class) {
        val metropolitan4 = setOf(
            METROPOLE,
            LARGE_CITY_METRO,
            MEDIUM_CITY_METRO,
            URBAN_AREA_METRO,
            SMALL_RURAL_AREA_METRO
        )
        val regiopolitan4 = setOf(
            REGIOPOLE,
            MEDIUM_CITY_REGIOPOLITAN,
            URBAN_AREA_REGIOPOLITAN,
            SMALL_RURAL_AREA_REGIOPOLITAN
        )
        val ruralNearUrban4 = setOf(
            CENTRAL_CITY_RURAL_NEAR_URBAN,
            MEDIUM_CITY_RURAL_NEAR_URBAN,
            URBAN_AREA_RURAL_NEAR_URBAN,
            SMALL_RURAL_AREA_NEAR_URBAN
        )
        val ruralPeripheral4 = setOf(
            CENTRAL_CITY_RURAL_PERIPHERAL,
            MEDIUM_CITY_RURAL_PERIPHERAL,
            URBAN_AREA_RURAL_PERIPHERAL,
            SMALL_RURAL_AREA_PERIPHERAL
        )

        val regiopoleLargeCities7 = setOf(LARGE_CITY_METRO, REGIOPOLE)
        val mediumCityUrban7 = setOf(
            MEDIUM_CITY_METRO,
            URBAN_AREA_METRO,
            MEDIUM_CITY_REGIOPOLITAN,
            URBAN_AREA_REGIOPOLITAN
        )
        val smallNearUrban7 = setOf(SMALL_RURAL_AREA_METRO, SMALL_RURAL_AREA_REGIOPOLITAN)
        val centralCityRural7 = setOf(CENTRAL_CITY_RURAL_NEAR_URBAN, CENTRAL_CITY_RURAL_PERIPHERAL)
        val mediumCityRural7 = setOf(
            MEDIUM_CITY_RURAL_NEAR_URBAN,
            URBAN_AREA_RURAL_NEAR_URBAN,
            MEDIUM_CITY_RURAL_PERIPHERAL,
            URBAN_AREA_RURAL_PERIPHERAL
        )
        val smallRural7 = setOf(SMALL_RURAL_AREA_NEAR_URBAN, SMALL_RURAL_AREA_PERIPHERAL)

        val urban2 = metropolitan4 + regiopolitan4
        val rural2 = ruralNearUrban4 + ruralPeripheral4

        val centralCityGem7 = setOf(CENTRAL_CITY_RURAL_NEAR_URBAN, CENTRAL_CITY_RURAL_PERIPHERAL)
        val mediumCityGem7 = setOf(
            MEDIUM_CITY_METRO,
            MEDIUM_CITY_REGIOPOLITAN,
            MEDIUM_CITY_RURAL_NEAR_URBAN,
            MEDIUM_CITY_RURAL_PERIPHERAL
        )
        val urbanAreaGem7 = setOf(
            URBAN_AREA_METRO,
            URBAN_AREA_REGIOPOLITAN,
            URBAN_AREA_RURAL_NEAR_URBAN,
            URBAN_AREA_RURAL_PERIPHERAL
        )
        val smallRuralGem7 = setOf(
            SMALL_RURAL_AREA_METRO,
            SMALL_RURAL_AREA_REGIOPOLITAN,
            SMALL_RURAL_AREA_NEAR_URBAN,
            SMALL_RURAL_AREA_PERIPHERAL
        )
    }

    override fun toRegioStaR2() = toRegioStaR4().toRegioStaR2()
    override fun toRegioStaR4() = when (this) {
        METROPOLE, LARGE_CITY_METRO, MEDIUM_CITY_METRO,
        URBAN_AREA_METRO, SMALL_RURAL_AREA_METRO -> Regiostar4.CITY_METROPOLITAN
        REGIOPOLE, MEDIUM_CITY_REGIOPOLITAN,
        URBAN_AREA_REGIOPOLITAN, SMALL_RURAL_AREA_REGIOPOLITAN -> Regiostar4.CITY_REGIOPOLITAN
        CENTRAL_CITY_RURAL_NEAR_URBAN, MEDIUM_CITY_RURAL_NEAR_URBAN,
        URBAN_AREA_RURAL_NEAR_URBAN, SMALL_RURAL_AREA_NEAR_URBAN -> Regiostar4.RURAL_URBAN_AREA
        CENTRAL_CITY_RURAL_PERIPHERAL, MEDIUM_CITY_RURAL_PERIPHERAL,
        URBAN_AREA_RURAL_PERIPHERAL, SMALL_RURAL_AREA_PERIPHERAL -> Regiostar4.RURAL_PERIPHERAL
    }

    override fun toRegioStaR7() = when (this) {
        METROPOLE -> Regiostar7.METROPOLITAN
        LARGE_CITY_METRO, REGIOPOLE -> Regiostar7.REGIOPOLIS_AND_LARGECITIES
        MEDIUM_CITY_METRO, URBAN_AREA_METRO,
        MEDIUM_CITY_REGIOPOLITAN, URBAN_AREA_REGIOPOLITAN -> Regiostar7.MEDIUM_CITIES_URBAN_AREA_OF_A_CITY_REGION
        SMALL_RURAL_AREA_METRO, SMALL_RURAL_AREA_REGIOPOLITAN -> Regiostar7.SMALL_TOWN_RURAL_AREA_OF_A_CITY_REGION
        CENTRAL_CITY_RURAL_NEAR_URBAN, CENTRAL_CITY_RURAL_PERIPHERAL -> Regiostar7.CENTRAL_CITIES_IN_RURAL_REGIONS
        MEDIUM_CITY_RURAL_NEAR_URBAN, URBAN_AREA_RURAL_NEAR_URBAN,
        MEDIUM_CITY_RURAL_PERIPHERAL, URBAN_AREA_RURAL_PERIPHERAL -> Regiostar7.MEDIUM_CITIES_URBAN_AREA
        SMALL_RURAL_AREA_NEAR_URBAN, SMALL_RURAL_AREA_PERIPHERAL -> Regiostar7.SMALL_TOWN_RURAL_AREAS_IN_RURAL_REGIONS
    }

    override fun toRegioStaR17() = this

    override fun toRegioStaR17Plus() = when (this) {
        METROPOLE -> warnCast(Regiostar17Plus.METROPOLE_CITY_CENTER_PERIPHERY, Regiostar17Plus.metropole)
        else -> Regiostar17Plus.decode(code)
    }

    override fun toRegioStaRGem5() = toRegioStaRGem7().toRegioStaRGem5()
    override fun toRegioStaRGem7() = when (this) {
        METROPOLE -> RegiostarGem7.METROPOLE
        REGIOPOLE -> RegiostarGem7.REGIOPOLE
        LARGE_CITY_METRO -> RegiostarGem7.LARGE_CITY

        CENTRAL_CITY_RURAL_PERIPHERAL,
        CENTRAL_CITY_RURAL_NEAR_URBAN -> RegiostarGem7.CENTRAL_CITY

        MEDIUM_CITY_REGIOPOLITAN, MEDIUM_CITY_RURAL_NEAR_URBAN, MEDIUM_CITY_RURAL_PERIPHERAL,
        MEDIUM_CITY_METRO -> RegiostarGem7.MEDIUM_CITY

        URBAN_AREA_REGIOPOLITAN, URBAN_AREA_RURAL_NEAR_URBAN, URBAN_AREA_RURAL_PERIPHERAL,
        URBAN_AREA_METRO -> RegiostarGem7.URBAN_AREA

        SMALL_RURAL_AREA_REGIOPOLITAN, SMALL_RURAL_AREA_NEAR_URBAN, SMALL_RURAL_AREA_PERIPHERAL,
        SMALL_RURAL_AREA_METRO -> RegiostarGem7.SMALL_TOWN_RURAL_AREA
    }
}

@Suppress("MagicNumber") // These magic numbers are ok
enum class Regiostar17Plus(override val code: Int, override val description: String) : RegioStaRClassification {
    METROPOLE_CITY_CENTER(1111, "Innenstadt einer Metropole"),
    METROPOLE_CITY_CENTER_PERIPHERY(1112, "Innenstadtrand einer Metropole"),
    METROPOLE_CITY_OUTSKIRTS(1113, "Stadtrand einer Metropole"),
    LARGE_CITY_METRO(112, LARGE_CITY_STR),
    MEDIUM_CITY_METRO(113, MEDIUM_CITY_METRO_STR),
    URBAN_AREA_METRO(114, URBAN_AREA_METRO_STR),
    SMALL_RURAL_AREA_METRO(115, RURAL_AREA_METRO_STR),
    REGIOPOLE(121, REGIOPOLE_STR),
    MEDIUM_CITY_REGIOPOLITAN(123, MEDIUM_CITY_REGIOPOLE_STR),
    URBAN_AREA_REGIOPOLITAN(124, URBAN_AREA_REGIOPOLE_STR),
    SMALL_RURAL_AREA_REGIOPOLITAN(125, RURAL_AREA_REGIOPOLE_STR),
    CENTRAL_CITY_RURAL_NEAR_URBAN(211, CENTRAL_CITY_URBAN_STR),
    MEDIUM_CITY_RURAL_NEAR_URBAN(213, MEDIUM_CITY_URBAN_STR),
    URBAN_AREA_RURAL_NEAR_URBAN(214, URBAN_AREA_URBAN_STR),
    SMALL_RURAL_AREA_NEAR_URBAN(215, RURAL_AREA_URBAN_STR),
    CENTRAL_CITY_RURAL_PERIPHERAL(221, CENTRAL_CITY_PERIPHERAL_STR),
    MEDIUM_CITY_RURAL_PERIPHERAL(223, MEDIUM_CITY_PERIPHERAL_STR),
    URBAN_AREA_RURAL_PERIPHERAL(224, URBAN_AREA_PERIPHERAL_STR),
    SMALL_RURAL_AREA_PERIPHERAL(225, RURAL_AREA_PERIPHERAL_STR);

    companion object : EnumDecodable<Regiostar17Plus>(Regiostar17Plus::class) {
        val metropole = setOf(METROPOLE_CITY_CENTER, METROPOLE_CITY_CENTER_PERIPHERY, METROPOLE_CITY_OUTSKIRTS)
    }

    override fun toRegioStaR2() = toRegioStaR17().toRegioStaR2()
    override fun toRegioStaR4() = toRegioStaR17().toRegioStaR4()
    override fun toRegioStaR7() = toRegioStaR17().toRegioStaR7()

    override fun toRegioStaR17() = when (this) {
        METROPOLE_CITY_CENTER, METROPOLE_CITY_CENTER_PERIPHERY, METROPOLE_CITY_OUTSKIRTS -> Regiostar17.METROPOLE
        else -> Regiostar17.decode(code)
    }

    override fun toRegioStaR17Plus() = this
    override fun toRegioStaRGem5() = toRegioStaR17().toRegioStaRGem5()
    override fun toRegioStaRGem7() = toRegioStaR17().toRegioStaRGem7()
}
