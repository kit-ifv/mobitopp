package datastructure

import utils.collections.exactlyOneOrNull
import java.util.*
import kotlin.time.Duration


interface PlanModel {
    fun add(leg: Leg)
    fun remove(leg: Leg)
    fun add(activity: Activity)
    fun remove(activity: Activity)
    fun replaceActivities(target: SortedSet<Activity>, to: SortedSet<Activity>)
    fun replaceLegs(target: SortedSet<Leg>, to: SortedSet<Leg>)

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
}

class ActionModel(private val dispatcher: Dispatcher) : PlanModel {

    constructor() : this(Dispatcher())

    internal val actions = sortedSetOf<Action>()

    init {
        dispatcher.register(this)
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

    override fun replaceActivities(target: SortedSet<Activity>, to: SortedSet<Activity>) {
        replaceActions(target, to)
    }

    override fun replaceLegs(target: SortedSet<Leg>, to: SortedSet<Leg>) {
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
        fun actions() = model.actions.toList()
    }

}

class BlockModel(val dispatcher: Dispatcher) : PlanModel {

    val legBlockList: MutableList<Trip> = mutableListOf()
    constructor() : this(Dispatcher())

    init {
        dispatcher.register(this)
    }

    val activityBlocks = ActivityBlock(sortedSetOf(), dispatcher)
    val legBlocks get() = activityBlocks.next
    override fun add(leg: Leg) {
        val test = tempIterator().first { !it.rejects(leg) }
        val newBlocks = test.insert(leg)
        newBlocks?.let { legBlockList.add(Trip(it.first)) }

    }

    override fun add(activity: Activity) {
        val test = tempIterator().first { !it.rejects(activity) }
        test.insert(activity)
    }

    override fun remove(leg: Leg) {
        val changedBlock = legBlocks?.first { block -> block.contains(leg) }
        changedBlock?.let {
            val b = it.remove(leg)
            if(b) {
                legBlockList.remove(Trip(changedBlock))
            }
        }
    }

    override fun remove(activity: Activity) {
        activityBlocks.first { it.contains(activity) }.remove(activity)
    }

    override fun replaceActivities(target: SortedSet<Activity>, to: SortedSet<Activity>) {
        val targetBlock = activityBlocks.exactlyOneOrNull { it.bounds(target) }
        //If the replacement strategy did not work we have to replace and insert every element on its own
        targetBlock?.replaceAll(to) ?: run {
            target.forEach { remove(it) }
            to.forEach { add(it) }
        }
    }

    override fun replaceLegs(target: SortedSet<Leg>, to: SortedSet<Leg>) {
        val targetBlock = legBlocks?.exactlyOneOrNull { it.bounds(target) }
        //Either the replacement strategy works, or we have to manually run everything
        targetBlock?.replaceAll(to) ?: run {
            target.forEach { remove(it) }
            to.forEach { add(it) }
        }
    }

    fun actions() = tempIterator().flatMap { it.item }
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

    class TripView(private val model: BlockModel) : List<Trip> by model.legBlockList {
        fun trips(): List<Trip> {
            return model.legBlocks?.map { Trip(it) } ?: emptyList()
        }
    }
}

//TODO find a way so that when the trip object "floats", as in, no longer in the blocklist that changes do not propagate
class Trip(private val legBlock: LegBlock) {
    val legs: SortedSet<out MovingAction> get() = legBlock.item

    fun overwrite(lambda: EditableTrip.() -> Unit): Boolean {

        val start = legBlock.previous.last()
        val end = legBlock.next.first()
        val e = EditableTrip(start, end, legBlock.item)
        e.lambda()
        val actions = listOf(start) + e.new + end
        if (e.new.isConsistent()) {
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
