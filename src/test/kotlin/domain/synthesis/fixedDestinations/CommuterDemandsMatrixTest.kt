package domain.synthesis.fixedDestinations

import BIELEFELD
import TestZone
import domain.shared.location.StandardLocation
import domain.shared.location.ZoneId
import domain.synthesis.behavior.fixedDestinations.communityBased.CommunityNumber
import domain.synthesis.behavior.fixedDestinations.communityBased.CommuterDemandsMatrix
import org.junit.jupiter.api.Test
import point
import kotlin.io.path.Path
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CommuterDemandsMatrixTest {
    val bielefeld = StandardLocation.fromWGS(BIELEFELD)

    @Test
    fun communityConfiguration() {
        val c1 = CommunityNumber(1)
        val c2 = CommunityNumber(2)
        val demands = CommuterDemandsMatrix(converter = { throw NotImplementedError() })
        assertEquals(demands[1, 2], 0.0)
        assertEquals(demands[c1, c2], 0.0)
        demands[1, 2] = 1.0

        assertEquals(demands[1, 2], 1.0)
        assertEquals(demands[c1, c2], 1.0)
        assertEquals(demands[2, 1], 0.0)
        assertEquals(demands[c2, c1], 0.0)

        demands[c2, c1] = 2.0

        assertEquals(demands[2, 1], 2.0)
        assertEquals(demands[c2, c1], 2.0)
        assertEquals(demands.total, 3.0)
    }

    @Test
    fun copyDoesNotAlter() {
        val demands = CommuterDemandsMatrix(converter = { CommunityNumber(2) })
        demands[1, 2] = 1.0
        demands[1, 3] = 1.5
        val concreteDemands = demands[1]

        assertTrue(concreteDemands.isSaturated(1))
        assertFalse(concreteDemands.isSaturated(2))
        assertFalse(concreteDemands.isSaturated(3))

        assertEquals(concreteDemands[2], 1.0)
        assertEquals(concreteDemands[3], 1.5)
        val copy = concreteDemands.copy()

        copy.decreaseDemandFor(bielefeld)
        assertEquals(concreteDemands[2], 1.0)
        assertEquals(concreteDemands[3], 1.5)
        assertEquals(copy[1], 0.0)
        assertEquals(copy[2], 0.0)
        assertTrue(copy.isSaturated(2))
        assertFalse(concreteDemands.isSaturated(2))

        assertEquals(copy[3], 1.5)
    }

    @Test
    fun decreaseDemandForNonexistingDoesNothing() {
        val demands = CommuterDemandsMatrix(converter = { CommunityNumber(1) })
        val demandFor = demands[1]
        demandFor.decreaseDemandFor(bielefeld)
        assertEquals(demandFor[1], 0.0)
        demandFor[1] = 1.0
        assertEquals(demandFor.total, 1.0)
        assertEquals(demandFor[1], 1.0)
    }

    @Test
    fun containsWorksProperly() {
        val demands = CommuterDemandsMatrix(converter = { CommunityNumber(2) })
        demands[1, 2] = 1.0
        demands[1, 3] = 1.5
        val concreteDemands = demands[1]
        assertFalse(concreteDemands.contains(1))
        assertTrue(concreteDemands.contains(2))
        assertTrue(concreteDemands.contains(3))
        assertEquals(demands.total, 2.5)
        concreteDemands.decreaseDemandFor(bielefeld)
        assertEquals(demands.total, 2.5) // Should be copied and thus unaffected
        assertEquals(concreteDemands.total, 1.5)

        assertEquals(concreteDemands[2], 0.0)
        assertTrue(concreteDemands.contains(2))
    }

    @Test
    fun decreaseDemand() {
        val demands = CommuterDemandsMatrix(converter = { CommunityNumber(2) })
        val demandFor = demands[1]
        demandFor[2] = 1.5
        demandFor.decreaseDemandFor(CommunityNumber(2))
        assertEquals(demandFor[2], 0.5)
    }

    @Test
    fun properParsing() {
        val demands = CommuterDemandsMatrix.parseRastatt()
        assertEquals(demands.convert(6113.toZone().point(BIELEFELD)), CommunityNumber(8216043))
        assertEquals(demands.convert(10015.toZone().point(BIELEFELD)), CommunityNumber(10015))
        assertEquals(demands.convert(6650.toZone().point(BIELEFELD)), CommunityNumber(8211000))
        assertEquals(demands.convert(6114.toZone().point(BIELEFELD)), CommunityNumber(8216043))

        val demandFor = demands[8216052]
        assertTrue(10002 in demandFor)
        assertEquals(demandFor[10002], 0.0)

        assertTrue(10006 in demandFor)
        assertEquals(demandFor[10006], 42.0)
    }

    @Test
    fun parseNonexistingCommunity() {
        val demands = CommuterDemandsMatrix.parseRastatt()
        assertEquals(CommunityNumber.INVALID, demands.convert(42.toZone().point(BIELEFELD)))
    }

    private fun Number.toZone(): TestZone {
        return TestZone(id = ZoneId(toLong()))
    }
    private fun CommuterDemandsMatrix.Companion.parseRastatt(): CommuterDemandsMatrix {
        return parse(
            Path("src/test/resources/synthesis/zone-to-community.csv"),
            Path("src/test/resources/synthesis/commuters-rastatt.csv")
        )
    }
}
