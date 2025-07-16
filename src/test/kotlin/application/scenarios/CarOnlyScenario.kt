package application.scenarios

import HouseholdSpawnLimits
import application.syntheticsim.ControllableImpedance
import application.syntheticsim.testAttractivenessModel
import core.events.ParallelSimulator
import core.events.SequentialSimulator
import core.modelsteps.asResource
import discreteChoice.models.FixedOrderChoiceModel
import discreteChoice.models.RandomChoiceModel
import domain.shared.enums.legacyChoiceModelModes
import domain.simulation.agent.BuildAgents
import domain.simulation.behavior.AvailabilityModelWithSharing
import domain.simulation.events.PersonBehavior
import domain.simulation.events.personStateMachine
import generateActivitySchedule
import generateHouseholds
import generateZones
import org.junit.jupiter.api.RepeatedTest
import spawnDrivers
import utils.units.sinceStart
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class CarOnlyScenario {
    @RepeatedTest(value = 1, name = RepeatedTest.LONG_DISPLAY_NAME)
    fun runSyntheticTest() {
        val random = Random(1)

        val legacyModes = legacyChoiceModelModes
        val zones = generateZones(2)

        val households = zones.generateHouseholds(
            1,
            spawnLimits = HouseholdSpawnLimits(
                numCars = 2..2,
                numPersons = 100..100,

            ),
            personLimits = spawnDrivers,
            memberships = mutableListOf(),
            personScope = { it.generateActivitySchedule(10, random) }
        )

        val impedance = ControllableImpedance()
        val availability = AvailabilityModelWithSharing(
            legacyModes,
            mapOf(),
            impedance
        )

        val car = legacyModes.car
        val syntheticBehavior = PersonBehavior(
            destinationChoice = RandomChoiceModel(
                "random destination",
                zones.map { it.centroid }.toSet()
            ),
            impedance = impedance,
            modeChoice = FixedOrderChoiceModel("prefer car", setOf(car, legacyModes.pedestrian), availability),
            attractivityModel = testAttractivenessModel,
            availabilityModel = availability,
            bikeSharingConnectionSelector = availability,
            choiceModelModes = legacyChoiceModelModes
        )

        val agents = BuildAgents(seed = 1L, personStateMachine, syntheticBehavior).buildPersonAgents(households)

        val sim = ParallelSimulator(timeStep = 1.minutes)
        val resource = agents.asResource("EO", "none")
        val testAgents = resource.elements.toList()
        sim.addAgents(testAgents)
        sim.run(0.days.sinceStart, 7.days.sinceStart)
    }
}
