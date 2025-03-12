package utils.binary

import domain.data.ChargingInfluence
import domain.data.Employment
import domain.data.Graduation
import domain.data.HouseholdId
import domain.data.MutableHousehold
import domain.data.MutablePerson
import domain.data.Person
import domain.data.PersonId
import domain.data.Sex
import units.CurrencyUnit
import units.UnitIntervalValue
import units.euros
import java.io.DataOutputStream
import java.nio.MappedByteBuffer
import java.nio.file.Path
@Suppress("MagicNumber")
class BinaryPersonReader(val map: (HouseholdId) -> MutableHousehold, private val contextSimulationSeed: Long) :
    BinaryReader<MutablePerson> {
    override fun fromBinary(path: Path): List<MutablePerson> {
        return path.operateOnMemoryFile {
            val size = getInt(0)
            val idArray = Array(size) {
                PersonId(-1L) to HouseholdId(-1)
            }

            for (i in 0 until size) {
                idArray[i] = extractIds(this, i * idByteSize + 4)
            }
            val persons = idArray.map {
                MutablePerson(
                    it.first,
                    map(it.second),
                    contextSimulationSeed
                )
            }
            for (i in 0 until size) {
                extractInfos(this, i * attributeByteSize + 4 + size * idByteSize, persons[i])
            }
            persons
        }
    }

    private fun extractIds(buffer: MappedByteBuffer, at: Int): Pair<PersonId, HouseholdId> {
        return TrackingBuffer(buffer, at).run {
            PersonId(nextLong) to HouseholdId(nextLong)
        }
    }

    private val idByteSize = 16

    private fun extractInfos(buffer: MappedByteBuffer, at: Int, person: MutablePerson) {
        TrackingBuffer(buffer, at).run {
            person.apply {
                age = nextInt
                employment = Employment.decode(nextInt)
                sex = Sex.decode(nextInt)
                income = nextDouble.euros
                hasBike = nextBoolean
                hasCommuterTicket = nextBoolean
                hasLicense = nextBoolean
                eMobilityAcceptance = UnitIntervalValue(nextDouble)
                chargingInfluence = ChargingInfluence.decode(nextInt)
                graduation = Graduation.decode(nextInt)
            }
        }
    }

    private val attributeByteSize = 39
}

class BinaryPersonWriter : BinaryWriter<Person> {
    override fun operateStream(outStream: DataOutputStream, elements: Collection<Person>) {
        val size = elements.size
        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file

        elements.forEach { outStream.encodeID(it) } // For each agent write the ID and the household ID
        elements.forEach { outStream.encodeAttributes(it) } // For each agent encode the attributes.
    }

    private fun DataOutputStream.encodeID(person: Person) {
        person.run {
            writeLong(id.value) //  8 Bytes
            writeLong(household.id.value) // 16 Bytes
        }
    }

    // Currently 39 byte
    private fun DataOutputStream.encodeAttributes(person: Person) {
        person.run {
            writeInt(age) // 4 Bytes
            writeInt(employment.encode()) // 8 Bytes
            writeInt(sex.encode()) // 12 Bytes
            writeDouble(income.toDouble(CurrencyUnit.EUROS)) // 20 Bytes
            writeBoolean(hasBike) // 21 Bytes
            writeBoolean(hasCommuterTicket) // 22 Bytes
            writeBoolean(hasLicense) // 23 Bytes
            writeDouble(eMobilityAcceptance.toDouble()) // 31 Bytes
            writeInt(chargingInfluence.encode()) // 35 Bytes
            writeInt(graduation.encode()) // 39 Bytes
            // TODO add memberships, they are currently missing
        }
    }
}
