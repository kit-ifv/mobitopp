package edu.kit.ifv.domain.shared.datastructure.schedule.blocks
import edu.kit.ifv.domain.shared.datastructure.schedule.LinkTrip
import edu.kit.ifv.domain.shared.datastructure.schedule.Schedule
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Activity
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Leg
import edu.kit.ifv.domain.shared.datastructure.schedule.action.LinkedActivity
import edu.kit.ifv.domain.shared.datastructure.schedule.action.LinkedLeg
import edu.kit.ifv.domain.shared.datastructure.schedule.action.MovingAction
import edu.kit.ifv.domain.shared.datastructure.schedule.action.StationaryAction
import edu.kit.ifv.domain.shared.datastructure.schedule.plans.IDispatcher
import edu.kit.ifv.domain.shared.location.StandardLocation
import java.util.*

class LinkedTrip(start: Collection<LinkedLeg>, override var previous: ActivityBlock, override var next: ActivityBlock) :
    ActionBlock<LinkedLeg>(),
    Iterable<LinkedTrip> {
    override var item: NavigableSet<LinkedLeg> = sortedSetOf()

    init {
        start.forEach {
            item.add(link(it))
        }
    }

    val startLocation: StandardLocation get() = item.first().startLocation
    var endLocation: StandardLocation
        get() = item.last().endLocation
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
            override fun hasNext(): Boolean = current?.let { nextIteratorElement != null } ?: true

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
        schedule,
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
            },
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

    override fun accepts(action: StationaryAction): Boolean = !containsAction(
        action,
    ) && item.size >= 2 && item.first() < action && item.last() > action

    override fun accepts(action: MovingAction): Boolean = !containsAction(
        action,
    ) && next.item.firstOrNull()?.startTime?.let {
        it >= action.endTime
    } ?: true

    override fun toString(): String = item.joinToString { it.toString() }

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

    override fun hashCode(): Int = item.hashCode()
}
