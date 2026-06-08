package edu.kit.ifv.core.statemachine
import edu.kit.ifv.utils.units.AbsoluteTime
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
