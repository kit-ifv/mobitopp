package edu.kit.ifv.application.syntheticsim

import BIELEFELD
import edu.kit.ifv.application.scenarios.ScenarioContext
import edu.kit.ifv.application.steps.HasAttractivenessModel
import edu.kit.ifv.application.steps.HasImpedance
import edu.kit.ifv.application.steps.HasModeAvailabilityModel
import edu.kit.ifv.core.statemachine.State
import edu.kit.ifv.core.statemachine.builder.StateData
import edu.kit.ifv.core.statemachine.usage.RecordingStateMachineFactory
import edu.kit.ifv.domain.shared.behavior.Attractiveness
import edu.kit.ifv.domain.shared.behavior.AttractivenessModel
import edu.kit.ifv.domain.shared.behavior.ChoiceModelModes
import edu.kit.ifv.domain.shared.behavior.asAttractiveness
import edu.kit.ifv.domain.shared.data.activity.ActivityId
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Activity
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.enums.LegacyActivityType
import edu.kit.ifv.domain.shared.enums.LegacyMode
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.shared.location.zone.MaximalZone
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.domain.simulation.agent.BuildAgents
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.agent.PrivateCarAgent
import edu.kit.ifv.domain.simulation.agent.getBestCarOrNull
import edu.kit.ifv.domain.simulation.agent.locationBySchedule
import edu.kit.ifv.domain.simulation.agent.toAgent
import edu.kit.ifv.domain.simulation.behavior.availability.ModeAvailabilityModel
import edu.kit.ifv.domain.simulation.behavior.availability.defaultAvailabilityModel
import edu.kit.ifv.domain.simulation.behavior.destinationchoice.createLegacyDestinationChoice
import edu.kit.ifv.domain.simulation.behavior.modechoice.createLegacyModeChoice
import edu.kit.ifv.domain.simulation.data.MutablePlannedActivity
import edu.kit.ifv.domain.simulation.data.PlannedActivity
import edu.kit.ifv.domain.simulation.data.household.Household
import edu.kit.ifv.domain.simulation.data.household.MutableHousehold
import edu.kit.ifv.domain.simulation.data.person.MutablePerson
import edu.kit.ifv.domain.simulation.data.person.Person
import edu.kit.ifv.domain.simulation.events.EndActivityMessage
import edu.kit.ifv.domain.simulation.events.EndLegMessage
import edu.kit.ifv.domain.simulation.events.FinishedPerson
import edu.kit.ifv.domain.simulation.events.FirstActivityMessage
import edu.kit.ifv.domain.simulation.events.PerformLeg
import edu.kit.ifv.domain.simulation.events.PerformingActivity
import edu.kit.ifv.domain.simulation.events.StartPerson
import edu.kit.ifv.domain.simulation.events.StartingTrip
import edu.kit.ifv.domain.simulation.events.personStateMachine
import edu.kit.ifv.domain.synthesis.ControllableAttractiveness
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModelImpl
import edu.kit.ifv.mobitopp.discretechoice.models.addFilter
import edu.kit.ifv.utils.units.AbsoluteTime
import edu.kit.ifv.utils.units.sinceStart
import generateHousehold
import generatePersons
import generateZones
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import point
import spawnCar
import spawnDrivers
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
    require(plan.plannedActivities.isNotEmpty()) {
        "ERROR"
    }
    plan.plannedActivities.forEach { this.plannedActivities.add(it) }
}

class PlanLoader(private val person: MutablePerson) {
    val plannedActivities = mutableListOf<PlannedActivity>()

