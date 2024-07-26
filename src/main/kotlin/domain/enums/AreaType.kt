package domain.enums

import utils.CodePlan
import utils.Encodable

/**
 * Area types can distinguish areas of different purpose: e.g. residential vs. industrial.
 * There are multiple definitions of area types:
 * hence each project can select which area type should be used.
 */
interface AreaType : Encodable {
    val description: String
}

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

    override fun encode() = this.code

    override val description: String
        get() = this.text

    companion object : CodePlan<AreaType> {
        override fun decode(i: Int) = Bbsr17.entries.first { it.code == i }
        override fun decode(s: String) = Bbsr17.valueOf(s)
        override fun values(): Set<AreaType> = Bbsr17.entries.toSet()
    }
}
