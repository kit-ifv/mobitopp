package edu.kit.ifv.domain.shared.datastructure.schedule.plans
import edu.kit.ifv.domain.shared.datastructure.schedule.blocks.ActionBlock
import edu.kit.ifv.domain.shared.datastructure.schedule.blocks.ActivityBlock

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
