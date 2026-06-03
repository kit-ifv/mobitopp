package domain.synthesis.data.household

import domain.jackson.BinaryWritable
import domain.shared.location.ZonedRoadAccessLocationDTO
import domain.shared.location.parser.LocationUtils.encodeLocation
import java.io.DataOutputStream

data class HouseholdBinaryRecord(
    val id: Long,
    val householdNumber: Long,
    val surveyYear: Int,
    val domCode: Int,
    val type: Int,
    val incomePerMonth: Double,
    val economicStatusCode: Int,
    val location: ZonedRoadAccessLocationDTO,
) : BinaryWritable {
    override fun writeTo(outStream: DataOutputStream) {
        outStream.run {
            writeLong(id)
            writeLong(householdNumber)
            writeInt(surveyYear)
            writeInt(domCode)
            writeInt(type)
            writeDouble(incomePerMonth)
            writeInt(economicStatusCode)
            encodeLocation(location)
        }
    }
}
