package datastructure.plans

import datastructure.Activity
import datastructure.Leg
import datastructure.LinkedAction
import java.util.*

/**
 * A Planview provides functions to modify all models registered at the same dispatcher.
 */
interface PlanView {
    val dispatcher: IDispatcher
    fun add(leg: Leg) = dispatcher.add(leg)
    fun add(activity: Activity) = dispatcher.add(activity)
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

interface IDispatcher {
    fun register(model: PlanModel)
    fun add(activity: Activity)
    fun add(leg: Leg)
    fun remove(leg: Leg)
    fun remove(activity: Activity)
    fun replaceActivities(target: SortedSet<Activity>, to: SortedSet<Activity>)
    fun replaceLegs(target: SortedSet<Leg>, to: SortedSet<Leg>)
    fun pollFirst(): LinkedAction?
    fun firstAction(): LinkedAction?
    fun dropUntil(activity: Activity)
}

/**
 * A dispatcher holds a collection of [PlanModel] and calls the requested modifications on the registered models.
 */
class Dispatcher(private val mutableCollection: MutableCollection<PlanModel> = mutableSetOf()) : IDispatcher {
    override fun register(model: PlanModel) {
        mutableCollection.add(model)
    }

    private inline fun modifyModels(action: PlanModel.() -> Unit) {
        mutableCollection.forEach { it.action() }
    }

    override fun add(activity: Activity) = modifyModels { add(activity) }
    override fun add(leg: Leg) = modifyModels { add(leg) }
    override fun remove(leg: Leg) = modifyModels { remove(leg) }
    override fun remove(activity: Activity) = modifyModels { remove(activity) }
    override fun replaceActivities(target: SortedSet<Activity>, to: SortedSet<Activity>) = modifyModels {
        replaceActivities(target, to)
    }

    override fun replaceLegs(target: SortedSet<Leg>, to: SortedSet<Leg>) =
        modifyModels { replaceLegs(target, to) }

    override fun pollFirst(): LinkedAction? {
        val target = mutableCollection.first().first()
        target?.let { modifyModels { removeFirst() } }
        return target
    }

    override fun firstAction(): LinkedAction? {
        return mutableCollection.first().first()
    }

    override fun dropUntil(activity: Activity) = modifyModels { dropUntil(activity) }
}
class SingularDispatcher : IDispatcher {
    lateinit var model: PlanModel
    override fun register(model: PlanModel) {
        this.model = model
    }

    override fun add(activity: Activity) {
        model.add(activity)
    }

    override fun add(leg: Leg) {
        model.add(leg)
    }

    override fun remove(leg: Leg) {
        model.remove(leg)
    }

    override fun remove(activity: Activity) {
        model.remove(activity)
    }

    override fun replaceActivities(target: SortedSet<Activity>, to: SortedSet<Activity>) {
        model.replaceActivities(target, to)
    }

    override fun replaceLegs(target: SortedSet<Leg>, to: SortedSet<Leg>) {
        model.replaceLegs(target, to)
    }

    override fun pollFirst(): LinkedAction? {
        return model.removeFirst()
    }

    override fun firstAction(): LinkedAction? {
        return model.first()
    }

    override fun dropUntil(activity: Activity) {
        model.dropUntil(activity)
    }
}
