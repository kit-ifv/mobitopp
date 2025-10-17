package application.scenarios

import application.syntheticsim.ControllableImpedance
import application.syntheticsim.testAttractivenessModel
import core.events.ParallelSimulator
import core.statemachine.usage.RecordingStateMachine
import core.statemachine.usage.renderAsPumlSequenceDiagram
import core.statemachine.usage.renderAsPumlStateCharts
import core.statemachine.usage.renderAsPumlTimingDiagram
import core.statemachine.usage.withRecording
import domain.shared.enums.legacyChoiceModelModes
import domain.simulation.agent.BuildAgents
import domain.simulation.agent.DrtAlgorithm
import domain.simulation.agent.DrtProviderAgent
import domain.simulation.agent.SimpleMatrixDrtAlgorithm
import domain.simulation.behavior.AvailabilityModelWithSharing
import domain.simulation.behavior.currentlyAffectedProviders
import domain.simulation.events.NoWriters
import domain.simulation.events.PersonBehavior
import domain.simulation.events.StandardDestinationImplementation
import domain.simulation.events.StandardModeImplementation
import domain.simulation.events.drtProviderStateMachine
import domain.simulation.events.personStateMachine
import domain.synthesis.data.DrtProviderId
import domain.synthesis.data.MutableDrtProviderData
import edu.kit.ifv.mobitopp.discretechoice.models.FixedOrderChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.RandomChoiceModel
import generateActivitySchedule
import generateHouseholds
import generateZones
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.RepeatedTest
import utils.units.sinceStart
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class RidepoolingScenario {

    @Suppress("LongMethod")
    @RepeatedTest(value = 10, name = RepeatedTest.LONG_DISPLAY_NAME)
    fun runSyntheticTest() {
        RecordingStateMachine.recordInteractions()

        val random = Random(1)

        val ridePooling = legacyChoiceModelModes.ridePooling
        val pedestrian = legacyChoiceModelModes.pedestrian
        val zones = generateZones(10)

        val provider = MutableDrtProviderData(DrtProviderId(1L)) {
            name = "TestDrtProvider"
            mode = ridePooling
        }

        val impedance = ControllableImpedance()
        val algorithm: DrtAlgorithm = SimpleMatrixDrtAlgorithm(
            impedance = impedance,
            avgWaitTime = 4.minutes,
            serviceArea = zones,
            operationHours = 5 to 18,
            numVehicles = zones.size,
        )

        val households = zones.generateHouseholds(
            10,
            memberships = mutableListOf(),
            drtMemberships = mutableListOf(provider),
            personScope = { it.generateActivitySchedule(10, random) }
        )

        // TODO base modes stet (here legacyChoiceModelModes.options) defined at various points: concentrate on one point!
        val availability = AvailabilityModelWithSharing(
            legacyChoiceModelModes,
            mapOf(),
            mapOf(ridePooling to setOf(provider.id)),
            impedance
        )

        val syntheticBehavior = PersonBehavior(
            destinationChoice = RandomChoiceModel(
                "random destination",
                zones.map { it.centroid }.toSet()
            ),
            modeChoice = FixedOrderChoiceModel(
                "prefer ridepooling",
                setOf(ridePooling, pedestrian),
                availability.asResourceAvailabilityFilter()
            ),
            modes = legacyChoiceModelModes,
            impedance = impedance,
            attractivityModel = testAttractivenessModel,
            availabilityModel = availability,
            bikeSharingConnectionSelector = availability,
            drtAvailabilitySelector = availability,
            spawnDestinationCharacteristics = StandardDestinationImplementation,
            spawnModeCharacteristics = StandardModeImplementation
        )

        val builder = BuildAgents(
            seed = 1L,
            NoWriters.personStateMachine.withRecording(),
            syntheticBehavior,
            drtStateMachine = drtProviderStateMachine.withRecording(),
            drtAlgorithm = algorithm,
        )
        val agents = builder.buildPersonAgents(households)

        agents.forEach { person ->
            val dest = zones.first { it != person.location.zone }
            val sharedResources =
                context(person, 5.hours.sinceStart, dest.centroid) {
                    availability.currentlyAffectedProviders(legacyChoiceModelModes.options)
                }
            assertTrue(
                sharedResources.any { it is DrtProviderAgent },
                "No sharing station available for person $person, from: ${person.location}, to: $dest"
            )
        }

        val sim = ParallelSimulator(timeStep = 1.minutes) // TODO test again with parallel sim
//        val testAgents = agents.asResource("EO", "none").elements.toList()
        sim.addAgents(agents)
        sim.run(0.days.sinceStart, 7.days.sinceStart)

        RecordingStateMachine.stateMachineUsage.renderAsPumlStateCharts()
        RecordingStateMachine.interactionRecorder.renderAsPumlTimingDiagram(agents.toList()[0])
        RecordingStateMachine.interactionRecorder.renderAsPumlSequenceDiagram(agents.toList()[0])
    }
}
