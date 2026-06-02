package domain.synthesis.parser.binary

import domain.shared.enums.ActivityType
import domain.shared.location.zone.Zone
import domain.shared.location.zone.ZoneId
import domain.shared.location.zone.attributes.HasRegionType
import domain.synthesis.data.PersonId
import domain.synthesis.parser.ActivityLocation
import domain.synthesis.parser.binary.LocationUtils.decodeLocation
import utils.CodePlan
import utils.binary.BinaryReader
import utils.binary.DefaultBinaryWriter
import java.nio.ByteBuffer

@Suppress("MagicNumber")
class FixedDestinationReader(
//    val personConverter: (PersonId) -> Person?,
    private val activityTypeConverter: CodePlan<ActivityType>,
    val zoneConverter: (ZoneId) -> Zone<HasRegionType>,
) : BinaryReader<ActivityLocation> {

    override fun ByteBuffer.decode(stringLength: Int): ActivityLocation? {
        val personId = PersonId(long)
        val activityType = activityTypeConverter.decode(int)
        val location = decodeLocation(zoneConverter)
        return ActivityLocation(personId, activityType, location)
    }
}

class FixedDestinationWriter : DefaultBinaryWriter<ActivityLocation>() {
//    override fun operateStream(outStream: DataOutputStream, elements: Collection<ActivityLocation>) {
//        val size = elements.size
//        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file
//        outStream.writeInt(0) // string length, not needed but required by format
//        elements.forEach { outStream.encodeElement(it) }
//    }
//
//    private fun DataOutputStream.encodeElement(act: ActivityLocation) {
//        act.run {
//            writeLong(person.id.value) //  8 Bytes
//            writeInt(activityType.code) // 12 Bytes
// //            writeLong(location.zone?.id?.value ?: -1) // 20 Bytes
//            encodeLocation(location) // 60 Bytes
//
//            // TODO maybe add lateral distance if needed.
//        }
//    }
}
