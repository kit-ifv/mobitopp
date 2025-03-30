package usecases.steps.binary

import domain.data.MutableLegacyZone
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.AreaType
import domain.enums.ZoneClassification
import units.DistanceUnit
import units.toDistance
import usecases.steps.binary.LocationUtils.decodeLocation
import utils.Decodable
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.file.Path
@Suppress("MagicNumber")
class BinaryZoneReader(val seed: Long, private val regionCode: Decodable<AreaType>) : BinaryReader<MutableLegacyZone> {
    override fun fromBinary(path: Path): List<MutableLegacyZone> {
        return createInputStream(path).use { dataStream ->
            val size = dataStream.readInt()
            val maxStringLength = dataStream.readInt()
            val zones = Array(size) {
                dataStream.decodeZone(maxStringLength)
            }
            zones.withIndex().forEach { (i, zone) -> zone.matrixColumn = i }
            zones.toList()
        }
    }

    private fun DataInputStream.decodeZone(maxStringLength: Int): MutableLegacyZone {
        return MutableLegacyZone(
            ZoneId(readLong()),
            // Since the zone is not yet built there is no way to map it to the correct zone,
            // that step happens in the zone constructor.
            decodeLocation { null },
            seed
        ).apply {
            visumId = readLong()
            name = readString(maxStringLength)
            regionType = regionCode.decode(readInt())
            classification = ZoneClassification.decode(readInt())
            parkingPlaces = readInt()
            isDestination = readBoolean()
            relief = readDouble().toDistance(DistanceUnit.METERS)
        }
    }
}

class BinaryZoneWriter : BinaryWriter<Zone> {
    override fun operateStream(outStream: DataOutputStream, elements: Collection<Zone>) {
        val size = elements.size
        val maxStringLength =
            elements.maxOf {
                it.name.length
            }
        // It is idiotic to give the zones a name, it was never used in old mobitopp,
        // and won't be used in new mobitopp
        outStream.writeInt(size) // Write the amount of zones found in the simulation
        outStream.writeInt(maxStringLength)
        elements.forEach { outStream.encodeZone(it, maxStringLength) }
    }

    fun DataOutputStream.encodeZone(zone: Zone, maxNameLength: Int) {
        zone.run {
            writeLong(id.value)
            writeLocation(centroid)
            writeLong(visumId)
            // Note that the matrix column field is not written, it is simply an index, and can thus be parsed in the
            // reader
            writeChars(name.padEnd(maxNameLength, '.'))
            writeInt(regionType.encode())
            writeInt(classification.encode())
            writeInt(parkingPlaces)
            writeBoolean(isDestination)
            writeDouble(relief.toDouble(DistanceUnit.METERS))
        }
    }
}
