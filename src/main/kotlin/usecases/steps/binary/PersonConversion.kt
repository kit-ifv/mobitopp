package usecases.steps.binary

import domain.data.*
import units.CurrencyUnit
import units.UnitIntervalValue
import units.euros
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
            writeInt(employment.encode())
            writeInt(sex.encode())
            writeDouble(income.toDouble(CurrencyUnit.EUROS))
            writeBoolean(hasBike)
            writeBoolean(hasCommuterTicket)
            writeBoolean(hasLicense)
            writeDouble(eMobilityAcceptance.toDouble())
            writeInt(chargingInfluence.encode())
            writeInt(graduation.encode())
            // TODO add memberships, they are currently missing
        }
    }
}
