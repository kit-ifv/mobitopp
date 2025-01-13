package choicemodels

import BIELEFELD
import TestZone
import benchmark.ControllableAttractiveness
import build
import buildPerson
import datastructure.Activity
import datastructure.Leg
import datastructure.LinkedActivity
import datastructure.Schedule
import datastructure.plans.BlockModel
import datastructure.plans.TrackableModel
import domain.data.CarEngineStatistics
import domain.data.ChargingInfluence
import domain.data.EconomicStatus
import domain.data.Employment
import domain.data.EngineType
import domain.data.Graduation
import domain.data.HouseholdId
import domain.data.MutableHousehold
import domain.data.MutablePerson
import domain.data.MutableSharingProvider
import domain.data.Person
import domain.data.PersonId
import domain.data.PrivateCar
import domain.data.Sex
import domain.data.buildEngine
import domain.data.lastTransportMode
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.enums.MODEUNKOWN
import domain.enums.Mode
import domain.location.Location
import domain.resources.Subscribable
import generateZones
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import point
import spawnCar
import syntheticsim.ControllableImpedance
import syntheticsim.Scenario
import units.CurrencyUnit
import units.DistanceUnit
import units.euros
import units.kilometers
import units.share
import units.toCurrency
import units.toDistance
import usecases.LegacyMode
import usecases.choicemodels.nextFixedActivity
import utils.collections.cartesianProduct
import utils.units.daysSinceStartOfWeek
import utils.units.sinceStart
import java.time.DayOfWeek
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * This is an abstract test that compares two utility functions in a simulation context over a lot of different attributes
 * to ensure that two utility functions produce the same result. The original idea was to test both destination and mode
 * choice, but perhaps this class can extend to other utility functions as well.
 *
 * @param T The type of the first utility function
 * @property comparison the inheriting class should provide an implementation to test the output of the two utility functions
 */
abstract class CompareTwoUtilityFunctions<T : Any> {
    abstract val comparison: TestSimulation.(T, T) -> Unit

    lateinit var a: T
    lateinit var b: T

    open val impedanceOverride: ControllableImpedance? = null
    val zones: List<TestZone> = generateZones(2)
    fun runTest(
        synthesis: TestSynthesis.() -> Unit,
        runtimeChanges: TestSimulation.() -> Unit,
        verification: TestSimulation.() -> Unit
    ) {
        val testRun = Run().runBoth(zones, impedanceOverride, synthesis, runtimeChanges)
        testRun.verification()
        testRun.comparison(a, b)
    }

