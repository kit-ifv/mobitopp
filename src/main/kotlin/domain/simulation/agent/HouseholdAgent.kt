package domain.simulation.agent

import Mutable
import domain.synthesis.data.household.HouseholdId
import domain.synthesis.data.household.IHousehold
import utils.random.StochasticActor
import kotlin.random.Random

@Mutable
abstract class HouseholdAgent(override val id: HouseholdId, seed: Long) :
    IHousehold,
    StochasticActor {
    final override val random: Random by lazy { Random(id.value + seed) }
    abstract override val members: Set<PersonAgent>
    abstract override val cars: Set<PrivateCarAgent>
}
