package domain.enums

import CodePlan
import Encodable

interface AreaType: Encodable {
    val description: String
}

enum class ZoneAreaType(private val code: Int): AreaType {
    DEFAULT(0),
    RURAL(1),
    PROVINCIAL(2),
    CITYOUTSKIRT(3),
    METROPOLITAN(4),
    CONURBATION(5);

    override fun encode() = this.code
    override val description: String
        get() = this.name

    companion object: CodePlan<AreaType> {
        override fun decode(i: Int) = ZoneAreaType.entries.first { it.code == i }
        override fun decode(s: String) = ZoneAreaType.valueOf(s)
    }
}

@Suppress("EnumNaming")
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

    companion object: CodePlan<AreaType> {
        override fun decode(i: Int) = Bbsr17.entries.first { it.code == i }
        override fun decode(s: String) = Bbsr17.valueOf(s)
    }
}
