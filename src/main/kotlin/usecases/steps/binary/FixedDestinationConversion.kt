package usecases.steps.binary

import domain.data.Person
import domain.data.PersonId
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.ActivityType
import usecases.steps.ActivityLocation
import utils.CodePlan
import java.io.DataOutputStream
import java.nio.MappedByteBuffer
import java.nio.file.Path
@Suppress("MagicNumber")
class FixedDestinationReader(
    val personConverter: (PersonId) -> Person,
    private val activityTypeConverter: CodePlan<ActivityType>,
    val zoneConverter: (ZoneId) -> Zone
) : BinaryReader<ActivityLocation> {
    override fun fromBinary(path: Path): List<ActivityLocation> {
        return path.operateOnMemoryFile {
            val size = this.getInt(0)
            (0 until size).map {
                extractContent(this, it * elementByteSize + 4)
            }
        }
    }

    private fun extractContent(buffer: MappedByteBuffer, at: Int): ActivityLocation {
        return TrackingBuffer(buffer, at).run {
            val person = personConverter(PersonId(nextLong))
            val activityType = activityTypeConverter.decode(nextInt)
            val location = nextLocation(zoneConverter)

            ActivityLocation(
                person,
                activityType,
                location
            )
        }
    }

    private val elementByteSize = 60
}

class FixedDestinationWriter : BinaryWriter<ActivityLocation> {
    override fun operateStream(outStream: DataOutputStream, elements: Collection<ActivityLocation>) {
        val size = elements.size
        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file
        elements.forEach { outStream.encodeElement(it) }
    }

    private fun DataOutputStream.encodeElement(act: ActivityLocation) {
        act.run {
            writeLong(person.id.value) //  8 Bytes
            writeInt(activityType.code) // 12 Bytes
            writeLong(location.zone?.id?.value ?: -1) // 20 Bytes
            writeLocation(location) // 60 Bytes

            // TODO maybe add lateral distance if needed.
        }
    }
}
