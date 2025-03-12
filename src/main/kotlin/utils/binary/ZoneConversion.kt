package utils.binary

import domain.data.MutableLegacyZone
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.AreaType
import domain.enums.ZoneClassification
import domain.location.LOCATIONUNKNOWN
import domain.location.Location
import units.DistanceUnit
import units.toDistance
import utils.Decodable
import java.io.DataOutputStream
import java.nio.MappedByteBuffer
import java.nio.file.Path
@Suppress("MagicNumber")
class BinaryZoneReader(val seed: Long, private val regionCode: Decodable<AreaType>) : BinaryReader<MutableLegacyZone> {
    override fun fromBinary(path: Path): List<MutableLegacyZone> {
        return path.operateOnMemoryFile {
            val size = this.getInt(0)
            val maxNameLength = this.getInt(4)
            val idArray = Array(size) {
                ZoneId(-1L) to LOCATIONUNKNOWN
            }

            for (i in 0 until size) {
                idArray[i] = extractIds(this, i * idByteSize + 8) // Since we are reading Size and
                // (MaxNameLength) we have an offset of 8 bytes and not 4
            }
            val zones = idArray.map {
                MutableLegacyZone(
                    it.first,
                    it.second,
                    seed
                )
            }
            for (i in 0 until size) {
                extractInfos(
                    this,
                    i * (attributeByteSize + maxNameLength * 2) +
                        8 + size * idByteSize,
                    zones[i],
                    maxNameLength = maxNameLength
                )
            }
            zones.withIndex().forEach { (i, zone) -> zone.matrixColumn = i }
            zones
        }
    }

    // 48 Bytes
    private fun extractIds(buffer: MappedByteBuffer, at: Int): Pair<ZoneId, Location> {
        return TrackingBuffer(buffer, at).run {
            val id = nextLong
            val centroid = nextLocation {
                null
            } // Since the zone is not yet built there is no way to map it to the correct zone, that step happens in the zone constructor.
            ZoneId(id) to centroid
        }
    }

    private val idByteSize = 48

    // 29 + ???? Bytes (Name is variable in length)
    private fun extractInfos(buffer: MappedByteBuffer, at: Int, zone: MutableLegacyZone, maxNameLength: Int) {
        TrackingBuffer(buffer, at).run {
            zone.apply {
                visumId = nextLong // 8
                name = readString(maxNameLength) // ??
                regionType = regionCode.decode(nextInt) // 12
                classification = ZoneClassification.decode(nextInt) // 16
                parkingPlaces = nextInt // 20
                isDestination = nextBoolean // 21
                relief = nextDouble.toDistance(DistanceUnit.METERS) // 29
            }
        }
    }

    private val attributeByteSize = 29
}

class BinaryZoneWriter : BinaryWriter<Zone> {
    override fun operateStream(outStream: DataOutputStream, elements: Collection<Zone>) {
        val size = elements.size
        val maxNameLength =
            elements.maxOf {
                it.name.length
            } // It is idiotic to give the zones a name, it was never used in old mobitopp, and won't be used in new mobitopp
        outStream.writeInt(size) // Write the amount of zones found in the simulation
        outStream.writeInt(maxNameLength)
        elements.forEach { outStream.encodeID(it) } // Encode constructor arguments of MutableZone
        elements.forEach {
            outStream.encodeAttributes(it, maxNameLength)
        } // Encode Secondary arugments, which are set in the builder
    }

    private fun DataOutputStream.encodeID(zone: Zone) {
        zone.run {
            writeLong(id.value)
            writeLocation(centroid)
        }
    }

    private fun DataOutputStream.encodeAttributes(zone: Zone, maxNameLength: Int) {
        zone.run {
            writeLong(visumId)
            // Note that the matrix column field is not written, it is simply an index, and can thus be parsed in the reader
            writeChars(name.padEnd(maxNameLength, '.'))
            writeInt(regionType.encode())
            writeInt(classification.encode())
            writeInt(parkingPlaces)
            writeBoolean(isDestination)
            writeDouble(relief.toDouble(DistanceUnit.METERS))
        }
    }
}
