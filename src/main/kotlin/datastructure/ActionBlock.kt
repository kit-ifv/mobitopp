package datastructure

import java.util.*

/**
 * An [ActionBlock] contains a navigable set of items of type [T]. Unlike the specific implementations the action block
 * does not know the type of the [previous] and [next] block.
 */
abstract class ActionBlock<T : LinkedAction> : Comparable<ActionBlock<*>> {
    internal abstract val item: NavigableSet<T>
    abstract val next: ActionBlock<*>?
    abstract val previous: ActionBlock<*>?

    /**
     * Adds an activity to the action block. Returns true if the structure of the block list changes and the relevant
     * views should be updated. Returns false if no update is required
     */
    abstract fun insert(activity: Activity): Pair<LegBlock, ActivityBlock>?

    /**
     * Adds a leg to the action block. Returns true if the structure of the block list changes and the relevant
     * views should be updated. Returns false if no update is required
     */
    abstract fun insert(leg: Leg): Pair<LegBlock, ActivityBlock>?

    abstract fun accepts(action: StationaryAction): Boolean
    abstract fun accepts(action: MovingAction): Boolean

    fun isEmpty() = item.isEmpty()
    fun removeFirst(): T? = item.pollFirst()
    fun isConsistent(): Boolean {
        val prev = previous?.item?.lastOrNull()
        val succ = next?.item?.firstOrNull()
        return (listOf(prev) + item + succ).filterNotNull().isConsistent()
    }

    fun contains(element: T): Boolean = item.contains(element)
    fun bounds(elements: Collection<Action>): Boolean {
        val sortedSet = elements.toSortedSet()
        if (sortedSet.isEmpty()) return false
        return sortedSet.first() >= firstElement() && sortedSet.last() <= lastElement()
    }

    /**
     * Implementations of action block are iterables (which are holding iterables). In order to differentiate between
     * the block iteration and the set iteration on [item] methods are labeled with xxxElement to highlight a difference
     * between iteration targets.
     */
    fun firstElement(): T = item.first()
    fun lastElement(): T = item.last()

    fun firstElementOrNull(): T? = item.firstOrNull()
    fun lastElementOrNull(): T? = item.lastOrNull()

    override fun compareTo(other: ActionBlock<*>): Int {
        return lastElement().compareTo(other.firstElement())
    }

    fun containsAction(action: Action): Boolean {
        return item.any { it.compareTo(action) == 0 }
    }

    fun lower(other: LinkedAction): LinkedAction? {
        return item.lower(item.find { it == other }) ?: previous?.lastElementOrNull()
    }

    fun higher(other: LinkedAction): LinkedAction? {
        return item.higher(item.find { it == other }) ?: next?.firstElementOrNull()
    }

    abstract fun clear()

}

