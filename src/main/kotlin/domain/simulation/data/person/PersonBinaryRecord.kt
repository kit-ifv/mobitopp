package domain.simulation.data.person

import domain.jackson.BinaryWritable
import java.io.DataOutputStream

data class PersonBinaryRecord(
    val id: Long,
    val householdId: Long,
    val age: Int,
    val employmentCode: Int,
    val sexCode: Int,
    val income: Double,
    val hasBike: Boolean,
    val hasCommuterTicket: Boolean,
    val hasLicense: Boolean,
    val eMobilityAcceptance: Double,
    val chargingInfluenceCode: Int,
    val graduationCode: Int,
    val sharingMemberships: List<Long>,
    val drtMemberships: List<Long>,
) : BinaryWritable {
    override fun writeTo(outStream: DataOutputStream) {
        outStream.run {
            writeLong(id)
            writeLong(householdId)
            writeInt(age)
            writeInt(employmentCode)
            writeInt(sexCode)
            writeDouble(income)
            writeBoolean(hasBike)
            writeBoolean(hasCommuterTicket)
            writeBoolean(hasLicense)
            writeDouble(eMobilityAcceptance)
            writeInt(chargingInfluenceCode)
            writeInt(graduationCode)
            writeInt(sharingMemberships.size)
            sharingMemberships.forEach { writeLong(it) }
            writeInt(drtMemberships.size)
            drtMemberships.forEach { writeLong(it) }
        }
    }
}
