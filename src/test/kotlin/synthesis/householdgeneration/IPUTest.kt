package synthesis.householdgeneration

import synthesis.SurveyHousehold
import units.euros
import kotlin.test.Test

class IPUTest: SynthesisTest() {

    private val households = SurveyHousehold<Any>(1, 1.euros, emptyList())





    @Test
    fun simpleIPU() {
        val IPU = IPU<Any> {vectors, observers ->

            vectors

        }
        // TODO make create household generic.
        createHousehold<Any> {

        }
//        IPU.synthesize(listOf(households), )
    }
}


