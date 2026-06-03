package domain.synthesis.parser

import domain.shared.enums.ActivityType
import domain.shared.location.StandardLocation
import domain.shared.location.ZonedRoadAccessLocationRecord
import domain.synthesis.data.person.PersonId
import domain.synthesis.parser.binary.LocationUtils.encodeLocation
import utils.binary.BinaryWritable
import utils.binary.Simplifiable
import java.io.DataOutputStream

data class ActivityLocation(val personId: PersonId, val activityType: ActivityType, val location: StandardLocation) :
    Simplifiable<ActivityLocationBinaryRecord> {
    override fun simplify(): ActivityLocationBinaryRecord = ActivityLocationBinaryRecord(
        personId.value,
        activityType.code,
        location,
    )
}

data class ActivityLocationBinaryRecord(
    val personId: Long,
    val activityCode: Int,
    val location: ZonedRoadAccessLocationRecord,
) : BinaryWritable {
    constructor(personId: Long, activityCode: Int, location: StandardLocation) : this(
        personId,
        activityCode,
        location.toRecord(),
    )
    override fun writeTo(outStream: DataOutputStream) {
        outStream.writeLong(personId)
        outStream.writeInt(activityCode)
        outStream.encodeLocation(location)
    }
}
