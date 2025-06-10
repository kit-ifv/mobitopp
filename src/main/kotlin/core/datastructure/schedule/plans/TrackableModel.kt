package core.datastructure.schedule.plans

import core.datastructure.schedule.LinkedAction
import core.datastructure.schedule.MovingAction
import core.datastructure.schedule.StationaryAction

class TrackableModel(private val separablePlanModel: SeparablePlanModel) : SeparablePlanModel by separablePlanModel {

    private val processedActivities: MutableList<StationaryAction> = mutableListOf()
    private val processedLegs: MutableList<MovingAction> = mutableListOf()

    fun pastActivities(): List<StationaryAction> = processedActivities
    fun pastLegs(): List<MovingAction> = processedLegs

    override fun removeFirst(): LinkedAction? {
        val target = separablePlanModel.removeFirst()
        when (target) {
            is StationaryAction -> processedActivities.add(target)
            is MovingAction -> {
                processedLegs.add(target)
            }
        }
        return target
    }
}
