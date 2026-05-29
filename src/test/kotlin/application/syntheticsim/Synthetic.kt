package application.syntheticsim

import BIELEFELD
import core.statemachine.State
import core.statemachine.builder.StateData
import core.statemachine.usage.RecordingStateMachineFactory
import domain.shared.behavior.Attractiveness
import domain.shared.behavior.AttractivenessModel
import domain.shared.behavior.ChoiceModelPurposes
import domain.shared.behavior.asAttractiveness
import domain.shared.datastructure.schedule.Activity
import domain.shared.enums.ActivityType
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.LegacyMode
import domain.shared.enums.legacyChoiceModelModes
import domain.shared.enums.legacyChoiceModelPurposes
import domain.shared.location.ZoneId
import domain.shared.location.zone.StandardZone
import domain.simulation.agent.BuildAgents
import domain.simulation.agent.PersonAgent
import domain.simulation.agent.PrivateCarAgent
import domain.simulation.agent.getBestCarOrNull
import domain.simulation.agent.locationBySchedule
import domain.simulation.agent.toAgent
import domain.simulation.behavior.AvailabilityModelWithSharing
import domain.simulation.behavior.legacyDestinationChoice
import domain.simulation.behavior.legacyModeChoice
import domain.simulation.events.EndActivityMessage
import domain.simulation.events.EndLegMessage
import domain.simulation.events.FinishedPerson
import domain.simulation.events.FirstActivityMessage
import domain.simulation.events.NoWriters
import domain.simulation.events.PerformLeg
import domain.simulation.events.PerformingActivity
import domain.simulation.events.PersonBehavior
import domain.simulation.events.StandardDestinationImplementation
import domain.simulation.events.StandardModeImplementation
import domain.simulation.events.StartPerson
import domain.simulation.events.StartingTrip
import domain.simulation.events.personStateMachine
import domain.synthesis.ControllableAttractiveness
import domain.synthesis.data.ActivityId
import domain.synthesis.data.Household
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.MutablePlannedActivity
import domain.synthesis.data.Person
import domain.synthesis.data.PlannedActivity
import generateHousehold
import generatePersons
import generateZones
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import point
import spawnCar
import spawnDrivers
import utils.units.AbsoluteTime
import utils.units.sinceStart
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

fun MutablePerson.loadActivityPlan(lambda: PlanLoader.() -> Unit) {
    val plan = PlanLoader(this)
    plan.apply(lambda)
//    plan.plannedActivities.forEach { addActivity(it) }
}

class PlanLoader(private val person: MutablePerson) {
    val plannedActivities = mutableListOf<PlannedActivity>()

    operator fun Triple<ActivityType, Number, Number>.unaryPlus() {
        person
        plannedActivities.add(
            MutablePlannedActivity(
                id = ActivityId(-1L),
                person = person,
                seed = 42L,
            ) {
                activityType = first
                observedTripDuration = (-1).minutes
                startTime = AbsoluteTime(second.toDouble().hours)
                duration = third.toDouble().hours
            },
        )
    }

    var start = AbsoluteTime.START
    operator fun ActivityType.unaryPlus() {
        val p = person

        plannedActivities.add(

            MutablePlannedActivity(
                ActivityId(-1L),
                person = p,
                seed = 42L,
            ) {
                activityType = this@unaryPlus
                observedTripDuration = (-1).minutes
                startTime = start
                duration = 4.hours
            }.also { start += 8.hours },

        )
    }
}

fun PersonAgent.hasAccessToCar(): Boolean = getBestCarOrNull() != null

