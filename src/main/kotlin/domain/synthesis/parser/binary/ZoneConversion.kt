package domain.synthesis.parser.binary

import domain.jackson.BinaryWritable
import domain.shared.enums.ZoneClassification
import domain.shared.enums.areatype.RegionType
import domain.shared.location.MutableLegacyZone
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.shared.location.ZonedRoadAccessLocation
import domain.synthesis.parser.binary.LocationUtils.decodeLocation
import domain.synthesis.parser.binary.LocationUtils.decodeNakedLocation
import domain.synthesis.parser.binary.LocationUtils.encodeLocation
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.toDistance
import utils.Decodable
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import utils.binary.readAsByteBuffer
import utils.binary.readString
import utils.binary.writeString
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.nio.file.Path

@Suppress("MagicNumber")
class BinaryZoneReader(
    val seed: Long,
    private val regionCode: Decodable<RegionType>,
) : BinaryReader<MutableLegacyZone> {
    override fun fromBinary(path: Path): List<MutableLegacyZone> {
        val byteBuffer = path.readAsByteBuffer()
        byteBuffer.long // Consume hash code at start of file
        val size = byteBuffer.int
        val stringLength = byteBuffer.int

        var elements = ArrayList<MutableLegacyZone>(size)
        repeat(size) {
            elements.add(byteBuffer.decode(stringLength))
        }
        elements.withIndex().forEach { (i, zone) -> zone.matrixColumn = i }
        return elements
    }

    override fun ByteBuffer.decode(stringLength: Int): MutableLegacyZone {
        return MutableLegacyZone(
            ZoneId(long),
            decodeNakedLocation() ,
            seed
        ).apply {
            visumId = long
            name = readString(stringLength)
            regionType = regionCode.decode(int)
            classification = ZoneClassification.decode(int)
            parkingPlaces = int
            isDestination = getBoolean()
            relief = double.toDistance(DistanceUnit.METERS)
        }
    }
}

// TODO string really hampers the construction as the maxlength is unknown
data class ZoneBinaryRecord(
    val id: Long,
    val centroid: ZonedRoadAccessLocation,
    val visumId: Long,
    val name: String,
    val regionTypeCode: Int,
    val classificationCode: Int,
    val parkingPlaces: Int,
    val isDestination: Boolean,
    val relief: Double,

    ) : BinaryWritable {
    override fun writeTo(outStream: DataOutputStream) {
        outStream.run {
            writeLong(id)
            encodeLocation(centroid)
            writeLong(visumId)
            writeChars(name)
            writeInt(parkingPlaces)
            writeBoolean(isDestination)
            writeDouble(relief)
        }
    }
}

class BinaryZoneWriter : BinaryWriter<Zone> {

    override fun operateStream(outStream: DataOutputStream, elements: Collection<Zone>) {
        val maxStringLength = getMaxStringSize(elements)
        elements.forEach { outStream.encodeZone(it, maxStringLength) }
    }

    override fun getMaxStringSize(elements: Collection<Zone>): Int {
        return elements.maxOf { it.name.length }
    }

    fun DataOutputStream.encodeZone(zone: Zone, maxNameLength: Int) {
        zone.run {
            writeLong(id.value)
            encodeLocation(centroid)
            writeLong(visumId)
            // Note that the matrix column field is not written, it is simply an index, and can thus be parsed in the
            // reader
            writeString(name, maxNameLength)
            writeInt(regionType.code)
            writeInt(classification.code)
            writeInt(parkingPlaces)
            writeBoolean(isDestination)
            writeDouble(relief.toDouble(DistanceUnit.METERS))
        }
    }
}
