package core.events

import core.statemachine.Agent
import core.statemachine.Event
import core.statemachine.Events
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import utils.Identifiable
import utils.collections.addProgressBar
import utils.units.AbsoluteTime
import utils.units.Time
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

abstract class Simulator(
    initEvents: Collection<Event<*>> = emptyList(),
    eventListeners: Collection<EventListener> = emptyList(),
    protected val queue: EventQueue = MapEventQueue(),
    val timeStep: Duration = 1.minutes
) {

    init {
        queue.addAll(initEvents)
    }

    private val listeners = mutableListOf<EventListener>().apply {
        addAll(eventListeners)
    }

    fun addListener(listener: EventListener) {
        listeners.add(listener)
    }

    fun <E> addAgents(agents: Collection<E>) where E : Identifiable<*>, E : Agent<*> {
        queue.addAll(
            agents.map {
                it.init()
            }.flatten().toList()
        )
    }
    fun <E> addAgent(agent: E) where E : Identifiable<*>, E : Agent<*> {
        addAgents(listOf(agent))
    }
    fun run(start: AbsoluteTime, end: AbsoluteTime) {
        for (time in progressClock(start, timeStep, end)) {
            queue.addAll(getFutureEvents(time))
        }
    }

    protected open fun getFutureEvents(now: Time): Collection<Event<*>> {
        val currentEvents = queue.popEventsUntil(now)
        val (present, future) = currentEvents.partition { it.receiveTime <= now }.let {
            it.first.toMutableList() to it.second.toMutableList()
        }

        while (present.isNotEmpty()) {
            val newEvents = executePresentEvents(present)
            val (newInstantEvents, newFutureEvents) = newEvents.partition { it.receiveTime <= now }

            present.forEach(::notifyListeners)
            present.clear()
            present.addAll(newInstantEvents)
            future.addAll(newFutureEvents)
        }

        return future
    }

    protected abstract fun executePresentEvents(present: Events): Events

    private fun notifyListeners(event: Event<*>) {
        listeners.forEach {
            it.notify(event)
        }
    }

    private fun progressClock(start: AbsoluteTime, timeStep: Duration, end: AbsoluteTime): Sequence<AbsoluteTime> {
        val seq = clock(start, timeStep, end)
        val count = clock(start, timeStep, end).count()

        return seq.iterator().addProgressBar(
            label = "simulation time",
            expectedCount = count.toLong(),
            visible = true
        ).asSequence()
    }

    private fun clock(start: AbsoluteTime, timeStep: Duration, end: AbsoluteTime): Sequence<AbsoluteTime> {
        var step = start

        return generateSequence {
            step.also { step += timeStep }.takeIf { it < end }
        }
    }
}

interface EventListener {
    fun notify(event: Event<*>)
}

class ParallelSimulator(
    initEvents: Collection<Event<*>> = emptyList(),
    eventListeners: Collection<EventListener> = emptyList(),
    queue: MapEventQueue = MapEventQueue(),
    timeStep: Duration = 1.minutes
) : Simulator(initEvents, eventListeners, queue, timeStep) {

    override fun executePresentEvents(present: Events): Events = runBlocking {
        coroutineScope {
            val deferredNewEvents = present.map {
                async(Dispatchers.Default) { it.execute() }
            }

            deferredNewEvents.awaitAll().flatten()
        }
    }
}

class SequentialSimulator(
    initEvents: Collection<Event<*>> = emptyList(),
    eventListeners: Collection<EventListener> = emptyList(),
    queue: MapEventQueue = MapEventQueue(),
    timeStep: Duration = 1.minutes
) : Simulator(initEvents, eventListeners, queue, timeStep) {

    override fun executePresentEvents(present: Events): Events = present.map { it.execute() }.flatten()
}
