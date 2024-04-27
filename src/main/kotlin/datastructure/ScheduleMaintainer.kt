package datastructure

import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

class Household(val members: MutableList<Person> = mutableListOf(), val car: Car = Car(START)) {

}

class Car(var position: Location) {
    var driver: Person? = null
}
class Person(var position: Location, val household: Household) {
    fun getCar(): Car {
        return household.car
    }
}

/**
 * A solution to prevent already executed actions to cal onBegin / on End
 */
class ArchivedAction(original: Action): Action by original  {
    override fun onBegin() {
        println("Cannot call onBegin on arcvhived action")
    }

    override fun onEnd() {
        println("Cannot call onEnd on archived action")
    }

}
class CurrentAction(original: Action): Action by original {
    override fun onBegin() {
        println("Cannot call onBegin on started action")
    }
}
class TeleportLeg(val person: Person, private val original: Leg): Leg by original {
    override fun onBegin() {
        person.position = original.startLocation
    }

    override fun onEnd() {
        person.position = original.endLocation
    }
}
class CarLeg(private val person: Person, private val original: Leg): Leg by original {

    private lateinit var car: Car
    override fun onBegin() {
        person.position = original.startLocation
        car = person.getCar()
        car.driver = person

    }

    override fun onEnd() {
        person.position = original.endLocation
        car.position = original.endLocation
        car.driver = null
    }

}

fun interface Event {
    fun happen()
}

fun main() {
    val household = Household()
    val car = household.car
    val person = Person(START, household)

    val t = ScheduleMaintainer(ActionModel(), {Event {it.onBegin()}} , {Event {it.onEnd()}})
    t.add(CarLeg(person, Leg.fromDuration(0.hours, 1.hours, START, OTHER)))
    println("${person.position} ${car.position} ${car.driver}")
    t.handleEvent()
    t.handleEvent()
    println("${person.position} ${car.position} ${car.driver}")
    t.handleEvent()
    val ee = t.getHistory()
    println("${person.position} ${car.position} ${car.driver}")

}
class ScheduleMaintainer(
    private val model: PlanModel,
    val convertToBeginEvent: (Action) -> Event,
    val convertToEndEvent: (Action) -> Event
) : PlanView {

    override val dispatcher: Dispatcher = Dispatcher()
    init {
        dispatcher.register(model)
    }


    private var currentTime: Duration = -Duration.INFINITE
    private val history: MutableList<ArchivedAction> = mutableListOf()

    private var current: CurrentAction? = null

    private var nextEvent: Event? = null

    fun getHistory(): List<ArchivedAction> = history
    fun handleEvent() {
        nextEvent?.happen()
        current?.let {
            nextEvent = convertToEndEvent(it)
            currentTime = it.endTime
            history.add(ArchivedAction(it))
            current = null

        } ?: run  {
            val target = pollFirst()
            target?.setNewAction()?: { println("No Actions found, do you know what you are doing?") }

        }
    }

    private fun Action.setNewAction() {
        currentTime = this.startTime
        current = CurrentAction(this)
        nextEvent = convertToBeginEvent(this)
    }
    fun replaceCurrent(action: Action) {
        require(listOfNotNull(action, model.first()).isConsistent())
        current?.let{
            current = CurrentAction(action)
            nextEvent = convertToEndEvent(action)
        }
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
        //This is fine, if a leg has been handled it is already removed
        super.remove(leg)
    }

    override fun remove(activity: Activity) {
        super.remove(activity)
    }

    override fun replaceActivities(target: SortedSet<Activity>, to: SortedSet<Activity>) {
        require(to.minOf{it.startTime > currentTime})
        super.replaceActivities(target, to)
    }

    override fun replaceLegs(target: SortedSet<Leg>, to: SortedSet<Leg>) {
        require(to.minOf{it.startTime > currentTime})
        super.replaceLegs(target, to)
    }

    override fun pollFirst(): Action? {
        return super.pollFirst()
    }


}