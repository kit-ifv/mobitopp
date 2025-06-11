package application.syntheticsim

import BIELEFELD
import TestZone
import domain.shared.behavior.AttractivenessModel
import domain.shared.enums.ActivityType
import domain.simulation.agent.BuildAgents
import domain.simulation.agent.PersonAgent
import domain.simulation.agent.PrivateCarAgent
import domain.simulation.agent.getBestCar
import domain.simulation.agent.locationBySchedule
import domain.simulation.agent.toAgent
import domain.simulation.behavior.ChoiceModelPurposes
import domain.simulation.behavior.LegacyActivityType
import domain.simulation.behavior.LegacyMode
import domain.simulation.behavior.SharingAvailabilityFilter
import domain.simulation.behavior.legacyChoiceModelModes
import domain.simulation.behavior.legacyChoiceModelPurposes
import domain.simulation.behavior.legacyDestinationChoice
import domain.simulation.behavior.legacyModeChoice
import domain.simulation.events.CarSelector
import domain.simulation.events.EndActivityEvent
import domain.simulation.events.EndLegEvent
import domain.simulation.events.EventWithScope
import domain.simulation.events.InitPersonEvent
import domain.simulation.events.ModeScopeDispatcher
import domain.simulation.events.PersonBehavior
import domain.simulation.events.StartActivityEvent
import domain.simulation.events.StartLegEvent
import domain.simulation.events.StartTripEvent
import domain.simulation.schedule.Activity
import domain.synthesis.ControllableAttractiveness
import domain.synthesis.data.ActivityId
import domain.synthesis.data.Household
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.MutablePlannedActivity
import domain.synthesis.data.Person
import domain.synthesis.data.PlannedActivity
import domain.synthesis.data.ZoneId
import generateHousehold
import generatePersons
import generateZones
import modeling.models.addFilter
import modeling.models.fixed
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import point
import spawnCar
import spawnDrivers
import utils.units.AbsoluteTime
import utils.units.sinceStart
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

fun MutablePerson.loadActivityPlan(lambda: PlanLoader.() -> Unit) {
    val plan = PlanLoader(this)
    plan.apply(lambda)
//    plan.plannedActivities.forEach { addActivity(it) }
}

class PlanLoader(private val person: MutablePerson) {
    val plannedActivities = mutableListOf<PlannedActivity>()

    operator fun Triple<ActivityType, Number, Number>.unaryPlus() {
        val p = person
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
            }
        )
    }

    var start = AbsoluteTime.START
    operator fun ActivityType.unaryPlus() {
        val p = person

        plannedActivities.add(

            MutablePlannedActivity(
                ActivityId(-1L),
                person = p,
                seed = 42L
            ) {
                activityType = this@unaryPlus
                observedTripDuration = (-1).minutes
                startTime = start
                duration = 4.hours
            }.also { start += 8.hours }

        )
    }
}

fun PersonAgent.hasAccessToCar(): Boolean {
    return getBestCar() != null
}

abstract class Scenario(
    val zones: List<TestZone>,
    val impedance: ControllableImpedance = ControllableImpedance(),
//    utilGenerator: MakeUtilities = MakeUtilities { a, l, m, h, _ ->
//        GeneratedHcUtilityFunction(
//            a,
//            l,
//            m,
//            legacyChoiceModelPurposes,
//            h
//        )
//    }
) {
    val currentAttractivenessModel: ControllableAttractiveness = ControllableAttractiveness(zones)

    abstract val households: List<Household>
    abstract val persons: List<Person>

    // When testing choice models with overridden utility calculation they still require activities for the signature.
    val fakeActivity =
        Activity.fromDuration(zones[0].point(BIELEFELD), (-1).hours.sinceStart, (-1).hours, ActivityType.UNKNOWN)

    val availability = SharingAvailabilityFilter(
        legacyChoiceModelModes,
        emptySet(),
        emptyMap(),
        impedance
    )

    val destinationChoice: OverridableDestinationChoiceModel = OverridableDestinationChoiceModel(
        legacyDestinationChoice
    )
    val modeChoice: OverridableModeChoiceModel = OverridableModeChoiceModel(
        legacyModeChoice.addFilter(availability)
    )

    // fun <S: ModelExecution<C>, C: PersonContext> S.loadSyntheticPerson() {
    //    context.personRepository.addBuilders(sequenceOf())
    //    addStep(BuildStep(
    //        "finish people",
    //        context.personRepository
    //    ))
    // }

    private val behavior = PersonBehavior(
        destinationChoice = destinationChoice.fixed(zones.map { it.centroid }.toSet()),
        modeChoice = modeChoice.fixed(legacyModeChoice.choices),
        impedance,
        ModeScopeDispatcher(mapOf(LegacyMode.CAR.let { it to CarSelector(it) })),
        attractivityModel = currentAttractivenessModel,
        availabilityModel = availability
    )

    fun PersonAgent.stepper(): EventStepper {
        return EventStepper(InitPersonEvent(this, behavior = behavior), destinationChoice, modeChoice)
    }

    fun EventStepper.stepOverFirstActivity() {
        take(3) // Handle Init, Start and End Activity.
    }
}

