package datastructure

import utils.collections.addByOrder
import utils.collections.exactlyOneOrNull
import java.util.*
import kotlin.time.Duration

interface LegTracker {
    fun add(leg: Leg)
    fun remove(leg: Leg)
    fun replaceLegs(target: Set<Leg>, to: Set<Leg>)
}

interface ActivityTracker {
    fun add(activity: Activity)
    fun remove(activity: Activity)
    fun replaceActivities(target: Set<Activity>, to: Set<Activity>)
}

/**
 * A plan model should maintain the state of the Action plan
 */
interface PlanModel : LegTracker, ActivityTracker {

    val dispatcher: Dispatcher

    // Remove first does not require a linked Action, once removed it can be free-floating again
    fun removeFirst(): Action?

    fun actions(): Collection<LinkedAction>

    /**
     * Return the first [Action] in the model. Or null if none is present
     */
    fun first(): LinkedAction?

    /**
     * Delete all actions from the model.
     */
    fun clear()

    fun dropUntil(activity: Activity)
}

interface SeparablePlanModel : PlanModel {
    fun activities(): Collection<LinkedActivity>
    fun legs(): Collection<LinkedLeg>
}


fun PlanModel.squeeze(from: Duration, to: Duration, force: Boolean = false) {
    val afterAction = actions().dropWhile { it.endTime <= from }
    var counter = to
    val requiredShift = afterAction.map {
        val offset = counter - it.startTime
        counter += it.duration
        offset
    }
    val targets = afterAction.zip(requiredShift).filter { it.second > Duration.ZERO }
    val valid = targets.all { (action, shift) ->
        (action.latestEndTime ?: Duration.INFINITE) >= action.endTime + shift &&
                (action.earliestStartTime ?: -Duration.INFINITE) <= action.startTime + shift
    }
    if (valid || force) {
        targets.reversed().forEach { (action, shift) ->
            action.shift(shift)
        }
    } else {
        error(
            "Some actions in the plan cannot support the requested squeeze ${
                targets.filter { (action, shift) ->
                    (action.latestEndTime ?: Duration.INFINITE) < action.endTime + shift ||
                            (action.earliestStartTime ?: -Duration.INFINITE) > action.startTime + shift
                }.map { (action, dur) ->
                    "${action.original} necessaryShift=$dur"
                }
            } \nrun with force=true IF and only IF you know what you are doing."
        )
    }
}

fun PlanModel.squeeze(action: Action, force: Boolean = false) {
    return this.squeeze(action.startTime, action.endTime, force)
}

fun PlanModel.shift(from: Duration, block: Duration, force: Boolean = false) {
    val targets = actions().dropWhile { it.endTime <= from }
    if (targets.all {
            it.startTime + block >= (it.earliestStartTime ?: -Duration.INFINITE) &&
                    it.endTime + block <= (it.latestEndTime ?: Duration.INFINITE)
        } || force
    ) {
        targets.forEach {
            it.shift(block)
        }
    } else {
        error("The schedule does not support the shift requested.")
    }
}

interface PlanView {
    val dispatcher: Dispatcher
    fun add(leg: Leg) = dispatcher.add(leg)
    fun add(activity: Activity) = dispatcher.add(activity)
    fun remove(leg: Leg) = dispatcher.remove(leg)
    fun remove(activity: Activity) = dispatcher.remove(activity)
    fun replaceActivities(target: SortedSet<Activity>, to: SortedSet<Activity>) =
        dispatcher.replaceActivities(target, to)

    fun dropUntil(activity: Activity) = dispatcher.dropUntil(activity)

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

    fun dropUntil(activity: Activity) = modifyModels { dropUntil(activity) }
}

class ActionModel(override val dispatcher: Dispatcher) : PlanModel {
    internal val actions = sortedSetOf<LinkedAction>()

    constructor() : this(Dispatcher())
    constructor(other: PlanModel) : this(other.dispatcher)

    init {
        dispatcher.register(this)
    }

    override fun actions(): Collection<LinkedAction> {
        return actions.toSet()
    }

    override fun first(): LinkedAction? {
        return actions.firstOrNull()
    }

    override fun clear() {
        actions.clear()
    }

    override fun dropUntil(activity: Activity) {
        actions.removeAll(actions.filter { it < activity }.toSet())
    }

    override fun removeFirst(): Action {
        val target = actions.first()

        actions.remove(target)
        return target
    }

    override fun add(leg: Leg) {

        val linkedLeg = LinkedLeg(leg)
        if (actions.contains(linkedLeg)) return
        linkedLeg.previous = actions.lower(linkedLeg)
        linkedLeg.next = actions.higher(linkedLeg)
        linkedLeg.previous?.next = linkedLeg
        linkedLeg.next?.previous = linkedLeg
        actions.add(linkedLeg)
    }

