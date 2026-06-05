package edu.kit.ifv.domain.simulation.parser.binary
import edu.kit.ifv.domain.shared.data.household.HouseholdId
import edu.kit.ifv.domain.shared.data.person.PersonId
import edu.kit.ifv.domain.shared.enums.person.ChargingInfluence
import edu.kit.ifv.domain.shared.enums.person.Employment
import edu.kit.ifv.domain.shared.enums.person.Graduation
import edu.kit.ifv.domain.shared.enums.person.Sex
import edu.kit.ifv.domain.simulation.data.drt.DrtProvider
import edu.kit.ifv.domain.simulation.data.drt.DrtProviderId
import edu.kit.ifv.domain.simulation.data.household.MutableHousehold
import edu.kit.ifv.domain.simulation.data.person.MutablePerson
import edu.kit.ifv.domain.simulation.data.person.Person
import edu.kit.ifv.domain.simulation.data.sharing.SharingProvider
import edu.kit.ifv.domain.simulation.data.sharing.SharingProviderId
import edu.kit.ifv.units.UnitIntervalValue
import edu.kit.ifv.units.euros
import edu.kit.ifv.utils.binary.BinaryReader
import edu.kit.ifv.utils.binary.DefaultBinaryWriter
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
