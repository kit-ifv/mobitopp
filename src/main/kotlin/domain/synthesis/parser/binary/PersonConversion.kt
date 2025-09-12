package domain.synthesis.parser.binary

import domain.synthesis.data.ChargingInfluence
import domain.synthesis.data.Employment
import domain.synthesis.data.Graduation
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import domain.synthesis.data.Sex
import units.CurrencyUnit
import units.UnitIntervalValue
import units.euros
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import utils.collections.addProgressBar
import java.io.DataOutputStream
import java.nio.ByteBuffer

@Suppress("MagicNumber")
class BinaryPersonReader(val converter: (HouseholdId) -> MutableHousehold?, private val contextSimulationSeed: Long) :
    BinaryReader<MutablePerson> {

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
            }
        }
    }
}

class BinaryPersonWriter : BinaryWriter<Person> {
    override fun operateStream(outStream: DataOutputStream, elements: Collection<Person>) {
        val size = elements.size
        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file
        outStream.writeInt(-1) // standardized format requires a string length

        elements.addProgressBar("Writing binary persons").forEach { outStream.encodePerson(it) }
    }

    private fun DataOutputStream.encodePerson(person: Person) {
        person.run {
            writeLong(id.value)
            writeLong(household.id.value)
            writeInt(age)
            writeInt(employment.code)
            writeInt(sex.code)
            writeDouble(income.toDouble(CurrencyUnit.EUROS))
            writeBoolean(hasBike)
            writeBoolean(hasCommuterTicket)
            writeBoolean(hasLicense)
            writeDouble(eMobilityAcceptance.toDouble())
            writeInt(chargingInfluence.code)
            writeInt(graduation.code)
            // TODO add memberships, they are currently missing
        }
    }
}
