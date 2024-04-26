package datastructure

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

//class DeprecatedTripViewTest {
//    private lateinit var schedule: DeprecatedSchedule
//    private lateinit var tripView: TripView
//    private val activity0 = Activity.fromDuration(START_LOC, 0.hours, 6.hours)
//    private val activity1 = Activity.fromDuration(OTHER_LOC, 8.hours, 8.hours)
//    private val activity2 =  Activity.fromDuration(THIRD_LOC, 17.hours, 2.hours)
//
//    @BeforeTest
//    fun setup() {
//        schedule = DeprecatedSchedule()
//        schedule.addConsistent(activity0)
//        schedule.addConsistent(activity1)
//        schedule.addConsistent(activity2)
//        tripView = TripView(schedule)
//    }
//    @Test
//    fun setupIsCorrect() {
//        assertEquals(2, tripView.size)
//        assertEquals(tripView[0].startLocation, START_LOC)
//        assertEquals(tripView[0].endLocation, OTHER_LOC)
//        assertEquals(tripView[1].startLocation, OTHER_LOC)
//        assertEquals(tripView[1].endLocation, THIRD_LOC)
//    }
//    @Test
//    fun deletingTripWorks() {
//        tripView[0].forceDelete()
//        assertEquals(schedule.size, 4)
//        schedule.assertEquals {
//            + activity0
//            + activity1
//            + activity1.createLegTo(activity2)
//            + activity2
//        }
//        assertEquals(tripView.size, 1)
//
//    }
//
//    @Test
//    fun adaptingTripWorks() {
//        tripView[0].adapt {
//            addPause(1.hours)
//
//            addLeg(THIRD_LOC, 10.minutes)
//            addPause(10.minutes)
//            addLeg(OTHER_LOC, 10.minutes)
//
//
//        }
//        schedule.assertEquals {
//            + activity0
//            + Leg.fromDuration(7.hours, 10.minutes, START_LOC, THIRD_LOC)
//            + Leg.fromDuration(7.hours + 20.minutes, 10.minutes, THIRD_LOC, OTHER_LOC)
//            + activity1
//            + activity1.createLegTo(activity2)
//            + activity2
//        }
//
//    }
//    @Test
//    fun changesPropagate() {
//        val target = tripView[0]
//
//        val atomicAction = schedule.plan.persistentSet.first()
//
//        atomicAction.changeStartLocation(THIRD_LOC)
//
//        assertEquals(target.previousActivity?.endLocation, THIRD_LOC)
//    }
//
//    @Test
//    fun changesPropagateOnSameLoc() {
//        val tripView = TripView(schedule)
//        val target = tripView.trips[0]
//
//
//        val firstActivity = target.previousActivity
//        firstActivity?.apply {
//            location = THIRD_LOC
//
//        }
//        val atomicAction = schedule.plan.persistentSet.first()
//
//        atomicAction.changeStartLocation(OTHER_LOC)
//
//        assertEquals(target.previousActivity?.endLocation, OTHER_LOC)
//    }
//
//    private fun DeprecatedSchedule.assertEquals(functor: ScheduleBuilder.() -> Unit) {
//        val b = ScheduleBuilder()
//        b.apply(functor)
//        assertEquals(this, b.schedule)
//    }
//}