package datastructure.plans

import datastructure.Activity
import datastructure.Leg
import datastructure.LinkedAction
import java.util.*

/**
 * A Planview provides functions to modify all models registered at the same dispatcher.
 */
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

fun PlanView.addAll(vararg elements: Activity) {
    elements.forEach { add(it) }
}

fun PlanView.addAll(vararg elements: Leg) {
    elements.forEach { add(it) }
}

/**
 * A dispatcher holds a collection of [PlanModel] and calls the requested modifications on the registered models.
 */
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

    fun pollFirst(): LinkedAction? {
        val target = mutableCollection.first().first()
        target?.let { modifyModels { removeFirst() } }
        return target
    }

    fun dropUntil(activity: Activity) = modifyModels { dropUntil(activity) }
}
