package edu.kit.ifv.domain.shared.datastructure.schedule.plans
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Activity
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Leg
import edu.kit.ifv.domain.shared.datastructure.schedule.action.LinkedAction
import edu.kit.ifv.domain.shared.datastructure.schedule.action.LinkedActivity
import java.util.SortedSet

interface IDispatcher {
    fun register(model: PlanModel)
    fun add(activity: Activity): LinkedActivity?
    fun add(leg: Leg)
    fun remove(leg: Leg)
    fun remove(activity: Activity)
    fun replaceActivities(target: SortedSet<Activity>, to: SortedSet<Activity>)
    fun replaceLegs(target: SortedSet<Leg>, to: SortedSet<Leg>)
    fun pollFirst(): LinkedAction?
    fun firstAction(): LinkedAction?
    fun dropUntil(activity: Activity)
}
