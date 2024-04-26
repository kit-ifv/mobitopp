package datastructure

import utils.collections.isStrictlySorted
import java.util.*
import kotlin.time.Duration


abstract class ActionBlock<T : Action> {
    internal abstract val item: NavigableSet<T>
    abstract val next: ActionBlock<*>?
    abstract val previous: ActionBlock<*>?
    abstract val dispatcher: Dispatcher?
    /**
     * Adds an activity to the action block. Returns true if the structure of the block list changes and the relevant
     * views should be updated. Returns false if no update is required
     */
    abstract fun insert(activity: Activity): Boolean
    /**
     * Adds a leg to the action block. Returns true if the structure of the block list changes and the relevant
     * views should be updated. Returns false if no update is required
     */
    abstract fun insert(leg: Leg): Boolean



    abstract fun rejects(action: Activity): Boolean
    abstract fun rejects(action: Leg): Boolean


    abstract fun isConsistent(): Boolean

    fun contains(element: T): Boolean = item.contains(element)
    fun bounds(elements: Collection<Action>): Boolean {

        val sortedSet = elements.toSortedSet()

        return sortedSet.first() >= first() && sortedSet.last() <= last()
    }

    fun first(): T = item.first()
    fun last(): T = item.last()
//
//    override fun add(element: T): Boolean {
//        return item.add(element)
//    }
//
//
//    override fun addAll(elements: Collection<T>): Boolean {
//        return item.addAll(elements)
//    }
//
//
//    override fun pollFirst(): T? {
//        val poll = item.pollFirst()
//
//        return poll
//    }
//
//    override fun pollLast(): T? {
//        val poll = item.pollLast()
//
//        return poll
//    }
}

