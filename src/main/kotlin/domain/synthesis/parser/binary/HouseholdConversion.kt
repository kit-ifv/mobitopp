package domain.synthesis.parser.binary

import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.parser.binary.LocationUtils.decodeLocation
import edu.kit.ifv.units.euros
import utils.binary.BinaryReader
import utils.binary.DefaultBinaryWriter
import java.nio.ByteBuffer

/**
 * Reads a [MutableHousehold] from a binary file. Similar to other readers it firsts reads at position 0 the size,
 * then the constructor arguments and third the attributes of the constructed objects.
 *
 * @property zoneConverter Required to find the proper Zone from the ZoneId in the Binary file
 * @property contextSimulationSeed an external factor that is needed in the mutable households and is not written to
 * the binary file.
 */
@Suppress("MagicNumber")
class BinaryHouseholdReader(private val zoneConverter: (ZoneId) -> Zone, private val contextSimulationSeed: Long) :
    BinaryReader<MutableHousehold> {

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
            contextSimulationSeed
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
class BinaryHouseholdWriter : DefaultBinaryWriter<Household>() {
//    override fun operateStream(outStream: DataOutputStream, elements: Collection<Household>) {
//        outStream.writeInt(elements.size) // Write Size as Int in the beginning of the file
//        outStream.writeInt(0) // Format requires string size to be specified.
//
//        elements.forEach { outStream.encodeHousehold(it) } // write all elements
//    }
//
//    private fun DataOutputStream.encodeHousehold(element: Household) {
//        writeLong(element.id.value)
//        element.run {
//            writeLong(householdNumber)
//            writeInt(surveyYear)
//            writeInt(domCode)
//            writeInt(type)
//            writeDouble(incomePerMonth.toDouble(CurrencyUnit.EUROS))
//            writeInt(economicStatus.code)
//            encodeLocation(location)
//        }
//    }
}
