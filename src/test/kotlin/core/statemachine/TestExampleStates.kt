package core.statemachine

import MessageCalled
import StateCalled
import core.statemachine.builder.BaseStateData
import core.statemachine.builder.on
import core.statemachine.builder.stateMachine
import random
import utils.units.AbsoluteTime
import utils.units.max
import utils.units.sinceStart
import java.util.PriorityQueue
import kotlin.random.Random
import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

interface StationMessage : Message
interface Station : Agent<StationMessage> {
    val name: String
}

class StationAgent(override val name: String) : StateBasedAgent<StationMessage>, Station {
    override val stateMachine = stationStateMachine.create(
        AbsoluteTime.START,
        this
    ) // TODO move factory to constructor?

    private val currentBusses: MutableList<Bus> = mutableListOf()
    private val waitingPassengersByDestination: MutableMap<Station, MutableList<Passenger>> = mutableMapOf()

    fun addBus(bus: Bus) {
        currentBusses.add(bus)
    }

    fun removeBus(bus: Bus) {
        currentBusses.remove(bus)
    }

    fun getBusForPassenger(passenger: Passenger): Bus? = currentBusses.firstOrNull {
        it.willDriveTo(passenger.to)
    }

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

    override fun toString() = name

    fun reportState() {
        println("$this:")
        println("  current busses: $currentBusses")
        println("  waiting passengers:")
        waitingPassengersByDestination.forEach { station, passengers ->
            println("    to $station: ${passengers.size}")
        }
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
    val departure: AbsoluteTime,
) : StateBasedAgent<PassengerMessage>, Passenger {
    override val stateMachine = passengerStateMachine.create(
        AbsoluteTime.START,
        this
    ) // TODO move factory to constructor?

    var stateDescriptor = "IDLE"
        private set

    private var currentLocation: Agent<*>? = null

    fun setWaiting(station: Station) {
        stateDescriptor = "WAITING"
        currentLocation = station
    }

    fun setRiding(bus: Bus) {
        stateDescriptor = "RIDING"
        currentLocation = bus
    }

    fun setArrived(station: Station) {
        stateDescriptor = "ARRIVED"
        currentLocation = station
    }

    override fun toString() = "$name: $from > $to"
}

interface BusMessage : Message
interface Bus : Agent<BusMessage> {
    val name: String
    fun willDriveTo(station: Station): Boolean
}
class BusAgent(
    override val name: String,
    val departure: AbsoluteTime,
    private val route: List<Station>,
    private val tripDuration: Map<Station, Duration>,
) : StateBasedAgent<BusMessage>, Bus {
    override val stateMachine = busStateMachine.create(AbsoluteTime.START, this) // TODO move factory to constructor?

    private var index = 0
    private val remainingRoute
        get() = route.subList(index, route.size)

    override fun willDriveTo(station: Station) = station in remainingRoute
    fun hasNext() = index < route.size - 1
    fun moveToNext(): Station? = route.getOrNull(++index)
    fun currentStation(): Station = route[index]

    private val passengersByDest: MutableMap<Station, MutableList<Passenger>> = mutableMapOf()

    fun removeDeboardingPassengers(): List<Passenger> = passengersByDest.remove(currentStation()) ?: emptyList()
    fun removeAllPassengers(): List<Passenger> = passengersByDest.values.flatten().also {
        passengersByDest.clear()
    }

    fun boardPassengers(passengers: List<Passenger>) {
        for ((station, group) in passengers.groupBy { it.to }) {
            passengersByDest.getOrPut(station) { mutableListOf() }.addAll(group)
        }
    }

    fun tripDuration(): Duration = tripDuration[currentStation()] ?: 5.minutes

    override fun toString() = name

    fun isFinished() = index >= route.size - 1

    val passengerCount: Int
        get() = passengersByDest.values.flatten().size
}

abstract class BusState(time: AbsoluteTime, override val agent: BusAgent) : BaseStateData(time) {

    constructor(state: BusState) : this(state.time, state.agent)

    val bus: BusAgent
        get() = agent

    val self: BusAgent
        get() = agent
}

abstract class StationState(time: AbsoluteTime, override val agent: StationAgent) : BaseStateData(time) {

    constructor(state: StationState) : this(state.time, state.agent)

    val station: StationAgent
        get() = agent

    val self: StationAgent
        get() = agent
}

abstract class PassengerState(time: AbsoluteTime, override val agent: PassengerAgent) : BaseStateData(time) {

    constructor(state: PassengerState) : this(state.time, state.agent)

    val passenger: PassengerAgent
        get() = agent

