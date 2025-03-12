package domain.data

import Mutable
import domain.location.Location
import domain.resources.Resource
import domain.resources.Subscribable
import units.Currency
import utils.Decodable
import utils.Encodable
import utils.ID
import utils.Identifiable
import utils.random.SeededActor

typealias HouseholdId = ID<Household>

@Mutable
abstract class Household(
    override val id: HouseholdId,
    seed: Long,
) : SeededActor<Household>(seed), Identifiable<HouseholdId>, Subscribable<Person>, Resource<Person> {

    abstract val householdNumber: Long
    abstract val surveyYear: Int
    abstract val location: Location
    abstract val domCode: Int
    abstract val type: Int
    abstract val incomePerMonth: Currency
    abstract val economicStatus: EconomicStatus
    abstract val members: Set<Person>
    abstract val cars: Set<PrivateCar>

    final override val name: String by lazy { "H_${id}_$householdNumber" }

    final override fun isAvailableFor(agent: Person): Boolean {
        return (location == agent.location) && !agent.inTransit
    }

    final override val resources: Set<Resource<Person>>
        get() = setOf(this)

    override fun equals(other: Any?): Boolean {
        if (other !is Household) return false
        return id == other.id &&
            householdNumber == other.householdNumber &&
            surveyYear == other.surveyYear &&
            location == other.location &&
            domCode == other.domCode &&
            type == other.type &&
            incomePerMonth == other.incomePerMonth &&
            economicStatus == other.economicStatus &&
            members == other.members &&
            cars == other.cars
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
        override fun values(): Set<EconomicStatus> = EconomicStatus.entries.toSet()
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
        override fun values(): Set<HouseholdType> = HouseholdType.entries.toSet()
    }
}
