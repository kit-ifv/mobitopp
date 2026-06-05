package domain.simulation.data.household

import Mutable
import domain.shared.data.household.HouseholdId
import domain.simulation.data.car.PrivateCar
import domain.simulation.data.person.Person
import kotlin.random.Random

@Mutable
abstract class Household(override val id: HouseholdId, seed: Long) : IHousehold {

    final override val random: Random by lazy { Random(id.value + seed) }

    abstract override val members: Set<Person>
    abstract override val cars: Set<PrivateCar>
}
