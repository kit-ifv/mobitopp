package domain.events

import datastructure.LinkTrip
import datastructure.alternateByImpedance
import domain.data.Person
import domain.data.PrivateCar
import domain.data.SharingStation
import domain.data.SharingVehicle
import domain.data.getBestCar
import domain.data.locationBySchedule
import domain.enums.Mode
import domain.location.Location
import domain.location.Metrics
import modeling.events.Agent
import modeling.events.Event
import usecases.models.ModeChoiceAlternative
import usecases.models.SharingAvailabilityFilter

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
        return agent.location == target // TODO check
    }

    override fun onEnter(t: Person) {
        car.keyHolder = t
        car.addDriver(t)
        car.state = PrivateCar.CarState.IN_USE
    }

    override fun onExit(t: Person) {
        car.location = observer.location
        car.removeDriver()
        car.state = PrivateCar.CarState.PARKED
        if (observer.location == t.household.location) { // TODO check
            car.keyHolder = null
        }
    }
}

class StationSharingVehicleScope(
    override val observer: Person,
//    private val startStation: SharingStation,
    private val vehicle: SharingVehicle,
    private val endStation: SharingStation,
) : EventScope<Person> {

    init {
        onEnter(observer)
    }

    override fun onEnter(t: Person) {
        // Nothing happens on enter
    }

    override fun onExit(t: Person) {
        vehicle.returnTo(endStation)
    }

    override fun exitScope(agent: Person) = agent.locationBySchedule() == endStation.location
}

fun interface ModeScopeSelector {
    fun pickScope(event: Event<Person>, mode: Mode, person: Person, legs: LinkTrip): List<Event<Person>>
}

class CarSelector(private val car: Mode) : ModeScopeSelector {
    override fun pickScope(event: Event<Person>, mode: Mode, person: Person, legs: LinkTrip): List<Event<Person>> {
        return if (mode == car) {
            val vehicle = requireNotNull(person.getBestCar()) {
                "No vehicle is available for this person ${person.id}." +
                    " Household at ${person.household.location} contains vehicles: " +
                    "${person.household.cars.map { "${it.id} ${it.state} ${it.keyHolder?.id} ${it.location}" }}\n\n"
            }
            listOf(EventWithScope(event, CarScope(person, vehicle, legs.legs.last().endLocation)))
        } else {
            listOf(event)
        }
    }
}

class SharingVehicleSelector(
    private val sharingMode: Mode,
    private val modeAvailabilityFilter: SharingAvailabilityFilter,
    private val metrics: Metrics,
    private val footMode: Mode,
) : ModeScopeSelector {

    override fun pickScope(event: Event<Person>, mode: Mode, person: Person, legs: LinkTrip): List<Event<Person>> {
        return if (mode == sharingMode) {
            require(legs.legs.size == 1) {
                "Expected trip with one leg, to replace it with 3 legs: foot - sharing - foot"
            }

            val origin = legs.origin
            val destination = legs.destination

            val checkSharing =
                modeAvailabilityFilter.checkSharing(
                    ModeChoiceAlternative(
                        person,
                        event.time,
                        origin,
                        destination,
                        sharingMode,
                        metrics,
                        person.sharedResources()
                    ),
                    sharingMode
                )

            val (startStation, endStation) = requireNotNull(checkSharing) {
                "Wie hast du es geschafft Ridesharing zu wählen ohne ein Stationspaar?"
            }

            // Vehicle is reserved when the trip is planned
            val vehicle = startStation.takeAny() // synchronized(startStation) {  }
            legs.alternateByImpedance(metrics) {
                taking(footMode to startStation.location)
                taking(sharingMode to endStation.location)
                taking(footMode to destination)
            }

            listOf(EventWithScope(event, StationSharingVehicleScope(person, vehicle, endStation)))
        } else {
            listOf(event)
        }
    }
}

class ModeScopeDispatcher(
    private val scopeByMode: Map<Mode, ModeScopeSelector>
) {

    @Suppress("SpreadOperator")
    constructor(vararg modeScopes: Pair<Mode, ModeScopeSelector>) : this(mapOf(*modeScopes))

    fun pickScope(event: Event<Person>, mode: Mode, person: Person, legs: LinkTrip): List<Event<Person>> =
        scopeByMode[mode]?.pickScope(event, mode, person, legs) ?: listOf(event)

    companion object {
        val empty = ModeScopeDispatcher(emptyMap())
    }
}
