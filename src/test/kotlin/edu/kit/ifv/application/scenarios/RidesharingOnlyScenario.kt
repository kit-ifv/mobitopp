package edu.kit.ifv.application.scenarios
import edu.kit.ifv.application.syntheticsim.ControllableImpedance
import edu.kit.ifv.core.events.ParallelSimulator
import edu.kit.ifv.core.modelsteps.resources.asResource
import edu.kit.ifv.core.statemachine.usage.RecordingStateMachine
import edu.kit.ifv.core.statemachine.usage.renderAsPumlSequenceDiagram
import edu.kit.ifv.core.statemachine.usage.renderAsPumlStateCharts
import edu.kit.ifv.core.statemachine.usage.renderAsPumlTimingDiagram
import edu.kit.ifv.core.statemachine.usage.withRecording
import edu.kit.ifv.domain.shared.enums.LegacyMode
import edu.kit.ifv.domain.shared.enums.legacyChoiceModelModes
import edu.kit.ifv.domain.simulation.agent.BuildAgents
import edu.kit.ifv.domain.simulation.agent.SharingStationAgent
import edu.kit.ifv.domain.simulation.behavior.availability.currentlyAffectedProviders
import edu.kit.ifv.domain.simulation.behavior.availability.defaultAvailabilityModel
import edu.kit.ifv.domain.simulation.data.sharing.MutableSharingProvider
import edu.kit.ifv.domain.simulation.data.sharing.SharingProviderId
import edu.kit.ifv.domain.simulation.events.personStateMachine
import edu.kit.ifv.mobitopp.discretechoice.models.FixedOrderChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.RandomChoiceModel
import edu.kit.ifv.utils.units.AbsoluteTime
import edu.kit.ifv.utils.units.sinceStart
import generateActivitySchedule
import generateHouseholds
import generateSharingStation
import generateZones
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.RepeatedTest
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class RidesharingOnlyScenario {

    @Suppress("LongMethod")
    @RepeatedTest(value = 10, name = RepeatedTest.LONG_DISPLAY_NAME)
    fun runSyntheticTest() {
        RecordingStateMachine.recordInteractions()

        val random = Random(1)

        legacyChoiceModelModes.car
        val bikeSharing = legacyChoiceModelModes.bikeSharing
        val pedestrian = legacyChoiceModelModes.pedestrian
        val zones = generateZones(10)

        val provider = MutableSharingProvider(SharingProviderId(1L)) {
            name = "Testprovider"
            mode = bikeSharing
            operatingHours = 0 .. 24
        }
        zones.map { it.generateSharingStation(provider, 1) }

        val households = zones.generateHouseholds(
            10,
            memberships = mutableListOf(provider),
            personScope = { it.generateActivitySchedule(10, random) },
        )

        // TODO base modes stet (here legacyChoiceModelModes.options) defined at various points: concentrate on one point!
        val impedance = ControllableImpedance()
        val availability = defaultAvailabilityModel(legacyChoiceModelModes,
            LegacyMode.TAXI, LegacyMode.E_SCOOTER,
            impedance = impedance
        )
//            mapOf(bikeSharing to setOf(provider.id)),

        context(impedance) {
            val context = ScenarioContext(
                scenarioName = "RidesharingOnlyScenario",
                destinationChoiceModel = RandomChoiceModel(
                    "random destination",
                    zones.map { it.centroidLocation }.toSet(),
                ),
                modeChoiceModel = FixedOrderChoiceModel(
                    "prefer ridesharing",
                    setOf(bikeSharing, pedestrian),
                    availability.asResourceAvailabilityFilter(),
                ),
                modeAvailability = availability,
            )
            val builder = BuildAgents(
                seed = 1L,
                context.personStateMachine.withRecording(),
            )

            builder.buildSharingProviderAgents(listOf(provider))
            val agents = builder.buildPersonAgents(households)

            agents.forEach { person ->
                val dest = zones.first { it.id != person.location.zoneId }
                val sharedResources = availability.currentlyAffectedProviders(
                    legacyChoiceModelModes.options,
                    person,
                    AbsoluteTime.START,
                    dest.centroidLocation
                )

                assertTrue(
                    sharedResources.any { it is SharingStationAgent },
                    "No sharing station available for person $person, from: ${person.location}, to: $dest",
                )
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
}
