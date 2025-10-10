package domain.synthesis.householdgeneration

import domain.synthesis.behavior.householdgeneration.SampleAndCollect
import domain.synthesis.behavior.householdgeneration.ScalableVector
import domain.synthesis.data.Sex
import kotlin.test.Test
import kotlin.test.assertEquals

class SampleAndCollectTest : SynthesisTest() {

    @Test
    fun properOverflow() {
        val strategy = SampleAndCollect<Unit>()
        val firstHousehold = createHousehold<Unit> {
            person(20, Sex.MALE) {}
            person(30, Sex.MALE) {}
        }
        val secondHousehold = createHousehold<Unit> {
            person(40, Sex.FEMALE) {
            }
            person(50, Sex.FEMALE) {}
            person(60, Sex.FEMALE) {}
        }
        val thirdHousehold = createHousehold<Unit> { }
        // Since this test targets the household size for simplicity, the numbers should match
        assertEquals(firstHousehold.size, 2)
        assertEquals(secondHousehold.size, 3)
        // The content of the vector is irrelevant, only the scalar is important
        val vector1 = ScalableVector(listOf(1, 2, 3), 1.6)
        val vector2 = ScalableVector(listOf(4, 5, 6), 1.6)
        val vector3 = ScalableVector(listOf(), 1.8001)
        val map = mapOf(
            vector1 to listOf(firstHousehold),
            vector2 to listOf(secondHousehold),
            vector3 to listOf(thirdHousehold)
        )
        val output = strategy.run {
            map.extractFrom()
        }
        assertEquals(output.size, 5)
        // To quickly differentiate between the outputs we don't compare the entire household structure but just the size
        /*
        Expected: household, other, other, third
         */
        assertEquals(output[0].size, 2)
        assertEquals(output[1].size, 3)
        assertEquals(output[2].size, 3)
        assertEquals(output[3].size, 0)
        assertEquals(output[4].size, 0)
    }
}
