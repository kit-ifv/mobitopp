package domain.simulation.agent

import Mutable
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.IHousehold
import utils.random.SeededActor

@Mutable
abstract class HouseholdAgent(
    override val id: HouseholdId,
    seed: Long,
) : SeededActor<Household>(seed), IHousehold {

    abstract override val members: Set<PersonAgent>
    abstract override val cars: Set<PrivateCarAgent>
}
