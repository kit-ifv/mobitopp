package edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.communitybased
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.utils.csv.DefaultCsvParser
import java.nio.file.Path

/**
 * A commuter demand matrix holds the information of commuter demands between community numbers.
 * @property demands The commute demands for an input community number, represented by a [MutableCommunityDemand]
 * @property converter a converter function to determine the corresponding community number for a given location.
 */
class CommuterDemandsMatrix(
    private val demands: MutableMap<CommunityNumber, MutableCommunityDemand> = mutableMapOf(),
    private val converter: (StandardLocation) -> CommunityNumber,
) {

    val total get() = demands.values.sumOf { it.total }
    fun convert(location: StandardLocation): CommunityNumber = converter(location)

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
        fun parse(mappingFile: Path, commuterFile: Path): CommuterDemandsMatrix {
            val match = readZoneToCommunity(mappingFile)
            return readCommuters(commuterFile) {
                val zoneID = it.zoneId

                match[zoneID] ?: CommunityNumber.INVALID.also {
                    "Zone id $zoneID cannot be converted to a community number." +
                        " Check that the file: $mappingFile contains the zone ID"
                }
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

        private fun readCommuters(file: Path, converter: (StandardLocation) -> CommunityNumber): CommuterDemandsMatrix {
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
