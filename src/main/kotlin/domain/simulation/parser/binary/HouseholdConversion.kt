package domain.simulation.parser.binary

import domain.shared.enums.household.EconomicStatus
import domain.shared.location.parser.LocationUtils.decodeLocation
import domain.shared.location.zone.Zone
import domain.shared.location.zone.ZoneId
import domain.shared.location.zone.attributes.HasRegionType
import domain.simulation.data.household.Household
import domain.simulation.data.household.HouseholdId
import domain.simulation.data.household.MutableHousehold
import edu.kit.ifv.units.euros
import utils.binary.BinaryReader
import utils.binary.DefaultBinaryWriter
import java.nio.ByteBuffer

/**
 * Reads a [MutableHousehold] from a binary file. Similar to other readers, it first reads at position 0 the size,
 * then the constructor arguments and third the attributes of the constructed objects.
 *
 * @property zoneConverter Required to find the proper Zone from the ZoneId in the Binary file
 * @property contextSimulationSeed an external factor that is needed in the mutable households and is not written to
 * the binary file.
 */
@Suppress("MagicNumber")
class BinaryHouseholdReader(
    private val zoneConverter: (ZoneId) -> Zone<HasRegionType>,
    private val contextSimulationSeed: Long,
) : BinaryReader<MutableHousehold> {

    override fun ByteBuffer.decode(stringLength: Int): MutableHousehold {
        val id = HouseholdId(long)
        val householdNumber = long
        val surveyYear = int
        val domCode = int
        val type = int
        val incomePerMonth = double.euros
        val economicStatus = EconomicStatus.decode(int)
        val location = decodeLocation(converter = zoneConverter)
        return MutableHousehold(
            id,
            contextSimulationSeed,
        ).apply {
            this.householdNumber = householdNumber
            this.surveyYear = surveyYear
            this.domCode = domCode
            this.type = type
            this.incomePerMonth = incomePerMonth
            this.economicStatus = economicStatus
            this.location = location
        }
    }
}

/**
 * Writes a collection of households into a binary file.
 */
class BinaryHouseholdWriter : DefaultBinaryWriter<Household>()
