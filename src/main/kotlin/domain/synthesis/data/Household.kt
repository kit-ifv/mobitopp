package domain.synthesis.data

import Mutable
import kotlin.random.Random

@Mutable
abstract class Household(override val id: HouseholdId, seed: Long) : IHousehold {

    final override val random: Random by lazy { Random(id.value + seed) }

    abstract override val members: Set<Person>
    abstract override val cars: Set<PrivateCar>
}

