package utils.collections

import buildPerson

import domain.data.EconomicStatus
import domain.data.Household
import domain.data.HouseholdId
import domain.data.MutableHousehold
import domain.data.MutablePerson

import domain.data.PersonId
import domain.data.Sex
import domain.location.LOCATIONUNKNOWN
import synthesis.HouseholdRepresentative
import synthesis.toRepresentative
import units.euros
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class Generator {

    private var householdId = HouseholdId(0L)
    private var personId = PersonId(0L)
    fun nextHouseholdId() = householdId.also { householdId = householdId.next() }
    fun nextPersonId() = personId.also { personId = personId.next() }
    var householdChanges: MutableHousehold.() -> Unit = {
    }
    val personChanges: MutableList<MutablePerson.() -> Unit> = mutableListOf()

    /**
     * In case the information of the household needs adaption.
     *
     * @param lambda
     * @receiver
     */
    fun information(lambda: MutableHousehold.() -> Unit) {
        householdChanges = lambda
    }

    fun person(lambda: MutablePerson.() -> Unit) {
        val newFunction: (Long, MutablePerson) -> MutablePerson  =  {l, p ->
            p.apply(lambda)
        }
        personChanges.add(lambda)
    }

    fun clear() {
        householdChanges = {}
        personChanges.clear()
    }

    fun household(lambda: Generator.() -> Unit): Household {
        return householdFromIdGenerator(this, lambda)
    }
}

fun householdFromIdGenerator(generator: Generator, lambda: Generator.() -> Unit): Household {
    generator.lambda()
    val household = MutableHousehold(id = generator.nextHouseholdId(), seed = 1).apply {
        householdNumber = 1
        surveyYear = 2024
        domCode = 1
        type = 1
        incomePerMonth = 0.euros
        economicStatus = EconomicStatus.MIDDLE
        location = LOCATIONUNKNOWN
        apply(generator.householdChanges)
    }
    //TODO rework this to work tomorrow

    generator.personChanges.forEach {
        household.buildPerson(generator.nextPersonId().value, it)
    }
    generator.clear()
    return household
}

fun Set<Household>.buildEquivalenceClasses() = equivalenceClassByRepresentative { it.toRepresentative() }
class EquivalenceClassTest {

    private val generator = Generator()
    private fun household(lambda: Generator.() -> Unit): Household {
        return generator.household(lambda)
    }

    @Test
    fun twoHouseholds() {
        val first = household {
            person {
                age = 10
                sex = Sex.MALE
            }
            person {
                age = 11
                sex = Sex.MALE
            }
            person {
                age = 20
                sex = Sex.FEMALE
            }
        }
        val second = household {
            person {
                age = 9
                sex = Sex.MALE
            }
            person {
                age = 21
                sex = Sex.FEMALE
            }
        }
        val eqC = setOf(first, second).equivalenceClasses { a, b -> a.toRepresentative() == b.toRepresentative() }
        assertEquals(eqC[first], setOf(first))
        assertEquals(eqC[second], setOf(second))
    }

    @Test
    fun theseShouldBeEquivalent() {
        val first = household {
            person {
                age = 2
                sex = Sex.MALE
            }
        }
        val second = household {
            person {
                age = 4
                sex = Sex.MALE
            }
        }
        assertEquals(first.toRepresentative(), second.toRepresentative())
    }

    /**
     * Equivalence groups can be built over any concept, the wikipedia article gave triangle area as an example, so
     * lets implement and verify that the equivalence classes work properly
     *
     */
    @Test
    fun triangles() {
        data class Triangle(val a: Pair<Double, Double>, val b: Pair<Double, Double>, val c: Pair<Double, Double>) {
            val area = calculateArea()
            private fun calculateArea(): Double {
                val (x1, y1) = a
                val (x2, y2) = b
                val (x3, y3) = c

                // Shoelace formula
                return 0.5 * kotlin.math.abs(
                    x1 * (y2 - y3) +
                            x2 * (y3 - y1) +
                            x3 * (y1 - y2)
                )
            }

            // Calculate the distance between two points
            private fun distance(p1: Pair<Double, Double>, p2: Pair<Double, Double>): Double {
                val (x1, y1) = p1
                val (x2, y2) = p2
                return sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
            }

            // Check if the triangles are congruent by comparing their side lengths
            fun congruent(other: Triangle): Boolean {
                // Get the side lengths of this triangle
                val sidesThis = listOf(
                    distance(a, b),
                    distance(b, c),
                    distance(c, a)
                ).sorted()

                // Get the side lengths of the other triangle
                val sidesOther = listOf(
                    distance(other.a, other.b),
                    distance(other.b, other.c),
                    distance(other.c, other.a)
                ).sorted()

                // Compare the sorted side lengths
                return sidesThis == sidesOther
            }
        }

        val firstTriangle = Triangle((0.0 to 0.0), (0.0 to 2.0), (2.0 to 2.0))
        val secondTriangle = Triangle((0.0 to 0.0), (2.0 to 0.0), (2.0 to 2.0))
        val thirdTriangle = Triangle((0.0 to 0.0), (0.0 to 2.0), (2.0 to 1.0))
        val randomTriangle = Triangle((42.0 to 30.0), (1.0 to 1.0), (13.0 to 37.0))
        assertNotEquals(firstTriangle, secondTriangle)
        assertNotEquals(secondTriangle, thirdTriangle)
        // When forming equivalence over the area all three triangles should be in the same group.
        val allTriangles = setOf(firstTriangle, secondTriangle, thirdTriangle)
        val representative = allTriangles.equivalenceClassByRepresentative { it.area }
        assertEquals(1, representative.size)
        assertEquals(setOf(2.0), representative.keys)
        assertEquals(allTriangles, representative.values.flatten().toSet())
        assertEquals(allTriangles, representative[2.0])

        val otherEquivalence = allTriangles.equivalenceClasses { a, b -> a.congruent(b) }
        assertEquals(otherEquivalence[firstTriangle]!!, setOf(firstTriangle, secondTriangle))
        assertEquals(otherEquivalence[secondTriangle]!!, setOf(firstTriangle, secondTriangle))
        assertEquals(otherEquivalence[thirdTriangle]!!, setOf(thirdTriangle))
        assertNull(otherEquivalence[randomTriangle])
    }
}

private fun Household.toRepresentative(): HouseholdRepresentative {
    return HouseholdRepresentative(
        (
                members.map {
                    it.toRepresentative()
                }.groupingBy { it }.eachCount().map { (element, count) -> Pair(count, element) }.toSet()
                )
    )
}