    override fun add(activity: Activity) {
        val linkedActivity = LinkedActivity(activity)
        if (actions.contains(linkedActivity)) return
        linkedActivity.previous = actions.lower(linkedActivity)
        linkedActivity.next = actions.higher(linkedActivity)

        linkedActivity.previous?.next = linkedActivity
        linkedActivity.next?.previous = linkedActivity
        actions.add(linkedActivity)
    }

    override fun remove(leg: Leg) {
        val target = actions.find { it.original == leg }
        target?.let { remove(it) }

    }

    override fun remove(activity: Activity) {
        val target = actions.find { it.original == activity }
        target?.let { remove(it) }

    }

    private fun remove(linkedAction: LinkedAction) {
        linkedAction.unlink()
        actions.remove(linkedAction)
    }

    override fun replaceActivities(target: Set<Activity>, to: Set<Activity>) {
        val targetSet = target.mapNotNull { act -> actions.find { it.original == act } }
        targetSet.forEach { remove(it) }
        to.forEach { add(it) }
    }

    override fun replaceLegs(target: Set<Leg>, to: Set<Leg>) {
        val targetSet = target.mapNotNull { act -> actions.find { it.original == act } }
        targetSet.forEach { remove(it) }
        to.forEach { add(it) }

    }


    fun view() = ActionView(this)
    class ActionView(private val model: ActionModel) : PlanView, Set<Action> by model.actions {
        override val dispatcher: Dispatcher = model.dispatcher
    }
}

class BlockModel(override val dispatcher: Dispatcher) : SeparablePlanModel {

    constructor() : this(Dispatcher())
    constructor(other: PlanModel) : this(other.dispatcher)

    init {
        dispatcher.register(this)
    }

    private val legBlockList: MutableList<LinkTrip> = mutableListOf()

    var activityBlocks = ActivityBlock(sortedSetOf())
        private set

    private val legBlocks get() = activityBlocks.next

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

    override fun activities(): Collection<LinkedActivity> {
        return activityBlocks.flatMap { it.item }
    }

    override fun legs(): Collection<LinkedLeg> {
        return legBlocks?.flatMap { it.item } ?: emptySet()
    }

    override fun dropUntil(activity: Activity) {
        val previousBlocks = activityBlocks.takeWhile { !it.containsAction(activity) }
        val newStart = activityBlocks.first { it.containsAction(activity) }
        val legBlocks = previousBlocks.mapNotNull { it.next }

        legBlockList.removeAll(legBlockList.filter { trip -> legBlocks.any { trip.matches(it) } })
        // not going through the clear method, but rather clearing items directly to avoid pointer issues
        previousBlocks.forEach { it.item.clear() }
        legBlocks.forEach { it.item.clear() }
        newStart.item.removeAll(newStart.item.filter { it < activity }.toSet())
        activityBlocks = newStart
        // Set previous to null and let GC handle the cleanup of all the previous blocks
        activityBlocks.previous = null
    }

    override fun removeFirst(): Action? {
        val target = actionBlocks.first { !it.isEmpty() }.removeFirst()
        return target
    }

    override fun add(leg: Leg) {
        val test = actionBlocks.takeWhile { !it.containsAction(leg) }.firstOrNull { it.accepts(leg) }
        if (test == null) return
        val newBlocks = test.insert(leg)
        newBlocks?.let {
            if (legBlockList.isNotEmpty()) {
                legBlockList.addByOrder(
                    LinkTrip(
                        it.first,
                        dispatcher
                    )
                )
            } else {
                legBlockList.add(LinkTrip(it.first, dispatcher))
            }
        }
    }

    override fun add(activity: Activity) {
        val test = actionBlocks.takeWhile { !it.containsAction(activity) }.firstOrNull { it.accepts(activity) }
        if (test == null) return
        test.insert(activity)
    }

    override fun remove(leg: Leg) {
        val changedBlock = legBlocks?.first { block -> block.containsAction(leg) }
        changedBlock?.let { legBlock ->
            val b = legBlock.remove(leg)
            if (b) {
                val targetTrip = legBlockList.find { trip -> trip.matches(legBlock) }
                targetTrip?.let {
                    it.removeDispatcher()
                    legBlockList.remove(it)
                }
            }
        }
    }

    override fun remove(activity: Activity) {
        activityBlocks.first { it.containsAction(activity) }.remove(activity)
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

    override fun replaceLegs(target: Set<Leg>, to: Set<Leg>) {
        val targetBlock =
            legBlocks?.exactlyOneOrNull { it.bounds(target.toSortedSet()) && it.bounds(to.toSortedSet()) }
        // Either the replacement strategy works, or we have to manually run everything
        targetBlock?.replaceAll(target, to) ?: run {
            target.forEach { remove(it) }
            to.forEach { add(it) }
        }
    }

    override fun actions(): List<LinkedAction> {
        return actionBlocks.flatMap { it.item }
    }

    override fun first(): LinkedAction? {
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
