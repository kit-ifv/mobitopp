package domain.simulation.data.person

import Mutable
import domain.synthesis.attributes.person.HasMutableSchedule
import domain.synthesis.attributes.person.HasPlannedActivities
import domain.synthesis.data.DrtProvider
import domain.synthesis.data.PlannedActivity
import domain.synthesis.data.SharingProvider
import domain.synthesis.data.household.MutableHousehold
import kotlin.random.Random

@Mutable
abstract class Person(final override val id: PersonId, override val household: MutableHousehold, seed: Long) :
    IPerson,
    HasMutableSchedule,
    HasPlannedActivities<PlannedActivity> {
    // Agent<Person> TODO merge Agent and Stochastic Actor, or agent should just be wrapper in simulation

    final override val random: Random by lazy { Random(id.value + seed) }

    abstract override val sharingMemberships: List<SharingProvider>
    abstract override val drtMemberships: List<DrtProvider>

//    abstract val plannedActivities: ClearableList<PlannedActivity>

//    val plannedActivities: List<PlannedActivity> //public view of activities
//        get() = plannedActivityList
//
//    internal abstract val plannedActivityList: MutableList<PlannedActivity>

    init {
        addAsMember()
    }

    private fun addAsMember() {
        this.household.members.add(this)
    }

    fun clearPlannedActivities() = this.plannedActivities.clear()
}
