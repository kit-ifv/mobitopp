package core.statemachine

import random
import utils.units.AbsoluteTime
import utils.units.sinceStart
import java.util.PriorityQueue
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

const val LOGGING = false

class TestExampleStates {

    fun createStations(vararg names: String) = names.map {
        StationAgent(it)
    }

    fun createBussesBySchedule(
        departures: Iterable<AbsoluteTime>,
        travelTimes: LinkedHashMap<Station, Duration>
    ): List<BusAgent> {
        val route = travelTimes.keys.toList()
        val start = route.first()
        val end = route.last()

        return departures.map {
            BusAgent(
                "$start > $end at $it",
                it,
                route,
                travelTimes
            )
        }
    }

    fun createRoute(stations: Map<String, Station>, vararg legs: Pair<String, Duration>) = linkedMapOf(
        *legs.map { stations[it.first]!! to it.second }.toTypedArray()
    )

    var idCount = 0L
    fun createPerson(start: AbsoluteTime, end: AbsoluteTime, stations: List<Station>): PassengerAgent {
        val id = idCount++
        val random = Random(id)

        val from = stations.random(random)
        val to = stations.filter { it != from }.random(random)
        val departure = (start..end).random(random)

        return PassengerAgent("$id", from, to, departure)
    }

    @Test
    fun runStateMachines() {
        val start: AbsoluteTime = AbsoluteTime.START
        val endDemand: AbsoluteTime = 4.hours.sinceStart
        val end: AbsoluteTime = 5.hours.sinceStart

        val stations = createStations("A", "B", "C", "D", "E").associateBy { it.name }

        val route1 = createRoute(
            stations,
            "A" to 0.minutes,
            "B" to 3.minutes,
            "C" to 5.minutes,
            "D" to 6.minutes,
            "E" to 4.minutes
        ) // 26 min //every full hour
        val departures1 = start..end step 60.minutes
        val bussesRoute1 = createBussesBySchedule(departures1, route1)

        val route2 = createRoute(
            stations,
            "E" to 0.minutes,
            "D" to 6.minutes,
            "C" to 4.minutes,
            "B" to 3.minutes,
            "A" to 5.minutes
        ) // 26 min //every hour, half past
        val departures2 = (start + 30.minutes)..end step 60.minutes
        val bussesRoute2 = createBussesBySchedule(departures2, route2)

        val route3 = createRoute(stations, "B" to 0.minutes, "D" to 7.minutes) // 9 min //at 10, 30, 50 past x
        val departures3 = (start + 10.minutes)..end step 20.minutes
        val bussesRoute3 = createBussesBySchedule(departures3, route3)

        val route4 = createRoute(stations, "D" to 0.minutes, "B" to 7.minutes) // 9 min // at 0, 20, 40 past x
        val departures4 = start..end step 20.minutes
        val bussesRoute4 = createBussesBySchedule(departures4, route4)

        val stationAgents = stations.values.toList()
        val busAgents = bussesRoute1 + bussesRoute2 + bussesRoute3 + bussesRoute4
        val passengerAgents = (0..1000).map { createPerson(start, endDemand, stationAgents) }

        val initEvents = (stationAgents + busAgents + passengerAgents).flatMap {
            it.init()
        }

        val queue = PriorityQueue<Event<*>>(initEvents.size, compareBy { it.receiveTime })
        queue.addAll(initEvents)

        while (!queue.isEmpty()) {
            val event = queue.poll()
            val messages = event.execute()
            if (LOGGING) {
                println("[${event.receiveTime}] ${queue.size} elements remaining in queue")
                println(
                    "  processing: ${event.sender.tag()} > ${event.receiver.tag()}: ${event.content::class.simpleName}"
                )
                println("  produced ${messages.size} events.")
            }
            queue.addAll(messages)
        }

        val passengersByStateDescriptor = passengerAgents.groupingBy { it.stateDescriptor }.eachCount()
        assertNull(passengersByStateDescriptor["WAITING"])
        assertNull(passengersByStateDescriptor["RIDING"])

        val busByFinishedStatus = busAgents.groupingBy { it.isFinished() }.eachCount()
        assertNull(busByFinishedStatus[false])
    }
}

fun Agent<*>.tag() = (this::class.simpleName ?: "Agent") + "[$this]"
