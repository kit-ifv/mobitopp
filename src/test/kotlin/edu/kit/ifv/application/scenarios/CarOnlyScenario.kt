package edu.kit.ifv.application.scenarios
import HouseholdSpawnLimits
import edu.kit.ifv.application.syntheticsim.ControllableImpedance
import edu.kit.ifv.core.events.ParallelSimulator
import edu.kit.ifv.core.modelsteps.resources.asResource
import edu.kit.ifv.core.statemachine.usage.RecordingStateMachine
import edu.kit.ifv.core.statemachine.usage.renderAsPumlSequenceDiagram
import edu.kit.ifv.core.statemachine.usage.renderAsPumlStateCharts
import edu.kit.ifv.core.statemachine.usage.renderAsPumlTimingDiagram
import edu.kit.ifv.core.statemachine.usage.withRecording
import edu.kit.ifv.domain.shared.enums.legacyChoiceModelModes
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.BuildAgents
import edu.kit.ifv.domain.simulation.behavior.availability.AvailabilityModelWithSharing
import edu.kit.ifv.domain.simulation.behavior.destinationchoice.DestinationChoiceCharacteristics
import edu.kit.ifv.domain.simulation.events.personStateMachine
import edu.kit.ifv.mobitopp.discretechoice.models.FixedOrderChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.RandomChoiceModel
import edu.kit.ifv.utils.units.sinceStart
import generateActivitySchedule
import generateHouseholds
import generateZones
import org.junit.jupiter.api.RepeatedTest
import spawnDrivers
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
            personScope = { it.generateActivitySchedule(10, random) },
        )

        val impedance = ControllableImpedance()
        val availability = AvailabilityModelWithSharing(
            legacyModes,
            mapOf(),
            mapOf(),
        )

        val car = legacyModes.car

        context(impedance) {
            val destinationChoice = RandomChoiceModel<StandardLocation, DestinationChoiceCharacteristics>(
                "random destination",
                zones.map { it.centroidLocation }.toSet(),
            )
            val modeChoice = FixedOrderChoiceModel(
                "prefer car",
                setOf(car, legacyModes.pedestrian),
                availability.asResourceAvailabilityFilter(),
            )

            val context = ScenarioContext(
                scenarioName = "carOnlyScenario",
                destinationChoiceModel = destinationChoice,
                modeChoiceModel = modeChoice,
                modeAvailability = availability,
            )

            val agents = BuildAgents(
                seed = 1L,
                context.personStateMachine.withRecording(),
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
}
