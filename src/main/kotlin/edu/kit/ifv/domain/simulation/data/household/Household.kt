package edu.kit.ifv.domain.simulation.data.household
import edu.kit.ifv.Mutable
import edu.kit.ifv.domain.shared.data.household.HouseholdId
import edu.kit.ifv.domain.simulation.data.car.PrivateCar
import edu.kit.ifv.domain.simulation.data.person.Person
import kotlin.random.Random

@Mutable
abstract class Household(override val id: HouseholdId, seed: Long) : IHousehold {

    final override val random: Random by lazy { Random(id.value + seed) }

    abstract override val members: Set<Person>
    abstract override val cars: Set<PrivateCar>
}
