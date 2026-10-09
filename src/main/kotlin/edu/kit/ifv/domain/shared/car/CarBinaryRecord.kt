package edu.kit.ifv.domain.shared.car
import edu.kit.ifv.binary.BinaryWritable
import java.io.DataOutputStream

data class CarBinaryRecord(
    val id: Long,
    val ownerId: Long,
    val seats: Int,
    val mainUserId: Long,
    val segmentCode: Int,
    val engineCode: Int,
) : BinaryWritable {
    override fun writeTo(outStream: DataOutputStream) {
        outStream.run {
            writeLong(id)
            writeLong(ownerId)
            writeInt(seats)
            writeLong(mainUserId)
            writeInt(segmentCode)
            writeInt(engineCode)
        }
    }
}
