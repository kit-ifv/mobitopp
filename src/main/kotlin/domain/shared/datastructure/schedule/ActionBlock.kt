package domain.shared.datastructure.schedule

import domain.shared.datastructure.schedule.plans.IDispatcher
import domain.shared.location.Location
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
    abstract fun insert(
        activity: Activity,
        callback: SortedSet<LinkedActivity>? = null
    ): LinkedActivity?

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

    override fun compareTo(other: ActionBlock<*>): Int {
        return item.last().compareTo(other.firstElement())
    }

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

    private fun lower(other: Action): LinkedAction? {
        return item.lastOrNull { it.original < other } ?: previous?.lastElementOrNull()
    }

    private fun higher(other: Action): LinkedAction? {
        return item.firstOrNull { it.original > other } ?: next?.firstElementOrNull()
    }

    abstract fun clear()

    abstract fun unlink()
    fun link(activity: Activity): LinkedActivity {
        return activity.link(lower(activity), higher(activity))
    }

    fun link(leg: Leg): LinkedLeg {
        return leg.link(lower(leg), higher(leg))
    }

    /**
     * Generates a readonly view of the target action block. A schedule can be passed if the view is required to be
     * built with knowledge of the past (handled actions)
     */
    abstract fun representative(dispatcher: IDispatcher? = null, schedule: Schedule? = null): Representative<T>
}

/** An [ActivityBlock] is an instantiation of an [ActionBlock] holding a set of [Activity]. It also holds a reference
 * to the preceding and succeeding [LinkedTrip], if they exist.
 */
