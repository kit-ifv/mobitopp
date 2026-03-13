package domain.synthesis.parser

import domain.jackson.BinaryWritable
import domain.jackson.Simplifiable
import domain.shared.enums.ActivityType
import domain.shared.location.StandardLocation
import domain.synthesis.data.Person
import domain.synthesis.parser.binary.LocationUtils.encodeLocation
import java.io.DataOutputStream

data class ActivityLocation(
    val person: Person,
    val activityType: ActivityType,
    val location: StandardLocation
) : Simplifiable<ActivityLocationBinaryRecord> {
    override fun simplify(): ActivityLocationBinaryRecord {
        return ActivityLocationBinaryRecord(
            person.id.value,
            activityType.code,
            location,
        )
    }
}

data class ActivityLocationBinaryRecord(
    val personId: Long,
    val activityCode: Int,
    val location: StandardLocation,
) : BinaryWritable {
    override fun writeTo(outStream: DataOutputStream) {
        outStream.writeLong(personId)
        outStream.writeInt(activityCode)
        outStream.encodeLocation(location)
    }
}
