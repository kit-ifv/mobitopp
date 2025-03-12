package utils.binary

import domain.data.EconomicStatus
import domain.data.Household
import domain.data.HouseholdId
import domain.data.MutableHousehold
import domain.data.Zone
import domain.data.ZoneId
import units.CurrencyUnit
import units.euros
import java.io.DataOutputStream
import java.nio.MappedByteBuffer
import java.nio.file.Path

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

    private val idByteSize = 8
    override fun fromBinary(path: Path): List<MutableHousehold> {
        return path.operateOnMemoryFile {
            val size = getInt(0)
            val ids = Array(size) {
                HouseholdId(-1)
            }

            for (i in 0 until size) {
                ids[i] = HouseholdId(getLong(i * idByteSize + 4))
            }
            val households = ids.map {
                MutableHousehold(
                    it,
                    contextSimulationSeed
                )
            }
            for (i in 0 until size) {
                extractInfos(this, i * attributesByteSize + 4 + size * idByteSize, households[i])
            }
            households
        }
    }

    private val attributesByteSize = 72
    private fun extractInfos(buffer: MappedByteBuffer, at: Int, household: MutableHousehold) {
        TrackingBuffer(buffer, at).run {
            household.apply {
                householdNumber = nextLong
                surveyYear = nextInt
                domCode = nextInt
                type = nextInt
                incomePerMonth = nextDouble.euros
                economicStatus = EconomicStatus.decode(nextInt)
                location = nextLocation(converter = zoneConverter)
            }
        }
    }
}

/**
 * Writes a collection of households into a binary file.
 */
class BinaryHouseholdWriter : BinaryWriter<Household> {
    override fun operateStream(outStream: DataOutputStream, elements: Collection<Household>) {
        outStream.writeInt(elements.size) // Write Size as Int in the beginning of the file
        // Separate the writing to mimic the construction of the object, so first write all the necessary constructor parameters
        elements.forEach { outStream.writeIDS(it) }
        // And then write all the secondary attributes that are set afterwards.
        elements.forEach { outStream.writeHouseholdAttributes(it) }
    }

    private fun DataOutputStream.writeIDS(element: Household) {
        writeLong(element.id.value) //  8 Bytes
    }

    private fun DataOutputStream.writeHouseholdAttributes(element: Household) {
        element.run {
            writeLong(householdNumber) //  8 Bytes
            writeInt(surveyYear) // 12 Bytes
            writeInt(domCode) // 16 Bytes
            writeInt(type) // 20 Bytes
            writeDouble(incomePerMonth.toDouble(CurrencyUnit.EUROS)) // 28 Bytes
            writeInt(economicStatus.encode()) // 32 Bytes
            writeLocation(location) // 72 Bytes
        }
    }
}
