package application.scenarios

import HouseholdSpawnLimits
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
import domain.simulation.behavior.AvailabilityModelWithSharing
import domain.simulation.events.NoWriters
import domain.simulation.events.PersonBehavior
import domain.simulation.events.StandardDestinationImplementation
import domain.simulation.events.StandardModeImplementation
import domain.simulation.events.personStateMachine
import edu.kit.ifv.mobitopp.discretechoice.models.FixedOrderChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.RandomChoiceModel
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
    @RepeatedTest(value = 10, name = RepeatedTest.LONG_DISPLAY_NAME)
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
            modeChoice = FixedOrderChoiceModel(
                "prefer car",
                setOf(car, legacyModes.pedestrian),
                availability.asResourceAvailabilityFilter()
            ),
            modes = legacyChoiceModelModes,
            attractivityModel = testAttractivenessModel,
            availabilityModel = availability,
            bikeSharingConnectionSelector = availability,
            drtAvailabilitySelector = availability,
            spawnDestinationCharacteristics = StandardDestinationImplementation,
            spawnModeCharacteristics = StandardModeImplementation

        )

        val agents = BuildAgents(
            seed = 1L,
            NoWriters.personStateMachine.withRecording(),
            syntheticBehavior
        ).buildPersonAgents(households)

        RecordingStateMachine.recordInteractions()

        val sim = ParallelSimulator(timeStep = 1.minutes)
        val resource = agents.asResource("EO", "none")
        val testAgents = resource.elements.toList()
        sim.addAgents(testAgents)
        sim.run(0.days.sinceStart, 7.days.sinceStart)

        RecordingStateMachine.stateMachineUsage.renderAsPumlStateCharts()
        RecordingStateMachine.interactionRecorder.renderAsPumlTimingDiagram(testAgents[0])
        RecordingStateMachine.interactionRecorder.renderAsPumlSequenceDiagram(testAgents[0])
    }
}
