package domain.shared.datastructure.schedule.plans

import domain.shared.datastructure.schedule.action.Activity
import domain.shared.datastructure.schedule.action.Leg
import domain.shared.datastructure.schedule.action.LinkedAction
import java.util.SortedSet

/**
 * A dispatcher holds a collection of [PlanModel] and calls the requested modifications on the registered models.
 */
class Dispatcher(private val mutableCollection: MutableCollection<PlanModel> = mutableSetOf()) : IDispatcher {
    override fun register(model: PlanModel) {
        mutableCollection.add(model)
    }

    private inline fun <T> modifyModels(action: PlanModel.() -> T): T {
        require(!mutableCollection.isEmpty()) {
            "Modify models should never be called on an empty dispatcher"
        }
        return mutableCollection.map { it.action() }.first()
    }

    override fun add(activity: Activity) = modifyModels { add(activity) }
    override fun add(leg: Leg) = modifyModels { add(leg) }
    override fun remove(leg: Leg) = modifyModels { remove(leg) }
    override fun remove(activity: Activity) = modifyModels { remove(activity) }
    override fun replaceActivities(target: SortedSet<Activity>, to: SortedSet<Activity>) = modifyModels {
        replaceActivities(target, to)
    }

    override fun replaceLegs(target: SortedSet<Leg>, to: SortedSet<Leg>) = modifyModels { replaceLegs(target, to) }

    override fun pollFirst(): LinkedAction? {
        val target = mutableCollection.first().first()
        target?.let { modifyModels { removeFirst() } }
        return target
    }

    override fun firstAction(): LinkedAction? = mutableCollection.first().first()

    override fun dropUntil(activity: Activity) = modifyModels { dropUntil(activity) }
}