class ActivityBlock(
    start: NavigableSet<LinkedActivity>,
    override var next: LegBlock? = null,
    override var previous: LegBlock? = null
) :
    ActionBlock<LinkedActivity>(), Iterable<ActivityBlock> {

        override val item: NavigableSet<LinkedActivity> = sortedSetOf()
    init {

        item.addAll(start.map { LinkedActivity(it.original, ::lower, ::higher) })
    }
    constructor() : this(sortedSetOf())
    constructor(activity: LinkedActivity) : this(sortedSetOf(activity))

    override fun toString(): String {
        return item.joinToString { it.toString() }
    }

    override fun clear() {
        item.clear()
        unlink()
    }


    override fun insert(activity: Activity): Pair<LegBlock, ActivityBlock>? {
        val a = LinkedActivity(
            activity,
            ::lower,
            ::higher
        )
        item.add(a)

        return null
    }

    fun replaceAll(delete: Set<Activity>, target: SortedSet<Activity>) {
        item.removeAll(item.filter { it.original in delete }.toSet())
        item.addAll(
            target.map {
                LinkedActivity(
                    it,
                    ::lower,
                    ::higher
                )
            }
        )
    }

    override fun insert(leg: Leg): Pair<LegBlock, ActivityBlock>? {
        val a = item.find { it.startTime >= leg.startTime }
        val targets = if (a == null) sortedSetOf<LinkedActivity>() else TreeSet(item.tailSet(a, true))
        val newLegBlock = LegBlock()
        val leeeg = LinkedLeg(
            leg,
            newLegBlock::lower,
            newLegBlock::higher
        )
        newLegBlock.insert(leeeg)
        val newActivityBlock = ActivityBlock(targets)

        val successor = next

        item.removeAll(targets)

        next = newLegBlock
        newLegBlock.previous = this
        newLegBlock.next = newActivityBlock
        newActivityBlock.previous = newLegBlock
        newActivityBlock.next = successor
        successor?.previous = newActivityBlock
        return newLegBlock to newActivityBlock
    }

    private fun unlink() {
        val prevLeg = previous!!
        val nextLeg = next!!

        val nextActivity = nextLeg.next
        prevLeg.item.addAll(nextLeg.item)

        prevLeg.next = nextActivity
        nextActivity.previous = prevLeg

        // Removing other links for GC support

//        dispatcher = null
        next = null
        previous = null

//        nextLeg.dispatcher = null
        nextLeg.previous = this
        nextLeg.next = this
    }

    fun remove(activity: Activity): Boolean {
        item.remove(item.find { it.original == activity })
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

class LegBlock(start: NavigableSet<LinkedLeg>) :
    ActionBlock<LinkedLeg>(), Iterable<LegBlock> {
    override val item: NavigableSet<LinkedLeg> = sortedSetOf()
        init {
            item.addAll(start.map{ LinkedLeg(it.original, ::lower, ::higher) })

        }
    constructor() : this(sortedSetOf())
    constructor(leg: LinkedLeg) : this(sortedSetOf(leg))

    override lateinit var next: ActivityBlock
    override lateinit var previous: ActivityBlock

    /**
     * Returns an iterator over the elements of this object.
     */
    override fun iterator(): Iterator<LegBlock> {
        return object : Iterator<LegBlock> {
            var current: LegBlock? = null
            var nextIteratorElement: LegBlock? = this@LegBlock

            /**
             * Returns `true` if the iteration has more elements.
             */
            override fun hasNext(): Boolean {
                return current?.let { nextIteratorElement != null } ?: true
            }

            /**
             * Returns the next element in the iteration.
             */
            override fun next(): LegBlock {
                current = nextIteratorElement
                nextIteratorElement = current?.next?.next
                return current ?: throw NoSuchElementException()
            }
        }
    }

    override fun clear() {
        item.clear()
        unlink()
    }

    /**
     * Inserts an activity into this leg block. As a leg block cannot maintain activities two additional Blocks are
     * spawned <OriginalBlock> -> (NewActivityBlock) -> <NewLegBlock>. The [activity] is inserted into the newly created
     * block. All legs from the original set that are
     */
    override fun insert(activity: Activity): Pair<LegBlock, ActivityBlock> {
        require(
            item.none {
                it.compareTo(activity) == 0
            }
        ) { "A leg overlaps with the target activity. This case cannot be handled" }

        val l = item.find { it.startTime >= activity.startTime } ?: item.last()
        val targets = TreeSet(item.tailSet(l, true))

        val newLegBlock = LegBlock(targets)
        val newActivityBlock = ActivityBlock()
        val linkA = LinkedActivity(
            activity,
            newActivityBlock::lower,
            newActivityBlock::higher
        )
        newActivityBlock.insert(linkA)

        val successor = next

        item.removeAll(targets)

        next = newActivityBlock
        newLegBlock.previous = newActivityBlock
        newLegBlock.next = successor
        newActivityBlock.previous = this
        newActivityBlock.next = newLegBlock
        successor.previous = newLegBlock
        return newLegBlock to newActivityBlock
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

    override fun insert(leg: Leg): Pair<LegBlock, ActivityBlock>? {
        item.add(
            LinkedLeg(
                leg,
                ::lower,
                ::higher
            )
        )
        return null
    }

    private fun unlink() {
        val previous = previous
        val next = next
        val overNext = next.next
        previous.item.addAll(next.item)

        previous.next = overNext
        overNext?.previous = previous
        // TODO find solution to point next and previous to something else, or drop existence invariant

//        dispatcher = null
        this.next = next
        this.previous = next

//        next.dispatcher = null
        next.next = null
        next.previous = null
    }

    internal fun replaceAll(target: Collection<Leg>, elements: Collection<Leg>) {
        require(elements.isNotEmpty()) { "Doesn't make sense to replace with nothing " }

        item.removeAll(item.filter { it.original in target }.toSet())
        item.addAll(
            elements.map {
                LinkedLeg(
                    it,
                    ::lower,
                    ::higher
                )
            }
        )
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
        if (other !is LegBlock) return false
        return next == other.next && item.zip(other.item).all { (a, b) -> a == b }
    }

    override fun hashCode(): Int {
        return item.hashCode()
    }
}
