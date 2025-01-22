package synthesis.fixedDestinations

import TestZone
import domain.data.Sex
import domain.data.Zone
import domain.data.ZoneId
import domain.location.FlightDistance
import domain.location.Location
import org.junit.jupiter.api.Assertions.*
import synthesis.CommuteDistance
import synthesis.householdgeneration.SynthesisTest
import units.Coordinate
import units.Distance
import units.Hemisphere
import units.Radians
import units.UTMPosition
import units.kilometers
import kotlin.test.Test

class UsingCommuteDistanceTest: SynthesisTest() {
    private val testZone1 = TestZone(id = ZoneId(1L))
    private val testZone2 = TestZone(id = ZoneId(2L))
    private val testZone3 = TestZone(id = ZoneId(3L))

    @Test
    fun undersaturatedDemand() {
        val zoneCommunityMapping: Map<Zone, CommunityNumber> = mapOf(
            testZone1 to CommunityNumber(1),
            testZone2 to CommunityNumber(2),
            testZone3 to CommunityNumber(2),

            )
        val locationsZone2 = listOf(
            Location(FakeCoordinate.FIRST, testZone2, null),
            Location(FakeCoordinate.SECOND, testZone2, null)
        )
//        val locations: Collection<Location> = TODO()
        val demand: MutableCommunityDemand = MutableCommunityDemand(
            converter = { zoneCommunityMapping.getValue(it.requireZone()) }
        )N
        val strategy = UsingCommuteDistance<CommuteDistance>(FlightDistance())
        val hh = createHousehold<CommuteDistance> {
            person(10, Sex.MALE) {
                object: CommuteDistance {
                    override val distanceWork: Distance = 1.kilometers
                }

            }
        }.toSynthesisHousehold()
        val output = strategy.assign(hh.members, demand, locationsZone2)
    }
}

private enum class FakeCoordinate(override val latitudeRadians: Radians, override val longitudeRadians: Radians) :
    Coordinate {

    FIRST(Radians(0.0), Radians(0.0)) {
        override fun fakeDistance(other: FakeCoordinate): Distance {
            return when(other) {
                FIRST -> 0.kilometers
                SECOND -> 1.kilometers
                THIRD -> 2.kilometers
            }
        }
    }, SECOND(Radians(0.0), Radians(0.0)) {
        override fun fakeDistance(other: FakeCoordinate): Distance {
            return when(other) {
                FIRST -> 1.1.kilometers
                SECOND -> 0.kilometers
                THIRD -> 1.2.kilometers
            }
        }
    }, THIRD(Radians(0.0), Radians(0.0)) {
        override fun fakeDistance(other: FakeCoordinate): Distance {
            return when(other) {
                FIRST -> 2.2.kilometers
                SECOND -> 2.1.kilometers
                THIRD -> 0.kilometers
            }
        }
    };


    abstract fun fakeDistance(other: FakeCoordinate): Distance

    override fun distance(other: Coordinate): Distance {
        if(other is FakeCoordinate) {
            return fakeDistance(other)
        }
        return other.distance(this)
    }
}