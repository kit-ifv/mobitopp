package domain.shared.datastructure.schedule

import domain.shared.datastructure.schedule.action.LinkedActivity
import domain.shared.datastructure.schedule.blocks.ActivityBlock

interface Representative<T> {
    val elements: List<T>
    val size get() = elements.size

    fun <X> accept(actionBlockVisitor: ActionBlockVisitor<X>): X
}

interface Agenda : Representative<LinkedActivity> {
    override val elements: List<LinkedActivity>
}

class RawAgenda(override val elements: List<LinkedActivity>) : Agenda {
    constructor(activityBlock: ActivityBlock) : this(activityBlock.item.toList())

    override fun <X> accept(actionBlockVisitor: ActionBlockVisitor<X>): X = actionBlockVisitor.visitActivityBlock(this)
}
