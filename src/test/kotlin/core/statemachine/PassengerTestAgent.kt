package core.statemachine

import MessageCalled
import StateCalled
import core.statemachine.builder.BaseStateData
import core.statemachine.builder.stateMachine
import utils.units.AbsoluteTime

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
        AbsoluteTime.Companion.START,
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
