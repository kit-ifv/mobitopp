//package datastructure
//
//import org.junit.jupiter.api.Assertions.assertFalse
//import org.junit.jupiter.api.Assertions.assertTrue
//import org.junit.jupiter.api.DynamicTest
//import org.junit.jupiter.api.TestFactory
//import utils.collections.permutations
//import kotlin.test.BeforeTest
//import kotlin.test.Test
//import kotlin.test.assertEquals
//import kotlin.test.assertNotNull
//import kotlin.test.assertNull
//import kotlin.time.Duration.Companion.hours
//
//
//class ScheduleTest {
//
//    private lateinit var newSchedule: NewSchedule
//    private val activity1: Activity = Activity.fromDuration(START, 0.hours, 7.hours)
//    private val leg1: Leg = Leg.fromDuration(7.hours, 0.5.hours, START, FOURTH)
//    private val leg1b: Leg = Leg.fromDuration(7.5.hours, 0.5.hours, FOURTH, OTHER)
//    private val activity2: Activity = Activity.fromDuration(OTHER, 8.hours, 4.hours)
//    private val activity2b: Activity = Activity.fromDuration(OTHER, 12.hours, 4.hours)
//    private val leg2: Leg = Leg.fromDuration(16.hours, 0.5.hours, OTHER, FOURTH)
//    private val leg2b: Leg = Leg.fromDuration(16.5.hours, 3.5.hours, FOURTH, THIRD)
//    private val activity3: Activity = Activity.fromDuration(THIRD, 20.hours, 10.hours)
//    private lateinit var first: ActivityBlock
//    @BeforeTest
//    fun setup(){
//        newSchedule = NewSchedule(activity1)
//        first = newSchedule.initial
//    }
//
//    @Test
//    fun temporary() {
//        assertFalse(first.rejects(activity3))
//        assertFalse(first.rejects(activity2))
//        assertFalse(first.rejects(leg1))
//        assertFalse(first.rejects(leg2))
//        assertNull(first.next)
//        assertNull(first.previous)
//
//        newSchedule.add(leg1)
//        val next = first.next
//        assertNotNull(next)
//        assertEquals(next.previous, newSchedule.initial)
//        assertNotNull(next.next)
//        assertEquals(next.item, sortedSetOf(leg1))
//        assertEquals(first.item, sortedSetOf(activity1))
//        val overNext = next.next
//        assertTrue(next.rejects(activity2))
//        assertFalse(overNext.rejects(activity2))
//        newSchedule.add(activity2)
//
//        assertEquals(next.previous, newSchedule.initial)
//        assertNotNull(next.next)
//        assertEquals(next.item, sortedSetOf(leg1))
//        assertEquals(overNext.previous, next)
//        assertEquals(next.next, overNext)
//        assertEquals(overNext.item, sortedSetOf(Activity.fromDuration(OTHER,  8.hours, 8.hours)))
//        assertTrue(newSchedule.initial.rejects(leg2))
//        assertTrue(next.rejects(leg2))
//        assertFalse(overNext.rejects(leg2))
//        newSchedule.add(leg2)
//
//        assertEquals(next.item, sortedSetOf(leg1))
//    }
//
//    @TestFactory
//    fun insertionOrderShouldNotMatter(): List<DynamicTest> {
//        val actions: List<LabeledRunnable<NewSchedule>> = listOf(
//            "+A2" to {add(activity2)},
//            "+A2b" to {add(activity2b)},
//            "+A3" to {add(activity3)},
//            "+L1" to {add(leg1)},
//            "+L1b" to {add(leg1b)},
//            "+L2" to {add(leg2)},
//            "+L2b" to {add(leg2b)},
//        )
//
//        return actions.permutations().toList().map { test ->
//            DynamicTest.dynamicTest(test.map { it.first }.toString()) {
//                val newSchedule = NewSchedule(activity1)
//                test.forEach {
//                    newSchedule.apply(it.second)
//                        .also { assertTrue(newSchedule.isConsistent()) { newSchedule.toString() } }
//                }
//                newSchedule.assertEquals(activity1) {
//                    +leg1
//                    +leg1b
//                    +activity2
//                    +activity2b
//                    +leg2
//                    +leg2b
//                    +activity3
//                }
//            }
//        }
//    }
////TODO reenable
////    @TestFactory
////    fun everyCombinationOfSomeDoubleAddedElements(): List<DynamicTest> {
////        val actions: List<Pair<LabeledRunnable<NewSchedule>, LabeledRunnable<NewSchedule>>> = listOf(
////            Pair("+L1" to { add(leg1)}, "-L1" to {remove(leg1) }),
////            Pair("+A2" to { add(activity2)}, "-A2" to { remove(activity2)}),
////            Pair("+L2" to {add(leg2) }, "-L2" to {remove(leg2) }),
////            Pair("+A3" to {add(activity3) }, "-A3" to { add(activity3)}),
////        )
////
////        return actions.orderedPermutations().toList().map { test ->
////            DynamicTest.dynamicTest(test.map { it.first }.toString()) {
////                val dualSetList = NewSchedule(activity1)
////                test.forEach {
////                    dualSetList.apply(it.second)
////                        .also { assertTrue(dualSetList.isConsistent()) { dualSetList.toString() } }
////                }
////                dualSetList.assertEquals(activity1) {
////
////                }
////                assertNull(dualSetList.initial.next)
////                assertNull(dualSetList.initial.previous)
////            }
////        }
////    }
//
//    @Test
//    fun builderIsProper() {
//        val newSchedule = NewSchedule(activity1)
//        val d = Dispatcher()
//        val legBlock1 = LegBlock(sortedSetOf(leg1, leg1b), d)
//        val activityBlock2 = ActivityBlock(sortedSetOf(activity2, activity2b), d)
//        val legBlock2 = LegBlock(sortedSetOf(leg2, leg2b), d)
//        val activityBlock3 = ActivityBlock(activity3, d)
//
//        newSchedule.actions.addAll(listOf(leg1, leg1b, leg2, leg2b, activity2, activity2b, activity3))
//        newSchedule.initial.next = legBlock1
//        legBlock1.previous = newSchedule.initial
//        legBlock1.next = activityBlock2
//        activityBlock2.previous = legBlock1
//        activityBlock2.next = legBlock2
//        legBlock2.previous = activityBlock2
//        legBlock2.next = activityBlock3
//        activityBlock3.previous = legBlock2
//
//        newSchedule.assertEquals(activity1) {
//            +leg1
//            +leg1b
//            +activity2
//            +activity2b
//            +leg2
//            +leg2b
//            +activity3
//        }
//
//
//    }
//
//    private fun NewSchedule.assertEquals(start: Activity, functor: NewScheduleBuilder.() -> Unit) {
//        val b = NewScheduleBuilder(start)
//        b.apply(functor)
//        assertEquals(b.schedule, this)
//    }
//
//    class NewScheduleBuilder(start: Activity) {
//        val schedule = NewSchedule(start)
//
//
//
//        operator fun Activity.unaryPlus() {
//            schedule.add(this)
//        }
//
//        operator fun Leg.unaryPlus() {
//            schedule.add(this)
//        }
//
//    }
//}