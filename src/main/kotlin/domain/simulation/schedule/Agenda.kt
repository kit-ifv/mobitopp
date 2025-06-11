package domain.simulation.schedule

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

    override fun <X> accept(actionBlockVisitor: ActionBlockVisitor<X>): X {
        return actionBlockVisitor.visitActivityBlock(this)
    }
}
