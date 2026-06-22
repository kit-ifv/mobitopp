package edu.kit.ifv.core.statemachine
import edu.kit.ifv.MessageCalled
import edu.kit.ifv.StateCalled
import edu.kit.ifv.core.statemachine.builder.BaseStateData
import edu.kit.ifv.core.statemachine.builder.on
import edu.kit.ifv.core.statemachine.builder.stateMachine
import edu.kit.ifv.core.statemachine.usage.withRecording
import edu.kit.ifv.utils.units.AbsoluteTime

interface StationMessage : Message
interface Station : Agent<StationMessage> {
    val name: String
}

class StationAgent(override val name: String) :
    StateBasedAgent<StationMessage>,
    Station {
    override val stateMachine = stationStateMachine.create(
        AbsoluteTime.START,
        this,
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

abstract class StationState(time: AbsoluteTime, override val agent: StationAgent) : BaseStateData(time) {

    constructor(state: StationState) : this(state.time, state.agent)

    val station: StationAgent
        get() = agent

    val self: StationAgent
        get() = agent
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
        ::startStation,
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
}.withRecording()
