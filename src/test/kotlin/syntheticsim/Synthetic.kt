package syntheticsim

import BIELEFELD
import TestZone
import benchmark.ControllableAttractiveness
import datastructure.Activity
import domain.data.ActivityId
import domain.data.Household
import domain.data.Person
import domain.data.PlannedActivity
import domain.data.ZoneId
import domain.data.getBestCar
import domain.data.locationBySchedule
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.enums.ZoneClassification
import domain.events.CarSelector
import domain.events.EndActivityEvent
import domain.events.EndLegEvent
import domain.events.EventWithScope
import domain.events.InitPersonEvent
import domain.events.ModeScopeDispatcher
import domain.events.PersonBehavior
import domain.events.StartActivityEvent
import domain.events.StartLegEvent
import domain.events.StartTripEvent
import domain.location.LOCATIONUNKNOWN
import domain.location.ZoneLocation
import generateHousehold
import generatePersons
import generateZones
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import point
import spawnCar
import spawnDrivers
import usecases.AttractivenessModel
import usecases.LegacyMode
import usecases.choicemodels.GeneratedHcUtilityFunction
import usecases.choicemodels.LegacyDestinationChoice
import usecases.choicemodels.LegacyModeChoiceModel
import usecases.choicemodels.MakeUtilities
import usecases.choicemodels.TripChoiceSituation
import usecases.legacyChoiceModelModes
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
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun Person.loadActivityPlan(lambda: PlanLoader.() -> Unit) {
    val plan = PlanLoader(this)
    plan.apply(lambda)
    plan.plannedActivities.forEach { addActivity(it) }
}

class PlanLoader(private val person: Person) {
    val plannedActivities = mutableListOf<PlannedActivity>()
    operator fun Triple<ActivityType, Number, Number>.unaryPlus() {
        plannedActivities.add(
            PlannedActivity(
                ActivityId(-1L),
                person,
                first,
                (-1).minutes,
                AbsoluteTime(
                    second.toDouble().toDuration(DurationUnit.HOURS),
                ),
                third.toDouble().toDuration(DurationUnit.HOURS),
                person.random
            )
        )
    }

    var start = AbsoluteTime.START
    operator fun ActivityType.unaryPlus() {
        plannedActivities.add(
            PlannedActivity(
                ActivityId(-1L),
                person,
                this,
                (-1).minutes,
                start,
                4.hours,
                person.random
            ).also { start += 8.hours }
        )
    }
}

fun Person.hasAccessToCar(): Boolean {
    return getBestCar() != null
}

abstract class Scenario(
    val zones: List<TestZone>,
    val impedance: ControllableImpedance = ControllableImpedance(),
    utilGenerator: MakeUtilities = MakeUtilities { a, l, m, h, p ->
        GeneratedHcUtilityFunction(
            a,
            l,
            m,
            h
        )
    }
) {
    val currentAttractivenessModel: ControllableAttractiveness = ControllableAttractiveness(zones)

    abstract val households: List<Household>
    abstract val persons: List<Person>

    // When testing choice models with overridden utility calculation they still require activities for the signature.
    val fakeActivity =
        Activity.fromDuration(zones[0].point(BIELEFELD), (-1).hours.sinceStart, (-1).hours, ActivityType.UNKNOWN)

    val destinationChoice: OverridableDestinationChoiceModel = OverridableDestinationChoiceModel(
        LegacyDestinationChoice(
            impedance,
            currentAttractivenessModel,
            umlands = { loc -> (loc as ZoneLocation).zone.classification == ZoneClassification.OUTLYING_AREA },
            zones.toSet(),
            modes = legacyChoiceModelModes,
        )
    )
    val modeChoice: OverridableModeChoiceModel = OverridableModeChoiceModel(
        LegacyModeChoiceModel(
            attractivenessModel = currentAttractivenessModel,
            modes = legacyChoiceModelModes,
            impedance = impedance,
            utilitiesGenerator = utilGenerator
        )
    )

    // fun <S: ModelExecution<C>, C: PersonContext> S.loadSyntheticPerson() {
    //    context.personRepository.addBuilders(sequenceOf())
    //    addStep(BuildStep(
    //        "finish people",
    //        context.personRepository
    //    ))
    // }

    private val behavior = PersonBehavior(
        destinationChoice = destinationChoice,
        modeChoice = modeChoice,
        impedance,
        ModeScopeDispatcher(mapOf(LegacyMode.CAR.let { it to CarSelector(it) }))
    )

    fun Person.stepper(): EventStepper {
        return EventStepper(InitPersonEvent(this, behavior = behavior), destinationChoice, modeChoice)
    }

    fun EventStepper.stepOverFirstActivity() {
        take(3) // Handle Init, Start and End Activity.
    }
}

