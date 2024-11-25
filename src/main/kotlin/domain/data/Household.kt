package domain.data

import Buildable
import domain.location.Location
import domain.resources.Resource
import domain.resources.Subscribable
import units.Currency
import utils.Decodable
import utils.Encodable
import utils.ID
import utils.Identifiable
import kotlin.random.Random

typealias HouseholdId = ID<Household>

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
@Buildable
@Suppress("ComplexInterface")
interface Household : Identifiable<HouseholdId>, Subscribable<Person>, Resource<Person> {
    val householdNumber: Long
    val surveyYear: Int
    val location: Location
    val domCode: Int
    val type: Int
    val incomePerMonth: Currency
    val economicStatus: EconomicStatus
    val random: Random
    val members: Set<Person>
    val cars: Set<PrivateCar>
    fun addMember(person: Person): Boolean
    fun addCar(privateCar: PrivateCar): Boolean

    override fun isAvailableFor(agent: Person): Boolean {
        return (location == agent.location) && !agent.inTransit
    }

    override val resources: Set<Resource<Person>>
        get() = setOf(this)
}

@Buildable
@Suppress("LongParameterList")
class DefaultHousehold(
    override val householdNumber: Long,
    override val surveyYear: Int,
    override val location: Location,
    override val domCode: Int,
    override val type: Int,
    override val incomePerMonth: Currency,
    override val economicStatus: EconomicStatus,
    override val random: Random,
    override val id: HouseholdId = ID(householdNumber),
    override val name: String = "Household: $id"

) : Household {
    override val members: MutableSet<Person> = mutableSetOf()
    override val cars: MutableSet<PrivateCar> = mutableSetOf()

    override fun addMember(person: Person): Boolean {
        return members.add(person)
    }

    override fun addCar(privateCar: PrivateCar): Boolean {
        return cars.add(privateCar)
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
