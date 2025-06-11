package domain.synthesis.behavior.fixedDestinations.communityBased

import domain.shared.location.Location
import domain.synthesis.data.ZoneId
import utils.csv.DefaultCsvParser
import java.nio.file.Path

/**
 * A commuter demand matrix holds the information of commuter demands between community numbers.
 * @property demands The commute demands for an input community number, represented by a [MutableCommunityDemand]
 * @property converter a converter function to determine the corresponding community number for a given location.
 */
class CommuterDemandsMatrix(
    private val demands: MutableMap<CommunityNumber, MutableCommunityDemand> = mutableMapOf(),
    private val converter: (Location) -> CommunityNumber
) {

    val total get() = demands.values.sumOf { it.total }
    fun convert(location: Location): CommunityNumber {
        return converter(location)
    }

    /**
     * Returns a mutable copy of the demands originating from the target community number. Note that the Mutable Demand
     * is intentionally used as a copy so that other operations may alter the state of the copy, whereas the demand in
     * the matrix remains the same.
     * @param communityNumber the origin community number
     */
    operator fun get(communityNumber: CommunityNumber): MutableCommunityDemand {
        val out = demands[communityNumber] ?: MutableCommunityDemand(converter, communityID = communityNumber)
        return out.copy()
    }

    operator fun get(i: Number): MutableCommunityDemand = get(i.toCommunity())

    /**
     *  Get the particular demand of commute between communityNumber i and j
     */
    operator fun get(i: CommunityNumber, j: CommunityNumber): Double = get(i)[j]
    operator fun get(i: Number, j: Number): Double = get(i.toCommunity(), j.toCommunity())

    /**
     * Set the demand of commute between communityNumber i and j to the target value.
     */
    operator fun set(i: CommunityNumber, j: CommunityNumber, value: Double) {
        val demandForI = demands.getOrPut(i) { MutableCommunityDemand(converter, communityID = j) }
        demandForI[j] = value
    }

    operator fun set(i: Number, j: Number, value: Double) = set(i.toCommunity(), j.toCommunity(), value)

    private fun Number.toCommunity(): CommunityNumber = CommunityNumber(this.toInt())

    override fun toString(): String = demands.toString()

    companion object {
        /**
         * Build a [CommuterDemandsMatrix] from file input, using a mapping of zones to communities and a commuter file
         * containing the demands.
         * The converter is converting the zoneId of the location based on the file mapping and thus requires the presence
         * of the zone field within the location.
         */
        fun parse(
            mappingFile: Path,
            commuterFile: Path,
        ): CommuterDemandsMatrix {
            val match = readZoneToCommunity(mappingFile)
            return readCommuters(commuterFile) {
                val zoneID = it.zoneID() ?: run {
                    println("Bad Zone")
                    ZoneId(-1)
                }

                require(match.containsKey(zoneID)) {
                    "Zone id $zoneID cannot be converted to a community number." +
                        " Check that the file: $mappingFile contains the zone ID"
                }
                match.getValue(zoneID)
            }
        }

        private fun readZoneToCommunity(file: Path): Map<ZoneId, CommunityNumber> {
            val parser = DefaultCsvParser { row ->
                Pair(
                    row("partId") { ZoneId(it.toLong()) },
                    row("regionId") { CommunityNumber.parse(it) },

                )
            }

            return parser.parse(file).toMap()
        }

        private fun readCommuters(file: Path, converter: (Location) -> CommunityNumber): CommuterDemandsMatrix {
            val communityDemand = CommuterDemandsMatrix(converter = converter)
            val parser = DefaultCsvParser { row ->
                CommuterInfo(
                    row("origin") { CommunityNumber.parse(it) },
                    row("destination") { CommunityNumber.parse(it) },
                    row("commuters").toInt(),

                )
            }
            parser.parse(file).forEach { (i, j, value) -> communityDemand[i, j] = value.toDouble() }
            return communityDemand
        }
    }

    data class CommuterInfo(val origin: CommunityNumber, val destination: CommunityNumber, val amount: Int)
}

/**
 * A read only view of the commute demand for an unknown input community number. This class provides all methods that
 * do not alter the state of the underlying demand.
 */
open class CommunityDemand(
    protected val converter: (Location) -> CommunityNumber,
    protected val demands: MutableMap<CommunityNumber, Double> = mutableMapOf(),
    val communityID: CommunityNumber
) {
    val total get() = demands.values.sum()
    val keys get() = demands.keys
    fun isEmpty() = demands.isEmpty()
    fun isNotEmpty() = demands.isNotEmpty()
    operator fun get(j: CommunityNumber): Double {
        return demands[j] ?: 0.0
    }

    operator fun get(j: Number): Double = get(j.toCommunity())

    operator fun contains(j: CommunityNumber): Boolean {
        return j in demands.keys
    }

    operator fun contains(j: Number): Boolean = contains(j.toCommunity())

    /**
     * A demand to a certain community is saturated once the demand has dropped below a positive number
     */
    fun isSaturated(j: CommunityNumber): Boolean {
        return get(j) <= 0.0
    }

    fun isSaturated(location: Location): Boolean = isSaturated(converter(location))
    fun isSaturated(j: Number): Boolean = isSaturated(j.toCommunity())

    override fun toString(): String = demands.toString()

    protected fun Number.toCommunity(): CommunityNumber = CommunityNumber(this.toInt())
}

/**
 * The Mutable Community Demand allows the alteration of demand, and provides the decreaseDemand method as a convenience
 * method to decrease the demand once an agent has been assigned.
 */
class MutableCommunityDemand(
    converter: (Location) -> CommunityNumber,
    demands: MutableMap<CommunityNumber, Double> = mutableMapOf(),
    communityID: CommunityNumber
) :
    CommunityDemand(converter, demands, communityID) {

    operator fun set(j: CommunityNumber, value: Double) {
        demands[j] = value
    }

    operator fun set(j: Number, value: Double) = set(j.toCommunity(), value)

    fun copy(): MutableCommunityDemand = MutableCommunityDemand(converter, demands.toMutableMap(), communityID)

    /**
     * Decrease the demand towards the target community number by 1. If the demand is not present, add it and set it to
     * 0
     */
    fun decreaseDemandFor(j: CommunityNumber) {
        demands[j] = (demands[j] ?: 1.0) - 1.0
    }

    fun decreaseDemandFor(location: Location) = decreaseDemandFor(converter(location))
}
