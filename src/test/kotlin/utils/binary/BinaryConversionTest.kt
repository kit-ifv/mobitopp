package utils.binary

import TEST_ZONE
import domain.data.CarEngineStatistics
import domain.data.CarSegment
import domain.data.EconomicStatus
import domain.data.Employment
import domain.data.EngineType
import domain.data.Graduation
import domain.data.Sex
import domain.data.buildEngine
import domain.enums.Regiostar17
import generateHousehold
import generatePerson
import generatePlannedActivity
import modeling.steps.Run
import org.junit.jupiter.api.Test
import spawnCar
import units.euros
import usecases.LegacyActivityType
import usecases.LegacyMode
import usecases.steps.ProjectContext
import usecases.steps.legacyData.loadHouseholds
import usecases.steps.legacyData.loadZones
import utils.units.sinceStart
import java.io.File
import kotlin.io.path.Path
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes


class BinaryConversionTest {

    private val ROOT_FS = "\\\\ifv-fs\\Forschung\\Projekte_intern\\mobitopp\\Output"

    private val rootRastatt = File("$ROOT_FS\\logiktram_rastatt_long-term-module\\rastatt")

    @Test
    fun householdTest() {
        Run {
            ProjectContext(
                scenarioName = "testSteps",
                areaTypeCodes = Regiostar17,
                demandFolder = rootRastatt,
                economicalStatusCodes = EconomicStatus,
                simulationSeed = 42,
                modes = LegacyMode,
            )
        }.steps {
            loadZones()
            loadHouseholds()
            writeZonesBinary(Path("src/test/resources/binaryZone.bin"))
            writeHouseholdBinary(Path("src/test/resources/binaryHousehold.bin"))
        }
    }

    private val path = Path("src/test/resources/temp.bin")

    @BeforeTest
    fun setup() {

    }

    @AfterTest
    fun removeTemporaryFile() {

    }

    @Test
    fun testZones() {

        val testZone = TEST_ZONE
        val writer = BinaryZoneWriter()
        writer.toBinary(path, listOf(testZone, testZone))

        val zones = BinaryZoneReader(1, Regiostar17).fromBinary(path)

        zones[0].let {
            assertEquals(it.visumId, testZone.visumId)
            assertEquals(it.name, testZone.name)
            assertEquals(it.classification, testZone.classification)
            assertEquals(it.parkingPlaces, testZone.parkingPlaces)
            assertEquals(it.relief, testZone.relief)
        }
        zones[1].let {
            assertEquals(it.visumId, testZone.visumId)
            assertEquals(it.name, testZone.name)
            assertEquals(it.classification, testZone.classification)
            assertEquals(it.parkingPlaces, testZone.parkingPlaces)
            assertEquals(it.relief, testZone.relief)
        }

    }

    private val zone = TEST_ZONE
    private val hh1 = zone.generateHousehold(1L) {
        householdNumber = 2
        type = 2
        domCode = 42
        surveyYear = 12025
        economicStatus = EconomicStatus.MIDDLE
        incomePerMonth = 2.euros
    }
    private val hh2 = zone.generateHousehold(2L) {
        householdNumber = 3
        type = 4
        domCode = 1337
        surveyYear = 0
        economicStatus = EconomicStatus.VERY_HIGH
        incomePerMonth = 1.euros

    }
    private val p1 = hh1.generatePerson(1L) {
        age = 20
        employment = Employment.INFANT
        sex = Sex.FEMALE
        income = 1.euros
        graduation = Graduation.OTHER
        hasBike = false
        hasLicense = false
        hasCommuterTicket = false

    }

    private val p2 = hh2.generatePerson(2L) {
        age = 3
        employment = Employment.RETIRED
        sex = Sex.MALE
        income = 2.euros
        graduation = Graduation.HIGH_SCHOOL_GRADUATE
        hasBike = true
        hasLicense = true
        hasCommuterTicket = true

    }

    @Test
    fun testHousehold() {
        val writer = BinaryHouseholdWriter()
        val reader = BinaryHouseholdReader({ zone }, 1L)
        writer.toBinary(path, listOf(hh1, hh2))
        val households = reader.fromBinary(path)
        households[0].let {
            assertEquals(it.householdNumber, hh1.householdNumber)
            assertEquals(it.name, hh1.name)
            assertEquals(it.type, hh1.type)
            assertEquals(it.domCode, hh1.domCode)
            assertEquals(it.surveyYear, hh1.surveyYear)
            assertEquals(it.economicStatus, hh1.economicStatus)
            assertEquals(it.incomePerMonth, hh1.incomePerMonth)
            assertEquals(hh1.location, it.location)


        }

        households[1].let {
            assertEquals(it.householdNumber, hh2.householdNumber)
            assertEquals(it.name, hh2.name)
            assertEquals(it.type, hh2.type)
            assertEquals(it.domCode, hh2.domCode)
            assertEquals(it.surveyYear, hh2.surveyYear)
            assertEquals(it.economicStatus, hh2.economicStatus)
            assertEquals(it.incomePerMonth, hh2.incomePerMonth)
            assertEquals(it.location, hh2.location)
        }
    }

