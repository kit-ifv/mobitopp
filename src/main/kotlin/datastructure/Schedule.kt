package datastructure

import java.util.*
import kotlin.time.Duration

interface Location

interface Action : Comparable<Action> {
    val startTime: Duration
    val duration: Duration
    val endTime: Duration get() = startTime + duration

    val startLocation: Location
    val endLocation: Location

    // TODO Intervals do not form a well defined order, It should not be used for comparison.
    // As in x <= y && x >= y => x == y is violated by intervals. We only have a partial order but Comparable induces
    // total ordering
    override fun compareTo(other: Action): Int {
        if (endTime <= other.startTime) return -1
        if (other.endTime <= startTime) return 1
        return 0
    }

    fun initiateAction() {

    }

    fun finalizeAction() {

    }
}

interface StationaryAction : Action {
    val location: Location
    override val startLocation: Location
        get() = location
    override val endLocation: Location
        get() = location
}

interface MovingAction : Action {
    override var startTime: Duration
    override var duration: Duration
    override var startLocation: Location
    override var endLocation: Location
}

interface Activity : StationaryAction {
    override var location: Location
    override var startTime: Duration
    override var duration: Duration


    /**
     * A default implementation to spawn a leg spanning from one activity to another.
     */
    fun createLegTo(other: Activity): Leg {
        return RawLeg(endTime, other.startTime - endTime, endLocation, other.startLocation)
    }

    override fun equals(other: Any?): Boolean
}

data class RawActivity(
    override var location: Location,
    override var startTime: Duration,
    override var duration: Duration
) : Activity {
    override val endTime: Duration = startTime + duration
}

typealias Plan = DualSetList<LinkedElement<Action>, LinkedLeg, LinkedActivity>

interface LinkedElement<out T : Action> : Action, Comparable<Action> {
    val plan: Schedule
    val original: T

    fun next(): LinkedElement<Action>? {
        return plan.elements().higher(this)
    }

    fun previous(): LinkedElement<Action>? = plan.elements().lower(this)
    fun changeEndLocation(to: Location)
    fun changeStartLocation(to: Location)

    fun remove()

    override fun compareTo(other: Action): Int {
        return original.compareTo(other)
    }

    fun consistentTo(
        new: Activity,
        spawner: (startTime: Duration, duration: Duration, start: Location, end: Location) -> Leg
    ) {
        if (endLocation != new.startLocation) {
            plan.add(spawner(endTime, new.startTime - endTime, endLocation, new.startLocation))
        }
    }

    fun consistentFrom(
        new: Action,
        spawner: (startTime: Duration, duration: Duration, start: Location, end: Location) -> Leg
    ) {
        if (endLocation != new.startLocation) {
            plan.add(spawner(endTime, new.startTime - endTime, endLocation, new.startLocation))
        }
    }
}

class LinkedActivity(
    override val original: Activity,
    override val plan: Schedule
) : Activity, LinkedElement<Activity> {

    override fun changeEndLocation(to: Location) {
        if (to != original.location) {
            original.location = to
            next()?.changeStartLocation(to)
        }

    }

    override fun changeStartLocation(to: Location) {
        if (to != original.location) {
            original.location = to
            next()?.changeStartLocation(to)
        }

    }

    override fun remove() {
        plan.plan.removeRight(this)
    }

    override var location: Location
        get() = original.location
        set(value) {
            if (value != original.location) {
                original.location = value
            }
        }
    override var startTime: Duration
        get() = original.startTime
        set(value) {}
    override var duration: Duration
        get() = original.duration
        set(value) {}

    override fun equals(other: Any?): Boolean {
        //TODO is it really equality, it could be a different linked plan, taking place at the same time, location etc.
        if (other !is Activity) return false
        return other == original
    }

    override val endTime: Duration
        get() = original.endTime

    override fun toString(): String {
        return original.toString()
    }

}

interface Leg : MovingAction {
    override fun equals(other: Any?): Boolean
}

data class RawLeg(
    override var startTime: Duration,
    override var duration: Duration,
    override var startLocation: Location,
    override var endLocation: Location
) : Leg {
    override val endTime: Duration = startTime + duration
}


class LinkedLeg(override val original: Leg, override val plan: Schedule) : Leg, LinkedElement<Leg> {
    override fun changeEndLocation(to: Location) {
        original.endLocation = to
        next()?.changeStartLocation(to)
    }

    override fun changeStartLocation(to: Location) {
        original.startLocation = to
        previous()?.changeEndLocation(to)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Leg) return false
        return other == original
    }

    override var startTime: Duration
        get() = original.startTime
        set(value) {
            if (previous()?.let { it.endTime <= value } != false) {
                original.startTime = value
            }
        }
    override var duration: Duration
        get() = original.duration
        set(value) {
            if (next()?.let { it.startTime <= original.startTime + value } != false) {
                original.duration = value
            }
        }
    override var startLocation: Location
        get() = original.startLocation
        set(value) {
            if (value != original.startLocation) {
                changeStartLocation(value)
            }
        }
    override var endLocation: Location
        get() = original.endLocation
        set(value) {
            if (value != original.endLocation) {
                changeEndLocation(value)
            }
        }
    override val endTime: Duration
        get() = original.endTime

    override fun remove() {
        plan.plan.removeLeft(this)
    }

    override fun toString(): String {
        return original.toString()
    }
}

