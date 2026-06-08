package edu.kit.ifv.domain.shared.datastructure.schedule.plans
import edu.kit.ifv.domain.shared.datastructure.schedule.action.LinkedAction
import edu.kit.ifv.domain.shared.datastructure.schedule.action.MovingAction
import edu.kit.ifv.domain.shared.datastructure.schedule.action.StationaryAction

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
