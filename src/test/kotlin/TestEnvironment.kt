import domain.shared.enums.ActivityType
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.ZoneClassification
import domain.shared.enums.areatype.RegioStaR17
import domain.shared.enums.areatype.RegionType
import domain.shared.location.StandardLocation
import domain.shared.location.StandardLocationImpl
import domain.shared.location.jts.PointCreator
import domain.shared.location.road.RoadAccess
import domain.shared.location.zone.MaximalZone
import domain.shared.location.zone.Zone
import domain.shared.location.zone.ZoneId
import domain.shared.location.zone.attributes.HasRegionType
import domain.shared.location.zone.attributes.MaximumZoneAttributes
import domain.shared.location.zone.toZoneId
import domain.synthesis.data.ActivityId
import domain.synthesis.data.MutablePlannedActivity
import domain.synthesis.data.MutableSharingProvider
import domain.synthesis.data.MutableSharingStation
import domain.synthesis.data.SharingProvider
import domain.synthesis.data.SharingStation
import domain.synthesis.data.SharingStationId
import domain.synthesis.data.car.CarId
import domain.synthesis.data.car.CarSegment
import domain.synthesis.data.car.MutablePrivateCar
import domain.synthesis.data.car.PrivateCar
import domain.synthesis.data.car.engine.CarEngineStatistics
import domain.synthesis.data.car.engine.EngineType
import domain.synthesis.data.car.engine.buildEngine
import domain.synthesis.data.drt.DrtProvider
import domain.synthesis.data.household.EconomicStatus
import domain.synthesis.data.household.Household
import domain.synthesis.data.household.HouseholdId
import domain.synthesis.data.household.MutableHousehold
import domain.synthesis.data.person.ChargingInfluence
import domain.synthesis.data.person.Employment
import domain.synthesis.data.person.Graduation
import domain.synthesis.data.person.MutablePerson
import domain.synthesis.data.person.PersonId
import domain.synthesis.data.person.Sex
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.euros
import edu.kit.ifv.units.meters
import edu.kit.ifv.units.share
import org.locationtech.jts.geom.Point
import utils.units.AbsoluteTime
import utils.units.sinceStart
import kotlin.random.Random
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

val BIELEFELD = PointCreator.createWGS(8.531007, 52.019101)

fun main() {
    val b = BIELEFELD
    println(b)
}
val TEST_ZONE =
    MaximalZone(42.toZoneId(), ZoneTestAttributesFake())

class TestZone(override val zoneId: ZoneId, override val attributes: HasRegionType) : Zone<HasRegionType> {
    private class AttributeImpl(override val regionType: RegionType) : HasRegionType

    constructor(zoneId: Number, regionType: RegionType = RegioStaR17.URBAN_AREA_METRO) : this(
        ZoneId(zoneId.toLong()),
        AttributeImpl(regionType),
    )

    override val centroidLocation: StandardLocation = StandardLocationImpl(BIELEFELD, this)
}

fun generateZones(numElements: Int): List<MaximalZone> = (0..<numElements).map {
    MaximalZone(it.toZoneId(), attributes = ZoneTestAttributesFake())
}

class ZoneTestAttributesFake(
    override val visumId: Long = 1,
    override val name: String = "Noname",
    override val classification: ZoneClassification = ZoneClassification.STUDY_AREA,
    override val isDestination: Boolean = true,
    override val relief: Distance = 0.meters,
    override val regionType: RegionType = RegioStaR17.URBAN_AREA_METRO,
    override val parkingPlaces: Int = 42,
    override val centroid: Point = BIELEFELD,
) : MaximumZoneAttributes

fun Zone<HasRegionType>.generateSharingStation(
    sharingProvider: MutableSharingProvider,
    vehicles: Int,
): SharingStation = MutableSharingStation(
    SharingStationId(sharingProvider.numberOfVehicles.toLong()),
    sharingProvider,
) {
    this.uid = "${this.id} Station"
    this.name = "noName"
    this.location = StandardLocationImpl(BIELEFELD, this@generateSharingStation)
    this.zonesByFoot.add(this@generateSharingStation)
    this.initialVehicleCount = vehicles
}

// fun generateZoneLocations(numElements: Int): List<StandardLocation> {
//    return (0..<numElements).map {
//        val testZone = TestZone(it.toLong())
//        StandardLocation(testZone.centroid.position, zone = testZone, roadAccess = RoadAccess.INVALID)
//    }
// }

fun Zone<HasRegionType>.point(point: Point): StandardLocation =
    StandardLocation(point, zone = this, roadAccess = RoadAccess.INVALID)

fun Long.toRoadPositionInZone(zone: Zone<HasRegionType>): StandardLocation =
    StandardLocation(BIELEFELD, zone, RoadAccess(this, 0.5.share()))

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
    age = 18..100,
)

class HouseholdSpawnLimits(
    val numCars: IntRange = 0..5,
    val numPersons: IntRange = 0..5,
    val economicStatus: Collection<EconomicStatus> = EconomicStatus.entries,

)

