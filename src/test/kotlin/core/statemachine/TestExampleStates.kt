package core.statemachine

import MessageCalled
import StateCalled
import core.statemachine.builder.BaseStateData
import core.statemachine.builder.on
import core.statemachine.builder.stateMachine

interface StationMessage : Message
interface Station : Agent<StationMessage>
class StationAgent : Station {

    private val currentBusses: MutableList<Bus> = mutableListOf()
    private val waitingPassengersByDestination: MutableMap<Station, MutableList<Passenger>> = mutableMapOf()

    fun addBus(bus: Bus) {
        currentBusses.add(bus)
    }

    fun removeBus(bus: Bus) {
        currentBusses.remove(bus)
    }

    fun getBusForPassenger(passenger: Passenger): Bus? = currentBusses.first { it.willDriveTo(passenger.to) }

    fun addWaitingPassenger(passenger: Passenger) {
        waitingPassengersByDestination.getOrPut(passenger.to) { mutableListOf() }.add(passenger)
    }

    fun removeWaitingPassengersForBus(bus: Bus): List<Passenger> {
        val coveredDestinations = waitingPassengersByDestination.keys.filter {
            bus.willDriveTo(it)
        }

        return coveredDestinations.mapNotNull {
            waitingPassengersByDestination.remove(it)
        }.flatten()
    }
}

interface PassengerMessage : Message
interface Passenger : Agent<PassengerMessage> {
    val to: Station
}
class PassengerAgent(
    val from: Station,
    override val to: Station,
    val departure: Time,
) : Passenger {

    private var currentLocation: Agent<*>? = null

    fun setWaiting(station: Station) {
        currentLocation = station
    }

    fun setRiding(bus: Bus) {
        currentLocation = bus
    }

    fun setArrived(station: Station) {
        currentLocation = station
    }
}

interface BusMessage : Message
interface Bus : Agent<BusMessage> {
    fun willDriveTo(station: Station): Boolean
}
class BusAgent(
    val departure: Time,
    private val route: List<Station>,
    private val tripDuration: Map<Station, Time>,
) : Bus {

    private var index = 0
    private val remainingRoute
        get() = route.subList(index, route.size - 1)

    override fun willDriveTo(station: Station) = station in remainingRoute
    fun hasNext() = index < route.size - 1
    fun next(): Station? = route.getOrNull(++index)
    fun current(): Station = route[index]

    private val passengersByDest: MutableMap<Station, MutableList<Passenger>> = mutableMapOf()

    fun removeDeboardingPassengers(): List<Passenger> = passengersByDest.remove(current()) ?: emptyList()
    fun boardPassengers(passengers: List<Passenger>) {
        for ((station, group) in passengers.groupBy { it.to }) {
            passengersByDest.getOrPut(station) { mutableListOf() }.addAll(group)
        }
    }

    fun tripDuration(): Time = tripDuration[current()] ?: 5uL
}

abstract class BusState(time: Time, override val agent: BusAgent) : BaseStateData(time) {

    constructor(state: BusState) : this(state.time, state.agent)

    val bus: BusAgent
        get() = agent

    val self: BusAgent
        get() = agent
}

abstract class StationState(time: Time, override val agent: StationAgent) : BaseStateData(time) {

    constructor(state: StationState) : this(state.time, state.agent)

    val station: StationAgent
        get() = agent

    val self: StationAgent
        get() = agent
}

abstract class PassengerState(time: Time, override val agent: PassengerAgent) : BaseStateData(time) {

    constructor(state: PassengerState) : this(state.time, state.agent)

    val passenger: PassengerAgent
        get() = agent

    val self: PassengerAgent
        get() = agent
}

// passenger states
@StateCalled("StartPassenger")
class PassengerStartState(time: Time, passenger: PassengerAgent) : PassengerState(time, passenger)

@StateCalled("WaitingForBus", PassengerStartState::class, RidingBusState::class)
class WaitingForBusState(time: Time, passenger: PassengerAgent, val station: Station) : PassengerState(time, passenger)

@StateCalled("RidingBus", PassengerState::class)
class RidingBusState(state: PassengerState, val bus: Bus) : PassengerState(state)

@StateCalled("Arrived", PassengerState::class)
class ArrivedState(state: PassengerState, val station: Station) : PassengerState(state)

// passenger messages
@MessageCalled("EnterStation", PassengerStartState::class)
class EnterStationMessage(val station: Station) : PassengerMessage

@MessageCalled("BoardBus")
class BoardBusMessage(val bus: Bus) : PassengerMessage

@MessageCalled("DeboardBus")
class DeboardBusMessage(val station: Station) : PassengerMessage

