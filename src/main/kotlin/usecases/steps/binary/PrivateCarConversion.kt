package usecases.steps.binary

import domain.data.CarEngineStatistics
import domain.data.CarId
import domain.data.CarSegment
import domain.data.EngineType
import domain.data.HouseholdId
import domain.data.MutableHousehold
import domain.data.MutablePrivateCar
import domain.data.Person
import domain.data.PersonId
import domain.data.PrivateCar
import domain.data.buildEngine
import domain.location.Location
import usecases.steps.binary.LocationUtils.encodeLocation
import java.io.DataInputStream
import java.io.DataOutputStream

@Suppress("MagicNumber")
class BinaryCarReader(
    val householdConverter: (HouseholdId) -> MutableHousehold,
    val personConverter: (PersonId) -> Person,
    private val carEngineStatistics: CarEngineStatistics = CarEngineStatistics(),
    private val determineLocation: DataInputStream.(MutablePrivateCar) -> Location
) : BinaryReader<MutablePrivateCar> {

    override fun DataInputStream.decode(stringLength: Int): MutablePrivateCar {
        return MutablePrivateCar(
            CarId(readLong()),
            householdConverter(HouseholdId(readLong()))
        ).apply {
            seats = readInt()

            val personId = PersonId(readLong())
            mainUser = if (personId != PersonId(Long.MIN_VALUE)) personConverter(personId) else null
            segment = CarSegment.decode(readInt())
            val engineType = EngineType.decode(readInt())
            engine = carEngineStatistics.buildEngine(segment, engineType)
            location = determineLocation(this)
        }
    }
}

class BinaryCarWriter : BinaryWriter<PrivateCar> {
    override fun operateStream(outStream: DataOutputStream, elements: Collection<PrivateCar>) {
        val size = elements.size
        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file
        outStream.writeInt(0) // string length, not needed here, so 0.
        elements.forEach { outStream.encodePrivateCar(it) }
    }

    private fun DataOutputStream.encodePrivateCar(car: PrivateCar) {
        car.run {
            writeLong(id.value) //  8 Bytes
            writeLong(owner.id.value) // 16 Bytes
            writeInt(seats) //  4 Bytes
            writeLong(mainUser?.id?.value ?: Long.MIN_VALUE) // 12 Bytes
            writeInt(segment.encode()) // 16 Bytes
            writeInt(engine.type.encode()) // 20 Bytes
            encodeLocation(location) // 60 Bytes
        }
    }
}