val testAttractivenessModel = object : AttractivenessModel {

    override val purposes: ChoiceModelPurposes = legacyChoiceModelPurposes

    override fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Double =
        when (zone) {
            ZoneId(0L) -> 0.0 // Home zone attractiveness should be 0
            ZoneId(1L) -> 999999.9 // Zone 1 should be the most attractive zone ever
            ZoneId(2L) -> 1.0 // Zone 2 should be barely attractive at all
            else -> throw NoSuchElementException("In this test the IDs should only be 0, 1, 2")
        }
}

class OneHouseholdTwoPersons : Scenario(generateZones(3)) {

    override val households: List<MutableHousehold> = listOf(
        zones[0].generateHousehold(id = 1) {
            householdNumber = 1
        }
    )
    val household = households[0]
    val car = household.spawnCar()
    override val persons: List<MutablePerson> = household.generatePersons(
        2,
        spawnLimits = spawnDrivers,
        memberships = mutableListOf()
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

    private val builder: BuildAgents = BuildAgents(seed = 1L)
    val firstAgent: PersonAgent
        get() = first.toAgent(builder)

    val secondAgent: PersonAgent
        get() = second.toAgent(builder)

    val carAgent: PrivateCarAgent
        get() = car.toAgent(builder)
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

            firstPerson.nextStep(1) { assertIs<InitPersonEvent>(it) }
            secondPerson.nextStep(1) { assertIs<InitPersonEvent>(it) }

            assertNull(firstAgent.schedule.present)
            assertNull(secondAgent.schedule.present)

            secondPerson.nextStep(1) { assertIs<StartActivityEvent>(it) }
            firstPerson.nextStep(1) { assertIs<StartActivityEvent>(it) }

            assertTrue(firstAgent.hasAccessToCar())
            assertTrue(secondAgent.hasAccessToCar())

            assertEquals(firstAgent.locationBySchedule(), household.location)
            assertEquals(secondAgent.locationBySchedule(), household.location)
            assertNotNull(firstAgent.schedule.present)
            assertNotNull(secondAgent.schedule.present)
            secondPerson.nextStep(1) { assertIs<EndActivityEvent>(it) }
            firstPerson.nextStep(1) { assertIs<EndActivityEvent>(it) }

            firstPerson.nextStep(1, zones[2].point(BIELEFELD), LegacyMode.CAR) { assertIs<StartTripEvent>(it) }
            assertEquals(carAgent.location, household.location)
            assertEquals(carAgent.driver, firstAgent)
            assertFalse(secondAgent.hasAccessToCar())
            firstPerson.nextStep(1) { assertIs<EventWithScope<StartLegEvent, PersonAgent>>(it) }
            assertEquals(firstAgent.locationBySchedule(), household.location)
            firstPerson.nextStep(1) { assertIs<EventWithScope<EndLegEvent, PersonAgent>>(it) }
            assertEquals(firstAgent.locationBySchedule(), zones[2].point(BIELEFELD))
            assertEquals(carAgent.driver, null)
            assertEquals(carAgent.location, firstAgent.locationBySchedule())
            assertTrue(firstAgent.hasAccessToCar())
            assertFalse(secondAgent.hasAccessToCar())

//            assertFalse(
//                LegacyMode.CAR in availability.filter(
//                    ModeChoiceAlternative(
//                        person = second,
//                        time = 5.hours.sinceStart,
//                        origin = LOCATIONUNKNOWN,
//                        destination = LOCATIONUNKNOWN,
//                        ),
//
//
//                )
//            )
            firstPerson.nextStep(1) { assertIs<StartActivityEvent>(it) }
            firstPerson.nextStep(1) { assertIs<EndActivityEvent>(it) }
            firstPerson.nextStep(1, household.location, LegacyMode.CAR) { assertIs<StartTripEvent>(it) }
            firstPerson.nextStep(1) { assertIs<EventWithScope<StartLegEvent, PersonAgent>>(it) }
            firstPerson.nextStep(1) { assertIs<EventWithScope<EndLegEvent, PersonAgent>>(it) }
            assertTrue(firstAgent.hasAccessToCar())
//            assertTrue(
//                LegacyMode.CAR in modeChoice.filter(
//                    DestinationAlternative(second, LOCATIONUNKNOWN, LOCATIONUNKNOWN),
//                    5.hours.sinceStart
//                )
//            )
            secondPerson.nextStep(1)
            firstPerson.nextStep(1) { assertIs<StartActivityEvent>(it) }
        }
    }
}
