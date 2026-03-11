package domain.synthesis.rules.measurements

import domain.synthesis.behavior.ISurveyHousehold
import domain.synthesis.data.HouseholdType
import domain.synthesis.householdgeneration.SynthesisTest
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class HouseholdTypeDefinitionTest : SynthesisTest() {
    private class X

    @Test
    fun properCompilationWithData() {
        val measurement = HouseholdTypeDefinition(HouseholdType.UNDEFINED)
        val personAgeDefinition = PersonAgeDefinition(0..99)
        val secondMeasurement = personAgeDefinition.asHouseholdDefinition()
        val household: ISurveyHousehold<X> = createHousehold {
            person(age = 42) {
                X()
            }
            person(age = 101) {
                X()
            }
        }

        val output = measurement.evaluation(household)
        val output2 = secondMeasurement.evaluation(household)
        assertEquals(output, true)
        assertEquals(output2, 1)
    }
}
