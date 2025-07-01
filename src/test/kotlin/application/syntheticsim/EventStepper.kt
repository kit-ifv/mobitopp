package application.syntheticsim

import core.events.Event
import domain.shared.enums.Mode
import domain.shared.location.Location
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * A simple event stepper. Mimics the behaviour of an event queue.
 */
class EventStepper(
    private var element: Event<*>,
    private val destination: OverridableDestinationChoiceModel,
    private val mode: OverridableModeChoiceModel,
) {
    private val eventQueue: Queue<Event<*>> = PriorityQueue(listOf(element))

    /**
     * Perform the execution of the first event in the queue. Tests against the expected size of the event queue
     * as well as a lambda expression (for assertions such as IsInstance)
     */
    fun nextStep(expectedQueueSize: Int, lambda: (Event<*>) -> Unit = {}) {
        element = eventQueue.poll()
        assertTrue(element.isValid, "Event $element is not valid")
        lambda(element)
        eventQueue.addAll(element.execute())
        assertEquals(
            eventQueue.size,
            expectedQueueSize,
            "Mismatch: current Queue is ${eventQueue.joinToString { it.toString() }}"
        )
    }

    /**
     * Takes a step through the queue, however it sets the override for destination and mode for the target step
     * and resets the overrides afterwards.
     */

    fun nextStep(expectedQueueSize: Int, destination: Location?, mode: Mode?, lambda: (Event<*>) -> Unit = {}) {
        this.destination.overrideDestination = destination
        this.mode.overrideMode = mode
        nextStep(expectedQueueSize, lambda)
        this.destination.overrideDestination = null
        this.mode.overrideMode = null
    }

    /**
     * Perform [n] steps without any background assertions
     */
    fun take(n: Int) {
        (0..<n).forEach { _ ->
            element = eventQueue.poll()
            eventQueue.addAll(element.execute())
        }
    }
}
