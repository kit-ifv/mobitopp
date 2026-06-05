package edu.kit.ifv.domain.simulation.data
import edu.kit.ifv.domain.shared.data.person.PersonId
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.ZonedRoadAccessLocationRecord
import edu.kit.ifv.domain.shared.location.parser.LocationUtils.encodeLocation
import edu.kit.ifv.utils.binary.BinaryWritable
import edu.kit.ifv.utils.binary.Simplifiable
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
