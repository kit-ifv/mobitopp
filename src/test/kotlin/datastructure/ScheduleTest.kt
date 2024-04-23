package datastructure

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

object START_LOC : Location
object OTHER_LOC : Location
object THIRD_LOC : Location

class ScheduleTest {

    @Test
    fun scheduleTemp() {
        val schedule = Schedule()
        schedule.add(RawLeg(0.hours, 1.hours, START_LOC, OTHER_LOC))

        schedule.addConsistent(RawActivity(OTHER_LOC, 1.5.hours, 1.hours))

        val t = schedule.elements().first()
        t.changeEndLocation(THIRD_LOC)
        assertEquals(t.original.endLocation, THIRD_LOC)
        val a = schedule.elements().last()
        assertEquals(a.original.startLocation, THIRD_LOC)

        println(schedule)
    }

    @Test
    fun tripView() {
        val schedule = Schedule()
        schedule.add(RawLeg(0.hours, 1.hours, START_LOC, OTHER_LOC))

        schedule.addConsistent(RawActivity(OTHER_LOC, 1.5.hours, 1.hours))

        schedule.add(RawLeg(3.hours, 1.hours, OTHER_LOC, THIRD_LOC))

        schedule.addConsistent(RawActivity(THIRD_LOC, 4.5.hours, 1.hours))

        val tripView = TripView(schedule)
        val trip = tripView.trips.last()

        trip.forceChange(sortedSetOf())
        println(tripView.trips)
    }

    @Test
    fun addingActivities() {
        val schedule = Schedule()

        (0..10000 step 2).forEach {
            schedule.addConsistent(RawActivity(OTHER_LOC, it.hours, 0.5.hours))
            schedule.addConsistent(RawActivity(THIRD_LOC, (it+1).hours, 0.5.hours))
        }
        println(schedule)

        val tripView = TripView(schedule)

        tripView.trips.forEach { it.forceChange(sortedSetOf()) }


    }


}
fun main() {
    //Holding a million Schedules in memory

    val target = (0..1000000).map { generatePlan() }

    println(target.last().plan)
}

private fun generatePlan(): Schedule {
    val s = Schedule()
    s.addConsistent(RawActivity(START_LOC, 0.hours, 7.hours))
    (0..7).forEach {
        s.addConsistent(RawActivity(OTHER_LOC, it.days + 8.hours, 8.hours))
        s.addConsistent(RawActivity(THIRD_LOC, it.days + 17.hours, 0.5.hours))
        s.addConsistent(RawActivity(START_LOC, it.days + 21.hours, 10.hours))
    }
    return s
}