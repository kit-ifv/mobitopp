package scenarios

import HouseholdSpawnLimits
import domain.agent.BuildAgents
import domain.events.CarSelector
import domain.events.InitPersonEvent
import domain.events.ModeScopeDispatcher
import domain.events.PersonBehavior
import generateActivitySchedule
import generateHouseholds
import generateZones
import modeling.events.ParallelSimulator
import modeling.models.FixedOrderChoiceModel
import modeling.models.RandomChoiceModel
import modeling.steps.asRepository
import modeling.steps.asResource
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.RepeatedTest
import spawnDrivers
import syntheticsim.ControllableImpedance
import syntheticsim.testAttractivenessModel
import usecases.legacyChoiceModelModes
import usecases.models.SharingAvailabilityFilter
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

//        val persons = households.flatMap { it.members }
        val car = legacyModes.car

        val agents = BuildAgents(seed = 1L).buildPersonAgents(households)
        assertTrue(agents.all { it.household in it.memberships })

        val impedance = ControllableImpedance()
        val availability = SharingAvailabilityFilter(
            legacyModes,
            emptySet(),
            mapOf(),
            impedance
        )

        val modeScopeDispatcher = ModeScopeDispatcher(
            car to CarSelector(car),
        )
        val syntheticBehavior = PersonBehavior(
            destinationChoice = RandomChoiceModel(
                "random destination",
                zones.map { it.centroid }.toSet()
            ),
            impedance = impedance,
            modeChoice = FixedOrderChoiceModel("prefer car", setOf(car, legacyModes.pedestrian), availability),
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
