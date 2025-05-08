package domain.data

import Mutable
import domain.location.Location
import units.Currency
import utils.Encodable
import utils.EnumDecodable
import utils.ID
import utils.Identifiable
import utils.random.SeededActor
import utils.random.StochasticActor

typealias HouseholdId = ID<Household>

interface IHousehold : Identifiable<HouseholdId>, StochasticActor {
    val householdNumber: Long
    val surveyYear: Int
    val location: Location
    val domCode: Int
    val type: Int
    val incomePerMonth: Currency
    val economicStatus: EconomicStatus
    val members: Set<IPerson>
    val cars: Set<IPrivateCar>
}

@Mutable
abstract class Household(
    override val id: HouseholdId,
    seed: Long,
) : SeededActor<Household>(seed), IHousehold {
    abstract override val members: Set<Person>
    abstract override val cars: Set<PrivateCar>
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
