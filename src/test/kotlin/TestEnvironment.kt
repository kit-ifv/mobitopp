import datastructure.Activity
import datastructure.RawActivity
import datastructure.Schedule
import datastructure.plans.BlockModel
import datastructure.plans.TrackableModel
import domain.data.ActivityId
import domain.data.CarSegment
import domain.data.ChargingInfluence
import domain.data.DefaultHouseholdBuilder
import domain.data.EconomicStatus
import domain.data.Employment
import domain.data.EngineType
import domain.data.Graduation
import domain.data.Household
import domain.data.HouseholdId
import domain.data.LegacyZone
import domain.data.Person
import domain.data.PersonBuilder
import domain.data.PersonId
import domain.data.PlannedActivity
import domain.data.PrivateCar
import domain.data.PrivateCarBuilder
import domain.data.Sex
import domain.data.SharingProvider
import domain.data.SharingStation
import domain.data.SharingVehicle
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.ActivityType
import domain.enums.AreaType
import domain.enums.LegacyActivityType
import domain.enums.ZoneAreaType
import domain.enums.ZoneClassification
import domain.location.Location
import domain.location.RoadAccess
import domain.resources.Subscribable
import units.Distance
import units.GPSCoordinate
import units.euros
import units.meters
import units.share
import utils.units.AbsoluteTime
import utils.units.sinceStart
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

val BIELEFELD = GPSCoordinate.degreesMinutesSeconds(52, 0, 59.99, 8, 30, 59.99)
val TEST_ZONE = TestZone()

@Buildable
@Suppress("LongParameterList")
class TestZone(
    point: GPSCoordinate = BIELEFELD,
    visumId: Long = 1L,
    matrixColumn: Int = 0,
    name: String = "TestZone",
    areaType: AreaType = ZoneAreaType.DEFAULT,
    regionType: Int = 0,
    classification: ZoneClassification = ZoneClassification.STUDY_AREA,
    override var parkingPlaces: Int = 1,
    isDestination: Boolean = true,
    relief: Distance = 0.meters,
    id: ZoneId = ZoneId(1L)
) : LegacyZone(
    id, visumId, name, areaType, regionType, classification,
    parkingPlaces, point.asLocation(), isDestination, relief, matrixColumn
) {

    override fun toString(): String {
        return "TestZone$id"
    }
}

fun generateZones(numElements: Int): List<TestZone> {
    return (0..<numElements).map { TestZone(BIELEFELD, id = ZoneId(it.toLong())) }
}

fun Zone.generateSharingStation(sharingProvider: SharingProvider, vehicles: Set<SharingVehicle>): SharingStation {
    return SharingStation(
        "${this.id} Station",
        "noName",
        this.point(BIELEFELD),
        setOf(this),
        sharingProvider,
        vehicles
    )
}

fun generateZoneLocations(numElements: Int): List<Location> {
    return (0..<numElements).map {
        val testZone = TestZone(BIELEFELD, id = ZoneId(it.toLong()))
        Location(testZone.centroid.coordinate, zone = testZone, roadAccess = null)
    }
}

fun Zone.point(gpsCoordinate: GPSCoordinate): Location {
    return Location(gpsCoordinate, zone = this, roadAccess = null)
}

fun GPSCoordinate.asLocation(): Location {
    return Location(this, zone = null, roadAccess = null)
}

fun Long.toRoadPosition(): Location {
    return Location(BIELEFELD, null, RoadAccess(this, 0.5.share()))
}

fun Long.toRoadPositionInZone(zone: Zone): Location {
    return Location(BIELEFELD, zone, RoadAccess(this, 0.5.share()))
}

val testHousehold = TEST_ZONE.generateHousehold {
    householdNumber = 1
}

data class PersonSpawnLimits(
    val age: IntRange = 0..100,
    val employment: Collection<Employment> = Employment.entries,
    val sex: Collection<Sex> = Sex.entries,
    val graduation: Collection<Graduation> = Graduation.entries,
    val income: IntRange = 0..10000,
    val hasBike: List<Boolean> = listOf(true, false),
    val hasCommuterTicket: List<Boolean> = listOf(true, false),
    val hasLicense: List<Boolean> = listOf(true, false),
)

val spawnDrivers = PersonSpawnLimits(
    hasLicense = listOf(true),
    age = 18..100
)

class HouseholdSpawnLimits(
    val numCars: IntRange = 0..5,
    val numPersons: IntRange = 0..5,
    val economicStatus: Collection<EconomicStatus> = EconomicStatus.entries

)

fun Zone.generateHouseholds(
    num: Int,
    random: Random = Random(1),
    spawnLimits: HouseholdSpawnLimits = HouseholdSpawnLimits(),
    personLimits: PersonSpawnLimits = PersonSpawnLimits(),
    membershipsMap: MutableMap<Subscribable<Person>, Boolean> = mutableMapOf(),
): List<Household> {
    return (0..<num).map {
        val h = generateHousehold {
            id = HouseholdId(it + this@generateHouseholds.id.id * 100)
            incomePerMonth = 0.euros
            economicStatus = spawnLimits.economicStatus.random(random)
            householdNumber = -1
        }
        repeat(spawnLimits.numCars.random(random)) {
            h.spawnCar()
        }

        h.generatePersons(
            spawnLimits.numPersons.random(random),
            random,
            personLimits,
            membershipsMap = membershipsMap.toMutableMap()
        )
        h
    }
}

