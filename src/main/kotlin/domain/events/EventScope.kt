package domain.events

import datastructure.LinkTrip
import domain.data.Person
import domain.data.PrivateCar
import domain.data.getBestCar
import domain.data.locationBySchedule
import domain.enums.Mode
import domain.enums.StandardMode
import domain.location.Location
import modeling.events.Agent
import modeling.events.Event

/**
 * An event scope spans the event processing of a target entity [T] holding additional information until the
 * scope is exited. The idea is that certain events will trigger an action at a later, possibly unknown time
 * (Such as entering a car and exiting a car - if the assumption is that someone in the future might model
 * car trips where a person does not immediately exit the vehicle after finishing a leg).
 */
interface EventScope<T : Agent<T>> {
    /**
     * The [observer] is used as a way to hold the state and observe the exit condition. This is due to the reason
     * that the type of generated events is usually unknown.
     */
    val observer: T

    /**
     * This function is the condition that needs to be met in order to exit the scope.
     */
    fun exitScope(agent: T): Boolean

    /**
     * Specifies an action that should be performed upon initializing the Event Scope.
     */

    fun onEnter(t: T): Unit

    /**
     * Specifis an action that should be performed upon exiting the scope.
     */
    fun onExit(t: T): Unit
}

/**
 * An EventWithScope wraps around a target [original] Event. The event is processed as usual, but if the scope exit
 * condition is not met, all the generated events are wrapped in the same Scope.
 */
class EventWithScope<E, A : Agent<A>>(private val original: Event<E>, private val scope: EventScope<A>) :
    Event<E>(original.time, original.priority, original.agent) {
    override fun process(entity: E): List<Event<*>> {
        return scope.run {
            val target = original.process(entity)

            if (exitScope(observer)) {
                onExit(observer)
                target
            } else {
                target.map { EventWithScope(it, scope) }
            }
        }
    }
}

/**
 * In a CarScope the observer should enter the vehicle upon initialization and first exit the vehicle when the agent
 * has reached the target location.
 */
class CarScope(override val observer: Person, private val car: PrivateCar, val target: Location) : EventScope<Person> {

    init {
        onEnter(observer)
    }

    override fun exitScope(agent: Person): Boolean {
        return agent.locationBySchedule() == target
    }

    override fun onEnter(t: Person) {
        car.addDriver(t)
        car.state = PrivateCar.CarState.IN_USE
    }

    override fun onExit(t: Person) {
        car.location = observer.locationBySchedule()
        car.removeDriver()
        car.state = PrivateCar.CarState.PARKED
    }
}

fun interface ModeScopeSelector {
    fun pickScope(event: Event<Person>, mode: Mode, person: Person, legs: LinkTrip): List<Event<Person>>
}

object CarSelector : ModeScopeSelector {
    override fun pickScope(event: Event<Person>, mode: Mode, person: Person, legs: LinkTrip): List<Event<Person>> {
        return if (mode == StandardMode.CAR) {
            // TODO dont't use !!
            listOf(EventWithScope(event, CarScope(person, person.getBestCar()!!, legs.legs.last().endLocation)))
        } else {
            listOf(event)
        }
    }
}
