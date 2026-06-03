package domain.synthesis.data.household

import domain.shared.location.ZonedRoadAccessLocationRecord
import domain.synthesis.parser.binary.LocationUtils.encodeLocation
import utils.binary.BinaryWritable
import java.io.DataOutputStream

data class HouseholdBinaryRecord(
    val id: Long,
    val householdNumber: Long,
    val surveyYear: Int,
    val domCode: Int,
    val type: Int,
    val incomePerMonth: Double,
    val economicStatusCode: Int,
    val location: ZonedRoadAccessLocationRecord,
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
