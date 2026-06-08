@file:Suppress("FunctionNameMaxLength")

package edu.kit.ifv.domain.shared.datastructure.schedule
import FOURTH
import OTHER
import START
import THIRD
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Action
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Activity
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Leg
import edu.kit.ifv.domain.shared.datastructure.schedule.plans.PlanModel
import edu.kit.ifv.domain.shared.datastructure.schedule.plans.isConsistent
import edu.kit.ifv.utils.collections.cartesianProduct
import edu.kit.ifv.utils.collections.isStrictlySorted
import edu.kit.ifv.utils.collections.orderedPermutations
import edu.kit.ifv.utils.collections.permutations
import edu.kit.ifv.utils.collections.subsets
import edu.kit.ifv.utils.units.sinceStart
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.util.*
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours

abstract class PlanModelTest {
    protected val activity1: Activity = Activity.fromDuration(START, 0.hours.sinceStart, 7.hours)
    protected val leg1: Leg = Leg.fromDuration(7.hours.sinceStart, 0.5.hours, START, FOURTH)
    protected val leg1b: Leg = Leg.fromDuration(7.5.hours.sinceStart, 0.5.hours, FOURTH, OTHER)
    protected val activity2: Activity = Activity.fromDuration(OTHER, 8.hours.sinceStart, 4.hours)
    protected val activity2b: Activity = Activity.fromDuration(OTHER, 12.hours.sinceStart, 4.hours)
    protected val leg2: Leg = Leg.fromDuration(16.hours.sinceStart, 0.5.hours, OTHER, FOURTH)
    protected val leg2b: Leg = Leg.fromDuration(16.5.hours.sinceStart, 3.5.hours, FOURTH, THIRD)
    protected val activity3: Activity = Activity.fromDuration(THIRD, 20.hours.sinceStart, 10.hours)

    abstract var model: PlanModel

    /* Start of the helper block, here are utilities that are used for encoding and decoding actions or legs defined
    which help readability in the test printout or by allowing easier execution of add/remove actions.
     */
    inner class Helper(
        val name: String,
        val executable: PlanModel.() -> Unit,
        val expected: SortedSet<Action>.() -> Unit,
    )

    private fun fromString(text: String): Helper = when {
        text.contains("L") -> decodeLeg(text)
        text.contains("A") -> decodeActivity(text)
        else -> throw NoSuchElementException("Still no element")
    }

    private fun decodeLeg(text: String): Helper {
        val leg = when (text.substring(1)) {
            "L1" -> leg1
            "L1b" -> leg1b
            "L2" -> leg2
            "L2b" -> leg2b
            else -> throw NoSuchElementException("Nope")
        }

        return when {
            text.startsWith('+') -> Helper(text, { add(leg) }, { add(leg) })
            text.startsWith('-') -> Helper(text, { remove(leg) }, { remove(leg) })
            else -> throw NoSuchElementException("Sorry")
        }
    }

    private fun decodeActivity(text: String): Helper {
        val activity = when (text.substring(1)) {
            "A1" -> activity1
            "A2" -> activity2
            "A2b" -> activity2b
            "A3" -> activity3
            else -> throw NoSuchElementException("Nope")
        }

        return when {
            text.startsWith('+') -> Helper(text, { add(activity) }, { add(activity) })
            text.startsWith('-') -> Helper(text, { remove(activity) }, { remove(activity) })
            else -> throw NoSuchElementException("Sorry")
        }
    }

    private fun invalidLeg(action: Action): Leg = Leg.fromDuration(
        action.startTime,
        action.duration,
        action.startLocation,
        action.endLocation,
    )

    private fun invalidActivity(action: Action): Activity = Activity.fromDuration(
        action.startLocation,
        action.startTime,
        action.duration,
    )

    private fun Triple<Collection<Action>, Collection<Action>, Collection<Action>>.decode(): String =
        first.joinToString {
            it.decodeToShorthand()
        } + "|" +
            second.joinToString { it.decodeToShorthand() } + "|" +
            third.joinToString { it.decodeToShorthand() }

    private fun Pair<Collection<Action>, Collection<Action>>.decode(): String = first.joinToString {
        it.decodeToShorthand()
    } + "|" + second.joinToString { it.decodeToShorthand() }

