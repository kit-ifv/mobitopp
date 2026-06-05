package domain.shared.datastructure.schedule.plans

import domain.shared.datastructure.schedule.action.Activity
import domain.shared.datastructure.schedule.action.Leg
import domain.shared.datastructure.schedule.action.LinkedActivity
import java.util.*

/**
 * A Planview provides functions to modify all models registered at the same dispatcher.
 */
interface PlanView {
    val dispatcher: IDispatcher
    fun add(leg: Leg) = dispatcher.add(leg)
    fun add(activity: Activity): LinkedActivity? = dispatcher.add(activity)
    fun remove(leg: Leg) = dispatcher.remove(leg)
    fun remove(activity: Activity) = dispatcher.remove(activity)
    fun replaceActivities(target: SortedSet<Activity>, to: SortedSet<Activity>) =
        dispatcher.replaceActivities(target, to)

    fun dropUntil(activity: Activity) = dispatcher.dropUntil(activity)

    fun replaceLegs(target: SortedSet<Leg>, to: SortedSet<Leg>) = dispatcher.replaceLegs(target, to)

    fun pollFirst() = dispatcher.pollFirst()

    fun firstAction() = dispatcher.firstAction()
}

fun PlanView.addAll(vararg elements: Activity) {
    elements.forEach { add(it) }
}

fun PlanView.addAll(vararg elements: Leg) {
    elements.forEach { add(it) }
}
