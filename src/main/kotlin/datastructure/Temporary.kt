package datastructure

import utils.collections.isStrictlySorted
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours


interface SharedBlocks<T: Action>: NavigableSet<T> {
    val item: NavigableSet<T>
    val actions: NavigableSet<Action>
    fun insert(activity: Activity): Pair<LegBlock, ActivityBlock>?
    fun insert(leg: Leg): Pair<LegBlock, ActivityBlock>?

    fun remove(activity: Activity): Boolean
    fun remove(leg: Leg): Boolean

    fun rejects(action: Activity): Boolean
    fun rejects(action: Leg): Boolean

    fun next(): SharedBlocks<*>?

    fun isConsistent(): Boolean

    fun contains1(action: Action): Boolean

    override fun add(element: T): Boolean {
        actions.add(element)
        return item.add(element)
    }



    override fun addAll(elements: Collection<T>): Boolean {
        actions.addAll(elements)
        return item.addAll(elements)
    }



    override fun pollFirst(): T? {
        val poll = item.pollFirst()
        poll?.let{
            actions.remove(it)
        }
        return poll
    }

    override fun pollLast(): T? {
        val poll = item.pollLast()
        poll?.let {
            actions.remove(it)
        }
        return poll
    }




















}

class ActivityBlock(override val item: NavigableSet<Activity>, override val actions: NavigableSet<Action> ): SharedBlocks<Activity>, NavigableSet<Activity> by item{
    constructor(activity: Activity, actions: NavigableSet<Action>): this(sortedSetOf(activity), actions)
    var next: LegBlock? = null
    var previous: LegBlock? = null

    override fun toString(): String {
        return item.joinToString { it.toString() }
    }

    override fun add(element: Activity) = super.add(element)
    override fun addAll(elements: Collection<Activity>) = super.addAll(elements)
    override fun pollFirst() = super.pollFirst()

    override fun pollLast() =  super.pollLast()



    override fun insert(activity: Activity) : Pair<LegBlock, ActivityBlock>? {
        item.add(activity)
        actions.add(activity)
        return null
    }

    override fun insert(leg: Leg): Pair<LegBlock, ActivityBlock> {
        actions.add(leg)
        val a = item.find { it.startTime >= leg.startTime }
        val targets = if(a == null) sortedSetOf<Activity>() else TreeSet(item.tailSet(a, true))

        val newLegBlock = LegBlock(leg, actions)
        val newActivityBlock = ActivityBlock(targets, actions)

        val succ = next

        item.removeAll(targets)

        next = newLegBlock
        newLegBlock.previous = this
        newLegBlock.next = newActivityBlock
        newActivityBlock.previous = newLegBlock
        newActivityBlock.next = succ
        succ?.previous = newActivityBlock
        return newLegBlock to newActivityBlock
    }

    private fun unlink() {
        val prevLeg = previous!!
        val nextLeg = next!!

        val nextActivity = nextLeg.next

        prevLeg.item.addAll(nextLeg.item)

        prevLeg.next = nextActivity
        nextActivity.previous = prevLeg

        //Removing other links for GC support
        next = null
        previous = null
        nextLeg.previous = this
        nextLeg.next = this



    }

    override fun remove(activity: Activity): Boolean {
        item.remove(activity)
        actions.remove(activity)
        if(item.isEmpty() && previous != null && next != null) {
            unlink()

        }
        return true
    }

    override fun remove(leg: Leg): Boolean {
        throw NoSuchElementException("Removing a leg from an activity block should never happen")
    }

    override fun rejects(action: Activity): Boolean {
        return next?.item?.firstOrNull()?.startTime?.let{it < action.endTime} ?: false
    }

    override fun rejects(action: Leg): Boolean {
        return next != null && (item.size < 2 || item.first() >= action || item.last() <= action)
    }



    /**
     * Returns the next element in the iteration.
     */
    override fun next(): SharedBlocks<*>? {
        return next
    }

    override fun isConsistent(): Boolean {
        val previousLeg = previous?.item?.lastOrNull()
        val nextLeg = next?.item?.firstOrNull()
        val actions = (listOf(previousLeg) + item.toList() + nextLeg).filterNotNull()
        return actions.isStrictlySorted()
    }

    override fun contains1(action: Action): Boolean {
        return item.contains(action)
    }

    override fun clear() {
        actions.removeAll(item)
        item.clear()
        unlink()
    }

