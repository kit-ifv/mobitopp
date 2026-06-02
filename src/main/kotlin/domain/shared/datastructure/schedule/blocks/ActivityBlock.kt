package domain.shared.datastructure.schedule.blocks

import domain.shared.datastructure.schedule.RawAgenda
import domain.shared.datastructure.schedule.Representative
import domain.shared.datastructure.schedule.Schedule
import domain.shared.datastructure.schedule.action.Activity
import domain.shared.datastructure.schedule.action.Leg
import domain.shared.datastructure.schedule.action.LinkedActivity
import domain.shared.datastructure.schedule.action.MovingAction
import domain.shared.datastructure.schedule.action.StationaryAction
import domain.shared.datastructure.schedule.plans.IDispatcher
import java.util.NavigableSet
import java.util.SortedSet
import java.util.TreeSet

/** An [ActivityBlock] is an instantiation of an [ActionBlock] holding a set of [domain.shared.datastructure.schedule.action.Activity]. It also holds a reference
 * to the preceding and succeeding [LinkedTrip], if they exist.
 */
class ActivityBlock(
    start: NavigableSet<LinkedActivity>,
    override var next: LinkedTrip? = null,
    override var previous: LinkedTrip? = null,
) : ActionBlock<LinkedActivity>(),
    Iterable<ActivityBlock> {

    override val item: NavigableSet<LinkedActivity> = sortedSetOf()

    init {
        start.forEach {
            item.add(link(it))
        }
    }

    constructor() : this(sortedSetOf())
    val location get() = item.first.location
    override fun toString(): String = item.joinToString { it.toString() }

    override fun clear() {
        item.forEach { it.unlink() }
        item.clear()
        unlink()
    }

    override fun representative(dispatcher: IDispatcher?, schedule: Schedule?): Representative<LinkedActivity> =
        RawAgenda(
            this,
        )

    override fun insert(activity: Activity, callback: SortedSet<LinkedActivity>?): LinkedActivity? {
        val element = link(activity)
        val inserted = item.add(element)
        callback?.add(element)

        return if (inserted) element else null
    }

    private fun removeLinked(linkedActivity: LinkedActivity) {
        linkedActivity.let {
            it.unlink()
            item.remove(it)
        }
    }

    fun replaceAll(delete: Set<Activity>, target: SortedSet<Activity>) {
        delete.mapNotNull { act -> item.find { it.original == act } }.forEach {
            removeLinked(it)
        }
        target.forEach { insert(it) }
    }

    override fun insert(leg: Leg): Pair<LinkedTrip, ActivityBlock> {
        val a = item.find { it.startTime >= leg.startTime }
        val targets = if (a == null) sortedSetOf<LinkedActivity>() else TreeSet(item.tailSet(a, true))
        val newActivityBlock = ActivityBlock(targets)
        val newLegBlock = LinkedTrip(this, newActivityBlock)

        val successor = next

        next = newLegBlock
        newLegBlock.previous = this
        newLegBlock.next = newActivityBlock
        newActivityBlock.previous = newLegBlock
        newActivityBlock.next = successor
        successor?.previous = newActivityBlock
        // This order is relevant (targets should only be removed after the re-linking because otherwise sorting breaks)
        item.removeAll(targets)
        newLegBlock.insert(leg)
        return newLegBlock to newActivityBlock
    }

    /**
     * Unlinking an action block requires additional work to keep consistency. When an action block A is removed the
     * original chain of A(prev) T1 -> A -> T2 (A_next) must be cleaned up: T2 serves no purpose anymore, since A is
     * cancelled. The T1 trip must instead redirect from A(prev) to A(next). If these two activity blocks happen at
     * the same location, then T1 is also without purpose, and should be removed.
     */
    override fun unlink() {
        val previousTrip = previous!!
        val nextTrip = next!!

        val nextActivity = nextTrip.next
        previousTrip.endLocation = nextActivity.location

        previousTrip.next = nextActivity
        nextActivity.previous = previousTrip

        // Removing other links for GC support

        next = null
        previous = null
        // Since these references cannot be null, we instead redirect them to [this] which will be removed from the
        // list, and hopefully GC collected at some point.
        nextTrip.previous = this
        nextTrip.next = this
        nextTrip.clear()

        if (previousTrip.isUseless()) {
            previousTrip.clear()
        }
    }

    fun remove(activity: Activity): Boolean {
        val target = item.find { it.original == activity }
        target?.let { removeLinked(it) }
        if (item.isEmpty() && previous != null && next != null) {
            unlink()
            return true
        }
        return false
    }

    override fun accepts(action: StationaryAction): Boolean = !containsAction(
        action,
    ) && next?.item?.firstOrNull()?.startTime?.let {
        it >= action.endTime
    } ?: true

    /**
     * This block must accept a leg if either no followup block exists or if the leg is smaller than the last element
     */
    override fun accepts(action: MovingAction): Boolean = !containsAction(action) && (
        next == null || item.lastOrNull()?.let {
            it > action
        } ?: false
        )

    override fun equals(other: Any?): Boolean {
        if (other !is ActivityBlock) return false
        return next == other.next && item.zip(other.item).all { (a, b) -> a == b }
    }

    override fun hashCode(): Int = item.hashCode()

    /**
     * Returns an iterator over the elements of this object.
     */
    override fun iterator(): Iterator<ActivityBlock> {
        return object : Iterator<ActivityBlock> {
            var current: ActivityBlock? = null
            var nextIteratorElement: ActivityBlock? = this@ActivityBlock

            /**
             * Returns `true` if the iteration has more elements.
             */
            override fun hasNext(): Boolean = current?.let { nextIteratorElement != null } ?: true

            /**
             * Returns the next element in the iteration.
             */
            override fun next(): ActivityBlock {
                current = nextIteratorElement
                nextIteratorElement = current?.next?.next
                return current ?: throw NoSuchElementException()
            }
        }
    }
}
