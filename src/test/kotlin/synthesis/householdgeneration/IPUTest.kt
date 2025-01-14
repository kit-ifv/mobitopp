package synthesis.householdgeneration

import TestZone
import domain.data.Sex
import domain.data.Zone
import domain.data.ZoneId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import synthesis.SurveyHousehold
import synthesis.SurveyPerson
import synthesis.amount
import units.euros
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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


