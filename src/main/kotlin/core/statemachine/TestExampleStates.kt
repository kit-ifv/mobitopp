package core.statemachine

import core.statemachine.builder.AnyStateType
import core.statemachine.builder.BaseStateData
import core.statemachine.builder.StateType
import core.statemachine.builder.on
import core.statemachine.builder.stateMachine
import kotlin.math.min
import kotlin.math.roundToLong

class BusAgent : Agent<BusMessage> {
    val passengersByDest: Map<String, List<Agent<*>>> = mapOf()
    var plannedLeave: LeaveMessage? = null

    fun hasNext(stop: String): Boolean = TODO("Not yet implemented")

    val nextStop: String
        get() = "test"

    fun shouldDeboard(stop: String): List<Agent<*>> = if (hasNext(stop)) {
        passengersByDest[stop] ?: emptyList()
    } else {
        passengersByDest.values.flatten()
    }

    fun traveltime(stop: String): UInt {
        return 15u
    }
}

typealias AnyMessageType = MessageType<out Message>
abstract class BusMessage(override val type: AnyMessageType) : Message

abstract class BusState(
    type: AnyStateType,
    time: Time,
    override val agent: BusAgent
) : BaseStateData(type, time) {

    constructor(state: BusState, type: AnyStateType) : this(type, state.time, state.agent)

    val bus: BusAgent
        get() = agent
}

val Arriving = StateType<ArrivingState>()
val Waiting = StateType<WaitingState>()
val Leaving = StateType<LeavingState>()
val Driving = StateType<DrivingState>()
val Finished = StateType<FinishedState>()

class ArrivingState(state: BusState, val stop: String, var deboarded: Int = 0) : BusState(state, Arriving)
fun BusState.arriving(stop: String) = ArrivingState(this, stop)

class WaitingState(state: BusState, val stop: String, var transferCount: Int) : BusState(state, Waiting)
fun BusState.waiting(stop: String, transfers: Int) = WaitingState(this, stop, transfers)

class LeavingState(state: BusState, val stop: String) : BusState(state, Leaving)
fun BusState.leaving(stop: String) = LeavingState(this, stop)

class DrivingState(state: BusState, val nextStop: String) : BusState(state, Driving)
fun LeavingState.driving(to: String) = DrivingState(this, to)

class FinishedState(state: BusState) : BusState(state, Finished)
fun BusState.finished() = FinishedState(this)

val Arrive = MessageType<ArriveMessage>()
data class ArriveMessage(override val time: Time, val stop: String) : BusMessage(Arrive)
fun arrive(time: Time, at: String) = ArriveMessage(time, at)

val BoardPersons = MessageType<BoardPersonsMessage>()
data class BoardPersonsMessage(override val time: Time, val persons: List<Agent<*>>) : BusMessage(BoardPersons)
// fun WaitingState.boardPersons(persons: ): List<Agent<*>> = BoardPersons(this)

val Leave = MessageType<LeaveMessage>()
data class LeaveMessage(override val time: Time, val agent: BusAgent) : BusMessage(Leave) {
    init {
        agent.plannedLeave = this
    }
}
fun WaitingState.leave(time: Time) = LeaveMessage(time, agent)






val busStateMachine = stateMachine("BusStateMachine") {

    transState(Arriving) { send ->

        val deboarding = bus.shouldDeboard(stop)
        deboarding.forEach {
            println(it)
            deboarded++
        }
    }.next {
        if (bus.hasNext(stop)) {
            waiting(stop, deboarded)
        } else {
            finished()
        }
    }

    state(Waiting) { send ->
        // send busstop: start boarding

        val waitFor: Double = min(transferCount * 0.5, 2.0)
        val at = time + waitFor.roundToLong().toUInt()
        send(leave(at), agent)
    }.on(BoardPersons) { message, send ->
        val persons = message.persons
        // send person: boarded

        // send self: updated leave
        val at = message.time + (0.5 * persons.size).toULong()
        send(leave(at), agent)

    }.transitionOn(Leave) { message, send ->
        if (message == bus.plannedLeave) {
            leaving(stop)
        } else {
            waiting(stop, 0) // should not happen, how to invalidate previous leave?
        }
    }

    transState(Leaving) { send ->
        // send bus stop: leaving
    }.next {
        driving(bus.nextStop)
    }

    state(Driving) { send ->
        send(arrive(time + bus.traveltime(nextStop), nextStop), bus)

    }.transitionOn(Arrive) { message, send ->
        arriving(message.stop)
    }
}
