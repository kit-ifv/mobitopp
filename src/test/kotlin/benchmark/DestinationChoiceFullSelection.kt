package benchmark

import BIELEFELD
import domain.data.point
import generateActivities
import generateHouseholds
import generateZones
import syntheticsim.ControllableImpedance
import usecases.LegacyMode
import usecases.choicemodels.ILegacyDestinationChoice
import usecases.choicemodels.LegacyDestinationChoice
import usecases.choicemodels.NoFilter
import usecases.choicemodels.destinationchoice.ModernizedDestinationChoice
import usecases.legacyChoiceModelModes
import kotlin.random.Random

fun main() {
    val controllableImpedance = ControllableImpedance()
    val zones = generateZones(100)
    val zoneLocations = zones.map { it.point(BIELEFELD) }
    val attractiveness = zones.spawnAttractiveness()
    val households = zones.generateHouseholds(10)
    val persons = households.flatMap { it.members }
    val activities = zones.generateActivities(10)
    controllableImpedance.run {
        zones.generateRandomValues(1 to 360, 0.1 to 100.2, 0.0 to 50.0)
    }
    val destinationChoice: ILegacyDestinationChoice = LegacyDestinationChoice(
        controllableImpedance,
        attractiveness,
        umlands = { false },
        zones.toSet(),
        legacyChoiceModelModes,
        NoFilter

    )

    val destinationChoice2: ILegacyDestinationChoice = ModernizedDestinationChoice(
        controllableImpedance,
        attractiveness,
        { false },
        zones.toSet(),

        legacyChoiceModelModes,
    )
    val amount = 100000
    val random = Random(42)
    repeat(amount) {
        val person = persons.random(random)
        val prev = activities.random(random)
        val next = activities.random(random)
        destinationChoice.run {
            zoneLocations.selectDestination(person, prev, next, LegacyMode.entries, 0.5)
        }
    }
    repeat(amount) {
        destinationChoice2.run {
            val person = persons.random(random)
            val prev = activities.random(random)
            val next = activities.random(random)
            zoneLocations.selectDestination(person, prev, next, LegacyMode.entries, 0.5)
        }
    }
}