val testAttractivenessModel = AttractivenessModel { i, _ ->
    when (i) {
        ZoneId(0L) -> 0.0 // Home zone attractiveness should be 0
        ZoneId(1L) -> 999999.9 // Zone 1 should be the most attractive zone ever
        ZoneId(2L) -> 1.0 // Zone 2 should be barely attractive at all
        else -> throw NoSuchElementException("In this test the IDs should only be 0, 1, 2")
    }
}

class OneHouseholdTwoPersons : Scenario(generateZones(3)) {

    override val households: List<Household> = listOf(
        zones[0].generateHousehold {
            householdNumber = 1
        }
    )
    val household = households[0]
    val car = household.spawnCar()
    override val persons: List<Person> = household.generatePersons(
        2,
        spawnLimits = spawnDrivers,
        membershipsMap = mutableMapOf()
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
            val firstPerson = first.stepper()
            val secondPerson = second.stepper()

            firstPerson.nextStep(1) {
                assertIs<InitPersonEvent>(it)
            }
            secondPerson.nextStep(1) { assertIs<InitPersonEvent>(it) }

            assertNull(first.schedule.present)
            assertNull(second.schedule.present)

            secondPerson.nextStep(1) { assertIs<StartActivityEvent>(it) }

            firstPerson.nextStep(1) { assertIs<StartActivityEvent>(it) }
            assertTrue(first.hasAccessToCar())
            assertTrue(second.hasAccessToCar())
            assertEquals(first.locationBySchedule(), household.location)
            assertEquals(second.locationBySchedule(), household.location)
            assertNotNull(first.schedule.present)
            assertNotNull(second.schedule.present)
            secondPerson.nextStep(1) { assertIs<EndActivityEvent>(it) }
            firstPerson.nextStep(1) { assertIs<EndActivityEvent>(it) }

            firstPerson.nextStep(1, zones[2].point(BIELEFELD), LegacyMode.CAR) { assertIs<StartTripEvent>(it) }
            assertEquals(car.location, household.location)
            assertEquals(car.driver, first)
            assertFalse(second.hasAccessToCar())
            firstPerson.nextStep(1) { assertIs<EventWithScope<StartLegEvent, Person>>(it) }
            assertEquals(first.locationBySchedule(), household.location)
            firstPerson.nextStep(1) { assertIs<EventWithScope<EndLegEvent, Person>>(it) }
            assertEquals(first.locationBySchedule(), zones[2].point(BIELEFELD))
            assertEquals(car.driver, null)
            assertEquals(car.location, first.locationBySchedule())
            assertTrue(first.hasAccessToCar())
            assertFalse(second.hasAccessToCar())

            assertFalse(
                LegacyMode.CAR in modeChoice.filter(
                    TripChoiceSituation(second, LOCATIONUNKNOWN, LOCATIONUNKNOWN),
                    5.hours.sinceStart
                )
            )
            firstPerson.nextStep(1) { assertIs<StartActivityEvent>(it) }
            firstPerson.nextStep(1) { assertIs<EndActivityEvent>(it) }
            firstPerson.nextStep(1, household.location, LegacyMode.CAR) { assertIs<StartTripEvent>(it) }
            firstPerson.nextStep(1) { assertIs<EventWithScope<StartLegEvent, Person>>(it) }
            firstPerson.nextStep(1) { assertIs<EventWithScope<EndLegEvent, Person>>(it) }
            assertTrue(first.hasAccessToCar())
            assertTrue(
                LegacyMode.CAR in modeChoice.filter(
                    TripChoiceSituation(second, LOCATIONUNKNOWN, LOCATIONUNKNOWN),
                    5.hours.sinceStart
                )
            )
            secondPerson.nextStep(1)
            firstPerson.nextStep(1) { assertIs<StartActivityEvent>(it) }
        }
    }
}
