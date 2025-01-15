package choicemodels

import Buildable
import asLocation
import datastructure.Activity
import domain.data.ChargingInfluence
import domain.data.EconomicStatus
import domain.data.Employment
import domain.data.Graduation
import domain.data.HouseholdId
import domain.data.MutableHousehold
import domain.data.MutableLegacyZone
import domain.data.MutablePerson
import domain.data.Person
import domain.data.PersonId
import domain.data.Sex
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.AreaType
import domain.enums.Mode
import domain.enums.Regiostar17
import domain.enums.ZoneAreaType
import domain.enums.ZoneClassification
import domain.location.CostMetric
import domain.location.DistanceMetric
import domain.location.DurationMetric
import domain.location.Location
import domain.location.Metrics
import domain.location.RoadAccess
import units.Currency
import units.CurrencyUnit
import units.Distance
import units.GPSCoordinate
import units.euros
import units.meters
import units.share
import units.toCurrency
import utils.units.AbsoluteTime
import utils.units.Time
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

val BIELEFELD = GPSCoordinate.degreesMinutesSeconds(52, 0, 59.99, 8, 30, 59.99)
val BIELEFELD_HBF = GPSCoordinate.decimalDegree(52.029287, 8.532729)
val TEST_ZONE = TestZone()
val TEST_ACTIVITY = Activity.Companion.fromDuration(
    TEST_ZONE.point(BIELEFELD),
    AbsoluteTime.START + 4.hours,
    4.hours
)

@Buildable
@Suppress("LongParameterList")
class TestZone(
    point: GPSCoordinate = BIELEFELD,
    visumId: Long = 1L,
    matrixColumn: Int = 0,
    name: String = "HomeZone",
    areaType: AreaType = ZoneAreaType.DEFAULT,
    regionType: Int = 0,
    classification: ZoneClassification = ZoneClassification.STUDY_AREA,
    override var parkingPlaces: Int = 1,
    isDestination: Boolean = true,
    relief: Distance = 0.meters,
    id: ZoneId = ZoneId(1L)
) : MutableLegacyZone(
    id = id,
    centroid = point.asLocation(),
    seed = 42L,
    {
        this.visumId = visumId
        this.name = name
        this.areaType = areaType
        this.regionType = Regiostar17.METROPOLE
        this.classification = classification
        this.parkingPlaces = parkingPlaces
        this.relief = relief
        this.matrixColumn = matrixColumn
        this.isDestination = isDestination
    }
)

val OTHER_TEST_ZONE = TestZone(BIELEFELD_HBF)

fun Zone.point(gpsCoordinate: GPSCoordinate): Location {
    return Location(gpsCoordinate, this, null)
}
fun Long.toRoadPosition(): Location {
    return Location(
        BIELEFELD,
        null,
        RoadAccess(this, 0.5.share())
    )
}
fun Long.toRoadPositionInZone(zone: Zone): Location {
    return this.toRoadPosition().withZone(zone)
}
class DebugImpedance : Metrics {
    val map: MutableMap<Triple<Mode, Location, Location>, Duration> = mutableMapOf()

    override fun duration(from: Location, to: Location, mode: Mode, time: Time): Duration {
        return map[Triple(mode, from, to)] ?: 0.minutes
    }

    override fun costMetric(mode: Mode, time: Time): CostMetric {
        error("Not implemented")
    }

    override fun distanceMetric(mode: Mode): DistanceMetric {
        error("Not implemented")
    }

    override fun durationMetric(mode: Mode, time: Time): DurationMetric {
        error("Not implemented")
    }

    override fun cost(from: Location, to: Location, mode: Mode, time: Time): Currency {
        return map[Triple(mode, from, to)]?.toDouble(DurationUnit.MINUTES)?.toCurrency(CurrencyUnit.EUROS) ?: 0.euros
    }

    override fun distance(from: Location, to: Location, mode: Mode): Distance {
        return map[Triple(mode, from, to)]?.toDouble(DurationUnit.MINUTES)?.meters ?: 0.meters
    }

    operator fun set(mode: Mode, from: Location, to: Location, duration: Duration) {
        map[Triple(mode, from, to)] = duration
    }
}
val testHousehold = TEST_ZONE.generateHousehold(id = 1) {
    householdNumber = 1
}
val testPerson = testHousehold.buildPerson(1L) {
}

val otherTestPerson = testHousehold.buildPerson(2L) {
    sex = Sex.FEMALE
}
val testHousehold1 = MutableHousehold(
    id = HouseholdId(1),
    seed = 42L,
) {
    location = 1L.toRoadPositionInZone(TestZone())
    householdNumber = 1
    surveyYear = 2024
    domCode = 1
    type = 1
    incomePerMonth = 0.euros
    economicStatus = EconomicStatus.MIDDLE
//    random = Random(1)
}

fun MutableHousehold.buildPerson(id: Long, lambda: MutablePerson.() -> Unit): Person {
    val builder = MutablePerson(
        id = PersonId(id),
        household = this,
        seed = 42L,
    ) {
        eMobilityAcceptance = 0.share()
        chargingInfluence = ChargingInfluence.NEVER
//        random = Random(1)
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

    // addMember(person) happens in init of MutablePerson now
    return builder
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
//        random = Random(1)
        location = roadIndex.toRoadPositionInZone(this@generateHouseholdBuilder)

        lambda()
    }

    return builder
}

fun Zone.generateHousehold(id: Long, roadIndex: Long = -1L, lambda: MutableHousehold.() -> Unit): MutableHousehold {
    return generateHouseholdBuilder(id, roadIndex, lambda)
}
