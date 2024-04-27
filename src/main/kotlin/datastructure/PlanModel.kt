package datastructure

import utils.collections.addByOrder
import utils.collections.exactlyOneOrNull
import java.util.*
import kotlin.time.Duration

/**
 * A plan model should maintain the state of the Action plan
 */
interface PlanModel {

    val dispatcher: Dispatcher

    fun removeFirst(): Action?
    fun add(leg: Leg)
    fun remove(leg: Leg)
    fun add(activity: Activity)
    fun remove(activity: Activity)
    fun replaceActivities(target: Set<Activity>, to: Set<Activity>)
    fun replaceLegs(target: Set<Leg>, to: Set<Leg>)

    fun actions(): Collection<Action>

    fun first(): Action?

    fun clear()

}

interface PlanView {
    val dispatcher: Dispatcher
    fun add(leg: Leg) = dispatcher.add(leg)
    fun add(activity: Activity) = dispatcher.add(activity)
    fun remove(leg: Leg) = dispatcher.remove(leg)
    fun remove(activity: Activity) = dispatcher.remove(activity)
    fun replaceActivities(target: SortedSet<Activity>, to: SortedSet<Activity>) =
        dispatcher.replaceActivities(target, to)

    fun replaceLegs(target: SortedSet<Leg>, to: SortedSet<Leg>) = dispatcher.replaceLegs(target, to)

    fun pollFirst() = dispatcher.pollFirst()

}


class Dispatcher(private val mutableCollection: MutableCollection<PlanModel> = mutableSetOf()) {
    fun register(model: PlanModel) {
        mutableCollection.add(model)
    }

    private inline fun modifyModels(action: PlanModel.() -> Unit) {
        mutableCollection.forEach { it.action() }
    }

    fun add(activity: Activity) = modifyModels { add(activity) }
    fun add(leg: Leg) = modifyModels { add(leg) }
    fun remove(leg: Leg) = modifyModels { remove(leg) }
    fun remove(activity: Activity) = modifyModels { remove(activity) }
    fun replaceActivities(target: SortedSet<Activity>, to: SortedSet<Activity>) = modifyModels {
        replaceActivities(target, to)
    }

    fun replaceLegs(target: SortedSet<Leg>, to: SortedSet<Leg>) =
        modifyModels { replaceLegs(target, to) }

    fun pollFirst(): Action? {
        val target = mutableCollection.first().first()
        target?.let { modifyModels { removeFirst() } }
        return target
    }
}

class ActionModel(override val dispatcher: Dispatcher) : PlanModel {

    constructor() : this(Dispatcher())
    constructor(other: PlanModel): this(other.dispatcher)
    internal val actions = sortedSetOf<Action>()

    init {
        dispatcher.register(this)
    }

    override fun actions(): Collection<Action> {
        return actions.toSet()
    }

    override fun first(): Action? {
        return actions.firstOrNull()
    }

    override fun clear() {
        actions.clear()
    }

    override fun removeFirst(): Action {
        val target = actions.first()

        actions.remove(target)
        return target
    }


    override fun add(leg: Leg) {
        actions.add(leg)
    }

    override fun add(activity: Activity) {
        actions.add(activity)
    }


    override fun remove(leg: Leg) {
        actions.remove(leg)
    }

    override fun remove(activity: Activity) {
        actions.remove(activity)
    }

    override fun replaceActivities(target: Set<Activity>, to: Set<Activity>) {
        replaceActions(target, to)
    }

    override fun replaceLegs(target: Set<Leg>, to: Set<Leg>) {
        replaceActions(target, to)
    }

    private fun replaceActions(target: Collection<Action>, to: Collection<Action>) {
        actions.removeAll(target.toSet())
        actions.addAll(to)
    }

    //TODO Debate whether the view class for a model should be nested or standalone
    fun view() = ActionView(this)
    class ActionView(private val model: ActionModel) : PlanView, Set<Action> by model.actions {
        override val dispatcher: Dispatcher = model.dispatcher
    }

}

class BlockModel(override val dispatcher: Dispatcher) : PlanModel {

    init {
        dispatcher.register(this)
    }

    val legBlockList: MutableList<Trip> = mutableListOf()

    constructor() : this(Dispatcher())
    constructor(other: PlanModel): this(other.dispatcher)

    val activityBlocks = ActivityBlock(sortedSetOf(), dispatcher)
    val legBlocks get() = activityBlocks.next
    override fun removeFirst(): Action? {
        val target = tempIterator().first { !it.isEmpty() }.removeFirst()
        return target
    }

