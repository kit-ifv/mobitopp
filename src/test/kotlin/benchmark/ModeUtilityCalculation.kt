package benchmark

import BIELEFELD
import domain.data.point
import generateActivities
import generateHouseholds
import generateZones
import syntheticsim.ControllableImpedance
import usecases.LegacyMode
import usecases.choicemodels.GeneratedHcUtilityFunction
import usecases.choicemodels.LegacyModeChoiceModel
import usecases.choicemodels.modechoice.ModernizedModeUtility
import usecases.legacyChoiceModelModes
import usecases.legacyChoiceModelPurposes
import kotlin.random.Random

fun main() {
    val controllableImpedance = ControllableImpedance()
    val zones = generateZones(10)
    val attractivenessModel = zones.spawnAttractiveness()
    val households = zones.generateHouseholds(10)
    val persons = households.flatMap { it.members }
    val activities = zones.generateActivities(10)
    controllableImpedance.run {
        zones.generateRandomValues(1 to 360, 0.1 to 100.2, 0.0 to 50.0)
    }
    val modernizedModeChoice = LegacyModeChoiceModel(
        attractivenessModel,
        modes = legacyChoiceModelModes,
        purposes = legacyChoiceModelPurposes,
        impedance = controllableImpedance,
        choiceFilter = { modes, _ -> modes }, // Factually no filter
        utilitiesGenerator = { a, _, m, _, p -> ModernizedModeUtility(m, a, p) }
//        utilitiesGenerator = {a, l, m , h -> GeneratedHcUtilityFunction(a, l, m, h) }
    )

    val modeChoice2 = LegacyModeChoiceModel(
        attractivenessModel,
        modes = legacyChoiceModelModes,
        purposes = legacyChoiceModelPurposes,
        impedance = controllableImpedance,
        choiceFilter = { modes, _ -> modes }, // Factually no filter
//        utilitiesGenerator = { a, l, m, h -> ModernizedModeUtility(m) }
        utilitiesGenerator = { a, l, m, h, _ -> GeneratedHcUtilityFunction(a, l, m, legacyChoiceModelPurposes, h) }
    )
    val random = Random(42)

    val choiceSet = LegacyMode.entries.toSet()
    repeat(10000000) {
        modeChoice2.selectMode(
            persons.random(random),
            zones.random(random).point(BIELEFELD),
            zones.random(random).point(BIELEFELD),
            activities.random(random),
            activities.random(random),
            choiceSet,
            controllableImpedance,
            0.5
        )
    }
    repeat(10000000) {
        modernizedModeChoice.selectMode(
            persons.random(random),
            zones.random(random).point(BIELEFELD),
            zones.random(random).point(BIELEFELD),
            activities.random(random),
            activities.random(random),
            choiceSet,
            controllableImpedance,
            0.5
        )
    }
}
