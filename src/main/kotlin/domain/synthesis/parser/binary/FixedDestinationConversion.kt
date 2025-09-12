package domain.synthesis.parser.binary

import domain.shared.enums.ActivityType
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import domain.synthesis.parser.ActivityLocation
import domain.synthesis.parser.binary.LocationUtils.decodeLocation
import domain.synthesis.parser.binary.LocationUtils.encodeLocation
import utils.CodePlan
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import java.io.DataOutputStream
import java.nio.ByteBuffer

@Suppress("MagicNumber")
class FixedDestinationReader(
    val personConverter: (PersonId) -> Person?,
    private val activityTypeConverter: CodePlan<ActivityType>,
    val zoneConverter: (ZoneId) -> Zone
) : BinaryReader<ActivityLocation> {

    override fun ByteBuffer.decode(stringLength: Int): ActivityLocation? {
        val person = personConverter(PersonId(long))
        val activityType = activityTypeConverter.decode(int)
        val location = decodeLocation(zoneConverter)
        return person?.let {
            ActivityLocation(it, activityType, location)
        }
    }
}

class FixedDestinationWriter : BinaryWriter<ActivityLocation> {
    override fun operateStream(outStream: DataOutputStream, elements: Collection<ActivityLocation>) {
        val size = elements.size
        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file
        outStream.writeInt(0) // string length, not needed but required by format
        elements.forEach { outStream.encodeElement(it) }
    }

    private fun DataOutputStream.encodeElement(act: ActivityLocation) {
        act.run {
            writeLong(person.id.value) //  8 Bytes
            writeInt(activityType.code) // 12 Bytes
            writeLong(location.zone?.id?.value ?: -1) // 20 Bytes
            encodeLocation(location) // 60 Bytes

            // TODO maybe add lateral distance if needed.
        }
    }
}