    private fun Action.decodeToShorthand(): String = when {
        compareTo(activity1) == 0 -> "A1"
        compareTo(activity2) == 0 -> "A2"
        compareTo(activity2b) == 0 -> "A2b"
        compareTo(activity3) == 0 -> "A3"
        compareTo(leg1) == 0 -> "L1"
        compareTo(leg1b) == 0 -> "L1b"
        compareTo(leg2) == 0 -> "L2"
        compareTo(leg2b) == 0 -> "L2b"
        else -> "UNKNOWN"
    }

    /**
     * Attempts to insert the activities (A1, A2, A2b and A3) as well as the legs (L1, L1b, L2) into the plan model.
     * as the actions do not overlap it can be expected that every element will be present in the model. This test
     * attempts to insert the elements in every possible permutation. Note that since Leg 2b is missing the plan is
     * not consistent. Therefore, only strict sorting can be tested.
     */
    @TestFactory
    fun add(): List<DynamicTest> {
        val actions =
            listOf("+A1", "+L1", "+L1b", "+A2", "+A2b", "+L2", "+A3").map { fromString(it) }

        val t = actions.permutations().map { testActions ->
            DynamicTest.dynamicTest(testActions.map { it.name }.toString()) {
                model.clear()
                val expected = sortedSetOf<Action>()
                // Required test as forgetting to reset the model can cause overspill from previous tests.
                assertContentEquals(model.actions(), expected)
                testActions.forEach {
                    model.apply(it.executable)
                    expected.apply(it.expected)
                    assertTrue(model.actions().isStrictlySorted())

                    assertContentEquals(model.actions(), expected)
                    assertTrue(model.isConsistent())
                }
            }
        }
        return t.toList()
    }

    /**
     * Attempts to perform adding and later removing the activities A1, A2 and the legs L1, L1b. Generates all
     * permutations where the insertion action of an element X occurs before the removal of element X.
     */
    @TestFactory
    fun remove(): List<DynamicTest> {
        val actions =
            listOf(
                "+A1" to "-A1",
                "+L1" to "-L1",
                "+L1b" to "-L1b",
                "+A2" to "-A2",
            ).map { fromString(it.first) to fromString(it.second) }
        return actions.orderedPermutations().map { test ->
            DynamicTest.dynamicTest(test.map { it.name }.toString()) {
                model.clear()
                val expected = sortedSetOf<Action>()
                assertContentEquals(model.actions(), expected)

                test.forEach {
                    model.apply(it.executable)
                    expected.apply(it.expected)
                    assertTrue(model.actions().isStrictlySorted())
                    assertContentEquals(model.actions(), expected)
                    assertTrue(model.isConsistent())
                }
            }
        }.toList()
    }

    /**
     * Tests whether the replacements of a collection of activities are producing proper models. Generates two distinct
     * subsets of A1, A2, A2b & A3 and tests all subsets of replacing and inserting from this original set. In addition,
     * tests where each subset of leg 1b, 2 and 2b is present from the start.
     */
    @TestFactory
    fun replaceActivities(): List<DynamicTest> {
        val activities = setOf(activity1, activity2, activity2b, activity3)
        val legsets = setOf(leg1b, leg2, leg2b).subsets()
        val original = activities.subsets()
        val new = activities.subsets()
        return original.cartesianProduct(new, legsets).map { test ->
            DynamicTest.dynamicTest(test.toString()) {
                model.clear()
                test.third.forEach { model.add(it) }
                activities.forEach { model.add(it) }
                model.replaceActivities(test.first, test.second)
                TreeSet<Action>(activities)

                assertTrue(model.isConsistent())
//                expected.addAll(test.third)
//                expected.removeAll(test.first)
//                expected.addAll(test.second)
//                assertContentEquals(model.actions(), expected)
            }
        }.toList()
    }

    /**
     * Tests whether the replacements of leg collections is producing a proper model output. Similar to the
     * [replaceActivities] test, every combination of original / new subsets from the legs is tested with the additional
     * test for different activities present in the model.
     */
    @TestFactory
    fun replaceLegs(): List<DynamicTest> {
        val activitiesSets = setOf(activity1, activity2, activity3).subsets()
        val legs = setOf(leg1, leg1b, leg2, leg2b)
        val original = legs.subsets()
        val new = legs.subsets()
        return original.cartesianProduct(new, activitiesSets).map { test ->
            DynamicTest.dynamicTest(test.decode()) {
                model.clear()
                legs.forEach { model.add(it) }
                test.third.forEach { model.add(it) }
                model.replaceLegs(test.first, test.second)
                val expected = TreeSet<Action>(legs)
                expected.addAll(test.third)
                expected.removeAll(test.first)
                expected.addAll(test.second)
                assertContentEquals(model.actions(), expected)
                assertTrue(model.isConsistent())
            }
        }.toList()
    }