    fun runTest(synthesis: TestSynthesis.() -> Unit, runtimeChanges: TestSimulation.() -> Unit) {
        runTest(synthesis, runtimeChanges) {}
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 4, 5])
    @DisplayName("Testing different number of cars in the household")
    fun testNumberOfCars(numCars: Int) {
        runTest(
            { },
            {
                setNumOwnedCars(numCars)
                impedance.standardCost = 0.euros
            }
        ) {
            assertEquals(person.household.cars.size, numCars)
        }
    }

    @ParameterizedTest
    @EnumSource
    @DisplayName("Testing all potential next activity types.")
    fun testAllActivityTypes(type: LegacyActivityType) {
        runTest(
            {},
            { nextActivity.type = type }
        )
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 10, 18, 20, 29, 30, 40, 49, 50, 60, 69, 70, 100, 121])
    @DisplayName("🎂 Testing different agent ages 👶👵")
    fun testAges(age: Int) {
        runTest(
            { setAge(age) },
            {}
        ) {
            assertEquals(person.age, age)
        }
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, 1.0, 10.0, 20.0, 999.9, 1000.0, 1337.0, 999.0, -10.0])
    @DisplayName("Testing different Travel Times ⏱️🏠")
    fun testTravelTimes(travelTime: Double) {
        val travelDuration = travelTime.toDuration(DurationUnit.MINUTES)
        runTest(
            {},
            {
                impedance.setTime(origin, destination, travelDuration)
            }
        ) {
            LegacyMode.entries.forEach {
                assertEquals(
                    impedance.duration(
                        origin.point(BIELEFELD),
                        destination.point(BIELEFELD),
                        it,
                        0.hours.sinceStart
                    ),
                    travelDuration
                )
            }
            assertNull(person.nextFixedActivity())
        }
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, 1.0, 10.0, 999.9, 1000.0, 1337.0, 999.0, -10.0])
    @DisplayName("Different Fixed Travel Times ⏱️🏢🏠")
    fun testFixedTravelTimes(travelTime: Double) {
        val travelDuration = travelTime.toDuration(DurationUnit.MINUTES)
        runTest(
            {},
            {
                impedance.setTime(destination, origin, travelDuration)
                addFixedDestination(originLocation)
            }
        ) {
            LegacyMode.entries.forEach {
                assertEquals(
                    impedance.duration(
                        destination.point(BIELEFELD),
                        origin.point(BIELEFELD),
                        it,
                        0.hours.sinceStart
                    ),
                    travelDuration
                )
            }
            assertNotNull(person.nextFixedActivity())
        }
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, 1.0, 10.0, 999.9, 1000.0, 1337.0, 999.0, -10.0])
    @DisplayName("🚗💸 Travel Cost Validation: Testing with Various Values 🧮")
    fun testTravelCost(travelCost: Double) {
        val money = travelCost.toCurrency(CurrencyUnit.EUROS)
        runTest(
            {},
            {
                impedance.setCost(origin, destination, money)
            }
        ) {
            LegacyMode.entries.forEach {
                assertEquals(
                    impedance.cost(
                        origin.point(BIELEFELD),
                        destination.point(BIELEFELD),
                        it,
                        0.hours.sinceStart
                    ),
                    money
                )
            }
            assertNull(person.nextFixedActivity())
        }
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, 1.0, 10.0, 999.9, 1000.0, 1337.0, 999.0, -10.0])
    @DisplayName("Different Fixed Travel Times 🏢\uD83D\uDE97\uD83D\uDCB8")
    fun testFixedTravelCost(travelCost: Double) {
        val money = travelCost.toCurrency(CurrencyUnit.EUROS)
        runTest(
            {},
            {
                impedance.setCost(destination, origin, money)
                addFixedDestination(originLocation)
            }
        ) {
            LegacyMode.entries.forEach {
                assertEquals(
                    impedance.cost(
                        destination.point(BIELEFELD),
                        origin.point(BIELEFELD),
                        it,
                        0.hours.sinceStart
                    ),
                    money
                )
            }
            assertNotNull(person.nextFixedActivity())
        }
    }

    @ParameterizedTest
    @ValueSource(doubles = [-0.1, 0.0, 0.1, 1.0, 0.9, 1.1, 2.0, 2.1, 10.0, 999.0, 1337.0, -10.0])
    @DisplayName("Testing different travel distances")
    fun testTravelDistances(travelDistance: Double) {
        val travelDistanceD = travelDistance.toDistance(DistanceUnit.KILOMETERS)
        runTest(
            {},
            { impedance.setDistance(origin, destination, travelDistanceD) }
        )
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    @DisplayName("Testing different availabilities of a commuter ticket")
    fun testCommuterTickets(hasTicket: Boolean) {
        runTest(
            { setCommuterTicket(hasTicket) },
            {}
        )
    }

    @ParameterizedTest
    @EnumSource
    @DisplayName("Testing utility calculation with different previous modes")
    fun testPreviousModes(mode: LegacyMode) {
        runTest(
            {},
            {
                previousLeg.transportType = mode
            }
        ) {
            person.schedule.run {
                assertEquals(this.past, listOf(this@runTest.startActivity, this@runTest.previousLeg))
                assertNull(this.present)
            }
            assertEquals(person.lastTransportMode(previousActivity), mode)
        }
    }

    @ParameterizedTest
    @EnumSource
    @DisplayName("Testing different impedance (cost, duration, distance) for specifically one target mode")
    fun testSingularImpedanceDiscrepancy(mode: LegacyMode) {
        runTest(
            {},
            {
                impedance.setTime(mode, origin, destination, 999.minutes)
                impedance.setCost(mode, origin, destination, 999.euros)
                impedance.setDistance(mode, origin, destination, 999.kilometers)
            }
        )
    }

    @ParameterizedTest
    @EnumSource(names = ["WORK", "EDUCATION"])
    @DisplayName("Testing different fixed destination types")
    fun testFixedDestinations(type: LegacyActivityType) {
        lateinit var fixedActivity: Activity
        runTest(
            {},
            {
                impedance.setTime(destination, origin, 42.minutes)
                fixedActivity = addFixedDestination(originLocation, type)
            }
        ) {
            person.schedule.run {
                assertEquals(this.past, listOf(this@runTest.startActivity, this@runTest.previousLeg))
                assertNull(this.present)
            }
            assertNotNull(person.nextFixedActivity())
            assertEquals(person.nextFixedActivity()!!, fixedActivity)
        }
    }

    @ParameterizedTest
    @EnumSource
    @DisplayName("Testing utility functions for different sex")
    fun testGender(target: Sex) {
        runTest(
            { setGender(target) },
            {}
        ) {
            assertEquals(person.sex, target)
        }
    }

    @ParameterizedTest
    @EnumSource
    @DisplayName("Testing all different economic states")
    fun testEconomicStatus(target: EconomicStatus) {
        runTest(
            { setEconomicStatus(target) },
            {}
        ) {
            assertEquals(person.household.economicStatus, target)
        }
    }

    @ParameterizedTest
    @EnumSource
    @DisplayName("Testing all different engine types")
    fun engineTypes(target: EngineType) {
        lateinit var car: PrivateCar
        runTest(
            {},
            {
                car = spawnCarWithEngine(target)
            }
        ) {
            val expectedCar = person.household.cars.filter { it.mainUser == person }
            assertEquals(target, car.engine.type)
            assertEquals(1, expectedCar.size)
            assertEquals(car, expectedCar[0])
        }
    }

    @ParameterizedTest
    @MethodSource("provideParkingPressureArguments")
    @DisplayName("🚗🔍 Parking Pressure: Testing different Availability 📊")
    fun parkingPressure(arguments: Pair<Double, Int>) {
        runTest({
        }, {
            setAttractiveness {
                set(destination.id, arguments.first)
                destination.parkingPlaces = arguments.second
            }
        }) {
            assertEquals(attractiveness.attractivenessFor(destination.id, LegacyActivityType.WORK), arguments.first)
            assertEquals(destination.parkingPlaces, arguments.second)
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    @DisplayName("Testing utility calculation for different moia memberships")
    fun moiaMembership(target: Boolean) {
        val provider = MutableSharingProvider {
            name = "Moia_an_member"
            mode = MODEUNKOWN
        }

        runTest(
            { addMembership(provider, target) },
            {}
        ) {
            assertTrue(person.memberships.containsKey(provider))
            assertEquals(person.memberships.getValue(provider), target)
        }
    }

    @ParameterizedTest
    @MethodSource("provideArguments")
    fun differentActivityTimes(arguments: Pair<DayOfWeek, Duration>) {
        val targetTime = arguments.first.daysSinceStartOfWeek() + arguments.second
        runTest(
            { },
            {
                previousActivity.endTime = targetTime.sinceStart
            }
        ) {
            assertEquals(previousActivity.endTime, targetTime.sinceStart)
            assertTrue(leg.startTime >= previousActivity.endTime)
            assertTrue(leg.endTime <= nextActivity.startTime)
        }
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 4, 5, 7, 8, 9, 10])
    fun testDifferentTimeBlocks(upUntil: Int) {
        runTest(
            { },
            {
                impedance.setTime(origin, destination, 0.hours.sinceStart..upUntil.hours.sinceStart, 999.hours)
                impedance.setCost(origin, destination, 0.hours.sinceStart..upUntil.hours.sinceStart, 999.euros)
            }
        ) {
            assertEquals(
                impedance.cost(
                    originLocation,
                    destinationLocation,
                    LegacyMode.PASSENGER,
                    (upUntil.hours - 1.minutes).sinceStart
                ),
                999.euros
            )
            assertEquals(
                impedance.cost(originLocation, destinationLocation, LegacyMode.PASSENGER, (upUntil).hours.sinceStart),
                1.euros
            )
            assertEquals(
                impedance.duration(
                    originLocation,
                    destinationLocation,
                    LegacyMode.PASSENGER,
                    (upUntil.hours - 1.minutes).sinceStart
                ),
                999.hours
            )
            assertEquals(
                impedance.duration(
                    originLocation,
                    destinationLocation,
                    LegacyMode.PASSENGER,
                    (upUntil).hours.sinceStart
                ),
                10.minutes
            )
        }
    }
    companion object {
        @JvmStatic
        fun provideArguments(): List<Pair<DayOfWeek, Duration>> {
            val weekdays = DayOfWeek.entries
            val timeSteps = listOf(0.0, 0.1, 4.9, 5.0, 5.1, 16.9, 17.0, 17.1, 20.9, 21.0, 21.1).map { it.hours }
            return weekdays.cartesianProduct(timeSteps)
        }

        @JvmStatic
        fun provideParkingPressureArguments(): List<Pair<Double, Int>> {
            return listOf(
                10.0 to 1,
                9999999.9 to 3,
                10.0 to 0,
                0.0 to 0,
                0.0000001 to 0,

            )
        }
    }
}

/**
 * A [Run] is performing both a [TestSynthesis] which runs the steps to build the test simulation. (Create Zones, create
 * the household, the person) and the steps to change relevant attributes at the synthesis level. In addition it contains a
 * [TestSimulation] which changes runtime
 * * @constructor Create empty Run
 */
class Run {

    fun runBoth(
        zones: List<TestZone>,
        impedance: ControllableImpedance? = null,
        lambda: TestSynthesis.() -> Unit,
        kappa: TestSimulation.() -> Unit
    ): TestSimulation {
        val s = if (impedance != null) TestSynthesis(zones, impedance) else TestSynthesis(zones)
        s.lambda()
        val d = s.run()
        d.kappa()
        return d
    }
}

/**
 * Test synthesis allows to build a small synthetic simulation while still holding access to the simulation attributes
 * to test conditions.
 *
 * This test scenario creates one household and one person. In the test synthesis step the builders are still valid and
 * active, so attributes of the household and person can be influenced here.
 *
 * @param zones pass in the zones in order to hold an external reference instead of creating the zones in the scenario
 * @param impedance pass in an optional impedance if you want to hold an external reference.
 */
class TestSynthesis(zones: List<TestZone>, impedance: ControllableImpedance = ControllableImpedance()) : Scenario(
    zones,
    impedance = impedance
) {
    override val households: List<MutableHousehold> by lazy {
        listOf(zones[0].build(hBuilder))
    }
    var hBuilder: () -> MutableHousehold = {
        MutableHousehold(id = HouseholdId(1), seed = 42L) {
            surveyYear = 2024
            domCode = 1
            type = 1
            incomePerMonth = 0.euros
            economicStatus = builderEcoStatus
//            random = Random(1)
            householdNumber = 1L
        }
    }
    private var builderEcoStatus = EconomicStatus.MIDDLE

    var pBuilder: (Long, MutableHousehold) -> MutablePerson = { id, hh ->
        MutablePerson(id = PersonId(id), household = hh, seed = 42L) {
            eMobilityAcceptance = 0.share()
            chargingInfluence = ChargingInfluence.NEVER
//            random = Random(1)
            age = builderAge
            employment = Employment.NONE
            sex = builderGender
            graduation = Graduation.UNDEFINED
            income = 0.euros
            hasBike = false
            hasCommuterTicket = builderCommuterTicket
            hasLicense = false
            memberships.putAll(builderMemberships)
        }
    }
    private var builderAge = 20
    private var builderCommuterTicket = false
    private val builderMemberships = mutableMapOf<Subscribable<Person>, Boolean>()
    private var builderGender = Sex.MALE

    override val persons: List<Person> = emptyList()

    fun setAge(target: Int) {
//        pBuilder = { i, h -> pBuilder(i, h).also { it.age = target } }
        builderAge = target
    }

    fun setCommuterTicket(target: Boolean) {
        builderCommuterTicket = target
//        pBuilder = { i, h -> pBuilder(i, h).also { it.hasCommuterTicket = target } }
    }

    fun addMembership(key: Subscribable<Person>, target: Boolean) {
        builderMemberships[key] = target
//        pBuilder = { i, h -> pBuilder(i, h).also { it.memberships[key] = target } }
    }

    fun setGender(target: Sex) {
        builderGender = target
//        pBuilder = { i, h -> pBuilder(i, h).also { it.sex = target } }
    }

    fun setEconomicStatus(target: EconomicStatus) {
        builderEcoStatus = target
//        hBuilder = { -> hBuilder().also { it.economicStatus = target } }
    }

    fun run(): TestSimulation {
        return TestSimulation(
            households[0].buildPerson(pBuilder),
            zones[0],
            zones[1],
            impedance,
            currentAttractivenessModel
        )
    }
}

/**
 * The Test simulation holds the runtime attributes which have to be assigned after the synthesis of the household and
 * person. Such as origin, destination of activities is assigned in this step.
 *
 * @property person the target person for the test
 * @property origin the origin zone
 * @property destination the destination zone
 * @property impedance hold a reference to the impedance to be able to induce changes and assert invariants.
 * @property attractiveness hold a reference to the attractiveness to induce changes and assert invariants.
 */
class TestSimulation(
    val person: MutablePerson,
    val origin: TestZone,
    val destination: TestZone,
    val impedance: ControllableImpedance,
    val attractiveness: ControllableAttractiveness
) {

    init {
        person.schedule = Schedule(TrackableModel(BlockModel()))
    }
    val originLocation = origin.point(BIELEFELD)
    val destinationLocation = destination.point(BIELEFELD)

    var availableModes: Set<Mode> = LegacyMode.entries.toSet()

    // Using !! as I know that the activities should fit into the schedule and thus return the linked activity
    val startActivity: LinkedActivity =
        person.schedule.add(Activity.fromDuration(originLocation, 0.hours.sinceStart, 4.hours))!!
    val previousLeg: Leg = Leg.fromDuration(4.hours.sinceStart, 10.minutes, originLocation, originLocation)
    val previousActivity = person.schedule.add(Activity.fromDuration(originLocation, 5.hours.sinceStart, 3.hours))!!
    val leg: Leg = Leg.fromDuration(8.hours.sinceStart, 10.minutes, originLocation, destinationLocation)
    val nextActivity = person.schedule.add(Activity.fromDuration(destinationLocation, 9.hours.sinceStart, 3.hours))!!

    init {
        person.schedule.add(previousLeg)
        person.schedule.add(leg)
        // Take 4 steps in the schedule to move one activity and leg in the past
        (0..<4).forEach { _ ->
            person.schedule.step()
        }
    }

    /**
     * Spawn car with a specific [EngineType]
     *
     * @param target the [EngineType]
     * @return a reference to the car for assertions.
     */
    fun spawnCarWithEngine(target: EngineType): PrivateCar {
        return person.household.spawnCar {
            engine = CarEngineStatistics().buildEngine(segment, target)
            mainUser = person
        }
    }

    /**
     * Induce changes on the [ControllableAttractiveness] object.
     *
     * @param lambda a function to change the attractiveness
     * @receiver the Controllable attractiveness reference held in this class
     */
    fun setAttractiveness(lambda: ControllableAttractiveness.() -> Unit) {
        attractiveness.lambda()
    }

    /**
     * Spawns a [target] number of [EngineType.COMBUSTION] cars in the household.
     *
     * @param target the number of desired cars.
     */
    fun setNumOwnedCars(target: Int) {
        (0..<target).forEach { _ ->
            person.household.spawnCar()
        }
    }

    /**
     * Add a fixed destination activity to the schedule of the agent.
     *
     * @param at the location where the activity should reside
     * @param type the activity type.
     * @return
     */
    fun addFixedDestination(at: Location, type: ActivityType = LegacyActivityType.WORK): LinkedActivity {
        val activity = Activity.fromDuration(at, 14.hours.sinceStart, 3.hours, type)
        // We assume that the activity fits into the schedule by design of the test thus !!
        return person.schedule.addWithPrecedingLeg(activity)!!
    }
}
