package core.statemachine

import core.statemachine.builder.StateData
import utils.units.AbsoluteTime

/**
 * Interface which creates a scope in which messages can be sent.
 * Collects all [Message]s generated via [Send] within the scope and creates the respective [Event]s.
 */
interface SendScope {
    /**
     * Creates a scope in which messages can be sent,
     * and creates [Event]s for all [Message]s generated via [Send] within the scope.
     * Additionally, returns the result of the scope function as the second component of the resulting [Pair]
     *
     * @param R The return type of the scope function
     * @param scope A function that uses the [Send] interface to send messages
     * @return A pair containing all events generated for the [Message]s sent within the scope
     *         and the result of the scope function
     */
    operator fun <R> invoke(scope: (Send) -> R): Pair<Events, R>
}

/**
 * Creates a scope in which [Message]s can be sent,
 * and creates events for each [Message] sent via [Send] within the scope.
 *
 * @param R The return type of the scope function
 * @param data The [core.statemachine.builder.StateData] containing the current agent and time
 * @param scope A function that uses the [Send] interface to send messages
 * @return A pair containing all events generated within the scope and the result of the scope function
 */
fun <R> sendScope(data: StateData, scope: (Send) -> R): Pair<Events, R> = SingleUseSendScope(data).invoke(scope)

/**
 * Interface for sending messages to other agents.
 * Sender and send time must be known in implementations of this interface to create Events.
 * Provides methods for scheduling messages to be delivered at specific times.
 */
interface Send {
    /**
     * Schedules a message to be delivered to an agent at a specific time.
     *
     * @param M The type of message to send
     * @param message The message to send
     * @param to The agent that should receive the message
     * @param at The time at which the message should be delivered
     */
    operator fun <M : Message> invoke(message: M, to: Agent<in M>, at: AbsoluteTime)

    /**
     * Schedules a message to be delivered to an agent immediately,
     * meaning that the receiving time of the message is equal to its sending time.
     *
     * @param M The type of message to send
     * @param message The message to send
     * @param to The agent that should receive the message
     */
    fun <M : Message> now(message: M, to: Agent<in M>)
}

/**
 * Implementation of [Send] and [SendScope] that collects events generated within a scope.
 * This class is designed to be used once and then discarded.
 *
 * @property sendTime The fixed time at which messages are being sent
 * @property sender The fixed agent that is sending messages
 */
private class SingleUseSendScope(private val sendTime: AbsoluteTime, private val sender: Agent<*>) :
    Send,
    SendScope {

    /**
     * Creates a [SingleUseSendScope] from state data.
     *
     * @param data The state data containing the current agent and time
     */
    constructor(data: StateData) : this(data.time, data.agent)

    /**
     * The list of events generated within this scope.
     */
    private val events: MutableList<Event<*>> = mutableListOf()

    override fun <R> invoke(scope: (Send) -> R): Pair<Events, R> {
        events.clear()
        val result = scope(this)
        return events to result
    }

    override fun <M : Message> invoke(message: M, to: Agent<in M>, at: AbsoluteTime) {
        events.add(Event<M>(sender, sendTime, to, at, message))
    }

    override fun <M : Message> now(message: M, to: Agent<in M>) {
        events.add(Event<M>(sender, sendTime, to, sendTime, message))
    }
}