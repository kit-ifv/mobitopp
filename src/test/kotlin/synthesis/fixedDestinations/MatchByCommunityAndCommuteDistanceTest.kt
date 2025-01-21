package synthesis.fixedDestinations

import TestZone
import domain.data.Sex
import domain.data.Zone
import domain.location.FlightDistance
import generateLocations
import synthesis.AssignAroundZoneCentroid
import synthesis.CommuteDistance
import synthesis.householdgeneration.SynthesisTest
import units.Distance
import units.GPSCoordinate
import units.kilometers
import kotlin.test.Test

class MatchByCommunityAndCommuteDistanceTest : SynthesisTest() {
    private class CommuteDistanceImpl(override val distanceWork: Distance) : CommuteDistance {

    }

    private val testZone = TestZone(GPSCoordinate.decimalDegree(0.0, 0.0))
    private val testZone2 = TestZone(GPSCoordinate.decimalDegree(0.0, 1.0)) // Exactly 60 nautical miles on the equator
    private val testZone3 = TestZone(GPSCoordinate.decimalDegree(0.0, 2.0))

    @Test
    fun testConstruction() {
        val household = createHousehold<CommuteDistance> {
            person(10, Sex.MALE) {
                CommuteDistanceImpl(10.kilometers)
            }

            person(10, Sex.MALE) {
                CommuteDistanceImpl(10.kilometers)
            }
        }
        val synthetisedHH = household.toSynthesisHousehold()
        synthetisedHH.location = AssignAroundZoneCentroid(10.0).generateLocation(testZone, synthetisedHH)
        val c1 = CommunityNumber(1)
        val c2 = CommunityNumber(2)
        val zoneToCommunities: Map<Zone, CommunityNumber> = mapOf(testZone to c1, testZone2 to c2)
        val commuterDemandsMatrix = CommuterDemandsMatrix(converter = { zoneToCommunities.getValue(it.requireZone()) })
        commuterDemandsMatrix[c1, c2] = 1.0

        val demands: Map<CommunityNumber, Double> = mapOf(c1 to 1.0)

        val locations = testZone2.generateLocations(10)


        val tmpSorter = TempLocationSorter<CommuteDistance>(
            demands = commuterDemandsMatrix,
            strategy = UsingCommuteDistance(FlightDistance())
        )
        val secondaryOutput = tmpSorter.match(synthetisedHH.members, locations)
//        val output = matcher.match(synthetisedHH.members, locations)

        println(secondaryOutput)
    }
}