    operator fun Triple<ActivityType, Number, Number>.unaryPlus() {
        person
        plannedActivities.add(
            MutablePlannedActivity(
                id = ActivityId(-1L),
                person = person.id,
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
                person = p.id,
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

abstract class Scenario(val zones: List<MaximalZone>, val impedance: ControllableImpedance = ControllableImpedance()) {
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

    protected val scenarioChoiceModelModes: ChoiceModelModes = ChoiceModelModes(
        car = LegacyMode.CAR,
        passenger = LegacyMode.PASSENGER,
        bike = LegacyMode.BIKE,
        pedestrian = LegacyMode.PEDESTRIAN,
        publicTransport = LegacyMode.PUBLICTRANSPORT,
        bikeSharing = LegacyMode.BIKESHARING,
        ridePooling = LegacyMode.RIDE_POOLING,
        carSharingFree = LegacyMode.CARSHARING_FREE,
        carSharingStation = LegacyMode.CARSHARING_STATION,
        taxi = LegacyMode.TAXI,
        eScooter = LegacyMode.E_SCOOTER,
        options = setOf(
            LegacyMode.CAR,
            LegacyMode.PASSENGER,
            LegacyMode.BIKE,
            LegacyMode.PEDESTRIAN,
            LegacyMode.PUBLICTRANSPORT,
        ),
    )
    val availability = defaultAvailabilityModel(
        scenarioChoiceModelModes,
        LegacyMode.TAXI,
        LegacyMode.E_SCOOTER,
        impedance = impedance,
    )

    fun destinationChoice(
        attractivenessModel: AttractivenessModel,
        impedance: Impedance,
        availabilityModel: ModeAvailabilityModel,
    ): OverridableDestinationChoiceModel = OverridableDestinationChoiceModel(
        createLegacyDestinationChoice(
            impedance,
            attractivenessModel,
            availabilityModel,
        ),
    )

    fun modeChoice(impedance: Impedance): OverridableModeChoiceModel = OverridableModeChoiceModel(
        createLegacyModeChoice(
            impedance,
        ).addFilter(context(impedance) { availability.asResourceAvailabilityFilter() }),
    )

//    protected val behavior
//        get() = PersonBehavior(
//        destinationChoice = destinationChoice.fixed(zones.map { it.centroidLocation }.toSet()),
//        modeChoice = modeChoice.fixed(legacyModeChoice.choices),
//        modes = legacyChoiceModelModes,
//        impedance,
//        attractivityModel = currentAttractivenessModel,
//        availabilityModel = availability,
//        bikeSharingConnectionSelector = availability,
//        drtAvailabilitySelector = availability,
//        spawnDestinationCharacteristics = StandardDestinationImplementation,
//        spawnModeCharacteristics = StandardModeImplementation,
//    )

    context(c: C)
    fun <C> PersonAgent.stepper(
        overridableModeChoiceModel: OverridableModeChoiceModel,
        destinationChoiceModel: OverridableDestinationChoiceModel,
    ): EventStepper where C : HasAttractivenessModel, C : HasImpedance, C : HasModeAvailabilityModel {
        val initEvent = this.init().takeIf { it.size == 1 }!!.take(1)[0]
        return EventStepper(
            initEvent,
            destinationChoiceModel,
            overridableModeChoiceModel,
        )
    }

    fun EventStepper.stepOverFirstActivity() {
        take(3) // Handle Init, Start and End Activity.
    }
}

val testAttractivenessModel = object : AttractivenessModel {

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

    val attractivenessModel: AttractivenessModel = currentAttractivenessModel

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

    // context
    val context =
        ScenarioContext(
            scenarioName = "synthetic",
            attractiveness = attractivenessModel,
            impedance = impedance,
            choiceModelModes = scenarioChoiceModelModes,
            modeAvailability = availability,
            destinationChoiceModel = destinationChoice(
                attractivenessModel,
                impedance,
                availability,
            ).fixed(zones.map { z -> z.centroidLocation }.toSet()),
            modeChoiceModel = modeChoice(impedance).fixed(createLegacyModeChoice(impedance).choices),
        )

    // context(impedance: Impedance, attractivenessModel: AttractivenessModel, availabilityModel: ModeAvailabilityModel)
    val stateMachine = RecordingStateMachineFactory(context.personStateMachine)

    fun statesOf(agent: PersonAgent) = stateMachine.of(agent)!!.history

    fun popStatesOf(agent: PersonAgent) = stateMachine.of(agent)!!.let { sm ->
        sm.history.toList().also {
            sm.clearHistory()
        }
    }

    private val builder: BuildAgents = BuildAgents(seed = 1L, stateMachine)

    val firstAgent: PersonAgent by lazy { first.toAgent(builder) }
    val secondAgent: PersonAgent by lazy { second.toAgent(builder) }
    val carAgent: PrivateCarAgent by lazy { car.toAgent(builder) }
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

            context(context) {
                val firstPerson = firstAgent.stepper(
                    (context.modeChoiceModel as FixedChoiceModelImpl).original as OverridableModeChoiceModel,
                    (context.destinationChoiceModel as FixedChoiceModelImpl).original
                        as OverridableDestinationChoiceModel,
                )
                val secondPerson = secondAgent.stepper(
                    (context.modeChoiceModel as FixedChoiceModelImpl).original as OverridableModeChoiceModel,
                    (context.destinationChoiceModel as FixedChoiceModelImpl).original
                        as OverridableDestinationChoiceModel,
                )

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
}
