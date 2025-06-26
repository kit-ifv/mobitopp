package core.statemachine

import MessageCalled
import StateCalled
import core.statemachine.builder.BaseStateData
import core.statemachine.builder.on
import core.statemachine.builder.stateMachine
import java.util.SortedMap
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextULong
import kotlin.test.Test

interface StationMessage : Message
interface Station : Agent<StationMessage>, Comparable<Station> {
    val name: String

    override fun compareTo(other: Station) = this.name.compareTo(other.name)
}
class StationAgent(override val name: String) : Station {

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
    val name: String
}
class PassengerAgent(
    override val name: String,
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
    val name: String
    fun willDriveTo(station: Station): Boolean
}
class BusAgent(
    override val name: String,
    val departure: Time,
    private val route: List<Station>,
    private val tripDuration: Map<Station, Time>,
) : Bus {

    private var index = 0
    private val remainingRoute
        get() = route.subList(index, route.size - 1)

    override fun willDriveTo(station: Station) = station in remainingRoute
    fun hasNext() = index < route.size - 1
    fun moveToNext(): Station? = route.getOrNull(++index)
    fun current(): Station = route[index]

    private val passengersByDest: MutableMap<Station, MutableList<Passenger>> = mutableMapOf()

    fun removeDeboardingPassengers(): List<Passenger> = passengersByDest.remove(current()) ?: emptyList()
    fun removeAllPassengers(): List<Passenger> = passengersByDest.values.flatten().also {
        passengersByDest.clear()
    }

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

@MessageCalled("DeboardBus", ArrivingState::class)
class DeboardBusMessage(val station: Station) : PassengerMessage

val passengerStateMachine = stateMachine<PassengerAgent>("PassengerStateMachine") {

    start( //TODO autogenerate start extension method if constructor has exactly arguments Time and Agent?
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

@MessageCalled("StopBoarding", ArrivingState::class, LeavingState::class)
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
        val bus = message.bus
        agent.removeBus(bus)
        send.now(confirmLeave(), bus)
        //
    }
}

// bus states

@StateCalled("StartBus")
class BusStartState(time: Time, bus: BusAgent) : BusState(time, bus)

@StateCalled("Arriving", BusStartState::class, DrivingState::class)
class ArrivingState(state: BusState) : BusState(state) {
    var deboardingCount: Int = 0
}

@StateCalled("Boarding", ArrivingState::class)
class BoardingState(state: BusState, deboardingCount: Int) : BusState(state) {
    private val boardingStart: Time = time
    private var interactionWaitTime: Time = inOutTime(deboardingCount)

    val plannedLeaveTime: Time
        get() = boardingStart + min(2uL, interactionWaitTime)

    fun updateWaitTime(boardingCount: Int) {
        interactionWaitTime += inOutTime(boardingCount)
    }

    private fun inOutTime(deboardingCount: Int): ULong = (deboardingCount * 0.1).toULong()
}

@StateCalled("Leaving", BoardingState::class)
class LeavingState(state: BusState) : BusState(state)

@StateCalled("Driving", LeavingState::class)
class DrivingState(state: BusState) : BusState(state)

@StateCalled("Finished", ArrivingState::class)
class FinishedState(state: BusState) : BusState(state)

// bus messages
@MessageCalled("Arrive")
class ArriveMessage : BusMessage

@MessageCalled("BoardPassengers", StationStartState::class)
data class BoardPassengersMessage(val passengers: List<Agent<*>>) : BusMessage {
    constructor(passenger: Passenger) : this(listOf(passenger))
}

@MessageCalled("Leave", BoardingState::class)
class LeaveMessage : BusMessage

@MessageCalled("ConfirmLeave", StationStartState::class)
class ConfirmLeaveMessage : BusMessage

val busStateMachine = stateMachine<BusAgent>("BusStateMachine") {

    start(
        StartBus,
        ::startBus
    ) { send ->
        send(arrive(), bus, bus.departure)
        //
    }.on(Arrive) { message, send ->
        arriving()
    }

    transState(
        Arriving
    ) { send ->
        val deboarding = if (bus.hasNext()) {
            bus.removeDeboardingPassengers()
        } else {
            bus.removeAllPassengers()
        }

        deboarding.forEach {
            send.now(deboardBus(bus.current()), it)
            deboardingCount++
        }
        //
    }.next { send ->
        if (bus.hasNext()) {
            boarding()
        } else {
            send.now(stopBoarding(), bus.current())
            finished()
        }
    }

    state(Boarding) { send ->
        send(leave(), self, plannedLeaveTime)
        //
    }.on(BoardPassengers) { message, send ->
        message.passengers.forEach {
            send.now(boardBus(bus), it)
        }

        updateWaitTime(message.passengers.size)
        send(leave(), self, plannedLeaveTime)
        //
    }.transitionOn(Leave) { message, send ->
        if (time == plannedLeaveTime) {
            leaving()
        } else {
            null
        }
    }

    state(Leaving) { send ->
        send.now(stopBoarding(), bus.current())
        //
    }.on(BoardPassengers) { message, send ->
        message.passengers.forEach {
            send.now(boardBus(bus), it)
        }
        //
    }.transitionOn(ConfirmLeave) { message, send ->
        driving()
    }

    state(Driving) { send ->
        bus.moveToNext()
        val tripDuration = bus.tripDuration()
        send(arrive(), self, time + tripDuration)
        //
    }.transitionOn(Arrive) { message, send ->
        arriving()
    }


}



class TestExampleStates {

    fun createStations(vararg names: String) = names.map {
        StationAgent(it)
    }

    fun createBussesBySchedule(
        departures: Iterable<Time>,
        travelTimes: SortedMap<Station, Time>
    ): List<BusAgent> {
        val route = travelTimes.keys.toList()
        val start = route.first()
        val end = route.last()

        return departures.map {
            BusAgent(
                "$start > $end at $it",
                it,
                route,
                travelTimes
            )
        }
    }

    fun createRoute(stations: Map<String, Station>, vararg legs: Pair<String, Time>) = sortedMapOf(*legs.map { stations[it.first]!! to it.second }.toTypedArray())

    var idCount = 0L
    fun createPerson(start: Time, end: Time, stations: List<Station>): PassengerAgent {
        val id = idCount++
        val random = Random(id)

        val from = stations.random(random)
        val to = stations.filter { it != from }.random(random)
        val departure = random.nextULong(start, end)

        return PassengerAgent("$id", from, to, departure)
    }

    @Test
    fun runStateMachines() {
        val start: Time = 0uL
        val end: Time = 5uL * 60uL

        val stations = createStations("A", "B", "C", "D", "E").associateBy { it.name }

        val route1 = createRoute(stations, "A" to 0uL, "B" to 3uL, "C" to 5uL, "D" to 6uL, "E" to 4uL) //26 min //every full hour
        val departures1 = start..end step 60L
        val bussesRoute1 = createBussesBySchedule(departures1, route1)

        val route2 = createRoute(stations, "E" to 0uL, "D" to 6uL, "C" to 4uL, "B" to 3uL, "A" to 5uL) //26 min //every hour, half past
        val departures2 = (start + 30uL)..end step 60L
        val bussesRoute2 = createBussesBySchedule(departures2, route2)

        val route3 = createRoute(stations, "B" to 0uL, "D" to 7uL) //9 min //at 10, 30, 50 past x
        val departures3 = (start + 10uL)..end step 20L
        val bussesRoute3 = createBussesBySchedule(departures3, route3)

        val route4 = createRoute(stations, "D" to 0uL, "B" to 7uL) //9 min // at 0, 20, 40 past x
        val departures4 = start..end step 20L
        val bussesRoute4 = createBussesBySchedule(departures4, route4)


        val stationAgents = stations.values.toList()
        val busAgents = bussesRoute1 + bussesRoute2 + bussesRoute3 + bussesRoute4
        val passengerAgents = (0..1000).map { createPerson(start, end, stationAgents) }





    }


}
