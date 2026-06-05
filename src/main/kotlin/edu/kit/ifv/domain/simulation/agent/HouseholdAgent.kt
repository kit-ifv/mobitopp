package edu.kit.ifv.domain.simulation.agent
import Mutable
import edu.kit.ifv.domain.shared.data.household.HouseholdId
import edu.kit.ifv.domain.simulation.data.household.IHousehold
import edu.kit.ifv.utils.random.StochasticActor
import kotlin.random.Random

@Mutable
abstract class HouseholdAgent(override val id: HouseholdId, seed: Long) :
    IHousehold,
    StochasticActor {
    final override val random: Random by lazy { Random(id.value + seed) }
    abstract override val members: Set<PersonAgent>
    abstract override val cars: Set<PrivateCarAgent>
}
