package core.statemachine

import core.statemachine.builder.StateData
import utils.units.AbsoluteTime
import kotlin.reflect.KClass

/**
 * Type alias for a Kotlin class representing a specific message type.
 * Used for type-safe message handling in the state machine.
 *
 * @param T The specific message type
 */
typealias MessageType<T> = KClass<T>

/**
 * Type alias for a Kotlin class representing any message type.
 * Used when the specific message type is not important.
 */
typealias AnyMessageType = KClass<out Message>

/**
 * Marker interface for all messages that can be sent between agents.
 * Messages are the primary means of communication in the state machine system.
 */
interface Message

/**
 * Events represent a message being received by an agent.
 * Additionally, they record the [sender] agent and [sendTime].
 * Events are scheduled to be processed at a specific [receiveTime],
 * hence they are [Comparable] by this [receiveTime].
 *
 * @param M The type of message contained in this event
 * @property sender The agent that sent this event
 * @property sendTime The time at which the message was sent
 * @property receiver The agent receiving the message
 * @property receiveTime The time at which this event should be processed / the message should arrive
 * @property content The actual message being sent
 */
data class Event<M : Message>(
    val sender: Agent<*>,
    val sendTime: AbsoluteTime,
    val receiver: Agent<in M>,
    val receiveTime: AbsoluteTime,
    val content: M,
) : Comparable<Event<*>> {
    /**
     * Executes this event by delivering the message to the receiver.
     *
     * @return Any events generated in response to this event
     */
    fun execute(): Events = receiver.processEvent(this)

    /**
     * Compares this event with another based on [receiveTime].
     * Used for ordering events in priority queues.
     *
     * @param other The event to compare with
     * @return A negative, zero, or positive value if this event should be processed before, at the same time, or after the other event
     */
    override fun compareTo(other: Event<*>) = receiveTime.compareTo(other.receiveTime)

    override fun toString() = "[$receiveTime, ${content::class.simpleName}]"
}

/** Type alias for a collection of events. */
typealias Events = Collection<Event<*>>

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
 * @param data The [StateData] containing the current agent and time
 * @param scope A function that uses the [Send] interface to send messages
 * @return A pair containing all events generated within the scope and the result of the scope function
 */
fun <R> sendScope(data: StateData, scope: (Send) -> R): Pair<Events, R> = SingleUseSendScope(data).invoke(scope)

// TODO maybe move to builder subpackage as it depends on StateData, a concept not known at this package level?

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