abstract class Scenario(
    val zones: List<StandardZone>,
    val impedance: ControllableImpedance = ControllableImpedance(),
) {
    val currentAttractivenessModel: ControllableAttractiveness = ControllableAttractiveness(zones)

    abstract val households: List<Household>
    abstract val persons: List<Person>

    // When testing choice models with overridden utility calculation they still require activities for the signature.
    val fakeActivity =
        Activity.fromDuration(
            zones[0].point(BIELEFELD),
            (-1).hours.sinceStart,
            (1).seconds,
            type = ActivityType.UNKNOWN,
        )

    val availability = AvailabilityModelWithSharing(
        legacyChoiceModelModes,
        emptyMap(),
        mapOf(),
        impedance,
    )

    val destinationChoice: OverridableDestinationChoiceModel = OverridableDestinationChoiceModel(
        legacyDestinationChoice,
    )
    val modeChoice: OverridableModeChoiceModel = OverridableModeChoiceModel(
        legacyModeChoice.addFilter(availability.asResourceAvailabilityFilter()),
    )

    protected val behavior = PersonBehavior(
        destinationChoice = destinationChoice.fixed(zones.map { it.centroidLocation }.toSet()),
        modeChoice = modeChoice.fixed(legacyModeChoice.choices),
        modes = legacyChoiceModelModes,
        impedance,
        attractivityModel = currentAttractivenessModel,
        availabilityModel = availability,
        bikeSharingConnectionSelector = availability,
        drtAvailabilitySelector = availability,
        spawnDestinationCharacteristics = StandardDestinationImplementation,
        spawnModeCharacteristics = StandardModeImplementation,
    )

    fun PersonAgent.stepper(): EventStepper {
        val initEvent = this.init().takeIf { it.size == 1 }!!.take(1)[0]
        return EventStepper(initEvent, destinationChoice, modeChoice)
    }

    fun EventStepper.stepOverFirstActivity() {
        take(3) // Handle Init, Start and End Activity.
    }
}

val testAttractivenessModel = object : AttractivenessModel {

    override val purposes: ChoiceModelPurposes = legacyChoiceModelPurposes

    override fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Attractiveness = when (zone) {
        ZoneId(0L) -> 0.0

        // Home zone attractiveness should be 0
        ZoneId(1L) -> 999999.9

        // Zone 1 should be the most attractive zone ever
        ZoneId(2L) -> 1.0

        // Zone 2 should be barely attractive at all
        else -> throw NoSuchElementException("In this test the IDs should only be 0, 1, 2")
    }.asAttractiveness()
}

class OneHouseholdTwoPersons : Scenario(generateZones(3)) {

    override val households: List<MutableHousehold> = listOf(
        zones[0].generateHousehold(id = 1) {
            householdNumber = 1
        },
    )
    val household = households[0]
    val car = household.spawnCar()
    override val persons: List<MutablePerson> = household.generatePersons(
        2,
        spawnLimits = spawnDrivers,
        memberships = mutableListOf(),
        drtMemberships = mutableListOf(),
    )
    val first = persons[0]
    val second = persons[1]

    //    controllableImpedance.apply {
//        difficultAccess(zones[0], zones[0])
//        easyAccess(zones[0], zones[1])
//        difficultAccess(zones[0], zones[2])
//        difficultAccess(zones[1], zones[0])
//        difficultAccess(zones[1], zones[1])
//        difficultAccess(zones[1], zones[2])
//        difficultAccess(zones[2], zones[0])
//        difficultAccess(zones[2], zones[1])
//        difficultAccess(zones[2], zones[2])
//
//    }
    val stateMachine = RecordingStateMachineFactory(NoWriters.personStateMachine)

    fun statesOf(agent: PersonAgent) = stateMachine.of(agent)!!.history
    fun popStatesOf(agent: PersonAgent) = stateMachine.of(agent)!!.let { sm ->
        sm.history.toList().also {
            sm.clearHistory()
        }
    }

    private val builder: BuildAgents = BuildAgents(seed = 1L, stateMachine, behavior)
    val firstAgent: PersonAgent
        get() = first.toAgent(builder)

    val secondAgent: PersonAgent
        get() = second.toAgent(builder)

    val carAgent: PrivateCarAgent
        get() = car.toAgent(builder)
}