    override fun retainAll(elements: Collection<Activity>): Boolean {
        if (elements.isEmpty()) {
            clear()
            return true
        }
        val diff = item - elements.toSet()
        val b = item.retainAll(elements.toSet())
        actions.removeAll(diff)
        return b
    }



    override fun removeAll(elements: Collection<Activity>): Boolean {
        val b = item.removeAll(elements.toSet())
        actions.removeAll(elements.toSet())
        if(isEmpty()){
            clear()
        }
        return b
    }


    override fun equals(other: Any?): Boolean {
        if(other !is ActivityBlock) return false
        return next == other.next  && item.zip(other.item).all {(a, b) -> a == b}
    }

}

class LegBlock(override val item: NavigableSet<Leg>, override val actions: NavigableSet<Action>): SharedBlocks<Leg>, NavigableSet<Leg> by item {
    constructor(leg: Leg, actions: NavigableSet<Action>): this(sortedSetOf(leg), actions)
    lateinit var next: ActivityBlock
    lateinit var previous: ActivityBlock


    override fun add(element: Leg) = super.add(element)
    override fun addAll(elements: Collection<Leg>) = super.addAll(elements)
    override fun pollFirst() = super.pollFirst()

    override fun pollLast() =  super.pollLast()
    override fun insert(activity: Activity): Pair<LegBlock, ActivityBlock> {

        actions.add(activity)
        val l = item.find {it.startTime >= activity.startTime}?: item.last()
        val targets = TreeSet(item.tailSet(l, true))


        val newLegBlock = LegBlock(targets, actions)
        val newActivityBlock = ActivityBlock(activity, actions)

        val succ = next

        item.removeAll(targets)

        next = newActivityBlock
        newLegBlock.previous = newActivityBlock
        newLegBlock.next = succ
        newActivityBlock.previous = this
        newActivityBlock.next = newLegBlock
        succ.previous = newLegBlock
        return newLegBlock to newActivityBlock
    }

    override fun rejects(action: Activity): Boolean {
        return item.size < 2 || item.first() >= action || item.last() <= action
    }

    override fun rejects(action: Leg): Boolean {
        return  next.item.firstOrNull()?.startTime?.let{it < action.endTime} ?: false
    }
    override fun toString(): String {
        return item.joinToString { it.toString() }
    }

    override fun insert(leg: Leg): Pair<LegBlock, ActivityBlock>? {
        item.add(leg)
        actions.add(leg)
        return null
    }
    override fun contains1(action: Action): Boolean {
        return item.contains(action)
    }

    override fun clear() {
        actions.removeAll(item)
        item.clear()
        unlink()
    }

    override fun retainAll(elements: Collection<Leg>): Boolean {
        if (elements.isEmpty()) {
            clear()
            return true
        }
        val diff = item - elements.toSet()
        val b = item.retainAll(elements.toSet())
        actions.removeAll(diff)
        return b
    }



    override fun removeAll(elements: Collection<Leg>): Boolean {
        val b = item.removeAll(elements.toSet())
        actions.removeAll(elements.toSet())
        if(isEmpty()){
            clear()
        }
        return b
    }


    override fun remove(activity: Activity): Boolean {
        throw NoSuchElementException("Removing an activity in a leg block should never happen")
    }
    private fun unlink()  {
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

    fun replaceAll(elements: Collection<Leg>) {
        require(elements.isNotEmpty()) {"Doesn't make sense to replace with nothing "}
        actions.removeAll(item)
        item.clear()
        addAll(elements)

    }
    override fun remove(element: Leg): Boolean {
        item.remove(element)
        actions.remove(element)
        if(item.isEmpty()) {
            unlink()
        }
        return true
    }

    override fun equals(other: Any?): Boolean {
        if(other !is LegBlock) return false
        return next == other.next && item.zip(other.item).all {(a, b) -> a == b}
    }

    /**
     * Returns the next element in the iteration.
     */
    override fun next(): SharedBlocks<*> {
        return next
    }

    override fun isConsistent(): Boolean {
        val previousActivity = previous.item.lastOrNull()
        val nextActivity = next.item.firstOrNull()
        val actions = (listOf(previousActivity) + item.toList() + nextActivity).filterNotNull()
        return actions.isStrictlySorted()
    }

}

class NewSchedule(initial: Activity) {
    val actions: NavigableSet<Action> = sortedSetOf(initial) //This should be only viewable, the proper maintenance


