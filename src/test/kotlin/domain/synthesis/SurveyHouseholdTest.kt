package domain.synthesis

import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.behavior.householdgeneration.ZoneRule
import domain.synthesis.data.Sex
import domain.synthesis.householdgeneration.SynthesisTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import utils.collections.invertMap
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SurveyHouseholdTest : SynthesisTest() {
    @Test
    fun testRepresentationByVector() {
        val hh = createHousehold {
            person(10, Sex.MALE) {
                1
            }
            person(10, Sex.MALE) {
                2
            }
        }
        val hh2 = createHousehold {
            person(10, Sex.MALE) {
                1
            }
            person(10, Sex.MALE) {
                2
            }
        }

        val rule = ZoneRule<ISurveyHousehold<out Any>>("fake Description", 10) {
            it.count { it.age == 10 }
        }
        val households = listOf(hh, hh2)
        assertNotEquals(hh, hh2)

        val map = households.associateWith { it.toScalableVector(listOf(rule)) }

        assertEquals(map[hh], map[hh2])
        assertFalse(map[hh] === map[hh2])
        val scalar1 = map[hh]!!
        val scalar2 = map[hh2]!!

        assertEquals(scalar1, scalar2)

        val inverse = map.invertMap()
        assertEquals(inverse.size, 1)
        assertContentEquals(inverse[scalar1], listOf(hh, hh2))
    }
}
