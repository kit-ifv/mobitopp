package datastructure.plans

import datastructure.Action
import datastructure.Activity
import datastructure.Leg
import datastructure.LinkedAction
import datastructure.LinkedActivity
import datastructure.LinkedLeg

class ActionModel(override val dispatcher: IDispatcher) : PlanModel {
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
        actions.forEach { it.unlink() }
        actions.clear()
    }

    override fun dropUntil(activity: Activity) {
        val external = first()?.previous
        actions.removeAll(actions.filter { it < activity }.toSet())
        val new = first()
        new?.previous = external
        external?.next = new
    }

    override fun removeFirst(): LinkedAction {
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
        override val dispatcher: IDispatcher = model.dispatcher
    }
}
