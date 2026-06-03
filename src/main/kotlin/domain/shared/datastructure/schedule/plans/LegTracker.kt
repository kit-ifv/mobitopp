package domain.shared.datastructure.schedule.plans

import domain.shared.datastructure.schedule.action.Leg

interface LegTracker {
    fun add(leg: Leg)
    fun remove(leg: Leg)
    fun replaceLegs(target: Set<Leg>, to: Set<Leg>)
}