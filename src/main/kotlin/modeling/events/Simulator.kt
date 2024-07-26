package modeling.events

import modeling.steps.Repository
import utils.Identifiable
import utils.collections.addProgressBar
import utils.units.AbsoluteTime
import utils.units.Time
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class Simulator(
    initEvents: Collection<Event<*>> = emptyList(),
    private val queue: MapEventQueue = MapEventQueue(),
    val timeStep: Duration = 1.minutes
) {

    init {
        queue.addAll(initEvents)
    }

    fun <E> addAgents(agents: Repository<E, *>, init: (E) -> Event<E>?) where E : Identifiable<*>, E : Agent<E> {
        queue.addAll(
            agents.elements.map {
                init(it)?.also { e -> it.updateNextEvent(e) }
            }.filterNotNull().toList()
        )
    }

    fun run(start: AbsoluteTime, end: AbsoluteTime) {
        for (time in progressClock(start, timeStep, end)) {
            val currentEvents = queue.popEventsUntil(time)

            val newEvents = processInstantEvents(currentEvents, time)
//            println("${time.inWholeMinutes} ${currentEvents.size} current events -> ${newEvents.size} new events")
            queue.addAll(newEvents)
        }
    }

    private fun processInstantEvents(events: Collection<Event<*>>, now: Time): Collection<Event<*>> {
        val instant = events.filter { it.time <= now }
        val latent = events.filter { it.time > now }.toMutableList()

        latent.addAll(
            instant.flatMap { processInstantEvents(it.execute(), now) }
        )

        return latent
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
