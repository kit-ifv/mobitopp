package choicemodels

import Buildable
import datastructure.Activity
import domain.data.ChargingInfluence
import domain.data.DefaultHouseholdBuilder
import domain.data.EconomicStatus
import domain.data.Employment
import domain.data.Graduation
import domain.data.Household
import domain.data.LegacyZone
import domain.data.Person
import domain.data.PersonBuilder
import domain.data.PersonId
import domain.data.Sex
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.AreaType
import domain.enums.Mode
import domain.enums.ZoneAreaType
import domain.enums.ZoneClassification
import domain.location.CostMetric
import domain.location.DistanceMetric
import domain.location.DurationMetric
import domain.location.Location
import domain.location.Metrics
import domain.location.RoadPosition
import domain.location.RoadPositionInZone
import domain.location.ZoneLocation
import domain.location.ZoneLocationImpl
import units.Coordinate
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
import kotlin.random.Random
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
    override var visumId: Long = 1L,
    override var matrixColumn: Int = 0,
    override var name: String = "HomeZone",
    override var areaType: AreaType = ZoneAreaType.DEFAULT,
    override var regionType: Int = 0,
    override var classification: ZoneClassification = ZoneClassification.STUDY_AREA,
    override var parkingPlaces: Int = 1,
    override var isDestination: Boolean = true,
    override var relief: Distance = 0.meters,
    override val id: ZoneId = ZoneId(1L)
) : LegacyZone {
    override var centroid: ZoneLocation = point(point)
}

val OTHER_TEST_ZONE = TestZone(BIELEFELD_HBF)

fun Zone.point(gpsCoordinate: GPSCoordinate): ZoneLocation {
    return ZoneLocationImpl(gpsCoordinate, this)
}
fun Long.toRoadPosition(): RoadPosition {
    return object : RoadPosition {
        override val road: Long = this@toRoadPosition
        override val roadAccess = 0.5.share()
        override val coordinate: Coordinate = BIELEFELD
    }
}
fun Long.toRoadPositionInZone(zone: Zone): RoadPositionInZone {
    return RoadPositionInZone(this.toRoadPosition(), zone)
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
val testHousehold = TEST_ZONE.generateHousehold {
    householdNumber = 1
}
val testPerson = testHousehold.buildPerson {
    personId = 1L
    id = PersonId(1L)
}
val otherTestPerson = testHousehold.buildPerson {
    personId = 2L
    id = PersonId(2L)
    sex = Sex.FEMALE
}
val testHousehold1 = DefaultHouseholdBuilder().apply {
    location = 1L.toRoadPositionInZone(TestZone())
    householdNumber = 1
    surveyYear = 2024
    domCode = 1
    type = 1
    incomePerMonth = 0.euros
    economicStatus = EconomicStatus.MIDDLE
    random = Random(1)
}.build()

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
    }
    builder.household = this
    val person = builder.build(lambda)
    addMember(person)
    return person
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
