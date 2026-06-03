package domain.simulation.parser.binary

import domain.synthesis.data.DrtProvider
import domain.synthesis.data.DrtProviderId
import domain.synthesis.data.SharingProvider
import domain.synthesis.data.SharingProviderId
import domain.synthesis.data.household.HouseholdId
import domain.synthesis.data.household.MutableHousehold
import domain.synthesis.data.person.ChargingInfluence
import domain.synthesis.data.person.Employment
import domain.synthesis.data.person.Graduation
import domain.synthesis.data.person.MutablePerson
import domain.synthesis.data.person.Person
import domain.synthesis.data.person.PersonId
import domain.synthesis.data.person.Sex
import edu.kit.ifv.units.UnitIntervalValue
import edu.kit.ifv.units.euros
import utils.binary.BinaryReader
import utils.binary.DefaultBinaryWriter
import java.nio.ByteBuffer

@Suppress("MagicNumber")
class BinaryPersonReader(
    val converter: (HouseholdId) -> MutableHousehold?,
    val sharingConverter: (SharingProviderId) -> SharingProvider,
    val drtConverter: (DrtProviderId) -> DrtProvider,
    private val contextSimulationSeed: Long,
) : BinaryReader<MutablePerson> {

    override fun ByteBuffer.decode(stringLength: Int): MutablePerson? {
        val id = PersonId(long)
        val household = converter(HouseholdId(long))
        val age = int
        val employment = Employment.decode(int)
        val sex = Sex.decode(int)
        val income = double.euros
        val hasBike = getBoolean()
        val hasCommuterTicket = getBoolean()
        val hasLicense = getBoolean()
        val eMobilityAcceptance = UnitIntervalValue(double)
        val chargingInfluence = ChargingInfluence.decode(int)
        val graduation = Graduation.decode(int)

        val sharingMemberships = List(int) {
            sharingConverter(SharingProviderId(long))
        }

        val drtMemberships = List(int) {
            drtConverter(DrtProviderId(long))
        }

        return household?.let {
            MutablePerson(id, it, contextSimulationSeed).apply {
                this.age = age
                this.employment = employment
                this.sex = sex
                this.income = income
                this.hasBike = hasBike
                this.hasCommuterTicket = hasCommuterTicket
                this.hasLicense = hasLicense
                this.eMobilityAcceptance = eMobilityAcceptance
                this.chargingInfluence = chargingInfluence
                this.graduation = graduation
                this.sharingMemberships.addAll(sharingMemberships)
                this.drtMemberships.addAll(drtMemberships)
            }
        }
    }
}

class BinaryPersonWriter : DefaultBinaryWriter<Person>()
