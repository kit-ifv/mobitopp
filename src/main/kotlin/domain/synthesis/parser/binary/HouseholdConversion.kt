package domain.synthesis.parser.binary

import domain.synthesis.data.EconomicStatus
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.Zone
import domain.synthesis.data.ZoneId
import domain.synthesis.parser.binary.LocationUtils.decodeLocation
import domain.synthesis.parser.binary.LocationUtils.encodeLocation
import units.CurrencyUnit
import units.euros
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import java.io.DataInputStream
import java.io.DataOutputStream

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

    override fun DataInputStream.decode(stringLength: Int): MutableHousehold {
        return MutableHousehold(
            HouseholdId(readLong()),
            contextSimulationSeed
        ).apply {
            householdNumber = readLong()
            surveyYear = readInt()
            domCode = readInt()
            type = readInt()
            incomePerMonth = readDouble().euros
            economicStatus = EconomicStatus.decode(readInt())
            location = decodeLocation(converter = zoneConverter)
        }
    }
}

/**
 * Writes a collection of households into a binary file.
 */
class BinaryHouseholdWriter : BinaryWriter<Household> {
    override fun operateStream(outStream: DataOutputStream, elements: Collection<Household>) {
        outStream.writeInt(elements.size) // Write Size as Int in the beginning of the file
        outStream.writeInt(0) // Format requires string size to be specified.

        elements.forEach { outStream.encodeHousehold(it) } // write all elements
    }

    private fun DataOutputStream.encodeHousehold(element: Household) {
        writeLong(element.id.value)
        element.run {
            writeLong(householdNumber)
            writeInt(surveyYear)
            writeInt(domCode)
            writeInt(type)
            writeDouble(incomePerMonth.toDouble(CurrencyUnit.EUROS))
            writeInt(economicStatus.code)
            encodeLocation(location)
        }
    }
}