fun Collection<Zone>.generateHouseholds(
    num: Int,
    random: Random = Random(1),
    spawnLimits: HouseholdSpawnLimits = HouseholdSpawnLimits(),
    personLimits: PersonSpawnLimits = PersonSpawnLimits(),
    membershipsMap: MutableMap<Subscribable<Person>, Boolean> = mutableMapOf()
): List<Household> {
    return flatMap { it.generateHouseholds(num, random, spawnLimits, personLimits, membershipsMap.toMutableMap()) }
}

fun Household.generatePersons(
    num: Int,
    random: Random = Random(1),
    spawnLimits: PersonSpawnLimits = PersonSpawnLimits(),
    membershipsMap: MutableMap<Subscribable<Person>, Boolean>,
): List<Person> {
    return (0..<num).map {
        val map = membershipsMap.toMutableMap()
        map[this] = true
        buildPerson {
            personId = it.toLong() + this@generatePersons.id.id * 100
            id = PersonId(it.toLong() + this@generatePersons.id.id * 100)
            age = spawnLimits.age.random(random)
            employment = spawnLimits.employment.random(random)
            sex = spawnLimits.sex.random(random)
            graduation = spawnLimits.graduation.random(random)
            income = spawnLimits.income.random(random).euros
            hasBike = spawnLimits.hasBike.random(random)
            hasCommuterTicket = spawnLimits.hasCommuterTicket.random(random)
            hasLicense = spawnLimits.hasLicense.random(random)
            memberships = map
        }.also { it.schedule = Schedule(TrackableModel(BlockModel())) }
    }
}

fun Person.generateActivitySchedule(
    num: Int,
    random: Random
) {
    val range = 0.days.sinceStart..1.days.sinceStart
    val targets = List(num) { range.random(random) }.sorted()

    targets.zipWithNext { a, b ->
        addActivity(
            PlannedActivity(
                ActivityId(-1L),
                this,
                LegacyActivityType.entries.random(random),
                0.minutes,
                a,
                (b - a) / 2,
                random
            )
        )
    }
}

fun ClosedRange<AbsoluteTime>.random(random: Random = Random(1)): AbsoluteTime {
    val rangeInSeconds = (start.secondsSinceStart..endInclusive.secondsSinceStart)
    val randomSeconds = random.nextLong(rangeInSeconds.first, rangeInSeconds.last)
    return randomSeconds.seconds.sinceStart
}

class ActivitySpawnLimits(
    val startTime: IntRange = 0..20,
    val endTime: IntRange = 0..20,
    val types: Collection<ActivityType> = LegacyActivityType.entries
)

fun Collection<LegacyZone>.generateActivities(
    num: Int,
    random: Random = Random(1),
    spawnLimits: ActivitySpawnLimits = ActivitySpawnLimits()
): List<Activity> {
    return (0..<num).map {
        RawActivity(
            this.random(random).point(BIELEFELD),
            spawnLimits.startTime.random(random).toAbsoluteTime(),
            spawnLimits.endTime.random(random).toAbsoluteTime(),
            type = spawnLimits.types.random(random)

        )
    }
}

fun Int.toAbsoluteTime(): AbsoluteTime {
    return AbsoluteTime(toDuration(DurationUnit.HOURS))
}

fun Household.spawnCar(lambda: PrivateCarBuilder.() -> Unit = {}): PrivateCar {
    return PrivateCarBuilder().apply {
        owner = this@spawnCar
        segment = CarSegment.MIDSIZE
        seats = 4
        engine = EngineType.COMBUSTION
    }.apply(lambda).build()
}

fun Household.buildPerson(builder: PersonBuilder): Person {
    builder.apply {
        builder.household = this@buildPerson
        personId = members.size + 1L
        id = PersonId(members.size + 1L)
    }
    val person = builder.build().also { it.schedule = Schedule(TrackableModel(BlockModel())) }
    addMember(person)
    return person
}

fun Household.buildPerson(lambda: PersonBuilder.() -> Unit): Person {
    val builder = PersonBuilder()
    builder.apply {
        household = this@buildPerson

        eMobilityAcceptance = 0.share()
        chargingInfluence = ChargingInfluence.NEVER
        random = Random(1)
        age = 20
        employment = Employment.NONE
        sex = Sex.MALE
        graduation = Graduation.UNDEFINED
        income = 0.euros
        hasBike = false
        hasCommuterTicket = false
        hasLicense = false

        personId = members.size + 1L
        id = PersonId(members.size + 1L)
    }
    builder.household = this
    val person = builder.build(lambda)
    addMember(person)
    return person
}

fun Zone.build(builder: DefaultHouseholdBuilder, roadIndex: Long = -1L): Household {
    builder.apply { location = roadIndex.toRoadPositionInZone(this@build) }
    return builder.build()
}

fun Zone.generateHouseholdBuilder(
    roadIndex: Long = -1L,
    lambda: DefaultHouseholdBuilder.() -> Unit
): DefaultHouseholdBuilder {
    val builder = DefaultHouseholdBuilder()
    builder.apply {
        surveyYear = 2024
        domCode = 1
        type = 1
        incomePerMonth = 0.euros
        economicStatus = EconomicStatus.MIDDLE
        random = Random(1)
        location = roadIndex.toRoadPositionInZone(this@generateHouseholdBuilder)
    }

    builder.apply(lambda)
    return builder
}

fun Zone.generateHousehold(roadIndex: Long = -1L, lambda: DefaultHouseholdBuilder.() -> Unit): Household {
    return generateHouseholdBuilder(roadIndex, lambda).build()
}