class ActivityBlock(override val item: NavigableSet<Activity>, override var dispatcher: Dispatcher?) :
    ActionBlock<Activity>(),  Iterable<ActivityBlock> {
    constructor(activity: Activity, dispatcher: Dispatcher?) : this(sortedSetOf(activity), dispatcher)

    override var next: LegBlock? = null
    override var previous: LegBlock? = null

    override fun toString(): String {
        return item.joinToString { it.toString() }
    }
    //Required Overwrite to prevent name shadowing from NavigableSet<Activity>
//    override fun add(element: Activity) = super.add(element)
//    override fun addAll(elements: Collection<Activity>) = super.addAll(elements)
//    override fun pollFirst() = super.pollFirst()
//
//    override fun pollLast() = super.pollLast()


    override fun insert(activity: Activity): Boolean {
        item.add(activity)

        return false
    }
    fun replaceAll(target: SortedSet<Activity>) {
        item.clear()
        item.addAll(target)
    }
    override fun insert(leg: Leg): Boolean {

        val a = item.find { it.startTime >= leg.startTime }
        val targets = if (a == null) sortedSetOf<Activity>() else TreeSet(item.tailSet(a, true))

        val newLegBlock = LegBlock(leg, dispatcher)
        val newActivityBlock = ActivityBlock(targets, dispatcher)

        val succ = next

        item.removeAll(targets)

        next = newLegBlock
        newLegBlock.previous = this
        newLegBlock.next = newActivityBlock
        newActivityBlock.previous = newLegBlock
        newActivityBlock.next = succ
        succ?.previous = newActivityBlock
        return true
    }

    private fun unlink() {
        val prevLeg = previous!!
        val nextLeg = next!!

        val nextActivity = nextLeg.next
        prevLeg.item.addAll(nextLeg.item)

        prevLeg.next = nextActivity
        nextActivity.previous = prevLeg

        //Removing other links for GC support

        dispatcher = null
        next = null
        previous = null

        nextLeg.dispatcher = null
        nextLeg.previous = this
        nextLeg.next = this


    }

     fun remove(activity: Activity): Boolean {
        item.remove(activity)
        if (item.isEmpty() && previous != null && next != null) {
            unlink()
            return true
        }
        return false
    }

    override fun rejects(action: Activity): Boolean {
        return next?.item?.firstOrNull()?.startTime?.let { it < action.endTime } ?: false
    }

    override fun rejects(action: Leg): Boolean {
        return next != null && (item.size < 2 || item.first() >= action || item.last() <= action)
    }


    override fun isConsistent(): Boolean {
        val previousLeg = previous?.last()
        val nextLeg = next?.first()
        val actions = (listOf(previousLeg) + item.toList() + nextLeg).filterNotNull()
        return actions.isStrictlySorted()
    }


//    override fun clear() {
//        item.clear()
//        unlink()
//    }
//
//    override fun retainAll(elements: Collection<Activity>): Boolean {
//        if (elements.isEmpty()) {
//            clear()
//            return true
//        }
//        val diff = item - elements.toSet()
//        val b = item.retainAll(elements.toSet())
//        return b
//    }
//
//
//    override fun removeAll(elements: Collection<Activity>): Boolean {
//        val b = item.removeAll(elements.toSet())
//        if (isEmpty()) {
//            clear()
//        }
//        return b
//    }


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
        return object: Iterator<ActivityBlock>{
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

class LegBlock(override val item: NavigableSet<Leg>, override var dispatcher: Dispatcher?): ActionBlock<Leg>(), Iterable<LegBlock>{
    constructor(leg: Leg, dispatcher: Dispatcher?) : this(sortedSetOf(leg), dispatcher)

    override lateinit var next: ActivityBlock
    override lateinit var previous: ActivityBlock

    /**
     * Returns an iterator over the elements of this object.
     */
    override fun iterator(): Iterator<LegBlock> {
        return object: Iterator<LegBlock> {
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
//    override fun add(element: Leg) = super.add(element)
//    override fun addAll(elements: Collection<Leg>) = super.addAll(elements)
//    override fun pollFirst() = super.pollFirst()
//
//    override fun pollLast() = super.pollLast()
    override fun insert(activity: Activity): Boolean {


        val l = item.find { it.startTime >= activity.startTime } ?: item.last()
        val targets = TreeSet(item.tailSet(l, true))


        val newLegBlock = LegBlock(targets, dispatcher)
        val newActivityBlock = ActivityBlock(activity, dispatcher)

        val succ = next

        item.removeAll(targets)

        next = newActivityBlock
        newLegBlock.previous = newActivityBlock
        newLegBlock.next = succ
        newActivityBlock.previous = this
        newActivityBlock.next = newLegBlock
        succ.previous = newLegBlock
        return true
    }

    override fun rejects(action: Activity): Boolean {
        return item.size < 2 || item.first() >= action || item.last() <= action
    }

    override fun rejects(action: Leg): Boolean {
        return next.item.firstOrNull()?.startTime?.let { it < action.endTime } ?: false
    }

    override fun toString(): String {
        return item.joinToString { it.toString() }
    }

    override fun insert(leg: Leg): Boolean {
        item.add(leg)
        return false
    }


//    override fun clear() {
//        item.clear()
//        unlink()
//    }
//
//    override fun retainAll(elements: Collection<Leg>): Boolean {
//        if (elements.isEmpty()) {
//            clear()
//            return true
//        }
//        val diff = item - elements.toSet()
//        val b = item.retainAll(elements.toSet())
//        return b
//    }
//
//
//    override fun removeAll(elements: Collection<Leg>): Boolean {
//        val b = item.removeAll(elements.toSet())
//        if (isEmpty()) {
//            clear()
//        }
//        return b
//    }





    private fun unlink() {
        val previous = previous
        val next = next
        val overNext = next.next
        previous.item.addAll(next.item)

        previous.next = overNext
        overNext?.previous = previous
        //TODO find solution to point next and previous to something else, or drop invariant tha previous and next exist

        dispatcher = null
        this.next = next
        this.previous = next

        next.dispatcher = null
        next.next = null
        next.previous = null




    }

    fun replaceAll(elements: Collection<Leg>) {
        require(elements.isNotEmpty()) { "Doesn't make sense to replace with nothing " }
        item.clear()
        item.addAll(elements)

    }

    fun remove(element: Leg): Boolean {
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




    override fun isConsistent(): Boolean {
        val previousActivity = previous.item.lastOrNull()
        val nextActivity = next.item.firstOrNull()
        val actions = (listOf(previousActivity) + item.toList() + nextActivity).filterNotNull()
        return actions.isStrictlySorted()
    }
}

@Deprecated("Remove")
class NewSchedule(initial: Activity) {
    internal val actions: NavigableSet<Action> = sortedSetOf(initial)


    internal var initial: ActivityBlock = ActivityBlock(initial, null)

    val trips get() = legBlockIterator().map { NewTrip(it) }

    fun toList(): List<ActionBlock<*>> {
        return tempIterator().map { it }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is NewSchedule) return false
        val b = actions.zip(other.actions).all { (a, b) -> a == b }
        val b1 = initial == other.initial
        return b && b1
    }

    override fun toString(): String {
        return actions.toString()
    }

    fun isConsistent(): Boolean {
        return tempIterator().all { it.isConsistent() }
    }

    fun tempIterator(): Iterable<ActionBlock<*>> {
        return Iterable {
            object : Iterator<ActionBlock<*>> {
                var current: ActionBlock<*>? = null
                var next: ActionBlock<*>? = initial

                /**
                 * Returns `true` if the iteration has more elements.
                 */
                override fun hasNext(): Boolean {
                    return current?.let { next != null } ?: true

                }

                /**
                 * Returns the next element in the iteration.
                 */
                override fun next(): ActionBlock<*> {
                    current = next
                    next = current?.next
                    return current ?: throw NoSuchElementException()
                }

            }
        }

    }

    fun legBlockIterator(): Iterable<LegBlock> {
        return Iterable {
            object : Iterator<LegBlock> {
                var current: LegBlock? = null
                var nextinternal: LegBlock? = initial.next

                /**
                 * Returns `true` if the iteration has more elements.
                 */
                override fun hasNext(): Boolean {
                    return nextinternal != null

                }

                /**
                 * Returns the next element in the iteration.
                 */
                override fun next(): LegBlock {
                    current = nextinternal
                    nextinternal = current?.next?.next
                    return current ?: throw NoSuchElementException()
                }

            }
        }
    }

    operator fun Activity.unaryPlus() {
        add(this)
    }

    operator fun Leg.unaryPlus() {
        add(this)
    }


    fun add(activity: Activity) {
        if (actions.contains(activity)) return
        val test = tempIterator().first { !it.rejects(activity) }

        test.insert(activity)
    }

    fun add(leg: Leg) {
        if (actions.contains(leg)) return
        val test = tempIterator().first { !it.rejects(leg) }
        test.insert(leg)
    }

//    fun remove(leg: Leg) {
//        val target = tempIterator().first { it.contains1(leg) }
//        target.remove(leg)
//    }
//
//
//    fun remove(activity: Activity) {
//        val target = tempIterator().first { it.contains1(activity) }
//        target.remove(activity)
//    }

}

object START : Location
object OTHER : Location
object THIRD : Location
object FOURTH : Location

class NewTrip(private val legBlock: LegBlock) {

    val legs: SortedSet<out MovingAction> get() = legBlock.item

    fun overwrite(lambda: EditableTrip.() -> Unit): Boolean {

        val start = legBlock.previous.last()
        val end = legBlock.next.first()
        val e = EditableTrip(start, end, legBlock.item)
         e.lambda()
        val actions = listOf(start) + e.new + end
        if (actions.isConsistent()) {
            legBlock.replaceAll(e.new)
            return true
        }
        println("Sorry: ${e.new} is not consistent. Try again")
        return false

    }

    class EditableTrip(
        val previousEndTime: Duration,
        val previousEndLocation: Location,
        val nextStartTime: Duration,
        val nextStartLocation: Location,
        val originals: Collection<MovingAction>
    ) {
        val new: SortedSet<Leg> = sortedSetOf()
        constructor(previous: Activity, next: Activity, originals: Collection<MovingAction>) : this(
            previous.endTime,
            previous.endLocation,
            next.startTime,
            next.startLocation,
            originals
        )
        operator fun Leg.unaryPlus() {
            new.add(this)
        }
        operator fun Collection<Leg>.unaryPlus() {
            new.addAll(this)
        }
    }
}
