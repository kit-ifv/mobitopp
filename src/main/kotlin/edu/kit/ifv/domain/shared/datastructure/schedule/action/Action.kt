package edu.kit.ifv.domain.shared.datastructure.schedule.action
import edu.kit.ifv.domain.shared.datastructure.schedule.ActionVisitor
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.utils.units.AbsoluteTime
import kotlin.time.Duration

/**
 * An Action is the central aspect of mobility behaviour in this simulation mobitopp. In its simplest form an action
 * consists of a [startLocation] and a [startTime], as well as an [endLocation] and an [endTime]. In this modelling
 * approach, an action is an atomic operation - There are no smaller representations of sub-actions. It is your duty
 * when modelling future problems to break your domain down to this level.
 *
 * Note that an action only holds location and time information, your implementation must provide additional details
 * should you require them.
 */
sealed interface Action : Comparable<Action> {
    val startTime: AbsoluteTime
    val actionType: ActionType

    /**
     * [duration] is a derived property of an action by the difference of [endTime] and [startTime]. If you see
     * that your code is relying heavily on this property it might be prudent to implement your own Action with
     * a backing field rather than a derived property
     */
    val duration: Duration get() = endTime - startTime
    val endTime: AbsoluteTime

    val startLocation: StandardLocation
    val endLocation: StandardLocation

    val earliestStartTime: AbsoluteTime
    val latestEndTime: AbsoluteTime

    /*Intervals do not form a well-defined order, we require a more idiomatic way of representing this fact

    maybe use a separate comparator?

     As in x <= y && x >= y => x == y is violated by intervals. We only have a partial order but Comparable induces
     total ordering */
    @Suppress("ReturnCount")
    /* The speed benefit of early returns clearly outweighs the fact that there are
     3 (the horror) return statements rather than 2. I think that this method may be simple enough that a foreign
     reader will understand what exactly is happening
     */
    override fun compareTo(other: Action): Int {
        if (startTime == other.startTime && endTime == other.endTime) {
            return 0
        }
        if (endTime <= other.startTime) return -1
        if (other.endTime <= startTime) return 1
        return 0
    }

    fun <T> accept(actionVisitor: ActionVisitor<T>): T
    operator fun compareTo(time: AbsoluteTime): Int {
        /*
            if (endTime < time) return -1
            if (startTime > time) return 1
            return 0

            in branch less
         */
        return (startTime > time).compareTo(endTime < time)
    }
}

/**
 * Strong consistency occurs when the actions are weakly consistent and all actions are well structured.
 */
fun Iterable<Action>.isConsistent(): Boolean = isWeaklyConsistent() && all { it.startTime <= it.endTime }

/**
 * This method verifies the consistency of any iterable of actions by having continuous locations and no time interval
 * overlaps
 *
 */
fun Iterable<Action>.isWeaklyConsistent(): Boolean = zipWithNext { first, second ->
    first.endLocation == second.startLocation && first.endTime <= second.startTime
}.all { it }

fun Iterable<Action>.hasTimeBoundViolations(): Boolean = any {
    it.startTime < it.earliestStartTime || it.endTime > it.latestEndTime
}

operator fun Iterable<Action>.contains(action: Action): Boolean = any { it.compareTo(action) == 0 }