@Suppress("LongParameterList")
fun Zone<HasRegionType>.generateHouseholds(
    num: Int,
    random: Random = Random(1),
    spawnLimits: HouseholdSpawnLimits = HouseholdSpawnLimits(),
    personLimits: PersonSpawnLimits = PersonSpawnLimits(),
    memberships: MutableList<SharingProvider> = mutableListOf(),
    drtMemberships: MutableList<DrtProvider>,
    personScope: (MutablePerson) -> Unit = {},
): List<Household> = (0..<num).map {
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
        drtMemberships,
        personScope,
    )
    h
}

@Suppress("LongParameterList")
fun Collection<Zone<HasRegionType>>.generateHouseholds(
    num: Int,
    random: Random = Random(1),
    spawnLimits: HouseholdSpawnLimits = HouseholdSpawnLimits(),
    personLimits: PersonSpawnLimits = PersonSpawnLimits(),
    memberships: MutableList<SharingProvider> = mutableListOf(),
    drtMemberships: MutableList<DrtProvider> = mutableListOf(),
    personScope: (MutablePerson) -> Unit = {},
): List<Household> = flatMap {
    it.generateHouseholds(
        num,
        random,
        spawnLimits,
        personLimits,
        memberships,
        drtMemberships,
        personScope,
    )
}

@Suppress("LongParameterList")
fun MutableHousehold.generatePersons(
    num: Int,
    random: Random = Random(1),
    spawnLimits: PersonSpawnLimits = PersonSpawnLimits(),
    memberships: List<SharingProvider>,
    drtMemberships: MutableList<DrtProvider>,
    personScope: (MutablePerson) -> Unit = {},
): List<MutablePerson> = (0..<num).map {
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
        this.drtMemberships.addAll(drtMemberships)

        personScope(this)
    }
}

fun MutablePerson.generateActivitySchedule(num: Int, random: Random) {
    val range = 0.days.sinceStart..1.days.sinceStart
    val targets = List(num) { range.random(random) }.sorted().distinct()

    targets.zipWithNext { a, b ->

        val act = MutablePlannedActivity(
            id = ActivityId(-1L),
            this.id,
            seed = 42L,
        ) {
            activityType = LegacyActivityType.entries.random(random)
            observedTripDuration = 0.minutes
            startTime = a
            duration = (b - a) / 2
        }

        this.plannedActivities.add(act)
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
    val types: Collection<ActivityType> = LegacyActivityType.entries,
)

// fun Collection<Zone<HasRegionType>>.generateActivities(
//    num: Int,
//    random: Random = Random(1),
//    spawnLimits: ActivitySpawnLimits = ActivitySpawnLimits(),
// ): List<Activity> = (0..<num).map {
//    RawActivity(
//        this.random(random).point(BIELEFELD),
//        spawnLimits.startTime.random(random).toAbsoluteTime(),
//        spawnLimits.endTime.random(random).toAbsoluteTime(),
//        type = spawnLimits.types.random(random),
//
//    )
// }

fun Int.toAbsoluteTime(): AbsoluteTime = AbsoluteTime(toDuration(DurationUnit.HOURS))

fun MutableHousehold.spawnCar(lambda: MutablePrivateCar.() -> Unit = {}): PrivateCar = MutablePrivateCar(
    id = CarId(cars.size + 1L),
    owner = this,
) {
    segment = CarSegment.MIDSIZE
    seats = 4
    val engineType = EngineType.COMBUSTION
    engine = CarEngineStatistics().buildEngine(segment, engineType)
    location = owner.location
}.apply(lambda)

fun MutableHousehold.generateAndAddPerson(builder: (Long, MutableHousehold) -> MutablePerson): MutablePerson {
    val person = builder(members.size + 1L, this)
    return person
}

fun MutableHousehold.generatePerson(id: Long, lambda: MutablePerson.() -> Unit = {}): MutablePerson {
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
    lambda: MutablePlannedActivity.() -> Unit,
): MutablePlannedActivity {
    val mutable = MutablePlannedActivity(ActivityId(id), this.id, seed)
    mutable.apply(lambda)
    return mutable
}

fun MutableHousehold.generateAndAddPerson(id: Long, lambda: MutablePerson.() -> Unit): MutablePerson {
    val person = generatePerson(id, lambda)

    members.add(person) // TODO should now be handled in MutablePerson init
    return person
}

fun Zone<HasRegionType>.build(builder: () -> MutableHousehold, roadIndex: Long = -1L): MutableHousehold {
    val household = builder().apply { location = roadIndex.toRoadPositionInZone(this@build) }
    return household
}

fun Zone<HasRegionType>.generateHouseholdBuilder(
    id: Long,
    roadIndex: Long = -1L,
    lambda: MutableHousehold.() -> Unit,
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
        householdNumber = -1
    }

    builder.apply(lambda)
    return builder
}

fun Zone<HasRegionType>.generateHousehold(
    id: Long,
    roadIndex: Long = -1L,
    lambda: MutableHousehold.() -> Unit = {},
): MutableHousehold = generateHouseholdBuilder(id, roadIndex, lambda)
