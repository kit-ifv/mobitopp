package domain.shared.datastructure.schedule.blocks

import domain.shared.datastructure.schedule.Representative
import domain.shared.datastructure.schedule.Schedule
import domain.shared.datastructure.schedule.action.Action
import domain.shared.datastructure.schedule.action.Activity
import domain.shared.datastructure.schedule.action.Leg
import domain.shared.datastructure.schedule.action.LinkedAction
import domain.shared.datastructure.schedule.action.LinkedActivity
import domain.shared.datastructure.schedule.action.LinkedLeg
import domain.shared.datastructure.schedule.action.MovingAction
import domain.shared.datastructure.schedule.action.StationaryAction
import domain.shared.datastructure.schedule.action.isConsistent
import domain.shared.datastructure.schedule.plans.IDispatcher
import utils.collections.iterate
import java.util.*

/**
 * An [ActionBlock] contains a navigable set of items of type [T]. Unlike the specific implementations the action block
 * does not know the type of the [previous] and [next] block.
 */
@Suppress("TooManyFunctions") // I have no good idea what to refactor out
abstract class ActionBlock<T : LinkedAction> : Comparable<ActionBlock<*>> {
    internal abstract val item: NavigableSet<T>
    abstract val next: ActionBlock<*>?
    abstract val previous: ActionBlock<*>?

    val size get() = item.size

    /**
     * Adds an activity to the action block. Returns true if the structure of the block list changes and the relevant
     * views should be updated. Returns false if no update is required
     */
    abstract fun insert(activity: Activity, callback: SortedSet<LinkedActivity>? = null): LinkedActivity?

    /**
     * Adds a leg to the action block. Returns true if the structure of the block list changes and the relevant
     * views should be updated. Returns false if no update is required
     */
    abstract fun insert(leg: Leg): Pair<LinkedTrip, ActivityBlock>?

    /**
     * Determines whether the block should accept an insertion of the target action.
     */
    abstract fun accepts(action: StationaryAction): Boolean

    /**
     * Determines whether the block should accept an insertion of the target action.
     */
    abstract fun accepts(action: MovingAction): Boolean

    fun isEmpty() = item.isEmpty()
    fun removeFirst(): T? {
        val result = item.pollFirst()
        if (isEmpty() && previous != null) {
            unlink()
        }
        return result
    }

    /**
     * Checks whether the block itself is consistent
     */
    fun isConsistent(): Boolean {
        val previous = previous?.item?.lastOrNull()
        val next = next?.item?.firstOrNull()
        return (previous.iterate(item) + next).filterNotNull().isConsistent()
    }

    fun contains(element: T): Boolean = item.contains(element)
    fun bounds(elements: Collection<Action>): Boolean {
        val sortedSet = elements.toSortedSet()
        if (sortedSet.isEmpty()) return false
        if (this.isEmpty()) {
            return false
        }
        val firstFits = previous?.lastElementOrNull()?.let { it <= sortedSet.first() } != false
        val lastFits = next?.firstElementOrNull()?.let { it >= sortedSet.last() } != false
        return firstFits && lastFits
    }

    /**
     * Implementations of action block are iterables (which are holding iterables). In order to differentiate between
     * the block iteration and the set iteration on [item] methods are labeled with xxxElement to highlight a difference
     * between iteration targets.
     */
    fun firstElement(): T = item.first()

    fun firstElementOrNull(): T? = item.firstOrNull()
    fun lastElementOrNull(): T? = item.lastOrNull()

    override fun compareTo(other: ActionBlock<*>): Int = item.last().compareTo(other.firstElement())

    fun compareTo(action: Action): Int {
        if (item.isEmpty()) return -1
        return (firstElement().compareTo(action) + item.last().compareTo(action)) / 2
    }

    @Suppress("ReturnCount") // For speed purposes this method contains multiple returns.
    fun containsAction(action: Action): Boolean {
        if (item.size == 0) return false
        if (item.first() > action) return false
        if (item.last() < action) return false
        return item.any { it.compareTo(action) == 0 }
    }

    private fun lower(other: Action): LinkedAction? = item.lastOrNull {
        it.original < other
    } ?: previous?.lastElementOrNull()

    private fun higher(other: Action): LinkedAction? = item.firstOrNull {
        it.original > other
    } ?: next?.firstElementOrNull()

    abstract fun clear()

    abstract fun unlink()
    fun link(activity: Activity): LinkedActivity = activity.link(lower(activity), higher(activity))

    fun link(leg: Leg): LinkedLeg = leg.link(lower(leg), higher(leg))

    /**
     * Generates a readonly view of the target action block. A schedule can be passed if the view is required to be
     * built with knowledge of the past (handled actions)
     */
    abstract fun representative(dispatcher: IDispatcher? = null, schedule: Schedule? = null): Representative<T>
}
