package datastructure

import utils.collections.addByOrder
import utils.collections.exactlyOneOrNull
import java.util.*

/**
 * A plan model should maintain the state of the Action plan
 */
interface PlanModel {

    val dispatcher: Dispatcher

    fun removeFirst(): Action?
    fun add(leg: MovingAction)
    fun remove(leg: MovingAction)
    fun add(activity: Activity)
    fun remove(activity: Activity)
    fun replaceActivities(target: Set<Activity>, to: Set<Activity>)
    fun replaceLegs(target: Set<MovingAction>, to: Set<MovingAction>)

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

    fun replaceLegs(target: SortedSet<out MovingAction>, to: SortedSet<out MovingAction>) =
        modifyModels { replaceLegs(target, to) }

    fun pollFirst(): Action? {
        val target = mutableCollection.first().first()
        target?.let { modifyModels { removeFirst() } }
        return target
    }
}

class ActionModel(override val dispatcher: Dispatcher) : PlanModel {
    internal val actions = sortedSetOf<Action>()

    constructor() : this(Dispatcher())
    constructor(other: PlanModel) : this(other.dispatcher)

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

    override fun add(leg: MovingAction) {
        actions.add(leg)
    }

    override fun add(activity: Activity) {
        actions.add(activity)
    }

    override fun remove(leg: MovingAction) {
        actions.remove(leg)
    }

    override fun remove(activity: Activity) {
        actions.remove(activity)
    }

    override fun replaceActivities(target: Set<Activity>, to: Set<Activity>) {
        replaceActions(target, to)
    }

    override fun replaceLegs(target: Set<MovingAction>, to: Set<MovingAction>) {
        replaceActions(target, to)
    }

    private fun replaceActions(target: Collection<Action>, to: Collection<Action>) {
        actions.removeAll(target.toSet())
        actions.addAll(to)
    }

    // TODO Debate whether the view class for a model should be nested or standalone
    fun view() = ActionView(this)
    class ActionView(private val model: ActionModel) : PlanView, Set<Action> by model.actions {
        override val dispatcher: Dispatcher = model.dispatcher
    }
}

class BlockModel(override val dispatcher: Dispatcher) : PlanModel {

    constructor() : this(Dispatcher())
    constructor(other: PlanModel) : this(other.dispatcher)

    init {
        dispatcher.register(this)
    }

    private val legBlockList: MutableList<LinkTrip> = mutableListOf()

    val activityBlocks = ActivityBlock(sortedSetOf())

    val legBlocks get() = activityBlocks.next

    private val actionBlocks = Iterable {
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

    override fun removeFirst(): Action? {
        val target = actionBlocks.first { !it.isEmpty() }.removeFirst()
        return target
    }

    override fun add(leg: MovingAction) {
        val test = actionBlocks.takeWhile { !it.containsAction(leg) }.firstOrNull { it.accepts(leg) }
        if (test == null) return
        val newBlocks = test.insert(leg)
        newBlocks?.let {
            if (legBlockList.isNotEmpty()) legBlockList.addByOrder(
                LinkTrip(
                    it.first,
                    dispatcher
                )
            ) else legBlockList.add(LinkTrip(it.first, dispatcher))
        }
    }

    override fun add(activity: Activity) {
        val test = actionBlocks.takeWhile { !it.containsAction(activity) }.firstOrNull { it.accepts(activity) }
        if (test == null) return
        test.insert(activity)
    }

    override fun remove(leg: MovingAction) {
        val changedBlock = legBlocks?.first { block -> block.contains(leg) }
        changedBlock?.let { legBlock ->
            val b = legBlock.remove(leg)
            if (b) {
                val targetTrip = legBlockList.find { trip -> trip.matches(legBlock) }
                targetTrip?.let {
                    it.unlink()
                    legBlockList.remove(it)
                }
            }
        }
    }

    override fun remove(activity: Activity) {
        activityBlocks.first { it.contains(activity) }.remove(activity)
    }

    override fun replaceActivities(target: Set<Activity>, to: Set<Activity>) {
        val targetBlock =
            activityBlocks.exactlyOneOrNull { it.bounds(target.toSortedSet()) && it.bounds(to.toSortedSet()) }
        // If the replacement strategy did not work we have to replace and insert every element on its own
        targetBlock?.replaceAll(target, to.toSortedSet()) ?: run {
            target.forEach { remove(it) }
            to.forEach { add(it) }
        }
    }

    override fun replaceLegs(target: Set<MovingAction>, to: Set<MovingAction>) {
        val targetBlock =
            legBlocks?.exactlyOneOrNull { it.bounds(target.toSortedSet()) && it.bounds(to.toSortedSet()) }
        // Either the replacement strategy works, or we have to manually run everything
        targetBlock?.replaceAll(target, to) ?: run {
            target.forEach { remove(it) }
            to.forEach { add(it) }
        }
    }

    override fun actions() = actionBlocks.flatMap { it.item }
    override fun first(): Action {
        return actionBlocks.first { !it.item.isEmpty() }.firstElement()
    }

    override fun clear() {
        legBlockList.clear()
        activityBlocks.next = null
        activityBlocks.item.clear()
    }

    fun view(): TripView {
        return TripView(this)
    }

    class TripView(private val model: BlockModel) : List<LinkTrip> by model.legBlockList, PlanView {
        override val dispatcher: Dispatcher = model.dispatcher
    }
}



