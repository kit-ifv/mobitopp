package domain.simulation.agent

import Mutable
import core.events.Resource
import core.events.SubscribableResource
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.IHousehold
import utils.random.SeededActor

@Mutable
abstract class HouseholdAgent(
    override val id: HouseholdId,
    seed: Long,
) : SeededActor<Household>(seed), IHousehold, SubscribableResource<PersonAgent> {

    abstract override val members: Set<PersonAgent>
    abstract override val cars: Set<PrivateCarAgent>

    final override val name: String by lazy { "H_${id}_$householdNumber" }

    final override fun isAvailableFor(agent: PersonAgent): Boolean {
        return (location == agent.location) && !agent.inTransit
    }

    final override val resources: Set<Resource<PersonAgent>>
        get() = setOf(this)
}
