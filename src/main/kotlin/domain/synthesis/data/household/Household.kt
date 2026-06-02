package domain.synthesis.data.household

import Mutable
import domain.synthesis.data.car.PrivateCar
import domain.synthesis.data.person.Person
import kotlin.random.Random

@Mutable
abstract class Household(override val id: HouseholdId, seed: Long) : IHousehold {

    final override val random: Random by lazy { Random(id.value + seed) }

    abstract override val members: Set<Person>
    abstract override val cars: Set<PrivateCar>
}
