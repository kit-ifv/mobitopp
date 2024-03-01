package domain.data

import Builder
import Decodable
import Encodable
import ID
import Identifiable
import domain.location.RoadPosition
import domain.location.RoadPositionInZone
import newId
import utils.units.Currency

/**
 * The minimal viable information about a household in the simulation.
 *  @property location A household will have a fixed location somewhere in the simulation world.
 *  @property incomePerMonth The household income as required by some utility functions
 *  @property economicStatus The economic status grouping (Might be derived from income)
 *
 */
interface HouseholdData: Identifiable<HouseholdData> {
    val householdNumber: Long
    val surveyYear: Int
    val location: RoadPositionInZone
    val domCode: Int
    val type: Int
    val incomePerMonth: Currency
    val economicStatus: EconomicStatus
}

@Suppress("LongParameterList")
class HouseholdDataBuilder(
    var householdNumber: Long? = null,
    var surveyYear: Int? = null,
    var homeZone: ZoneData? = null,
    var roadPosition: RoadPosition? = null,
    var domCode: Int? = null,
    var type: Int? = null,
    var incomePerMonth: Currency? = null,
    var economicStatus: EconomicStatus? = null
): Builder<HouseholdData>, Identifiable<HouseholdDataBuilder> {
    override val id: ID<HouseholdDataBuilder>
        get() = ID(this.householdNumber!!.toULong())



    override fun build()= object:HouseholdData {
        override val location: RoadPositionInZone = this@HouseholdDataBuilder.roadPosition!!.let {
            RoadPositionInZone(
                it,
                this@HouseholdDataBuilder.homeZone!!
            )
        }

        override val domCode: Int = this@HouseholdDataBuilder.domCode!!
        override val type: Int = this@HouseholdDataBuilder.type!!
        override val incomePerMonth: Currency = this@HouseholdDataBuilder.incomePerMonth!!
        override val economicStatus: EconomicStatus = this@HouseholdDataBuilder.economicStatus!!
        override val id: ID<HouseholdData> = this.newId()
        override val householdNumber: Long = this@HouseholdDataBuilder.householdNumber!!
        override val surveyYear: Int = this@HouseholdDataBuilder.surveyYear!!
    }

}

/**
 * The economic status as taken from the original mobiTopp codebase
 */
enum class EconomicStatus(val code: Int): Encodable {
    VERY_LOW(1),
    LOW(2),
    MIDDLE(3),
    HIGH(4),
    VERY_HIGH(5);

    override fun encode() = this.code

    companion object : Decodable<EconomicStatus> {
        override fun decode(i: Int) = EconomicStatus.values().first {it.code == i}
    }
}

enum class HouseholdType(private val code: Int) : Encodable {

    SINGLE_HH_WITH_CHILDREN(1),
    SINGLE_HH(2),
    COUPLE_WITH_CHILDREN(3),
    COUPLE_WITHOUT_CHILDREN(4),
    OTHER_MULTI_PERSON_HH(5);

    override fun encode() = this.code

    companion object : Decodable<HouseholdType> {
        override fun decode(i: Int) = HouseholdType.values().first {it.code == i}
    }
}
