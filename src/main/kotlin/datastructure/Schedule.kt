package datastructure

import datastructure.plans.Dispatcher
import datastructure.plans.PlanView
import datastructure.plans.SeparablePlanModel
import java.util.*
import kotlin.time.Duration

/**
 * Current Action is a wrapper class that only allows modification of [LinkedAction] attributes which are in the future:
 * The [endLocation], [endTime] and [latestEndTime], while protecting alteration for all other attributes
 */
class CurrentAction(private val linkedAction: LinkedAction) : Action by linkedAction {

    val original: Action = linkedAction.original

    val type: ActionType = linkedAction.actionType
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

/**
 * A [Schedule] maintains the state of a plan as defined in the model when being executed. It holds the attributes
 * [past],  [present] and [future] to represent the actions that took place, the activity that may be performed now and
 * planned actions. This class is a [PlanView] and thus can alter the model. Note that alterations need to be later
 * than the ast executed element to maintain consistency.
 *
 * @property past The Actions that have been completely handled from the plan.
 * @property present The currently performed action if any
 * @property future The planned Actions.
 *
 */
class Schedule(
    private val model: SeparablePlanModel,

) : PlanView {

    override val dispatcher: Dispatcher = Dispatcher()

    init {
        dispatcher.register(model)
    }

    private var currentTime: Duration = -Duration.INFINITE

    // The past should not be altered by external code, so to protect this attribute we have the [past] access
    private val alterableHistory: MutableList<Action> = mutableListOf()

    val past: List<Action>
        get() = alterableHistory

    var present: CurrentAction? = null
        private set

    val future get() = model.actions().toList()

    fun actions(): List<LinkedAction> = model.actions().toList()

    fun tripView() = model.view()
    fun lastAction(): Action = present ?: past.last()
    fun activities() = model.activities()
    fun step() {
        present?.let {
            currentTime = it.endTime
            alterableHistory.add(it.original)
            present = null
        } ?: run {
            val target = pollFirst()
            target?.setNewAction() ?: { println("No Actions remaining in the plan") }
        }
    }

    private fun LinkedAction.setNewAction() {
        currentTime = this.startTime
        present = CurrentAction(this)
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
