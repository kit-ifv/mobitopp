package datastructure

import FOURTH
import OTHER
import START
import THIRD
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import utils.collections.cartesianProduct
import utils.collections.isStrictlySorted
import utils.collections.orderedPermutations
import utils.collections.permutations
import utils.collections.subsets
import java.util.*
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours


abstract class PlanModelTest {
    protected val activity1: Activity = Activity.fromDuration(START, 0.hours, 7.hours)
    protected val leg1: Leg = Leg.fromDuration(7.hours, 0.5.hours, START, FOURTH)
    protected val leg1b: Leg = Leg.fromDuration(7.5.hours, 0.5.hours, FOURTH, OTHER)
    protected val activity2: Activity = Activity.fromDuration(OTHER, 8.hours, 4.hours)
    protected val activity2b: Activity = Activity.fromDuration(OTHER, 12.hours, 4.hours)
    protected val leg2: Leg = Leg.fromDuration(16.hours, 0.5.hours, OTHER, FOURTH)
    protected val leg2b: Leg = Leg.fromDuration(16.5.hours, 3.5.hours, FOURTH, THIRD)
    protected val activity3: Activity = Activity.fromDuration(THIRD, 20.hours, 10.hours)

    abstract var model: PlanModel

    inner class Helper(
        val name: String,
        val executable: PlanModel.() -> Unit,
        val expected: SortedSet<Action>.() -> Unit
    )

    private fun fromString(text: String): Helper {

        return when {
            text.contains("L") -> decodeLeg(text)
            text.contains("A") -> decodeActivity(text)
            else -> throw NoSuchElementException("Still no element")
        }

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

    @TestFactory
    fun add(): List<DynamicTest> {

        val actions =
            listOf("+A1", "+L1", "+L1b", "+A2", "+A2b", "+L2", "+A3").map { fromString(it) }


        val t = actions.permutations().map { testActions ->
            DynamicTest.dynamicTest(testActions.map { it.name }.toString()) {
                model.clear()
                val expected = sortedSetOf<Action>()
                assertContentEquals(model.actions(), expected)
                testActions.forEach {

                    model.apply(it.executable)
                    expected.apply(it.expected)
                    assertTrue(model.actions().isStrictlySorted())
                    assertContentEquals(model.actions(), expected)
                }
                assertEquals(expected.size, 7)
            }
        }
        return t.toList()
    }

    @TestFactory
    fun remove(): List<DynamicTest> {
        val actions =
            listOf(
                "+A1" to "-A1",
                "+L1" to "-L1",
                "+L1b" to "-L1b",
                "+A2" to "-A2"
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
                }
            }

        }.toList()

    }

    @TestFactory
    fun replaceActivities(): List<DynamicTest> {
        val activities = setOf(activity1, activity2, activity2b, activity3)
        val legsets = setOf(leg1b, leg2, leg2b).subsets()
        val original = activities.subsets()
        val new = activities.subsets()
        return original.cartesianProduct(new, legsets).map { test ->
            DynamicTest.dynamicTest(test.toString()) {
                model.clear()
                test.third.forEach {model.add(it)}
                activities.forEach { model.add(it) }
                model.replaceActivities(test.first, test.second)
                val expected = TreeSet<Action>(activities)
                expected.addAll(test.third)
                expected.removeAll(test.first)
                expected.addAll(test.second)
                assertContentEquals(model.actions(), expected)
            }
        }.toList()
    }

    @TestFactory
    fun replaceLegs(): List<DynamicTest> {
        val activities = setOf(activity1, activity2, activity3).subsets()
        val legsets = setOf(leg1, leg1b, leg2, leg2b)
        val original = legsets.subsets()
        val new = legsets.subsets()
        return original.cartesianProduct(new, activities).map { test ->
            DynamicTest.dynamicTest(test.toString()) {
                model.clear()
                legsets.forEach {model.add(it)}
                test.third.forEach { model.add(it) }
                model.replaceLegs(test.first, test.second)
                val expected = TreeSet<Action>(legsets)
                expected.addAll(test.third)
                expected.removeAll(test.first)
                expected.addAll(test.second)
                assertContentEquals(model.actions(), expected)
            }
        }.toList()
    }


}