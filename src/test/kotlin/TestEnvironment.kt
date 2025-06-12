import domain.shared.datastructure.schedule.Activity
import domain.shared.datastructure.schedule.RawActivity
import domain.shared.enums.ActivityType
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.ZoneClassification
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.shared.location.LegacyZone
import domain.shared.location.Location
import domain.shared.location.MutableLegacyZone
import domain.shared.location.RoadAccess
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.synthesis.data.ActivityId
import domain.synthesis.data.CarEngineStatistics
import domain.synthesis.data.CarId
import domain.synthesis.data.CarSegment
import domain.synthesis.data.ChargingInfluence
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.Employment
import domain.synthesis.data.EngineType
import domain.synthesis.data.Graduation
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.MutablePlannedActivity
import domain.synthesis.data.MutablePrivateCar
import domain.synthesis.data.MutableSharingProvider
import domain.synthesis.data.MutableSharingStation
import domain.synthesis.data.PersonId
import domain.synthesis.data.PrivateCar
import domain.synthesis.data.Sex
import domain.synthesis.data.SharingProvider
import domain.synthesis.data.SharingStation
import domain.synthesis.data.SharingStationId
import domain.synthesis.data.buildEngine
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
val ITZEHOE = GPSCoordinate.decimalDegree(53.925032, 9.515585)
val SCHWEINFURT = GPSCoordinate.decimalDegree(50.049994, 10.233302)
val TEST_ZONE = TestZone()

@Buildable
@Suppress("LongParameterList")
class TestZone(
    point: GPSCoordinate = BIELEFELD,
    visumId: Long = 1L,
    matrixColumn: Int = 0,
    name: String = "TestZone",
    regionType: RegionType = RegioStaR17.METROPOLE,
    classification: ZoneClassification = ZoneClassification.STUDY_AREA,
    override var parkingPlaces: Int = 1,
    isDestination: Boolean = true,
    relief: Distance = 0.meters,
    id: ZoneId = ZoneId(1L)
) : MutableLegacyZone(
    id,
    point.asLocation(),
    42L,
    {
        this.visumId = visumId
        this.name = name
        this.regionType = regionType
        this.classification = classification
        this.parkingPlaces = parkingPlaces
        this.isDestination = isDestination
        this.relief = relief
        this.matrixColumn = matrixColumn
    }
) {

    override fun toString(): String {
        return "TestZone$id"
    }
}

fun generateZones(numElements: Int): List<TestZone> {
    return (0..<numElements).map { TestZone(BIELEFELD, id = ZoneId(it.toLong())) }
}

