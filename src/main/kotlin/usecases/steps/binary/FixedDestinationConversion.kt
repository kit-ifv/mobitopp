package usecases.steps.binary

import domain.data.Person
import domain.data.PersonId
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.ActivityType
import usecases.steps.ActivityLocation
import usecases.steps.binary.LocationUtils.decodeLocation
import usecases.steps.binary.LocationUtils.encodeLocation
import utils.CodePlan
import java.io.DataInputStream
import java.io.DataOutputStream

@Suppress("MagicNumber")
class FixedDestinationReader(
    val personConverter: (PersonId) -> Person,
    private val activityTypeConverter: CodePlan<ActivityType>,
    val zoneConverter: (ZoneId) -> Zone
) : BinaryReader<ActivityLocation> {

    override fun DataInputStream.decode(stringLength: Int): ActivityLocation {
        val person = personConverter(PersonId(readLong()))
        val activityType = activityTypeConverter.decode(readInt())
        val location = decodeLocation(zoneConverter)

        return ActivityLocation(
            person,
            activityType,
            location
        )
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
