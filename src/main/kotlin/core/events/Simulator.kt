package core.events

import core.statemachine.Agent
import core.statemachine.Event
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
    protected val queue: EventQueue = MapEventQueue(),
    val timeStep: Duration = 1.minutes
) {

    init {
        queue.addAll(initEvents)
    }

    fun <E> addAgents(agents: Collection<E>) where E : Identifiable<*>, E : Agent<*> {
        queue.addAll(
            agents.map {
                it.init()
            }.flatten().toList()
        )
    }

    fun run(start: AbsoluteTime, end: AbsoluteTime) {
        for (time in progressClock(start, timeStep, end)) {
            queue.addAll(getFutureEvents(time))
        }
    }

    protected abstract fun getFutureEvents(now: Time): Collection<Event<*>>

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

class ParallelSimulator(
    initEvents: Collection<Event<*>> = emptyList(),
    queue: MapEventQueue = MapEventQueue(),
    timeStep: Duration = 1.minutes
) : Simulator(initEvents, queue, timeStep) {

    override fun getFutureEvents(now: Time): Collection<Event<*>> {
        val currentEvents = queue.popEventsUntil(now)
        val (present, future) = currentEvents.partition { it.receiveTime <= now }.let {
            it.first.toMutableList() to it.second.toMutableList()
        }

        runBlocking {
            while (present.isNotEmpty()) {
                coroutineScope {
                    val deferredNewEvents = present.map {
                        async(Dispatchers.Default) { it.execute() }
                    }

                    val newEvents = deferredNewEvents.awaitAll().flatten()

                    val (newInstantEvents, newFutureEvents) = newEvents.partition { it.receiveTime <= now }

                    present.clear()
                    present.addAll(newInstantEvents)
                    future.addAll(newFutureEvents)
                }
            }
        }

        return future
    }
}

class SequentialSimulator(
    initEvents: Collection<Event<*>> = emptyList(),
    queue: MapEventQueue = MapEventQueue(),
    timeStep: Duration = 1.minutes
) : Simulator(initEvents, queue, timeStep) {

    override fun getFutureEvents(now: Time): Collection<Event<*>> {
        val currentEvents = queue.popEventsUntil(now)
        val (present, future) = currentEvents.partition { it.receiveTime <= now }.let {
            it.first.toMutableList() to it.second.toMutableList()
        }

        while (present.isNotEmpty()) {
            val newEvents = present.map { it.execute() }.flatten()
            val (newInstantEvents, newFutureEvents) = newEvents.partition { it.receiveTime <= now }

            present.clear()
            present.addAll(newInstantEvents)
            future.addAll(newFutureEvents)
        }

        return future
    }
}
