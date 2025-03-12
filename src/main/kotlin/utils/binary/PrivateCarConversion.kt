package utils.binary

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
import java.io.DataOutputStream
import java.nio.MappedByteBuffer
import java.nio.file.Path
@Suppress("MagicNumber")
class BinaryCarReader(
    val householdConverter: (HouseholdId) -> MutableHousehold,
    val personConverter: (PersonId) -> Person,
    private val carEngineStatistics: CarEngineStatistics = CarEngineStatistics(),
    private val determineLocation: TrackingBuffer.(MutablePrivateCar) -> Location
) : BinaryReader<MutablePrivateCar> {
    override fun fromBinary(path: Path): List<MutablePrivateCar> {
        return path.operateOnMemoryFile {
            val size = this.getInt(0)
            // Preallocate id array
            val idArray = Array(size) {
                CarId(-1L) to HouseholdId(-1L)
            }

            for (i in 0 until size) {
                idArray[i] = extractIds(this, i * idByteSize + 4)
            }
            val cars = idArray.map {
                MutablePrivateCar(
                    it.first,
                    householdConverter(it.second)
                )
            }
            for (i in 0 until size) {
                extractContent(this, i * attributeByteSize + 4 + size * idByteSize, cars[i])
            }

            cars
        }
    }

    private val idByteSize = 16
    private fun extractIds(buffer: MappedByteBuffer, at: Int): Pair<CarId, HouseholdId> {
        return TrackingBuffer(buffer, at).run {
            CarId(nextLong) to HouseholdId(nextLong)
        }
    }

    @Suppress("MagicNumber")
    private fun extractContent(buffer: MappedByteBuffer, at: Int, car: MutablePrivateCar) {
        TrackingBuffer(buffer, at).run {
            car.apply {
                seats = nextInt

                val personId = PersonId(nextLong)
                mainUser = if (personId != PersonId(Long.MIN_VALUE)) personConverter(personId) else null
                segment = CarSegment.decode(nextInt)
                val engineType = EngineType.decode(nextInt)
                engine = carEngineStatistics.buildEngine(segment, engineType)
                location = determineLocation(this@run, this)
            }
        }
    }

    private val attributeByteSize = 60
}

class BinaryCarWriter : BinaryWriter<PrivateCar> {
    override fun operateStream(outStream: DataOutputStream, elements: Collection<PrivateCar>) {
        val size = elements.size
        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file

        elements.forEach { outStream.encodeIDs(it) } // For each agent write the ID and the household ID
        elements.forEach { outStream.encodeAttributes(it) } // Afterwards, write the attributes of the car
    }

    private fun DataOutputStream.encodeIDs(car: PrivateCar) {
        writeLong(car.id.value) //  8 Bytes
        writeLong(car.owner.id.value) // 16 Bytes
    }

    private fun DataOutputStream.encodeAttributes(car: PrivateCar) {
        car.run {
            writeInt(seats) //  4 Bytes
            writeLong(mainUser?.id?.value ?: Long.MIN_VALUE) // 12 Bytes
            writeInt(segment.encode()) // 16 Bytes
            writeInt(engine.type.encode()) // 20 Bytes
            writeLocation(location) // 60 Bytes
        }
    }
}
