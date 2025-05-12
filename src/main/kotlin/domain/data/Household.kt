package domain.data

import Mutable
import domain.location.Location
import domain.resources.Resource
import domain.resources.Subscribable
import units.Currency
import utils.Encodable
import utils.EnumDecodable
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
}

/**
 * The economic status as taken from the original mobiTopp codebase
 */
enum class EconomicStatus(override val code: Int) : Encodable {
    VERY_LOW(1),
    LOW(2),
    MIDDLE(3),
    HIGH(4),
    VERY_HIGH(5);

    override val description: String = name

    companion object : EnumDecodable<EconomicStatus>(EconomicStatus::class)
}

enum class HouseholdType(override val code: Int) : Encodable {

    SINGLE_HH_WITH_CHILDREN(1),
    SINGLE_HH(2),
    COUPLE_WITH_CHILDREN(3),
    COUPLE_WITHOUT_CHILDREN(4),
    OTHER_MULTI_PERSON_HH(5);

    override val description: String = name

    companion object : EnumDecodable<HouseholdType>(HouseholdType::class)
}
