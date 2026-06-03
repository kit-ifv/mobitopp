package domain.shared.datastructure.schedule

import domain.shared.datastructure.schedule.action.Activity
import domain.shared.datastructure.schedule.action.Leg

interface ActionVisitor<T> {
    fun visitLeg(leg: Leg): T
    fun visitActivity(activity: Activity): T
}

interface ActionBlockVisitor<T> {
    fun visitTrip(leg: LinkTrip): T
    fun visitActivityBlock(activity: Agenda): T
}
