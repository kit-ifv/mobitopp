package choicemodels

import benchmark.spawnAttractiveness
import datastructure.StationaryAction
import domain.data.Person
import domain.enums.Mode
import domain.location.Metrics
import domain.location.ZoneLocation
import syntheticsim.ControllableImpedance
import usecases.LegacyMode
import usecases.choicemodels.IGeneratedHcUtilityFunction
import usecases.choicemodels.LegacyModeChoiceModel
import usecases.choicemodels.modechoice.ModeParameters
import usecases.choicemodels.modechoice.ModernizedModeUtility
import usecases.legacyChoiceModelModes
import kotlin.math.abs
import kotlin.test.BeforeTest
import kotlin.test.assertTrue

class ModeChoiceUtilityTest : CompareTwoUtilityFunctions<IGeneratedHcUtilityFunction>() {
    override val comparison: TestSimulation.(
        IGeneratedHcUtilityFunction,
        IGeneratedHcUtilityFunction
    ) -> Unit = { first, second ->
        testAllCalculations(
            first,
            second,
            person,
            originLocation,
            destinationLocation,
            previousActivity,
            nextActivity,
            LegacyMode.entries.toSet(),
            impedance,
            0.5
        )
    }

    @BeforeTest
    fun setup() {
        val attractiveness = zones.spawnAttractiveness()
        val legacyModeChoice =
            LegacyModeChoiceModel(attractiveness, modes = legacyChoiceModelModes, impedance = ControllableImpedance())
        a = legacyModeChoice.utilities
//        b = a
        b = ModernizedModeUtility(
            legacyChoiceModelModes, attractiveness,
            parameters = ModeParameters(
                legacyChoiceModelModes
            )
        )
    }
}

/**
 * Return the difference between two Utility function interfaces for target function
 *
 * @param a the first Utility function interface
 * @param b the second utiltiy function interface.
 * @param functor the function to evaluate
 * @receiver
 * @return the difference U_a - U_b as double
 */
private fun testFunction(
    a: IGeneratedHcUtilityFunction,
    b: IGeneratedHcUtilityFunction,
    functor: IGeneratedHcUtilityFunction.(
        Person,
        ZoneLocation,
        ZoneLocation,
        StationaryAction,
        StationaryAction,
        Set<Mode>,
        Metrics,
        Double
    ) -> Double
): (
    Person,
    ZoneLocation,
    ZoneLocation,
    StationaryAction,
    StationaryAction,
    Set<Mode>,
    Metrics,
    Double
) -> Double {
    return { person, origin, destination, previous, next, choice, impedance, random ->
        a.functor(person, origin, destination, previous, next, choice, impedance, random) - b.functor(
            person, origin, destination, previous, next, choice, impedance, random
        )
    }
}

/**
 * Run all utility functions for a and b and subtract the results. If the functions are equal the result should be
 * less than epsilon.
 *
 * @param a
 * @param b
 * @param person
 * @param origin
 * @param destination
 * @param previousActivity
 * @param nextActivity
 * @param choiceSet
 * @param impedance
 * @param randomNumber
 */
@Suppress("LongParameterList")
private fun testAllCalculations(
    a: IGeneratedHcUtilityFunction,
    b: IGeneratedHcUtilityFunction,
    person: Person,
    origin: ZoneLocation,
    destination: ZoneLocation,
    previousActivity: StationaryAction,
    nextActivity: StationaryAction,
    choiceSet: Set<Mode>,
    impedance: Metrics,
    randomNumber: Double,
    epsilon: Double = 0.0001
) {
    val targetFunctions = listOf(
        IGeneratedHcUtilityFunction::calculateU_bs,
        IGeneratedHcUtilityFunction::calculateU_mf,
        IGeneratedHcUtilityFunction::calculateU_oev,
        IGeneratedHcUtilityFunction::calculateU_pkw,
        IGeneratedHcUtilityFunction::calculateU_rad,
        IGeneratedHcUtilityFunction::calculateU_moia,
        IGeneratedHcUtilityFunction::calculateU_taxi,
        IGeneratedHcUtilityFunction::calculateU_cs_ff,
        IGeneratedHcUtilityFunction::calculateU_cs_sb,
        IGeneratedHcUtilityFunction::calculateU_escooter,
        IGeneratedHcUtilityFunction::calculateU_fuss,
    )
    val results = targetFunctions.map {
        try {
            Result.success(
                testFunction(a, b, it).invoke(
                    person,
                    origin,
                    destination,
                    previousActivity,
                    nextActivity,
                    choiceSet,
                    impedance,
                    randomNumber
                )
            )
        } catch (e: Error) {
            Result.failure(e)
        }
    }
    results.filter { it.isFailure || it.isSuccess && it.getOrNull()?.let { d -> abs(d) > epsilon } ?: false }
    val badOutputs = targetFunctions.zip(results).filter {
        it.second.isFailure || it.second.isSuccess && it.second.getOrNull()?.let { d -> abs(d) > epsilon } ?: false
    }
    assertTrue(
        message = "The following utility functions are not equal\n${
            badOutputs.joinToString(
                separator = "\n",
                prefix = "-----\n",
                postfix = "\n-----"
            ) { "${it.first.name} -> ${it.second}" }
        }"
    ) { badOutputs.isEmpty() }
}
