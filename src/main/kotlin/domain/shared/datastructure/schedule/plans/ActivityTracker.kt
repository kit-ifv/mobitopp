package domain.shared.datastructure.schedule.plans

import domain.shared.datastructure.schedule.action.Activity
import domain.shared.datastructure.schedule.action.LinkedActivity

interface ActivityTracker {
    fun add(activity: Activity): LinkedActivity?
    fun remove(activity: Activity)
    fun replaceActivities(target: Set<Activity>, to: Set<Activity>)
}