fun Zone.generateSharingStation(
    sharingProvider: MutableSharingProvider,
    vehicles: Int
): SharingStation {
    return MutableSharingStation(
        SharingStationId(sharingProvider.numberOfVehicles.toLong()),
        sharingProvider,
    ) {
        this.uid = "${this.id} Station"
        this.name = "noName"
        this.location = point(BIELEFELD)
        this.zonesByFoot.add(this@generateSharingStation)
        this.initialVehicleCount = vehicles
    }
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

val testHousehold = TEST_ZONE.generateHousehold(1) {
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

@Suppress("LongParameterList")
fun Zone.generateHouseholds(
    num: Int,
    random: Random = Random(1),
    spawnLimits: HouseholdSpawnLimits = HouseholdSpawnLimits(),
    personLimits: PersonSpawnLimits = PersonSpawnLimits(),
    memberships: MutableList<SharingProvider> = mutableListOf(),
    personScope: (MutablePerson) -> Unit = {},
): List<Household> {
    return (0..<num).map {
        val h = generateHousehold(it + this@generateHouseholds.id.value * 100) {
            incomePerMonth = 0.euros
            economicStatus = spawnLimits.economicStatus.random(random)
            // TODO we could use this.random, since household is a stochastic actor
            householdNumber = -1
        }
        repeat(spawnLimits.numCars.random(random)) {
            h.spawnCar()
        }

        h.generatePersons(
            spawnLimits.numPersons.random(random),
            random,
            personLimits,
            memberships,
            personScope
        )
        h
    }
}

@Suppress("LongParameterList")
fun Collection<Zone>.generateHouseholds(
    num: Int,
    random: Random = Random(1),
    spawnLimits: HouseholdSpawnLimits = HouseholdSpawnLimits(),
    personLimits: PersonSpawnLimits = PersonSpawnLimits(),
    memberships: MutableList<SharingProvider> = mutableListOf(),
    personScope: (MutablePerson) -> Unit = {},
): List<Household> {
    return flatMap { it.generateHouseholds(num, random, spawnLimits, personLimits, memberships, personScope) }
}

fun MutableHousehold.generatePersons(
    num: Int,
    random: Random = Random(1),
    spawnLimits: PersonSpawnLimits = PersonSpawnLimits(),
    memberships: List<SharingProvider>,
    personScope: (MutablePerson) -> Unit = {},
): List<MutablePerson> {
    return (0..<num).map {
        generateAndAddPerson(it.toLong() + this@generatePersons.id.value * 100) {
            age = spawnLimits.age.random(random)
            employment = spawnLimits.employment.random(random)
            sex = spawnLimits.sex.random(random)
            graduation = spawnLimits.graduation.random(random)
            income = spawnLimits.income.random(random).euros
            hasBike = spawnLimits.hasBike.random(random)
            hasCommuterTicket = spawnLimits.hasCommuterTicket.random(random)
            hasLicense = spawnLimits.hasLicense.random(random)
            sharingMemberships.addAll(memberships)

            personScope(this)
        }
    }
}

fun MutablePerson.generateActivitySchedule(
    num: Int,
    random: Random
) {
    val range = 0.days.sinceStart..1.days.sinceStart
    val targets = List(num) { range.random(random) }.sorted()

    targets.zipWithNext { a, b ->

        MutablePlannedActivity(
            id = ActivityId(-1L),
            this,
            seed = 42L
        ) {
            activityType = LegacyActivityType.entries.random(random)
            observedTripDuration = 0.minutes
            startTime = a
            duration = (b - a) / 2
        }
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

fun MutableHousehold.spawnCar(lambda: MutablePrivateCar.() -> Unit = {}): PrivateCar {
    return MutablePrivateCar(
        id = CarId(cars.size + 1L),
        owner = this
    ) {
        segment = CarSegment.MIDSIZE
        seats = 4
        val engineType = EngineType.COMBUSTION
        engine = CarEngineStatistics().buildEngine(segment, engineType)
        location = owner.location
    }.apply(lambda)
}

fun MutableHousehold.generateAndAddPerson(builder: (Long, MutableHousehold) -> MutablePerson): MutablePerson {
    val person = builder(members.size + 1L, this)
    return person
}

fun MutableHousehold.generatePerson(id: Long, lambda: MutablePerson.() -> Unit): MutablePerson {
    val builder = MutablePerson(
        id = PersonId(id),
        household = this,
        seed = 42L,
    ) {
        eMobilityAcceptance = 0.share()
        chargingInfluence = ChargingInfluence.NEVER
        age = 20
        employment = Employment.NONE
        sex = Sex.MALE
        graduation = Graduation.UNDEFINED
        income = 0.euros
        hasBike = false
        hasCommuterTicket = false
        hasLicense = false

        lambda()
    }
    return builder
}

fun MutablePerson.generatePlannedActivity(
    id: Long,
    seed: Long = 1L,
    lambda: MutablePlannedActivity.() -> Unit
): MutablePlannedActivity {
    val mutable = MutablePlannedActivity(ActivityId(id), this, seed)
    mutable.apply(lambda)
    return mutable
}

fun MutableHousehold.generateAndAddPerson(id: Long, lambda: MutablePerson.() -> Unit): MutablePerson {
    val person = generatePerson(id, lambda)

    members.add(person) // TODO should now be handled in MutablePerson init
    return person
}

fun Zone.build(builder: () -> MutableHousehold, roadIndex: Long = -1L): MutableHousehold {
    val household = builder().apply { location = roadIndex.toRoadPositionInZone(this@build) }
    return household
}

fun Zone.generateHouseholdBuilder(
    id: Long,
    roadIndex: Long = -1L,
    lambda: MutableHousehold.() -> Unit
): MutableHousehold {
    val builder = MutableHousehold(
        id = HouseholdId(id),
        seed = 42L,
    ) {
        surveyYear = 2024
        domCode = 1
        type = 1
        incomePerMonth = 0.euros
        economicStatus = EconomicStatus.MIDDLE
        location = roadIndex.toRoadPositionInZone(this@generateHouseholdBuilder)
    }

    builder.apply(lambda)
    return builder
}

fun Zone.generateHousehold(id: Long, roadIndex: Long = -1L, lambda: MutableHousehold.() -> Unit): MutableHousehold {
    return generateHouseholdBuilder(id, roadIndex, lambda)
}
