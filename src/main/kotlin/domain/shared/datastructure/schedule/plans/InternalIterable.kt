package domain.shared.datastructure.schedule.plans

import domain.shared.datastructure.schedule.blocks.ActionBlock
import domain.shared.datastructure.schedule.blocks.ActivityBlock

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