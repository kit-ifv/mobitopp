package choicemodels

import BIELEFELD
import datastructure.StationaryAction
import domain.data.Person
import domain.data.point
import domain.enums.LegacyActivityType
import domain.enums.Mode
import domain.enums.StandardMode
import domain.location.Metrics
import domain.location.ZoneLocation
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertDoesNotThrow
import syntheticsim.ControllableImpedance
import syntheticsim.OneHouseholdTwoPersons
import syntheticsim.loadActivityPlan
import syntheticsim.testAttractivenessModel
import usecases.choicemodels.FakePlan
import usecases.choicemodels.IGeneratedHcUtilityFunction
import usecases.choicemodels.LegacyModeChoiceModel
import usecases.choicemodels.speedupMap
import utils.CodePlan
import utils.collections.subsets
import utils.units.sinceStart
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours

class ModeChoiceTest {
    val scenario = OneHouseholdTwoPersons()
    val original = scenario.modeChoice.original

    @Test
    fun properAvailability() {
        scenario.run {
            first.loadActivityPlan {
                +LegacyActivityType.HOME
                +LegacyActivityType.WORK
            }
            first.stepper().stepOverFirstActivity()
            first.schedule.activities().first().location = zones[1].point(BIELEFELD)
        }
        original.choose(scenario.first, 0.hours.sinceStart)
    }

    @TestFactory
    fun directModeSelect(): List<DynamicTest> {
        scenario.run {
            first.loadActivityPlan {
                +LegacyActivityType.HOME
                +LegacyActivityType.WORK
            }
            return speedupMap.values.subsets().filter { it.isNotEmpty() }.map {
                DynamicTest.dynamicTest(it.toString()) {
                    assertDoesNotThrow {
                        original.selectMode(
                            first,
                            zones[0].point(BIELEFELD),
                            zones[1].point(BIELEFELD),
                            first.schedule.activities().first(),
                            first.schedule.activities().take(1).first(),
                            it,
                            impedance,
                            0.5
                        )
                    }
                }
            }
        }
    }

    /**
     * Somehow the calculation for PT generates NaNs
     */
    @Test
    fun ptTest() {
        scenario.run {
            first.loadActivityPlan {
                +LegacyActivityType.HOME
                +LegacyActivityType.WORK
            }
            assertDoesNotThrow {
                original.selectMode(
                    first,
                    zones[0].point(BIELEFELD),
                    zones[1].point(BIELEFELD),
                    first.schedule.activities().first(),
                    first.schedule.activities().take(1).first(),
                    setOf(StandardMode.PUBLICTRANSPORT),
                    impedance,
                    0.5
                )
            }
        }
    }

    /**
     * Run a select Mode on the choice model, only targeting potential modes and random as input parameters.
     *
     * @param potentialModes The available modes.
     * @param random the random value for later selection.
     * @return
     */
    private fun LegacyModeChoiceModel.calculate(
        potentialModes: Set<Mode> = speedupMap.values.toSet(),
        random: Double
    ): Mode {
        return scenario.run {
            selectMode(
                first,
                zones[0].point(BIELEFELD),
                zones[1].point(BIELEFELD),
                fakeActivity,
                fakeActivity,
                potentialModes,
                impedance,
                random
            )
        }
    }

    /**
     * If the Utility of a single option is gigantic (U = 9000) and the other options are at a (U = 0) the option should
     * be the primary selection target and thus invariant to the random number passed as input.
     *
     * @return
     */
    @TestFactory
    fun modechoiceFunction(): List<DynamicTest> {
        return speedupMap.values.map { mode ->
            DynamicTest.dynamicTest(mode.toString()) {
                val controllableUtilityFunction = ControllableUtilityFunction(FakePlan)
                val choiceModel = LegacyModeChoiceModel(
                    testAttractivenessModel,
                    modes = FakePlan,
                    impedance = ControllableImpedance(),
                    utilitiesGenerator = { _, _, _, _ -> controllableUtilityFunction }
                )
                controllableUtilityFunction[mode] = 9000.0
                assertEquals(controllableUtilityFunction[mode], 9000.0)
                (speedupMap.values - mode).forEach {
                    assertEquals(controllableUtilityFunction[it], 0.0)
                }
                // Mode should always be the one with U = 9000 regardless of random number.
                assertEquals(mode, choiceModel.calculate(random = 0.5))
                assertEquals(mode, choiceModel.calculate(random = 0.00001))
                assertEquals(mode, choiceModel.calculate(random = 0.99999))
            }
        }
    }

    @Test
    fun equalSelectionProbability() {
        val controllableUtilityFunction = ControllableUtilityFunction(FakePlan)
        val choiceModel = LegacyModeChoiceModel(
            testAttractivenessModel,
            modes = FakePlan,
            impedance = ControllableImpedance(),
            utilitiesGenerator = { _, _, _, _ -> controllableUtilityFunction }
        )
        scenario.run {
            choiceModel.selectMode(
                first,
                zones[0].point(BIELEFELD),
                zones[1].point(BIELEFELD),
                fakeActivity,
                fakeActivity,
                speedupMap.values.toSet(),
                impedance,
                0.5

            )
        }
    }
}

class ControllableUtilityFunction(override val modes: CodePlan<Mode>) : IGeneratedHcUtilityFunction {

    private val map: MutableMap<Mode, Double> = mutableMapOf()
    operator fun set(mode: Mode, value: Double) {
        map[mode] = value
    }

    operator fun get(mode: Mode): Double {
        return map[mode] ?: 0.0
    }

    override fun calculateU_fuss(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return this[StandardMode.PEDESTRIAN]
    }

    override fun calculateU_rad(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return this[StandardMode.BIKE]
    }

    override fun calculateU_pkw(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return this[StandardMode.CAR]
    }

    override fun calculateU_mf(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return this[StandardMode.PASSENGER]
    }

    override fun calculateU_oev(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return this[StandardMode.PUBLICTRANSPORT]
    }

    override fun calculateU_bs(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return this[StandardMode.BIKESHARING]
    }

    override fun calculateU_moia(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return this[StandardMode.RIDE_POOLING]
    }

    override fun calculateU_escooter(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return this[StandardMode.E_SCOOTER]
    }

    override fun calculateU_cs_ff(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return this[StandardMode.CARSHARING_FREE]
    }

    override fun calculateU_cs_sb(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return this[StandardMode.CARSHARING_STATION]
    }

    override fun calculateU_taxi(
        person: Person,
        origin: ZoneLocation,
        destination: ZoneLocation,
        previousActivity: StationaryAction,
        nextActivity: StationaryAction,
        choiceSet: Set<Mode>,
        impedance: Metrics,
        randomNumber: Double
    ): Double {
        return this[StandardMode.TAXI]
    }
}