    val self: PassengerAgent
        get() = agent
}

// passenger states
@StateCalled("StartPassenger")
class PassengerStartState(time: AbsoluteTime, passenger: PassengerAgent) : PassengerState(time, passenger)

@StateCalled("WaitingForBus", PassengerStartState::class, RidingBusState::class)
class WaitingForBusState(time: AbsoluteTime, passenger: PassengerAgent, val station: Station) : PassengerState(
    time,
    passenger
)

@StateCalled("RidingBus", PassengerState::class)
class RidingBusState(state: PassengerState, val bus: Bus) : PassengerState(state)

@StateCalled("Arrived", PassengerState::class)
class ArrivedState(state: PassengerState, val station: Station) : PassengerState(state)

// passenger messages
@MessageCalled("EnterStation", PassengerStartState::class)
class EnterStationMessage(val station: Station) : PassengerMessage

@MessageCalled("BoardBus")
class BoardBusMessage(val bus: Bus) : PassengerMessage

@MessageCalled("DeboardBus", DeboardingState::class)
class DeboardBusMessage(val station: Station) : PassengerMessage

val passengerStateMachine = stateMachine<PassengerAgent>("PassengerStateMachine") {

    start( // TODO autogenerate start extension method if constructor has exactly arguments Time and Agent?
        StartPassenger,
        ::startPassenger
    ) { send ->
        send(enterStation(self.from), self, self.departure)
        //
    }.transitionOn(EnterStation) { message, send ->
        waitingForBus(station = self.from)
    }

    state(WaitingForBus) { send ->
        self.setWaiting(station)
        val waitingPassenger = waitingPassenger(self)
        send.now(waitingPassenger, station)
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
            println("    WARNING: passenger exits at station: $station other that desired destination: ${self.to}")
            waitingForBus(station = station)
        }
    }

    finState(Arrived) {
        self.setArrived(station)
    }
}

// station states
@StateCalled("StartStation")
class StationStartState(time: AbsoluteTime, station: StationAgent) : StationState(time, station)

// station messages
@MessageCalled("WaitingPassenger", WaitingForBusState::class)
class WaitingPassengerMessage(val passenger: Passenger) : StationMessage

@MessageCalled("StartBoarding", BoardingState::class)
class StartBoardingMessage(val bus: Bus) : StationMessage

@MessageCalled("StopBoarding", LeavingStationState::class)
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
        if (boarding.isNotEmpty()) {
            send.now(boardPassengers(boarding), bus)
        }
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
class BusStartState(time: AbsoluteTime, bus: BusAgent) : BusState(time, bus)

@StateCalled("Deboarding", BusStartState::class, DrivingState::class)
class DeboardingState(state: BusState) : BusState(state) {
    var deboardingCount: Int = 0
}

@StateCalled("Boarding", DeboardingState::class)
class BoardingState(state: BusState, deboardingCount: Int) : BusState(state) {
    private val boardingStart: AbsoluteTime = time
    private var interactionWaitTime: Duration = inOutTime(deboardingCount)
    private val minWaitTime = 2.minutes

    val plannedLeaveTime: AbsoluteTime
        get() = boardingStart + max(minWaitTime, interactionWaitTime)

    fun updateWaitTime(boardingCount: Int): Boolean {
        interactionWaitTime += inOutTime(boardingCount)
        val leaveTimeChanged = interactionWaitTime > minWaitTime
        return leaveTimeChanged
    }

    private fun inOutTime(deboardingCount: Int): Duration = 10.seconds * deboardingCount
}

@StateCalled("LeavingStation", BoardingState::class, DeboardingState::class)
class LeavingStationState(state: BusState) : BusState(state)

@StateCalled("Driving", LeavingStationState::class)
class DrivingState(state: BusState) : BusState(state)

@StateCalled("FinishedBus", LeavingStationState::class)
class FinishedBusState(state: BusState) : BusState(state)

// bus messages
@MessageCalled("Arrive")
class ArriveMessage : BusMessage

