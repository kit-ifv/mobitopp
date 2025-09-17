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
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.UnitIntervalValue
import edu.kit.ifv.units.euros
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import java.io.DataInputStream
import java.io.DataOutputStream

@Suppress("MagicNumber")
class BinaryPersonReader(val map: (HouseholdId) -> MutableHousehold, private val contextSimulationSeed: Long) :
    BinaryReader<MutablePerson> {

    override fun DataInputStream.decode(stringLength: Int): MutablePerson {
        return MutablePerson(
            PersonId(readLong()),
            map(HouseholdId(readLong())),
            contextSimulationSeed
        ).apply {
            age = readInt()
            employment = Employment.decode(readInt())
            sex = Sex.decode(readInt())
            income = readDouble().euros
            hasBike = readBoolean()
            hasCommuterTicket = readBoolean()
            hasLicense = readBoolean()
            eMobilityAcceptance = UnitIntervalValue(readDouble())
            chargingInfluence = ChargingInfluence.decode(readInt())
            graduation = Graduation.decode(readInt())
        }
    }
}

class BinaryPersonWriter : BinaryWriter<Person> {
    override fun operateStream(outStream: DataOutputStream, elements: Collection<Person>) {
        val size = elements.size
        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file
        outStream.writeInt(-1) // standardized format requires a string length

        elements.forEach { outStream.encodePerson(it) }
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