    @Test
    fun testPerson() {
        val map = listOf(hh1, hh2).associateBy { it.id }
        val reader = BinaryPersonReader(map::getValue, 1)
        val writer = BinaryPersonWriter()

        writer.toBinary(path, listOf(p1, p2))
        val persons = reader.fromBinary(path)
        persons[0].let {
            assertEquals(it.age, p1.age)
            assertEquals(it.employment, p1.employment)
            assertEquals(it.graduation, p1.graduation)
            assertEquals(it.hasBike, p1.hasBike)
            assertEquals(it.hasLicense, p1.hasLicense)
            assertEquals(it.hasCommuterTicket, p1.hasCommuterTicket)
            assertEquals(it.sex, p1.sex)

        }

        persons[1].let {
            assertEquals(it.age, p2.age)
            assertEquals(it.employment, p2.employment)
            assertEquals(it.graduation, p2.graduation)
            assertEquals(it.hasBike, p2.hasBike)
            assertEquals(it.hasLicense, p2.hasLicense)
            assertEquals(it.hasCommuterTicket, p2.hasCommuterTicket)
            assertEquals(it.sex, p2.sex)

        }
    }
    private val act1 = p1.generatePlannedActivity(1L) {
        this.startTime = 1.hours.sinceStart
        this.duration = 1.hours
        this.activityType = LegacyActivityType.HOME
        this.observedTripDuration = 30.minutes
    }

    private val act2 = p2.generatePlannedActivity(2L) {
        this.startTime = 3.hours.sinceStart
        this.duration = 4.hours
        this.activityType = LegacyActivityType.SHOPPING
        this.observedTripDuration = 90.minutes
    }
    @Test
    fun testActivities() {
        val map = listOf(p1, p2).associateBy { it.id }
        val reader = BinaryActivityReader(LegacyActivityType, map::getValue, 1L)
        val writer = BinaryActivityWriter()
        writer.toBinary(path, listOf(act1, act2))
        val activities = reader.fromBinary(path)
        activities[0].let {
            assertEquals(it.person, act1.person)
            assertEquals(it.activityType, act1.activityType)
            assertEquals(it.id, act1.id)
            assertEquals(it.startTime, act1.startTime)
            assertEquals(it.duration, act1.duration)
            assertEquals(it.observedTripDuration, act1.observedTripDuration)
        }

        activities[1].let {
            assertEquals(it.person, act2.person)
            assertEquals(it.activityType, act2.activityType)
            assertEquals(it.id, act2.id)
            assertEquals(it.startTime, act2.startTime)
            assertEquals(it.duration, act2.duration)
            assertEquals(it.observedTripDuration, act2.observedTripDuration)
        }
    }

    private val car1 = hh1.spawnCar {
        this.seats = 4
        this.segment = CarSegment.MIDSIZE
        this.engine = CarEngineStatistics().buildEngine(this.segment, EngineType.ELECTRIC)
    }
    private val car2 = hh2.spawnCar {
        this.seats = 9
        this.segment = CarSegment.SMALL
        this.engine = CarEngineStatistics().buildEngine(this.segment, EngineType.HYBRID)
    }
    @Test
    fun testCars() {
        val hhMap = listOf(hh1, hh2).associateBy { it.id }
        val personMap = listOf(p1, p2).associateBy { it.id }
        val zoneMap = listOf(zone).associateBy { it.id }
        val reader = BinaryCarReader(hhMap::getValue, personMap::getValue, zoneMap::getValue)
        val writer = BinaryCarWriter()

        writer.toBinary(path, listOf(car1, car2))
        val cars = reader.fromBinary(path)

        cars[0].let {
            assertEquals(it.seats, car1.seats)
            assertEquals(it.location, car1.location)
            assertEquals(it.engine.type, car1.engine.type)
            assertEquals(it.engine.range, car1.engine.range)
            assertEquals(it.segment, car1.segment)
        }
        cars[1].let{
            assertEquals(it.seats, car2.seats)
            assertEquals(it.location, car2.location)
            assertEquals(it.engine.type, car2.engine.type)
            assertEquals(it.engine.range, car2.engine.range)
            assertEquals(it.segment, car2.segment)
        }
    }

}