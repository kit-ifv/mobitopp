package domain.shared.datastructure.schedule.plans

import domain.shared.datastructure.schedule.action.LinkedActivity
import domain.shared.datastructure.schedule.action.LinkedLeg
import domain.shared.datastructure.schedule.blocks.ActionBlock

interface SeparablePlanModel : PlanModel {

    fun activities(): Collection<LinkedActivity>

    fun lastActivity(): LinkedActivity
    fun legs(): Collection<LinkedLeg>

    fun view(): BlockModel.TripView

    fun nextBlock(): ActionBlock<*>?
}