    var initial: ActivityBlock = ActivityBlock(initial, actions)
    var current = actions.iterator()

    fun doNext() {
        if(!current.hasNext()) {
            return
        }
        val internal = current.next()
        internal.initiateAction()
    }


    fun toList(): List<SharedBlocks<*>> {
        return iterator().map { it }
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
    fun isConsistent() : Boolean {
        return iterator().all { it.isConsistent() }
    }
    fun iterator(): Iterable<SharedBlocks<*>> {
        return Iterable {
            object: Iterator<SharedBlocks<*>> {
                var current: SharedBlocks<*>? = null
                var next: SharedBlocks<*>? = initial
                /**
                 * Returns `true` if the iteration has more elements.
                 */
                override fun hasNext(): Boolean {
                    return current?.let{next != null}?:true

                }

                /**
                 * Returns the next element in the iteration.
                 */
                override fun next(): SharedBlocks<*> {
                    current = next
                    next = current?.next()
                    return current?: throw NoSuchElementException()
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
    operator fun Leg.unaryMinus() {
        remove(this)
    }

    operator fun Activity.unaryMinus() {
        remove(this)
    }
    fun add(activity: Activity) {
        if (actions.contains(activity)) return
//        actions.add(activity)

        val test = iterator().first{!it.rejects(activity)}

        val newBlocks = test.insert(activity)
    }

    fun remove(leg: Leg) {
//        actions.remove(leg)
        val target = iterator().first{it.contains1(leg)}
        target.remove(leg)
    }
    fun remove(activity:Activity) {
//        actions.remove(activity)
        val target = iterator().first{it.contains1(activity)}
        target.remove(activity)
    }
    fun print() {
        println(iterator().map { it.toString() })

    }
    fun add(leg: Leg) {
        if (actions.contains(leg)) return
//        actions.add(leg)
        val test = iterator().first{!it.rejects(leg)}
        val newBlocks = test.insert(leg)
    }

}
object START : Location
object OTHER: Location
object THIRD: Location
object FOURTH: Location

class NewTrip(val legBlock: LegBlock) {

    fun overwrite(elements: SortedSet<Leg>): Boolean {
        val start = legBlock.previous.last()
        val end = legBlock.next.first()
        val actions = listOf(start) + elements + end
        if(actions.isConsistent()) {
            legBlock.replaceAll(elements)
            return true
        }

        return false

    }

}
class LimitedLeg(private val original: Leg, val actions: NavigableSet<Action>): Leg {
    val previous: Action? get() = actions.lower(original)
    val next: Action? get() = actions.higher(original)
    override fun equals(other: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override var startTime: Duration
        get() = original.startTime
        set(value) {
            val fitsPrevious = previous?.endTime?.let{it <= value} ?: true
            val fitsNext = next?.startTime?.let{it >= value + duration}?:true
            if(fitsPrevious && fitsNext) {
                original.startTime = value
            }
        }
    override var duration: Duration
        get() = original.duration
        set(value) {
            val fitsNext = next?.startTime?.let{it >= startTime + value} ?: true
            if(fitsNext) {
                original.duration = value
            }
        }
    override var startLocation: Location
        get() = original.startLocation
        set(value) {
            original.startLocation = value
        }
    override var endLocation: Location
        get() = original.endLocation
        set(value) {
            original.endLocation = value
        }

}

fun main() {

    val target = (0..1).map { generatePlan() }
    val testPlan = target.last()
    val trip = NewTrip(testPlan.initial.next!!)
    val t = trip.legBlock.first()
    trip.legBlock.remove(t)
    println(testPlan)

}

private fun generatePlan(): NewSchedule {
    val s = NewSchedule(RawActivity(START, 0.hours, 7.hours))
    (0..7).forEach {
        s.add(RawLeg(it.days + 7.hours, 0.5.hours, START, OTHER))
        s.add(RawActivity(OTHER, it.days + 8.hours, 8.hours))
        s.add(RawLeg(it.days + 16.hours, 0.5.hours, OTHER, THIRD))
        s.add(RawActivity(THIRD, it.days + 17.hours, 0.5.hours))
        s.add(RawLeg(it.days + 20.hours, 0.5.hours, THIRD, START))
        s.add(RawActivity(START, it.days + 21.hours, 10.hours))
    }
    return s
}