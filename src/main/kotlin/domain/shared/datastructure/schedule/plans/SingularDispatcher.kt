package domain.shared.datastructure.schedule.plans

import domain.shared.datastructure.schedule.action.Activity
import domain.shared.datastructure.schedule.action.Leg
import domain.shared.datastructure.schedule.action.LinkedAction
import domain.shared.datastructure.schedule.action.LinkedActivity
import java.util.SortedSet

class SingularDispatcher : IDispatcher {
    lateinit var model: PlanModel
    override fun register(model: PlanModel) {
        this.model = model
    }

    override fun add(activity: Activity): LinkedActivity? = model.add(activity)

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

    override fun pollFirst(): LinkedAction? = model.removeFirst()

    override fun firstAction(): LinkedAction? = model.first()

    override fun dropUntil(activity: Activity) {
        model.dropUntil(activity)
    }
}
