package datastructure

import datastructure.plans.PlanView
import datastructure.plans.SeparablePlanModel
import datastructure.plans.SingularDispatcher
import datastructure.plans.TrackableModel
import domain.enums.MODEUNKOWN
import domain.location.Location
import utils.units.AbsoluteTime
import java.util.*
import kotlin.NoSuchElementException

/**
 * Current Action is a wrapper class that only allows modification of [LinkedAction] attributes which are in the future:
 * The [endLocation], [endTime] and [latestEndTime], while protecting alteration for all other attributes
 */
class CurrentAction(private val linkedAction: LinkedAction) : Action by linkedAction {

    val original: Action = linkedAction.original

    val type: ActionType = linkedAction.actionType
    override var endTime: AbsoluteTime
        get() = linkedAction.endTime
        set(value) {
            linkedAction.endTime = value
        }
    override var endLocation: Location
        get() = linkedAction.endLocation
        set(value) {
            linkedAction.endLocation = value
        }
    override var latestEndTime: AbsoluteTime
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
    private val model: TrackableModel,

) : PlanView {
    constructor(separablePlanModel: SeparablePlanModel) : this(TrackableModel(separablePlanModel))

    override val dispatcher: SingularDispatcher = SingularDispatcher()

    init {
        dispatcher.register(model)
    }

    private var currentTime: AbsoluteTime = AbsoluteTime.MINUS_INFINITY

    // The past should not be altered by external code, so to protect this attribute we have the [past] access
    private val alterableHistory: MutableList<Action> by lazy {
        mutableListOf()
    }

    val past: List<Action>
        get() = alterableHistory

    var present: CurrentAction? = null
        private set

    val future get() = model.actions().toList()
    fun actions(): List<LinkedAction> = model.actions().toList()

    fun tripView() = model.view()
    fun lastStartedAction(): Action = present ?: past.last()

    fun nextAction(): Action? = present ?: model.first()
    fun activities() = model.activities()

    fun pastActivities() = model.pastActivities()

    fun nextBlock() = model.nextBlock()?.representative(dispatcher = dispatcher, this)

    fun legs() = model.legs()

    fun pastLegs() = model.pastLegs()

    fun lastAction(): Action? = alterableHistory.lastOrNull()

    /**
     * Either ends the current action if one is present, or sets the first future action to be the next present action
     * in a sense, it steps through the points of the schedule. Note that the time is updated based on the end time, so
     * no actions later than the step can be added to the plan.
     */
    fun step(): Location {
        return present?.let {
            currentTime = it.endTime
            alterableHistory.add(it.original)
            present = null
            pollFirst()
            it.endLocation
        } ?: run {
            val target = firstAction()
            target?.setNewAction() ?: { println("No Actions remaining in the plan") }
            target?.startLocation ?: throw NoSuchElementException("No Location can be found")
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

    override fun add(activity: Activity): LinkedActivity? {
        require(activity.startTime >= currentTime)
        return super.add(activity)
    }

    fun addWithPrecedingLeg(activity: Activity): LinkedActivity? {
        require(activity.startTime >= currentTime)
        // If the last action is larger than the activity there is something wrong and inserting with a leg cannot be
        // done trivially
        if (actions().lastOrNull()?.let { it >= activity } == true) {
            // System.err.println("Warning: Activity $activity is not the last action, inserting without additional leg")
            val linkedActivity = add(activity)
            return linkedActivity
        }

        val last = model.activities().lastOrNull()

        last?.let {
            super.add(
                it.createLegTo(activity, MODEUNKOWN)
            )
        }
        return super.add(activity)
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
