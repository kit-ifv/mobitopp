package domain.simulation.data.person

import Mutable
import domain.simulation.data.DrtProvider
import domain.simulation.data.PlannedActivity
import domain.simulation.data.SharingProvider
import domain.simulation.data.household.MutableHousehold
import domain.synthesis.attributes.person.HasMutableSchedule
import domain.synthesis.attributes.person.HasPlannedActivities
import domain.synthesis.data.drt.DrtProvider
import domain.synthesis.data.PlannedActivity
import domain.synthesis.data.SharingProvider
import domain.synthesis.data.household.MutableHousehold
import kotlin.random.Random

@Mutable
abstract class Person(final override val id: PersonId, override val household: MutableHousehold, seed: Long) :
    IPerson,
    HasMutableSchedule,
    HasPlannedActivities<PlannedActivity> {

    final override val random: Random by lazy { Random(id.value + seed) }

    abstract override val sharingMemberships: List<SharingProvider>
    abstract override val drtMemberships: List<DrtProvider>

    init {
        addAsMember()
    }

    private fun addAsMember() {
        this.household.members.add(this)
    }

    fun clearPlannedActivities() = this.plannedActivities.clear()
}
