package domain.simulation.data

import utils.binary.BinaryWritable
import java.io.DataOutputStream

data class ActivityBinaryRecord(
    val id: Long,
    val personId: Long,
    val observedTripDuration: Int,
    val startTime: Long,
    val duration: Int,
    val activityCode: Int,
) : BinaryWritable {
    override fun writeTo(outStream: DataOutputStream) {
        outStream.run {
            writeLong(id)
            writeLong(personId)
            writeInt(observedTripDuration)
            writeLong(startTime)
            writeInt(duration)
            writeInt(activityCode)
        }
    }
}
