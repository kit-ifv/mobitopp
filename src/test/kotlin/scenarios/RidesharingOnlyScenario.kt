package scenarios

import discreteChoice.models.FixedOrderChoiceModel
import discreteChoice.models.RandomChoiceModel
import domain.data.MutableSharingProvider
import domain.data.SharingProviderId
import domain.events.CarSelector
import domain.events.InitPersonEvent
import domain.events.ModeScopeDispatcher
import domain.events.PersonBehavior
import domain.events.SharingVehicleSelector
import generateActivitySchedule
import generateHouseholds
import generateSharingStation
import generateZones
import modeling.events.ParallelSimulator
import modeling.steps.asRepository
import modeling.steps.asResource
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.RepeatedTest
import syntheticsim.ControllableImpedance
import syntheticsim.testAttractivenessModel
import usecases.legacyChoiceModelModes
import usecases.models.SharingAvailabilityFilter
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