    /**
     * In the default [PlanModel] representation of either [BlockModel] or [ActionModel] an element with overlap to an
     * existing element should not be inserted in the collection. This test generates all combinations of activities
     * and legs from A1, A2, A2b and L1, L1b and a fake leg overlapping any of these.
     */
    @TestFactory
    open fun invalidInsertsOfLegDontWork(): List<DynamicTest> {
        val activitySets = setOf(activity1, activity2, activity2b).subsets()
        val legSubsets = setOf(leg1, leg1b).subsets()

        return activitySets.cartesianProduct(legSubsets).flatMap { test ->
            (test.first + test.second).map { badAction ->

                val invalid = invalidLeg(badAction)
                DynamicTest.dynamicTest(test.decode() + " # " + invalid.decodeToShorthand()) {
                    model.clear()
                    test.first.forEach { model.add(it) }
                    test.second.forEach { model.add(it) }
                    model.add(invalid)
                    assertContentEquals(model.actions(), TreeSet(test.first + test.second))
                    assertTrue(model.isConsistent())
                }
            }
        }
    }

    /**
     * Similar to [invalidInsertsOfLegDontWork] this test creates a fake activity with overlap and attempts to insert.
     */
    @TestFactory
    open fun invalidInsertsOfActivitiesDontWork(): List<DynamicTest> {
        val activitySets = setOf(activity1, activity2, activity2b).subsets()
        val legSubsets = setOf(leg1, leg1b).subsets()

        return activitySets.cartesianProduct(legSubsets).flatMap { test ->
            (test.first + test.second).map { badAction ->

                val invalid = invalidActivity(badAction)
                DynamicTest.dynamicTest(test.decode() + " # " + invalid.decodeToShorthand()) {
                    model.clear()
                    test.first.forEach { model.add(it) }
                    test.second.forEach { model.add(it) }
                    model.add(invalid)
                    assertContentEquals(model.actions(), TreeSet(test.first + test.second))
                    assertTrue(model.isConsistent())
                }
            }
        }
    }

    /**
     * Regardless of insertion order the first element of (A1, L1, L1b, A2) should always be A1
     */
    @TestFactory
    fun firstElementShouldBeA1(): List<DynamicTest> {
        val actions = listOf("+A1", "+L1", "+L1b", "+A2").map { fromString(it) }
        return actions.permutations().map { test ->
            DynamicTest.dynamicTest(test.map { it.name }.toString()) {
                model.clear()
                test.forEach { model.apply(it.executable) }

                assertEquals(activity1, model.first()?.original)
                val target = model.removeFirst()
                assertEquals(target?.next, model.first())
                assertEquals(target, model.first()?.previous)
                assertEquals(activity1, target?.original)
                assertEquals(leg1, model.first()?.original)
            }
        }.toList()
    }

    @TestFactory
    fun droppingToActivityShouldWork(): List<DynamicTest> {
        val activities = setOf(activity1, activity2, activity2b, activity3)
        val legs = setOf(leg1, leg1b, leg2, leg2b)
        return activities.map { test ->
            DynamicTest.dynamicTest(test.toString()) {
                model.clear()
                activities.forEach { model.add(it) }
                legs.forEach { model.add(it) }

                model.dropUntil(test)
                val target = (activities.filter { it >= test } + legs.filter { it >= test }).toSortedSet()

                assertContentEquals(model.actions(), target)
            }
        }
    }

    @TestFactory
    fun poppingAnElementShouldMaintainALink(): List<DynamicTest> {
        val activities = setOf(activity1, activity2, activity2b, activity3)
        val legs = setOf(leg1, leg1b, leg2, leg2b)
        return activities.map { test ->
            DynamicTest.dynamicTest(test.toString()) {
                model.clear()
                assertContentEquals(model.actions(), emptyList())
                activities.forEach { model.add(it) }
                legs.forEach { model.add(it) }
                val removedElement = model.removeFirst()
                model.dropUntil(test)
                val firstElement = model.first()
                assertEquals(removedElement?.next, firstElement)
                assertEquals(removedElement, firstElement?.previous)
                val target = (
                    setOf(
                        activity2,
                        activity2b,
                        activity3,
                    ).filter { it >= test } + legs.filter { it > test }
                    ).toSortedSet()
                assertContentEquals(model.actions(), target)
                assertTrue(model.isConsistent())
            }
        }
    }
}
