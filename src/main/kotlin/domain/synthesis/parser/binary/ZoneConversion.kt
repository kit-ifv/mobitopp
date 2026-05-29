package domain.synthesis.parser.binary

import domain.jackson.BinaryWritable
import domain.shared.enums.areatype.RegionType
import domain.shared.location.ZoneId
import domain.shared.location.ZonedRoadAccessLocation
import domain.shared.location.zone.StandardZone
import domain.shared.location.zone.ZoneAttributes
import domain.synthesis.parser.binary.LocationUtils.decodeNakedLocation
import domain.synthesis.parser.binary.LocationUtils.encodeLocation
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
class BinaryZoneReader(val seed: Long, private val regionCode: Decodable<RegionType>) : BinaryReader<StandardZone> {
    override fun fromBinary(path: Path): List<StandardZone> {
        val byteBuffer = path.readAsByteBuffer()
        byteBuffer.long // Consume hash code at start of file
        val size = byteBuffer.int
        val stringLength = byteBuffer.int

        var elements = ArrayList<StandardZone>(size)
        repeat(size) {
            elements.add(byteBuffer.decode(stringLength))
        }
//        elements.withIndex().forEach { (i, zone) -> zone.matrixColumn = i }
        return elements
    }

    override fun ByteBuffer.decode(stringLength: Int): StandardZone {
        val zoneId = ZoneId(long)
        val position = decodeNakedLocation().position
        long // advance the reader, the visumId field is no longer needed in the construction of a zone
        readString(stringLength) // advance and drop the name field
        val regionType = regionCode.decode(int)
        int // drop classification
        int // drop parking places
        getBoolean() // drop is destination
        double // drop relief
        return StandardZone(
            zoneId,
            position,
            ZoneAttributes(regionType),
        )
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

class BinaryZoneWriter : BinaryWriter<StandardZone> {

    override fun operateStream(outStream: DataOutputStream, elements: Collection<StandardZone>) {
        val maxStringLength = getMaxStringSize(elements)
        elements.forEach { outStream.encodeZone(it, maxStringLength) }
    }
    private val fakeName = "FakeName"
    override fun getMaxStringSize(elements: Collection<StandardZone>): Int = elements.maxOf {
//            it.name.length
        fakeName.length
    }

    fun DataOutputStream.encodeZone(zone: StandardZone, maxNameLength: Int) {
        zone.run {
            writeLong(id.value)
            encodeLocation(centroidLocation)
            writeLong(-1L)
            // Note that the matrix column field is not written, it is simply an index, and can thus be parsed in the
            // reader
            writeString(fakeName, maxNameLength)
            writeInt(attributes.regionType.code)
            writeInt(-1) // classification
            writeInt(-1) // parking places
            writeBoolean(true) // isDestination
            writeDouble(0.0) // relief
        }
    }
}
