package scenarios

import HouseholdSpawnLimits
import domain.events.CarSelector
import domain.events.InitPersonEvent
import domain.events.ModeScopeDispatcher
import domain.events.PersonBehavior
import domain.location.ZoneLocationImpl
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
import usecases.choicemodels.ModeAvailabilityFilter
import usecases.legacyChoiceModelModes
import utils.units.sinceStart
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class CarOnlyScenario {
    @RepeatedTest(value = 1, name = RepeatedTest.LONG_DISPLAY_NAME)
    fun runSyntheticTest() {
        val legacyModes = legacyChoiceModelModes
        val zones = generateZones(2)

        val households = zones.generateHouseholds(
            1,
            spawnLimits = HouseholdSpawnLimits(
                numCars = 2..2,
                numPersons = 100..100,

            ),
            personLimits = spawnDrivers,
            membershipsMap = mutableMapOf()
        )
        val persons = households.flatMap { it.members }
        val car = legacyModes.car
        val random = Random(1)
        assertTrue(persons.all { it.household in it.memberships })

        persons.forEach { it.generateActivitySchedule(10, random) }
        val impedance = ControllableImpedance()
        val availability = ModeAvailabilityFilter(
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
                zones.map { ZoneLocationImpl(it.centroid.coordinate, it) }.toSet()
            ),
            impedance = impedance,
            modeChoice = FixedOrderChoiceModel("prefer car", setOf(car, legacyModes.pedestrian), availability),
            scopeDispatcher = modeScopeDispatcher
        )

        val sim = ParallelSimulator(timeStep = 1.minutes)
        val resource = persons.asResource("EO", "none")
        val test = resource.asRepository()
        sim.addAgents(test) { person ->
            InitPersonEvent(person, syntheticBehavior)
        }
        sim.run(0.days.sinceStart, 7.days.sinceStart)
    }
}
