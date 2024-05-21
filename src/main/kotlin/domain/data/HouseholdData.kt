package domain.data

import domain.location.RoadPosition
import domain.location.RoadPositionInZone
import units.Currency
import utils.Builder
import utils.Decodable
import utils.Encodable
import utils.ID
import utils.Identifiable
import kotlin.random.Random

typealias HouseholdId = ID<HouseholdData>

/**
 * The minimal viable information about a household in the simulation.
 *  @property location A household will have a fixed location somewhere in the simulation world.
 *  @property incomePerMonth The household income as required by some utility functions
 *  @property economicStatus The economic status grouping (Might be derived from income)
 *  @property householdNumber id of household in survey
 *  @property surveyYear year the survey was conducted
 *  @property domCode domestic code (legacy mobiTopp)
 *  @property type household type (legacy mobiTopp)
 *  @property members a set of household members
 *  @property random a random value provider for decisions carried out by this agent
 */
interface HouseholdData : Identifiable<HouseholdId> {
    val householdNumber: Long
    val surveyYear: Int
    val location: RoadPositionInZone
    val domCode: Int
    val type: Int
    val incomePerMonth: Currency
    val economicStatus: EconomicStatus
    val random: Random
    val members: Set<PersonData>
    fun addMember(person: PersonData): Boolean
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
) : Builder<HouseholdData> {

    override fun build() = object : HouseholdData {
        override val location: RoadPositionInZone = RoadPositionInZone(
            requireNotNull(this@HouseholdDataBuilder.roadPosition) {
                "roadPosition is not initialized in HouseholdDataBuilder: ${this@HouseholdDataBuilder}"
            },
            requireNotNull(this@HouseholdDataBuilder.homeZone) {
                "homeZone is not initialized in HouseholdDataBuilder: ${this@HouseholdDataBuilder}"
            },
        )

        override val domCode: Int = requireNotNull(this@HouseholdDataBuilder.domCode) {
            "domCode is not initialized in HouseholdDataBuilder: ${this@HouseholdDataBuilder}"
        }

        override val type: Int = requireNotNull(this@HouseholdDataBuilder.type) {
            "type is not initialized in HouseholdDataBuilder: ${this@HouseholdDataBuilder}"
        }

        override val incomePerMonth: Currency = requireNotNull(this@HouseholdDataBuilder.incomePerMonth) {
            "incomePerMonth is not initialized in HouseholdDataBuilder: ${this@HouseholdDataBuilder}"
        }

        override val economicStatus: EconomicStatus = requireNotNull(this@HouseholdDataBuilder.economicStatus) {
            "economicalStatus is not initialized in HouseholdDataBuilder: ${this@HouseholdDataBuilder}"
        }

        override val id: ID<HouseholdData> = ID(
            requireNotNull(this@HouseholdDataBuilder.householdNumber) {
                "id is not initialized in HouseholdDataBuilder: ${this@HouseholdDataBuilder}"
            }
        )

        override val householdNumber: Long = requireNotNull(this@HouseholdDataBuilder.householdNumber) {
            "householdNumber is not initialized in HouseholdDataBuilder: ${this@HouseholdDataBuilder}"
        }

        override val surveyYear: Int = requireNotNull(this@HouseholdDataBuilder.surveyYear) {
            "surveyYear is not initialized in HouseholdDataBuilder: ${this@HouseholdDataBuilder}"
        }

        private val hhMembers = mutableSetOf<PersonData>()
        override fun addMember(person: PersonData) = hhMembers.add(person)

        override val members: Set<PersonData>
            get() = hhMembers

        override val random: Random = Random(id.id)
    }

    override fun toString(): String {
        return "HouseholdDataBuilder(" +
            "householdNumber=$householdNumber, " +
            "surveyYear=$surveyYear, homeZone=$homeZone, " +
            "roadPosition=$roadPosition, " +
            "domCode=$domCode, " +
            "type=$type, " +
            "incomePerMonth=$incomePerMonth, " +
            "economicStatus=$economicStatus)"
    }
}

/**
 * The economic status as taken from the original mobiTopp codebase
 */
enum class EconomicStatus(val code: Int) : Encodable {
    VERY_LOW(1),
    LOW(2),
    MIDDLE(3),
    HIGH(4),
    VERY_HIGH(5);

    override fun encode() = this.code

    companion object : Decodable<EconomicStatus> {
        override fun decode(i: Int) = entries.first { it.code == i }
        override fun decode(s: String) = valueOf(s)
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
        override fun decode(i: Int) = entries.first { it.code == i }
        override fun decode(s: String) = valueOf(s)
    }
}
