package domain.synthesis.data

import Mutable
import domain.shared.enums.ActivityType
import domain.shared.location.Location
import kotlinx.serialization.Serializable
import utils.Identifiable
import utils.random.StochasticActor
import utils.units.AbsoluteTime
import kotlin.random.Random
import kotlin.time.Duration

@Serializable
@JvmInline
value class ActivityId(val value: Long) {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    fun compareTo(other: ActivityId): Int {
        return value.compareTo(other.value)
    }

    /**
     * Robin: I added a method to iterate over ids, I want to use this feature for generating autoincrementing ids
     * in the test cases
     *
     * @return the next higher id.
     */
    fun next(): ActivityId {
        return ActivityId(value + 1)
    }
}

@Mutable
abstract class PlannedActivity(
    override val id: ActivityId,
    val person: MutablePerson,
    seed: Long,
) : StochasticActor, Identifiable<ActivityId> { // : SeededActor<PlannedActivity>(seed), Identifiable<ActivityId> {

    final override val random: Random by lazy { Random(id.value + seed) }

    init {
        this.addAsActivity()
    }

    private fun addAsActivity() {
        this.person.plannedActivities.add(this)
    }

    abstract val activityType: ActivityType
    abstract val observedTripDuration: Duration
    abstract val startTime: AbsoluteTime
    abstract val duration: Duration
    abstract val location: Location?

    val endTime: AbsoluteTime
        get() = startTime + duration

    override fun toString(): String {
        return "${activityType.description.first()}(${activityType.code}) start=$startTime duration=$duration"
    }
}
