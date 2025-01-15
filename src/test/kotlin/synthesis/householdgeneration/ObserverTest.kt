package synthesis.householdgeneration

import domain.data.Sex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import synthesis.SurveyHousehold
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ObserverTest: SynthesisTest() {
    private val hh1: SurveyHousehold<Any>
            = createHousehold {
                person(10, Sex.FEMALE) {}

            }
        private val hh2 = createHousehold<Any> {
        person(10, Sex.FEMALE) {}
        person(10, Sex.MALE) {}
    }

    @Test
    fun ruleVectorization() {
        val rule1 = ZoneCheckRule<Any> ("hhsize == 1", 20){it.size == 1}
        val rule2 = ZoneCheckRule<Any> ("hhsize == 2", 10){it.size == 2}



        assertContentEquals(listOf(rule1, rule2).vectorized(hh1).content, listOf(1, 0))
        assertContentEquals(listOf(rule1, rule2).vectorized(hh2).content, listOf(0, 1))

        val rule3 = ZoneRule<Any>("aged 10", -0) {it.members.count { it.age == 10 }}

        assertContentEquals(listOf(rule1, rule2, rule3).vectorized(hh1).content, listOf(1, 0, 1))
        assertContentEquals(listOf(rule1, rule2, rule3).vectorized(hh2).content, listOf(0, 1, 2))

    }
    @Test
    fun observerCreation() {
        val rule1 = ZoneCheckRule<Any> ("hhsize == 1", 20){it.size == 1}
        val rule2 = ZoneCheckRule<Any> ("hhsize == 2", 10){it.size == 2}

        val vector1 = listOf(rule1, rule2).vectorized(hh1)
        val vector2 = listOf(rule1, rule2).vectorized(hh2)
        assertContentEquals(vector1.content, listOf(1, 0))
        assertContentEquals(vector2.content, listOf(0, 1))

        val observer1 = rule1.createObserver(0, listOf(vector1, vector2))
        assertEquals(observer1.name, rule1.description)
        assertEquals(observer1.expected, rule1.target)
        assertEquals(observer1.vectors, listOf(vector1))
        assertTrue(observer1.sanityCheck())

        val observer2 = rule2.createObserver(1, listOf(vector1, vector2))
        assertEquals(observer2.name, rule2.description)
        assertEquals(observer2.expected, rule2.target)
        assertEquals(observer2.vectors, listOf(vector2))
        assertTrue(observer2.sanityCheck())
    }
    @Test
    fun observerManipulation() {
        // Both households match this rule, but hh2 = 2 and hh1 = 1, for different impacts on the rule
        val rule = ZoneRule<Any> ("hhsize == 1", 20){it.size}
        val vector1 = listOf(rule).vectorized(hh1)
        val vector2 = listOf(rule).vectorized(hh2)

        val observer = rule.createObserver(0, listOf(vector1, vector2))
        assertEquals(observer.sum(), 3.0)
        assertEquals(observer.absoluteDifference, 17.0)
        assertEquals(observer.relativeDifference, 17.0 / 20)

        observer *= 2
        assertEquals(observer.sum(), 6.0)
        assertEquals(vector1.scalar, 2.0)
        assertEquals(vector2.scalar, 2.0)

        observer.optimize()
        assertEquals(observer.relativeDifference, 0.0)
        assertEquals(vector1.scalar, 20.0 / 3)
        assertEquals(vector2.scalar, 20.0 / 3)

    }

    /**
     * Theoretically the vector encoding can be acted upon individually. However, the methods provided by the observer
     * cannot treat any of the content vectors preferentially.
     */
    @Test
    fun vectorManipulation() {
        val rule = ZoneRule<Any> ("hhsize == 1", 20){it.size}
        val vector1 = listOf(rule).vectorized(hh1)
        val vector2 = listOf(rule).vectorized(hh2)

        val observer = rule.createObserver(0, listOf(vector1, vector2))

        vector1 *= 3
        assertEquals(vector1.scalar, 3.0)
        assertEquals(observer.sum(), 5.0)

        observer.optimize()
        assertEquals(vector1.scalar, 12.0)
        assertEquals(vector2.scalar, 4.0)
    }
}