val passengerStateMachine = stateMachine<PassengerAgent>("PassengerStateMachine") {

    start(
        StartPassenger,
        ::startPassenger
    ) { send ->
        send(enterStation(self.from), self, self.departure)
        //
    }.transitionOn(EnterStation) { message, send ->
        waitingForBus(passenger = self, station = self.from)
    }

    state(WaitingForBus) { send ->
        self.setWaiting(station)
        send(waitingPassenger(self), station, time)
        //
    }.transitionOn(BoardBus) { message, send ->
        ridingBus(bus = message.bus)
    }

    state(RidingBus) {
        self.setRiding(bus)
        //
    }.transitionOn(DeboardBus) { message, send ->
        val station = message.station
        if (station == self.to) {
            arrived(station = station)
        } else {
            waitingForBus(passenger = self, station = station)
        }
    }

    finState(Arrived) {
        self.setArrived(station)
    }
}

// station states
@StateCalled("StartStation")
class StationStartState(time: Time, station: StationAgent) : StationState(time, station)

// station messages
@MessageCalled("WaitingPassenger", WaitingForBusState::class)
class WaitingPassengerMessage(val passenger: Passenger) : PassengerMessage

@MessageCalled("StartBoarding", ArrivingState::class)
class StartBoardingMessage(val bus: Bus) : StationMessage

@MessageCalled("StopBoarding", BoardingState::class)
class StopBoardingMessage(val bus: Bus) : StationMessage

val stationStateMachine = stateMachine<StationAgent>("StationAgentStateMachine") {

    start(
        StartStation,
        ::startStation
    ).on(WaitingPassenger) { message, send ->
        val passenger = message.passenger
        agent.getBusForPassenger(passenger)?.let {
            send.now(boardPassengers(passenger), it)
        } ?: run {
            agent.addWaitingPassenger(passenger)
        }
        //
    }.on(StartBoarding) { message, send ->
        val bus = message.bus
        agent.addBus(bus)
        val boarding = agent.removeWaitingPassengersForBus(bus)
        send.now(boardPassengers(boarding), bus)
        //
    }.on(StopBoarding) { message, send ->
        agent.removeBus(message.bus)
        //
    }
}

// bus states

@StateCalled("StartBus")
class BusStartState(time: Time, bus: BusAgent) : BusState(time, bus)

@StateCalled("Arriving", BusStartState::class, DrivingState::class)
class ArrivingState(state: BusState) : BusState(state)

@StateCalled("Boarding", ArrivingState::class)
class BoardingState(state: BusState) : BusState(state)

@StateCalled("Leaving", BoardingState::class)
class LeavingState(state: BusState) : BusState(state)

@StateCalled("Driving", LeavingState::class)
class DrivingState(state: BusState) : BusState(state)

@StateCalled("Finished", ArrivingState::class)
class FinishedState(state: BusState) : BusState(state)

// bus messages
@MessageCalled("Arrive")
class ArriveMessage(val station: Station) : BusMessage

@MessageCalled("BoardPassengers", StationStartState::class)
data class BoardPassengersMessage(val passengers: List<Agent<*>>) : BusMessage {
    constructor(passenger: Passenger) : this(listOf(passenger))
}

@MessageCalled("Leave")
class LeaveMessage : BusMessage

val busStateMachine = stateMachine<BusAgent>("BusStateMachine") {

    start(
        StartBus,
        ::startBus
    ) { send ->
        send(arrive(bus.current()), bus, bus.departure)
        //
    }.on(Arrive) { message, send ->
        arriving()
    }
}

// val busStateMachine = stateMachine("BusStateMachine") {
//
//    transState(Arriving) { send ->
//
//        val deboarding = bus.shouldDeboard(stop)
//        deboarding.forEach {
//            println(it)
//            deboarded++
//        }
//    }.next {
//        if (bus.hasNext(stop)) {
//            waiting(stop, deboarded)
//        } else {
//            finished()
//        }
//    }
//
//    state(Waiting) { send ->
//        // send busstop: start boarding
//
//        val waitFor: Double = min(transferCount * 0.5, 2.0)
//        val at = time + waitFor.roundToLong().toUInt()
//        send(leave(at), agent)
//    }.on(BoardPersons) { message, send ->
//        val persons = message.persons
//        // send person: boarded
//
//        // send self: updated leave
//        val at = bus.plannedLeave!! + (0.5 * persons.size).toULong()
//
//        send(leave(bus), agent, at)
//    }.transitionOn(Leave) { message, send ->
//        if (time == bus.plannedLeave) {
//            leaving(stop)
//        } else {
//            null
//        }
//    }
//
//    transState(Leaving) { send ->
//        // send bus stop: leaving
//    }.next {
//        driving(bus.nextStop)
//    }
//
//    state(Driving) { send ->
//        send(arrive(nextStop), bus, time + bus.traveltime.toULong())
//    }.transitionOn(Arrive) { message, send ->
//        arriving(message.stop)
//    }
//
//    finState(Finished)
// }
