package application.scenarios

import application.syntheticsim.ControllableImpedance
import application.syntheticsim.testAttractivenessModel
import core.events.ParallelSimulator
import core.events.SequentialSimulator
import core.modelsteps.asResource
import discreteChoice.models.FixedOrderChoiceModel
import discreteChoice.models.RandomChoiceModel
import domain.shared.enums.legacyChoiceModelModes
import domain.simulation.agent.BuildAgents
import domain.simulation.agent.SharingStationAgent
import domain.simulation.behavior.AvailabilityModelWithSharing
import domain.simulation.events.PersonBehavior
import domain.simulation.events.personStateMachine
import domain.synthesis.data.MutableSharingProvider
import domain.synthesis.data.SharingProviderId
import generateActivitySchedule
import generateHouseholds
import generateSharingStation
import generateZones
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.RepeatedTest
import utils.units.sinceStart
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class RidesharingOnlyScenario {

    @Suppress("LongMethod")
    @RepeatedTest(value = 10, name = RepeatedTest.LONG_DISPLAY_NAME)
    fun runSyntheticTest() {
        val random = Random(1)

        val car = legacyChoiceModelModes.car
        val bikeSharing = legacyChoiceModelModes.bikeSharing
        val pedestrian = legacyChoiceModelModes.pedestrian
        val zones = generateZones(10)

        val provider = MutableSharingProvider(SharingProviderId(1L)) {
            name = "Testprovider"
            mode = bikeSharing
        }
        zones.map { it.generateSharingStation(provider, 1) }

        val households = zones.generateHouseholds(
            10,
            memberships = mutableListOf(provider),
            personScope = { it.generateActivitySchedule(10, random) }
        )

        val impedance = ControllableImpedance()
        val availability = AvailabilityModelWithSharing(
            legacyChoiceModelModes,
            mapOf(bikeSharing to setOf(provider.id)),
            impedance
        )

        val syntheticBehavior = PersonBehavior(
            destinationChoice = RandomChoiceModel(
                "random destination",
                zones.map { it.centroid }.toSet()
            ),
            modeChoice = FixedOrderChoiceModel("prefer ridesharing", setOf(bikeSharing, pedestrian), availability),
            impedance = impedance,
            attractivityModel = testAttractivenessModel,
            availabilityModel = availability,
            bikeSharingConnectionSelector = availability,
            choiceModelModes = legacyChoiceModelModes,
        )

        val builder = BuildAgents(seed = 1L, personStateMachine, syntheticBehavior)
        val agents = builder.buildPersonAgents(households)

        agents.forEach { person ->
            val (_, sharedResources) = availability.situativeAvailability(person)
            assertTrue(sharedResources.any { it is SharingStationAgent })
        }

        val sim = ParallelSimulator(timeStep = 1.minutes) // TODO test again with parallel sim
        val resource = agents.asResource("EO", "none")
        val testAgents = resource.elements.toList()
        sim.addAgents(testAgents)
        sim.run(0.days.sinceStart, 7.days.sinceStart)
    }
}
