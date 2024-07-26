package choicemodels

import DebugImpedance
import TEST_ACTIVITY
import TEST_ZONE
import TestZone
import buildPerson
import domain.data.Household
import domain.data.Person
import domain.data.PersonBuilder
import domain.data.PersonId
import domain.data.ZoneId
import domain.enums.ActivityType
import domain.enums.Mode
import domain.enums.StandardMode
import domain.location.Location
import generateHousehold
import org.junit.jupiter.api.Test
import usecases.AttractivenessModel
import usecases.choicemodels.GeneratedHcUtilityFunction
import usecases.choicemodels.LegacyModeChoiceModel
import usecases.choicemodels.LegacyModeChoiceParameters
import kotlin.test.BeforeTest
import kotlin.time.Duration.Companion.minutes

class GeneratedHcUtilityFunctionTest {

    val attractiveness = object : AttractivenessModel {
        override fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Double = 1.0
    }

    val availabilities: (Location, Mode) -> Boolean = { _, _ -> true }
    val gen = GeneratedHcUtilityFunction(attractiveness, modes = StandardMode)
    val personBuilder = PersonBuilder()

    val impedance = DebugImpedance()

    val modeChoice =
        LegacyModeChoiceModel(
            attractivenessModel = attractiveness,
            modes = StandardMode,
            impedance = impedance,
        )

    lateinit var household: Household
    lateinit var agent1: Person
//    val person: Person = Person(1L, household, 20, ChargingInfluence.NEVER)

    @BeforeTest
    fun setup() {
        household = TEST_ZONE.generateHousehold {
            householdNumber = 1
        }
        agent1 = household.buildPerson {
            personId = 1L
            id = PersonId(1L)
        }
    }

    @Test
    fun calculateU_fuss() {
        impedance[StandardMode.PEDESTRIAN, TestZone().centroid, TestZone().centroid] = 10.minutes

        val target = gen.calculateU_fuss(
            person = agent1,
            origin = TEST_ZONE.centroid,
            destination = TEST_ZONE.centroid,
            previousActivity = TEST_ACTIVITY,
            nextActivity = TEST_ACTIVITY,
            choiceSet = StandardMode.entries.toSet(),
            impedance = impedance,
            0.0
        )

        modeChoice.run {
            val wrapper =
                LegacyModeChoiceParameters(agent1, TEST_ACTIVITY, TEST_ACTIVITY, TEST_ZONE.centroid, TEST_ZONE.centroid)
            StandardMode.entries.toSet().select(wrapper)
        }
        println(target)
    }
}
