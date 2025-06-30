package core.statemachine

import MessageCalled
import StateCalled
import core.statemachine.builder.BaseStateData
import core.statemachine.builder.on
import core.statemachine.builder.stateMachine
import utils.units.AbsoluteTime
import utils.units.max
import kotlin.collections.iterator
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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
    override val stateMachine = busStateMachine.create(
        AbsoluteTime.Companion.START,
        this
    ) // TODO move factory to constructor?

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