operator fun Collection<State>.contains(stateClass: KClass<out StateData>): Boolean = any {
    when {
        it.name == stateClass.simpleName -> true
        else -> false
    }
}

class Synthetic {
    private val scenario = OneHouseholdTwoPersons()

    /**
     * This test checks for car availability within a designated household. The first person will take the car.
     * Thus, the car should not be available for the second person. However, the car should be available as soon as
     * the first person returns home.
     */
    @Test
    @Suppress("LongMethod")
    fun oneHouseholdOneCar() {
        scenario.run {
            first.loadActivityPlan {
                +Triple(LegacyActivityType.HOME, 0, 4)
                +Triple(LegacyActivityType.WORK, 8, 4)
                +Triple(LegacyActivityType.HOME, 16, 4)
            }
            second.loadActivityPlan {
                +Triple(LegacyActivityType.HOME, 0, 5)
                +Triple(LegacyActivityType.WORK, 8, 4)
            }
            val firstPerson = firstAgent.stepper()
            val secondPerson = secondAgent.stepper()

            firstPerson.inspect(1) {
                assert(StartPerson in popStatesOf(firstAgent))
            }
            secondPerson.inspect(1) {
                assert(StartPerson in popStatesOf(secondAgent))
            }

            assertNull(firstAgent.schedule.present)
            assertNull(secondAgent.schedule.present)
            assertEquals(firstAgent.locationBySchedule(), household.location)
            assertEquals(secondAgent.locationBySchedule(), household.location)

            firstPerson.nextStep(1) {
                // it is the event to be processed; lambda is now checked AFTER execute!
                assertIs<FirstActivityMessage>(it.content)
                assert(PerformingActivity in popStatesOf(firstAgent))
            }
            secondPerson.nextStep(1) {
                assertIs<FirstActivityMessage>(it.content)
                assert(PerformingActivity in popStatesOf(secondAgent))
            }

            assertTrue(firstAgent.hasAccessToCar())
            assertTrue(secondAgent.hasAccessToCar())
            assertNotNull(firstAgent.schedule.present)
            assertNotNull(secondAgent.schedule.present)

            firstPerson.nextStep(1, zones[2].point(BIELEFELD), LegacyMode.CAR) {
                assertIs<EndActivityMessage>(it.content)

                val visitedStates = popStatesOf(firstAgent)
                assert(StartingTrip in visitedStates)
                assert(PerformLeg in visitedStates)

                assertEquals(carAgent.driver, firstAgent)
                assertEquals(carAgent.location, household.location)
                assertEquals(firstAgent.locationBySchedule(), household.location)
                assertFalse(secondAgent.hasAccessToCar())
            }

            firstPerson.nextStep(1) {
                assertIs<EndLegMessage>(it.content)

                val states = popStatesOf(firstAgent)
                assert(PerformingActivity in states) { "PerformingActivity is expected in $states" }

                assertEquals(firstAgent.locationBySchedule(), zones[2].point(BIELEFELD))
                assertEquals(carAgent.driver, null)
                assertEquals(carAgent.location, firstAgent.locationBySchedule())
                assertTrue(firstAgent.hasAccessToCar())
                assertFalse(secondAgent.hasAccessToCar())
            }

            firstPerson.nextStep(1, household.location, LegacyMode.CAR) {
                assertIs<EndActivityMessage>(it.content)

                val visitedStates = popStatesOf(firstAgent)
                assert(StartingTrip in visitedStates)
                assert(PerformLeg in visitedStates)
            }

            firstPerson.nextStep(1) {
                assertIs<EndLegMessage>(it.content)
                assert(PerformingActivity in popStatesOf(firstAgent))

                assertTrue(firstAgent.hasAccessToCar())
                assertTrue(secondAgent.hasAccessToCar())
            }

            firstPerson.nextStep(0) {
                assertIs<EndActivityMessage>(it.content)
                assert(FinishedPerson in popStatesOf(firstAgent))
            }
        }
    }
}
