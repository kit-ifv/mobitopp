package application.scenarios

import application.syntheticsim.ControllableImpedance
import application.syntheticsim.testAttractivenessModel
import core.events.ParallelSimulator
import core.modelsteps.asResource
import core.statemachine.usage.RecordingStateMachine
import core.statemachine.usage.renderAsPumlSequenceDiagram
import core.statemachine.usage.renderAsPumlStateCharts
import core.statemachine.usage.renderAsPumlTimingDiagram
import core.statemachine.usage.withRecording
import domain.shared.enums.legacyChoiceModelModes
import domain.simulation.agent.BuildAgents
import domain.simulation.agent.SharingStationAgent
import domain.simulation.behavior.AvailabilityModelWithSharing
import domain.simulation.behavior.currentlyAffectedResources
import domain.simulation.events.PersonBehavior
import domain.simulation.events.StandardDestinationImplementation
import domain.simulation.events.StandardModeImplementation
import domain.simulation.events.personStateMachine
import domain.synthesis.data.MutableSharingProvider
import domain.synthesis.data.SharingProviderId
import edu.kit.ifv.mobitopp.discretechoice.models.FixedOrderChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.RandomChoiceModel
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
        RecordingStateMachine.recordInteractions()

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

        // TODO base modes stet (here legacyChoiceModelModes.options) defined at various points: concentrate on one point!
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
            modes = legacyChoiceModelModes,
            impedance = impedance,
            attractivityModel = testAttractivenessModel,
            availabilityModel = availability,
            bikeSharingConnectionSelector = availability,
            spawnDestinationCharacteristics = StandardDestinationImplementation,
            spawnModeCharacteristics = StandardModeImplementation
        )

        val builder = BuildAgents(
            seed = 1L,
            personStateMachine.withRecording(),
            syntheticBehavior
        )
        val agents = builder.buildPersonAgents(households)

        agents.forEach { person ->
            val sharedResources =
                context(person) { availability.currentlyAffectedResources(legacyChoiceModelModes.options) }
            assertTrue(sharedResources.any { it is SharingStationAgent })
        }

        val sim = ParallelSimulator(timeStep = 1.minutes) // TODO test again with parallel sim
        val resource = agents.asResource("EO", "none")
        val testAgents = resource.elements.toList()
        sim.addAgents(testAgents)
        sim.run(0.days.sinceStart, 7.days.sinceStart)

        RecordingStateMachine.stateMachineUsage.renderAsPumlStateCharts()
        RecordingStateMachine.interactionRecorder.renderAsPumlTimingDiagram(agents.toList()[0])
        RecordingStateMachine.interactionRecorder.renderAsPumlSequenceDiagram(agents.toList()[0])
    }
}