@MessageCalled("BoardPassengers", StationStartState::class)
data class BoardPassengersMessage(val passengers: List<Passenger>) : BusMessage {
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
    }.transitionOn(Arrive) { message, send ->
        deboarding()
    }

    transState(
        Deboarding
    ) { send ->
        val deboarding = if (bus.hasNext()) {
            bus.removeDeboardingPassengers()
        } else {
            bus.removeAllPassengers()
        }

        deboarding.forEach {
            send.now(deboardBus(bus.currentStation()), it)
            deboardingCount++
        }
        //
    }.next { send ->
        if (bus.hasNext()) {
            boarding()
        } else {
            leavingStation()
        }
    }

    state(Boarding) { send ->
        send.now(startBoarding(), bus.currentStation())
        send(leave(), self, plannedLeaveTime)
        //
    }.on(BoardPassengers) { message, send ->
        message.passengers.forEach {
            send.now(boardBus(bus), it)
        }
        bus.boardPassengers(message.passengers)

        if (updateWaitTime(message.passengers.size)) {
            send(leave(), self, plannedLeaveTime)
        }
        //
    }.transitionOn(Leave) { message, send ->
        if (time == plannedLeaveTime) {
            leavingStation()
        } else {
            null
        }
    }

    state(LeavingStation) { send ->
        send.now(stopBoarding(), bus.currentStation())
        //
    }.on(BoardPassengers) { message, send ->
        // could keep passengers imprisoned in bus if next state is finished :(
        bus.boardPassengers(message.passengers)
        message.passengers.forEach {
            send.now(boardBus(bus), it)
        }
        //
    }.transitionOn(ConfirmLeave) { message, send ->
        if (bus.hasNext()) {
            driving()
        } else {
            finishedBus()
        }
    }

    state(Driving) { send ->
        bus.moveToNext()
        val tripDuration = bus.tripDuration()
        send(arrive(), self, time + tripDuration)
        //
    }.transitionOn(Arrive) { message, send ->
        deboarding()
    }

    finState(FinishedBus) {
        if (bus.passengerCount > 0) {
            println(
                "    WARNING: $bus still has ${bus.passengerCount} passengers on board when going into finished state!"
            )
        }
    }
}

class TestExampleStates {

    fun createStations(vararg names: String) = names.map {
        StationAgent(it)
    }

    fun createBussesBySchedule(
        departures: Iterable<AbsoluteTime>,
        travelTimes: LinkedHashMap<Station, Duration>
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

    fun createRoute(stations: Map<String, Station>, vararg legs: Pair<String, Duration>) = linkedMapOf(
        *legs.map { stations[it.first]!! to it.second }.toTypedArray()
    )

    var idCount = 0L
    fun createPerson(start: AbsoluteTime, end: AbsoluteTime, stations: List<Station>): PassengerAgent {
        val id = idCount++
        val random = Random(id)

        val from = stations.random(random)
        val to = stations.filter { it != from }.random(random)
        val departure = (start..end).random(random)

        return PassengerAgent("$id", from, to, departure)
    }

    @Test
    fun runStateMachines() {
        val start: AbsoluteTime = AbsoluteTime.START
        val endDemand: AbsoluteTime = 4.hours.sinceStart
        val end: AbsoluteTime = 5.hours.sinceStart

        val stations = createStations("A", "B", "C", "D", "E").associateBy { it.name }

        val route1 = createRoute(
            stations,
            "A" to 0.minutes,
            "B" to 3.minutes,
            "C" to 5.minutes,
            "D" to 6.minutes,
            "E" to 4.minutes
        ) // 26 min //every full hour
        val departures1 = start..end step 60.minutes
        val bussesRoute1 = createBussesBySchedule(departures1, route1)

        val route2 = createRoute(
            stations,
            "E" to 0.minutes,
            "D" to 6.minutes,
            "C" to 4.minutes,
            "B" to 3.minutes,
            "A" to 5.minutes
        ) // 26 min //every hour, half past
        val departures2 = (start + 30.minutes)..end step 60.minutes
        val bussesRoute2 = createBussesBySchedule(departures2, route2)

        val route3 = createRoute(stations, "B" to 0.minutes, "D" to 7.minutes) // 9 min //at 10, 30, 50 past x
        val departures3 = (start + 10.minutes)..end step 20.minutes
        val bussesRoute3 = createBussesBySchedule(departures3, route3)

        val route4 = createRoute(stations, "D" to 0.minutes, "B" to 7.minutes) // 9 min // at 0, 20, 40 past x
        val departures4 = start..end step 20.minutes
        val bussesRoute4 = createBussesBySchedule(departures4, route4)

        val stationAgents = stations.values.toList()
        val busAgents = bussesRoute1 + bussesRoute2 + bussesRoute3 + bussesRoute4
        val passengerAgents = (0..1000).map { createPerson(start, endDemand, stationAgents) }

        val initEvents = (stationAgents + busAgents + passengerAgents).flatMap {
            it.init()
        }

        val queue = PriorityQueue<Event<*>>(initEvents.size, compareBy { it.receiveTime })
        queue.addAll(initEvents)

        while (!queue.isEmpty()) {
            val event = queue.poll()
            println("[${event.receiveTime}] ${queue.size} elements remaining in queue")
            println("  processing: ${event.sender.tag()} > ${event.receiver.tag()}: ${event.content::class.simpleName}")
            val messages = event.execute()
            println("  produced ${messages.size} events.")
            queue.addAll(messages)
        }

        stationAgents.forEach {
            it.reportState()
            println()
        }

        println(
            passengerAgents.groupingBy { it.stateDescriptor }.eachCount()
        )

        println(
            busAgents.groupingBy { it.isFinished() }.eachCount()
        )
    }
}

fun Agent<*>.tag() = (this::class.simpleName ?: "Agent") + "[$this]"
