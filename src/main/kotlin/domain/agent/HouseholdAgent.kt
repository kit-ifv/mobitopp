package domain.agent

import Mutable
import domain.data.Household
import domain.data.HouseholdId
import domain.data.IHousehold
import domain.resources.Resource
import domain.resources.SubscribableResource
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
