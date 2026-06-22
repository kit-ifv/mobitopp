package edu.kit.ifv.domain.shared.datastructure.schedule.plans
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Action
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Activity
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Leg
import edu.kit.ifv.domain.shared.datastructure.schedule.action.LinkedAction
import edu.kit.ifv.domain.shared.datastructure.schedule.action.LinkedActivity
import edu.kit.ifv.domain.shared.datastructure.schedule.action.LinkedLeg

class ActionModel(override val dispatcher: IDispatcher) : PlanModel {
    internal val actions = sortedSetOf<LinkedAction>()

    constructor() : this(Dispatcher())
    constructor(other: PlanModel) : this(other.dispatcher)

    init {
        dispatcher.register(this)
    }

    override fun actions(): Collection<LinkedAction> = actions.toSet()

    override fun first(): LinkedAction? = actions.firstOrNull()

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

    override fun add(activity: Activity): LinkedActivity? {
        val linkedActivity = LinkedActivity(activity)
        /* In the action set we cannot feasibly test whether the activity is contained because it overlaps with a leg
        or an activity, as the sole purpose of this class is to drop this separation, as such we cannot return an element

         */
        if (actions.contains(linkedActivity)) return null
        linkedActivity.previous = actions.lower(linkedActivity)
        linkedActivity.next = actions.higher(linkedActivity)

        linkedActivity.previous?.next = linkedActivity
        linkedActivity.next?.previous = linkedActivity
        actions.add(linkedActivity)
        return linkedActivity
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
    class ActionView(private val model: ActionModel) :
        PlanView,
        Set<Action> by model.actions {
        override val dispatcher: IDispatcher = model.dispatcher
    }
}