class ActivityBlock(
    start: NavigableSet<LinkedActivity>,
    override var next: LinkedTrip? = null,
    override var previous: LinkedTrip? = null
) :
    ActionBlock<LinkedActivity>(), Iterable<ActivityBlock> {

    override val item: NavigableSet<LinkedActivity> = sortedSetOf()

    init {
        start.forEach {
            item.add(link(it))
        }
    }

    constructor() : this(sortedSetOf())
    val location get() = item.first.location
    override fun toString(): String {
        return item.joinToString { it.toString() }
    }

    override fun clear() {
        item.forEach { it.unlink() }
        item.clear()
        unlink()
    }

    override fun representative(
        dispatcher: IDispatcher?,
        schedule: Schedule?
    ): Representative<LinkedActivity> = RawAgenda(
        this
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

    override fun accepts(action: StationaryAction): Boolean {
        return !containsAction(action) && next?.item?.firstOrNull()?.startTime?.let { it >= action.endTime } ?: true
    }

    /**
     * This block must accept a leg if either no followup block exists or if the leg is smaller than the last element
     */
    override fun accepts(action: MovingAction): Boolean {
        return !containsAction(action) && (next == null || item.lastOrNull()?.let { it > action } ?: false)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ActivityBlock) return false
        return next == other.next && item.zip(other.item).all { (a, b) -> a == b }
    }

    override fun hashCode(): Int {
        return item.hashCode()
    }

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
            override fun hasNext(): Boolean {
                return current?.let { nextIteratorElement != null } ?: true
            }

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

class LinkedTrip(
    start: Collection<LinkedLeg>,
    override var previous: ActivityBlock,
    override var next: ActivityBlock
) :
    ActionBlock<LinkedLeg>(), Iterable<LinkedTrip> {
    override var item: NavigableSet<LinkedLeg> = sortedSetOf()

    init {
        start.forEach {
            item.add(link(it))
        }
    }

    val startLocation: Location get() = item.first().startLocation
    var endLocation: Location get() = item.last().endLocation
        set(value) {
            item.last().endLocation = value
        }

    fun isUseless() = startLocation == endLocation

    constructor(previous: ActivityBlock, next: ActivityBlock) : this(emptyList(), previous, next)

    /**
     * Returns an iterator over the elements of this object.
     */
    override fun iterator(): Iterator<LinkedTrip> {
        return object : Iterator<LinkedTrip> {
            var current: LinkedTrip? = null
            var nextIteratorElement: LinkedTrip? = this@LinkedTrip

            /**
             * Returns `true` if the iteration has more elements.
             */
            override fun hasNext(): Boolean {
                return current?.let { nextIteratorElement != null } ?: true
            }

            /**
             * Returns the next element in the iteration.
             */
            override fun next(): LinkedTrip {
                current = nextIteratorElement
                nextIteratorElement = current?.next?.next
                return current ?: throw NoSuchElementException()
            }
        }
    }

    override fun clear() {
        item.forEach { it.unlink() }
        item.clear()
        unlink()
    }

    override fun representative(dispatcher: IDispatcher?, schedule: Schedule?): LinkTrip = LinkTrip(
        this,
        dispatcher,
        schedule
    )

    /**
     * Inserts an activity into this leg block. As a leg block cannot maintain activities two additional Blocks are
     * spawned <OriginalBlock> -> (NewActivityBlock) -> <NewLegBlock>. The [activity] is inserted into the newly created
     * block. All legs from the original set that are too large are moved to the new block
     */
    override fun insert(activity: Activity, callback: SortedSet<LinkedActivity>?): LinkedActivity? {
        require(
            item.none {
                it.compareTo(activity) == 0
            }
        ) { "A leg overlaps with the target activity. This case cannot be handled" }

        val l = item.find { it.startTime >= activity.startTime } ?: item.last()
        val targets = TreeSet(item.tailSet(l, true))

        val newActivityBlock = ActivityBlock()
        val newLegBlock = LinkedTrip(targets, newActivityBlock, next)

        val successor = next

        item.removeAll(targets)

        next = newActivityBlock
        newLegBlock.previous = newActivityBlock
        newLegBlock.next = successor
        newActivityBlock.previous = this
        newActivityBlock.next = newLegBlock
        successor.previous = newLegBlock
        return newActivityBlock.insert(activity, callback)
    }

    override fun accepts(action: StationaryAction): Boolean {
        return !containsAction(action) && item.size >= 2 && item.first() < action && item.last() > action
    }

    override fun accepts(action: MovingAction): Boolean {
        return !containsAction(action) && next.item.firstOrNull()?.startTime?.let { it >= action.endTime } ?: true
    }

    override fun toString(): String {
        return item.joinToString { it.toString() }
    }

    override fun insert(leg: Leg): Pair<LinkedTrip, ActivityBlock>? {
        item.add(link(leg))
        return null
    }

    override fun unlink() {
        val previous = previous
        val next = next
        val overNext = next.next
        previous.item.addAll(next.item)

        previous.next = overNext
        overNext?.previous = previous

        this.next = next
        this.previous = next

        next.next = null
        next.previous = null
    }

    /**
     * If the replacement happens within a leg block only, the replacement can be done internally, instead of inserting
     * each element on its own a bulk operation can be performed.
     */
    internal fun replaceAll(target: Collection<Leg>, elements: Collection<Leg>) {
        require(elements.isNotEmpty()) { "Doesn't make sense to replace with nothing " }
        val previous = item.first().previous
        target.mapNotNull { act -> item.find { it.original == act } }.forEach {
            removeLinked(it)
        }
        val retainElements = item.filter { it !in target }
        val links = (elements.map { LinkedLeg(it) } + retainElements).sorted()
        links.zipWithNext { a, b ->
            a.next = b
            b.previous = a
        }
        previous?.next = links.first()
        links.first().previous = previous
        links.last().next = next.firstElementOrNull()
        next.firstElementOrNull()?.previous = links.last()
        item = TreeSet(links)
    }

    private fun removeLinked(linkedLeg: LinkedLeg) {
        linkedLeg.let {
            it.unlink()
            item.remove(it)
        }
    }

    fun remove(element: MovingAction): Boolean {
        item.remove(element)
        if (item.isEmpty()) {
            unlink()
            return true
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (other !is LinkedTrip) return false
        return next == other.next && item.zip(other.item).all { (a, b) -> a == b }
    }

    override fun hashCode(): Int {
        return item.hashCode()
    }
}
