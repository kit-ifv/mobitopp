package application.scenarios

import application.syntheticsim.ControllableImpedance
import application.syntheticsim.testAttractivenessModel
import core.events.ParallelSimulator
import core.modelsteps.asRepository
import core.modelsteps.asResource
import discreteChoice.models.FixedOrderChoiceModel
import discreteChoice.models.RandomChoiceModel
import domain.shared.enums.legacyChoiceModelModes
import domain.simulation.agent.BuildAgents
import domain.simulation.agent.SharingStationAgent
import domain.simulation.agent.toAgent
import domain.simulation.behavior.SharingAvailabilityFilter
import domain.simulation.events.CarSelector
import domain.simulation.events.InitPersonEvent
import domain.simulation.events.ModeScopeDispatcher
import domain.simulation.events.PersonBehavior
import domain.simulation.events.SharingVehicleSelector
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

        val builder = BuildAgents(seed = 1L)
        val providerAgent = provider.toAgent(builder)
        val agents = builder.buildPersonAgents(households)
        agents.forEach {
                person ->
            if (!person.sharedResources().any { it is SharingStationAgent }) {
                println("err")
            }
            assertTrue(person.sharedResources().any { it is SharingStationAgent })
        }

        val impedance = ControllableImpedance()
        val availability = SharingAvailabilityFilter(
            legacyChoiceModelModes,
            providerAgent.stations.toSet(),
            mapOf(bikeSharing to setOf(providerAgent)),
            impedance
        )

        val modeScopeDispatcher = ModeScopeDispatcher(
            car to CarSelector(car),
            bikeSharing.let {
                it to SharingVehicleSelector(
                    it, availability, impedance, pedestrian
                )
            }
        )
        val syntheticBehavior = PersonBehavior(
            destinationChoice = RandomChoiceModel(
                "random destination",
                zones.map { it.centroid }.toSet()
            ),
            impedance = impedance,
            modeChoice = FixedOrderChoiceModel("prefer ridesharing", setOf(bikeSharing, pedestrian), availability),
            scopeDispatcher = modeScopeDispatcher,
            attractivityModel = testAttractivenessModel,
            availabilityModel = availability
        )

        val sim = ParallelSimulator(timeStep = 1.minutes)
        val resource = agents.asResource("EO", "none")
        val test = resource.asRepository()
        sim.addAgents(test) { person ->
            InitPersonEvent(person, syntheticBehavior)
        }
        sim.run(0.days.sinceStart, 7.days.sinceStart)
    }
}
