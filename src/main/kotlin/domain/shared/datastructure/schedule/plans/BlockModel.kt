package domain.shared.datastructure.schedule.plans

import domain.shared.datastructure.schedule.Action
import domain.shared.datastructure.schedule.ActionBlock
import domain.shared.datastructure.schedule.Activity
import domain.shared.datastructure.schedule.ActivityBlock
import domain.shared.datastructure.schedule.Leg
import domain.shared.datastructure.schedule.LinkTrip
import domain.shared.datastructure.schedule.LinkedAction
import domain.shared.datastructure.schedule.LinkedActivity
import domain.shared.datastructure.schedule.LinkedLeg
import domain.shared.datastructure.schedule.LinkedTrip
import utils.collections.addByOrder
import utils.collections.exactlyOneOrNull
import java.util.SortedSet

class InternalIterator(activityBlock: ActivityBlock) : Iterator<ActionBlock<*>> {
    private var current: ActionBlock<*>? = null
    private var next: ActionBlock<*>? = activityBlock

    /**
     * Returns `true` if the iteration has more elements.
     */
    override fun hasNext(): Boolean = current?.let { next != null } ?: true

    /**
     * Returns the next element in the iteration.
     */
    override fun next(): ActionBlock<*> {
        current = next
        next = current?.next
        return current ?: throw NoSuchElementException()
    }

    fun reset(target: ActivityBlock) {
        current = null
        next = target
    }
}

class InternalIterable(val activityBlock: () -> ActivityBlock) : Iterable<ActionBlock<*>> {
    private val internalIterator = InternalIterator(activityBlock())

    /**
     * Returns an iterator over the elements of this object.
     */
    override fun iterator(): Iterator<ActionBlock<*>> {
        internalIterator.reset(activityBlock())
        return internalIterator
    }
}

class BlockModel(
    override val dispatcher: IDispatcher,
    private val legBlockList: MutableList<LinkTrip> = mutableListOf(),
    private var activityBlocks: ActivityBlock = ActivityBlock(sortedSetOf()),
    private val activitySortedSet: SortedSet<LinkedActivity> = sortedSetOf(),
) : SeparablePlanModel {

    constructor(dispatcher: IDispatcher) : this(dispatcher, mutableListOf())
    constructor() : this(Dispatcher())

    init {
        dispatcher.register(this)
    }

//    @Suppress("MagicNumber") // 3 legs per trip is the assumed standard
//    private val legBlockList: MutableList<LinkTrip> by lazy {
//        ArrayList(3)
//    }

//    private var activityBlocks = ActivityBlock(sortedSetOf())

//    private val activitySortedSet: SortedSet<LinkedActivity> by lazy {
//        sortedSetOf()
//    }

    private val legBlocks get() = activityBlocks.next

    private val actionBlocks = InternalIterable { activityBlocks }

    override fun nextBlock(): ActionBlock<*>? = actionBlocks.firstOrNull { !it.isEmpty() }
    override fun activities(): Collection<LinkedActivity> = activitySortedSet

    override fun legs(): Collection<LinkedLeg> = legBlocks?.flatMap { it.item } ?: emptySet()

    override fun dropUntil(activity: Activity) {
        val previousElement = first()?.previous

        val previousBlocks = activityBlocks.takeWhile { it.compareTo(activity) == -1 }.filter { !it.isEmpty() }
        val newStart = activityBlocks.firstOrNull { it.containsAction(activity) }

        val legBlocks = legBlocks?.filter { it.compareTo(activity) == -1 } ?: emptyList()

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
        if (targetBlock.isEmpty() && targetBlock is LinkedTrip) {
            legBlockList.removeFirst()
        }
        if (target == activitySortedSet.first()) {
            activitySortedSet.remove(activitySortedSet.first())
        }
        return target
    }

    override fun add(leg: Leg) {
        val correspondence = getCorrespondingBlock(leg) {
            accepts(leg)
        }

        val newBlocks = correspondence?.insert(leg)
        newBlocks?.let {
            if (legBlockList.isNotEmpty()) {
                legBlockList.addByOrder(
                    LinkTrip(
                        it.first,
                        dispatcher,
                    ),
                )
            } else {
                legBlockList.add(LinkTrip(it.first, dispatcher))
            }
        }
    }

    private fun <T : Action> getCorrespondingBlock(
        element: T,
        acceptor: ActionBlock<*>.(T) -> Boolean,
    ): ActionBlock<*>? {
        for (block in actionBlocks) {
            if (block.containsAction(element)) {
                return null
            }
            if (block.acceptor(element)) {
                return block
            }
        }
        return null
    }

    override fun add(activity: Activity): LinkedActivity? {
        val correspondence = getCorrespondingBlock(activity) {
            accepts(activity)
        }

        return correspondence?.insert(activity, activitySortedSet)
    }

    override fun remove(leg: Leg) {
        val changedBlock = legBlocks?.firstOrNull { block -> block.containsAction(leg) }
        changedBlock?.let { legBlock ->
            val blockIsNowEmpty = legBlock.remove(leg)
            if (blockIsNowEmpty) {
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
        activitySortedSet.removeIf { it.original == activity }
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
        targetBlock?.let {
            it.replaceAll(target, to)
            legBlockList
        } ?: run {
            target.forEach {
                remove(it)
            } // TODO here is a bug: (or exotic behaviour) When deleting all elements within a block,
            //  the block is removed from the activity schedule, and the new legs are added to a new leg block which
            //  is a completely different object. The LinkTrip holds a reference to the
            //  original block and may induce headache when debugging
            to.forEach { add(it) }
        }
    }

    override fun actions(): List<LinkedAction> = actionBlocks.flatMap { it.item }

    override fun first(): LinkedAction? = actionBlocks.firstOrNull { !it.item.isEmpty() }?.firstElement()

    override fun lastActivity(): LinkedActivity =
        throw UnsupportedOperationException("BlockModel.lastActivity() should not be called!")

    override fun clear() {
        legBlockList.clear()
        activityBlocks.next = null
        activityBlocks.item.clear()
    }

    override fun view(): TripView = TripView(this)

    class TripView(private val model: BlockModel) :
        List<LinkTrip> by model.legBlockList,
        PlanView {
        override val dispatcher: IDispatcher = model.dispatcher
    }
}