    override fun add(leg: Leg) {
        val test = tempIterator().first { !it.rejects(leg) }
        val newBlocks = test.insert(leg)
        newBlocks?.let {
            if (legBlockList.isNotEmpty()) legBlockList.addByOrder(Trip(it.first)) else legBlockList.add(Trip(it.first))
        }

    }

    override fun add(activity: Activity) {
        val test = tempIterator().first { !it.rejects(activity) }
        test.insert(activity)
    }

    override fun remove(leg: Leg) {
        val changedBlock = legBlocks?.first { block -> block.contains(leg) }
        changedBlock?.let { legBlock ->
            val b = legBlock.remove(leg)
            if (b) {
                val targetTrip = legBlockList.find { trip -> trip.matches(legBlock) }
                targetTrip?.let { legBlockList.remove(it) }
            }
        }
    }

    override fun remove(activity: Activity) {
        activityBlocks.first { it.contains(activity) }.remove(activity)
    }

    override fun replaceActivities(target: Set<Activity>, to: Set<Activity>) {
        val targetBlock =
            activityBlocks.exactlyOneOrNull { it.bounds(target.toSortedSet()) && it.bounds(to.toSortedSet()) }
        //If the replacement strategy did not work we have to replace and insert every element on its own
        targetBlock?.replaceAll(target, to.toSortedSet()) ?: run {
            target.forEach { remove(it) }
            to.forEach { add(it) }
        }
    }

    override fun replaceLegs(target: Set<Leg>, to: Set<Leg>) {
        val targetBlock =
            legBlocks?.exactlyOneOrNull { it.bounds(target.toSortedSet()) && it.bounds(to.toSortedSet()) }
        //Either the replacement strategy works, or we have to manually run everything
        targetBlock?.replaceAll(target, to) ?: run {
            target.forEach { remove(it) }
            to.forEach { add(it) }
        }
    }

    override fun actions() = tempIterator().flatMap { it.item }
    override fun first(): Action {
        return tempIterator().first { !it.item.isEmpty() }.first()
    }

    override fun clear() {
        legBlockList.clear()
        activityBlocks.next = null
        activityBlocks.item.clear()
    }

    fun tempIterator(): Iterable<ActionBlock<*>> {
        return Iterable {
            object : Iterator<ActionBlock<*>> {
                var current: ActionBlock<*>? = null
                var next: ActionBlock<*>? = activityBlocks

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

    fun view(): TripView {
        return TripView(this)
    }

    class TripView(private val model: BlockModel) : List<Trip> by model.legBlockList, PlanView {
        override val dispatcher: Dispatcher = model.dispatcher
        fun trips(): List<Trip> {
            return model.legBlocks?.map { Trip(it) } ?: emptyList()
        }
    }
}

//TODO find a way so that when the trip object "floats", as in, no longer in the blocklist that changes do not propagate
class Trip(private val legBlock: LegBlock) : Comparable<Trip> {
    val legs: SortedSet<out MovingAction> get() = legBlock.item

    fun overwrite(lambda: EditableTrip.() -> Unit): Boolean {

        val start = legBlock.previous.last()
        val end = legBlock.next.first()
        val e = EditableTrip(start, end, legBlock.item)
        e.lambda()
        val actions = listOf(start) + e.new + end
        if (e.new.isConsistent()) {
            legBlock.replaceAll(legBlock.item, e.new)
            return true
        }
        println("Sorry: ${e.new} is not consistent. Try again")
        return false

    }

    fun matches(target: LegBlock): Boolean {
        return legBlock === target
    }

    class EditableTrip(
        val previousEndTime: Duration,
        val previousEndLocation: Location?,
        val nextStartTime: Duration,
        val nextStartLocation: Location?,
        val originals: Collection<MovingAction>
    ) {
        val new: SortedSet<Leg> = sortedSetOf()

        //TODO restore assertion that a schedule always has a start activity, and that a trip can only exist between
        //two real existing activity blocks
        constructor(previous: Activity?, next: Activity?, originals: Collection<MovingAction>) : this(
            previous?.endTime ?: -Duration.INFINITE,
            previous?.endLocation,
            next?.startTime ?: Duration.INFINITE,
            next?.startLocation,
            originals
        )

        operator fun Leg.unaryPlus() {
            new.add(this)
        }

        operator fun Collection<Leg>.unaryPlus() {
            new.addAll(this)
        }
    }

    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: Trip): Int {
        return legBlock.compareTo(other.legBlock)
    }

}
