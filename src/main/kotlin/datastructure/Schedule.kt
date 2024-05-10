package datastructure

import java.util.*
import kotlin.time.Duration

class CurrentAction(private val linkedAction: LinkedAction): Action by linkedAction {

    override var endTime: Duration
        get() = linkedAction.endTime
        set(value) {
            linkedAction.endTime = value
        }
    override var endLocation: Location
        get() = linkedAction.endLocation
        set(value) {
            linkedAction.endLocation = value
        }
    override var latestEndTime: Duration
        get() = linkedAction.latestEndTime
        set(value) {
            linkedAction.latestEndTime = value
        }


}
class Schedule(
    private val model: PlanModel,

) : PlanView {

    override val dispatcher: Dispatcher = Dispatcher()
    init {
        dispatcher.register(model)
    }

    private var currentTime: Duration = -Duration.INFINITE
    private val history: MutableList<Action> = mutableListOf()


    private var current: CurrentAction? = null



    fun getHistory(): List<Action> = history
    fun handleEvent() {
        current?.let {

            currentTime = it.endTime
            history.add(it)
            current = null
        } ?: run {
            val target = pollFirst()
            target?.setNewAction() ?: { println("No Actions remaining in the plan") }
        }
    }

    private fun LinkedAction.setNewAction() {
        currentTime = this.startTime
        current = CurrentAction(this)
    }
    override fun add(leg: Leg) {
        require(leg.startTime >= currentTime)
        super.add(leg)
    }

    override fun add(activity: Activity) {
        require(activity.startTime >= currentTime)
        super.add(activity)
    }
    override fun remove(leg: Leg) {
        // This is fine, if a leg has been handled it is already removed
        super.remove(leg)
    }

    override fun remove(activity: Activity) {
        super.remove(activity)
    }

    override fun replaceActivities(target: SortedSet<Activity>, to: SortedSet<Activity>) {
        require(to.minOf { it.startTime > currentTime })
        super.replaceActivities(target, to)
    }

    override fun replaceLegs(target: SortedSet<Leg>, to: SortedSet<Leg>) {
        require(to.minOf { it.startTime > currentTime })
        super.replaceLegs(target, to)
    }

}
