package datastructure.plans

import datastructure.ActionBlock
import datastructure.Activity
import datastructure.ActivityBlock
import datastructure.Leg
import datastructure.LegBlock
import datastructure.LinkTrip
import datastructure.LinkedAction
import datastructure.LinkedActivity
import datastructure.LinkedLeg
import utils.collections.addByOrder
import utils.collections.exactlyOneOrNull
import java.util.NoSuchElementException

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
        val previousElement = first()?.previous

        val previousBlocks = activityBlocks.takeWhile { it.compareTo(activity) == -1 }.filter { !it.isEmpty() }
        val newStart = activityBlocks.firstOrNull { it.containsAction(activity) }
        val legBlocks = previousBlocks.mapNotNull { it.next }

        legBlockList.removeAll(legBlockList.filter { trip -> legBlocks.any { trip.matches(it) } })
        // not going through the clear method, but rather clearing items directly to avoid pointer issues
        previousBlocks.forEach { activityBlock ->
            val targets = activityBlock.item
            targets.forEach { it.unlink() }
            targets.clear()
        }
        legBlocks.forEach { legBlock ->
            val targets = legBlock.item
            targets.forEach { it.unlink() }
            targets.clear()
        }

        val badElements = newStart?.item?.filter { it < activity }

        badElements?.forEach { remove(it) }
        activityBlocks = newStart ?: activityBlocks
        // Set previous to null and let GC handle the cleanup of all the previous blocks
        activityBlocks.previous = null

        val start = first()
        start?.previous = previousElement
        previousElement?.next = start
    }

    override fun removeFirst(): LinkedAction? {
        val targetBlock = actionBlocks.first { !it.isEmpty() }
        val target = targetBlock.removeFirst()
        if (targetBlock.isEmpty() && targetBlock is LegBlock) {
            legBlockList.removeFirst()
        }
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
        return actionBlocks.firstOrNull { !it.item.isEmpty() }?.firstElement()
    }

    override fun clear() {
        legBlockList.clear()
        activityBlocks.next = null
        activityBlocks.item.clear()
    }

    override fun view(): TripView {
        return TripView(this)
    }

    class TripView(private val model: BlockModel) : List<LinkTrip> by model.legBlockList, PlanView {
        override val dispatcher: Dispatcher = model.dispatcher
    }
}