class Schedule(var current: Duration = Duration.ZERO) {
    val plan = Plan()
    val size get() = plan.persistentSet.size
    fun block(index: Int): DualSetContainer<LinkedElement<Action>, LinkedLeg, LinkedActivity> {
        return plan.entries[index]
    }

    fun elements() = plan.persistentSet

    fun addConsistent(activity: Activity) {

        val linkedActivity = LinkedActivity(activity, this)
        linkedActivity.previous()?.consistentTo(linkedActivity, ::RawLeg)
        linkedActivity.next()?.consistentFrom(linkedActivity, ::RawLeg)
        plan.addRight(linkedActivity)
    }

    fun add(activity: Activity) {
        plan.addRight(LinkedActivity(activity, this))
    }

    fun add(leg: Leg) {
        plan.addLeft(LinkedLeg(leg, this))
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Schedule) return false
        return plan == other.plan
    }
}


class TripView(private val schedule: Schedule) :
    Listener<List<DualSetContainer<LinkedElement<Action>, LinkedLeg, LinkedActivity>>> {
    var trips: List<Trip> = emptyList()

    init {

        schedule.plan.entries.listener.add(this)
        update(schedule.plan.entries)
    }


    operator fun get(index: Int): Trip {
        return trips[index]
    }

    val size get() = trips.size
    override fun update(new: List<DualSetContainer<LinkedElement<Action>, LinkedLeg, LinkedActivity>>) {
        val completeTrips = new.zipWithNext { previous, current ->
            Trip(
                current.left,
                previous.right.last(),
                current.right.first(),
                schedule
            )
        }
        trips = if (new.first().left.isEmpty()) completeTrips else listOf(
            Trip(
                new.first().left,
                null,
                new.first().right.first(),
                schedule
            )
        ) + completeTrips
    }

    override fun toString(): String {
        return trips.toString()
    }
}

fun Iterable<Action>.isConsistent(): Boolean {
    val t = zipWithNext { first, second -> first.endLocation == second.startLocation && first.endTime <= second.startTime }
    return t.all { it }
}
class Trip(
    val legs: SortedSet<LinkedLeg>,
    val previousActivity: LinkedActivity?,
    val nextActivity: LinkedActivity?,
    val plan: Schedule
) {


    val startLocation get() = previousActivity?.endLocation ?: legs.first().startLocation
    val endLocation get() = nextActivity?.startLocation ?: legs.last().endLocation


    val duration get() = legs.last().endTime - legs.first().startTime
    fun forceChange(to: SortedSet<Leg>) {
        //TODO this method should verify consistency of "to"
        if (to.isEmpty()) {
            legs.forEach {
                plan.plan.removeLeft(it)
            }
        } else {
            val newSet = TreeSet(to.map { LinkedLeg(it, plan) })
//            plan.replaceAll()
//            plan.
//            legs.clear()
//            legs.addAll(newSet)

        }


    }
    fun isConsistent() = legs.isConsistent()
    fun adapt(lambda: TripBuilder.() -> Unit) {
        val builder = TripBuilder(this)
        builder.apply(lambda)
        val target = builder.build()
        require(target.isNotEmpty()) {
            "Cannot work with an empty list of legs for a trip."
        }
        val fitsStartLocation = previousActivity?.location == target.first().startLocation
        val fitsEndLocation = nextActivity?.location == target.last().endLocation
        val fitsStartTime = previousActivity?.endTime?.let { it <= target.first().startTime } ?: true
        val fitsEndTime = nextActivity?.startTime?.let { it >= target.last().startTime } ?: true
        if(fitsStartLocation && fitsEndLocation && fitsStartTime && fitsEndTime) {
            forceChange(target)
        } else{
            println("Could not change trip as it does not fit within the plan")
        }
    }

    fun forceDelete() {
        forceChange(sortedSetOf())
    }

    fun isEmpty() = legs.isEmpty()


    override fun toString(): String {
        return legs.toString()
    }
}
//typealias Pause = Duration

@JvmInline
value class Pause(val duration: Duration)
class TripBuilder(val trip: Trip) {
    private val legs = sortedSetOf<Leg>()
    private var currentTime = trip.previousActivity?.endTime ?: Duration.ZERO
    private var currentLocation = trip.startLocation

    val previousLocation = trip.startLocation

    fun addPause(duration: Duration) {
        currentTime += duration
    }
    fun addLeg(target: Location, duration: Duration) {
        legs.add(RawLeg(startTime = currentTime, duration=duration, currentLocation, endLocation = target))
        currentLocation = target
        currentTime += duration
    }
    fun build() = legs

}
