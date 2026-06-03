package domain.simulation.data

import utils.binary.BinaryWritable
import utils.binary.Simplifiable
import domain.shared.enums.ActivityType
import domain.shared.location.StandardLocation
import domain.shared.location.ZonedRoadAccessLocationDTO
import domain.shared.location.parser.LocationUtils.encodeLocation
import domain.shared.location.toDTO
import domain.simulation.data.person.PersonId
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
    val location: ZonedRoadAccessLocationDTO,
) : BinaryWritable {
    constructor(personId: Long, activityCode: Int, location: StandardLocation) : this(
        personId,
        activityCode,
        location.toDTO(),
    )
    override fun writeTo(outStream: DataOutputStream) {
        outStream.writeLong(personId)
        outStream.writeInt(activityCode)
        outStream.encodeLocation(location)
    }
}
