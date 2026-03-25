package domain.synthesis.householdgeneration

import TestZone
import domain.shared.location.RoadAccess
import domain.shared.location.StandardLocation
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.shared.location.attributes.HasZone
import domain.shared.location.toPoint
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.HasCommuteDistance
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.behavior.HouseholdFactory
import domain.synthesis.behavior.SmallestSurveyPerson
import domain.synthesis.behavior.SurveyHousehold
import domain.synthesis.behavior.SurveyPerson
import domain.synthesis.SynthesisHousehold
import domain.synthesis.data.HouseholdType
import domain.synthesis.data.Sex
import edu.kit.ifv.units.Currency
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.WGS84Coordinate
import edu.kit.ifv.units.euros
import edu.kit.ifv.units.kilometers
import org.junit.jupiter.api.Assertions.assertEquals
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import kotlin.test.Test

class ToolTest : SynthesisTest() {
    val zone1 = TestZone(id = ZoneId(1))
    private val zones = listOf(zone1)

    @Test
    fun checkHouseholdCreation() {
        val firstAge = 10
        val firstSex = Sex.MALE
        val secondAge = 42
        val secondSex = Sex.FEMALE
        val household = createHousehold<MinimumPersonAttributes> {
            person {
                Attrs(
                    firstAge,
                    firstSex,
                )
            }
            person {
                Attrs(
                    secondAge,
                    secondSex,
                )
            }
        }
        assertEquals(household.size, 2)
        val firstPerson = household.members[0]
        assertEquals(firstPerson.age, firstAge)
        assertEquals(firstPerson.sex, firstSex)

        val secondPerson = household.members[1]
        assertEquals(secondPerson.age, secondAge)
        assertEquals(secondPerson.sex, secondSex)
    }

}

open class SynthesisTest {

    protected fun <T : MinimumPersonAttributes> createHousehold(lambda: HouseholdBuilder<T>.() -> Unit): SurveyHousehold<MinimumHouseholdAttributes, T> {
        val builder = HouseholdBuilder<T>()
        builder.apply(lambda)
        return builder.createHousehold()
    }

    /**
     * Spawn in a synthesis household, if you happen to have a location at hand where the household should be.
     */
    protected fun <T : MinimumPersonAttributes> StandardLocation.createHousehold(lambda: HouseholdBuilder<T>.() -> Unit): SynthesisHousehold<MinimumHouseholdAttributes, T> {
        val builder = HouseholdBuilder< T>()
        builder.apply(lambda)
        val createHousehold = builder.createHousehold()
        val synthesisHousehold = HouseholdFactory.createFrom(createHousehold)
        synthesisHousehold.attributes.location = this
        return synthesisHousehold
    }

    protected fun fakeLocation() = FakeCoord()
    protected fun Zone.spawnFakeLoc(): StandardLocation {
        val point: Point = GeometryFactory().createPoint(Coordinate(counter, counter)).also { counter++ }
        return StandardLocation(point, this, RoadAccess.INVALID)
    }

    private var counter: Double = .0

    protected fun Zone.spawnLocation(coordinate: WGS84Coordinate): StandardLocation {
        return StandardLocation(coordinate.toPoint(), this, RoadAccess.INVALID)
    }

    protected class FakeCoord : HasZone {
        val id = counter

        override fun toString(): String {
            return "FakeLoc($id)"
        }

        override val position: Point
            get() = TODO("Not yet implemented")
        override val zoneID: ZoneId = ZoneId(id)

        companion object {
            var counter: Long = 0
                get() = field++
                private set
        }
    }
    private data class HAttrs(
        override val income: Currency = 1.euros,
        override val type: HouseholdType = HouseholdType.UNDEFINED,
        override var location: StandardLocation = StandardLocation.LOCATIONUNKNOWN
    ): MinimumHouseholdAttributes

    protected data class Attrs(
        override val age: Int,
        override val sex: Sex,
        override val distanceWork: Distance = (-999).kilometers

    ): MinimumPersonAttributes, HasCommuteDistance
    protected class HouseholdBuilder<T : MinimumPersonAttributes> {
        var id: Long = 0
        var income = 0.euros
        val members: MutableList<SurveyPerson<T>> = mutableListOf()

        var attributeSpawner : () -> MinimumHouseholdAttributes = {
            HAttrs()
        }

        inner class PersonBuilder {
            var sex: Sex = Sex.MALE
            var age: Int = 0
            lateinit var information: () -> T
            fun toPerson(): SurveyPerson<T> {
                return SmallestSurveyPerson(-1, attributes = information())
            }
        }

        fun person(lambda: () -> T): SmallestSurveyPerson<T> {
            val smallestSurveyPerson =
                SmallestSurveyPerson(personId = members.size + 1, attributes = lambda())
            members.add(smallestSurveyPerson)
            return smallestSurveyPerson
        }

        fun createHousehold(): SurveyHousehold<MinimumHouseholdAttributes, T> {
            return SurveyHousehold(id,  members, attributeSpawner())
        }
    }

}
