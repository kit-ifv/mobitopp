package domain.synthesis.parser.binary

import domain.jackson.BinaryWritable
import domain.jackson.CarBinaryRecord
import domain.synthesis.data.CarEngineStatistics
import domain.synthesis.data.CarId
import domain.synthesis.data.CarSegment
import domain.synthesis.data.EngineType
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePrivateCar
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import domain.synthesis.data.PrivateCar
import domain.synthesis.data.buildEngine
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import utils.binary.DefaultBinaryWriter
import utils.binary.RepresentativeBinaryWriter
import java.io.DataOutputStream
import java.nio.ByteBuffer

@Suppress("MagicNumber")
class BinaryCarReader(
    val householdConverter: (HouseholdId) -> MutableHousehold?,
    val personConverter: (PersonId) -> Person?,
    private val carEngineStatistics: CarEngineStatistics = CarEngineStatistics(),
//    private val determineLocation: DataInputStream.(MutablePrivateCar) -> Location TODO clean up
) : BinaryReader<MutablePrivateCar> {

    override fun ByteBuffer.decode(stringLength: Int): MutablePrivateCar? {
        val id = CarId(long)
        val household = householdConverter(HouseholdId(long))
        val seats = int
        val personId = PersonId(long)
        val segment = CarSegment.decode(int)
        val engineTyoe = EngineType.decode(int)

        return household?.let {
            MutablePrivateCar(id, it).apply {
                this.seats = seats
                this.mainUser = if (personId != PersonId(Long.MIN_VALUE)) personConverter(personId) else null
                this.segment = segment
                this.engine = carEngineStatistics.buildEngine(segment, engineTyoe)
            }
        }
    }
}

class BinaryCarWriter: DefaultBinaryWriter<PrivateCar>()

//class BinaryCarWriter : RepresentativeBinaryWriter<PrivateCar> {
//    override fun operateSimplifiedStream(
//        outStream: DataOutputStream,
//        elements: Collection<BinaryWritable>,
//    ) {
//        val size = elements.size
//        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file
//        outStream.writeInt(0) // string length, not needed here, so 0.
//        elements.forEach { it.writeTo(outStream) }
//    }
//
////    override fun operateStream(outStream: DataOutputStream, elements: Collection<PrivateCar>) {
////        val size = elements.size
////        outStream.writeInt(size) // Write the amount of agents that are expected to be found in this file
////        outStream.writeInt(0) // string length, not needed here, so 0.
////        elements.forEach { outStream.encodePrivateCar(it) }
////    }
//
//    private fun DataOutputStream.encodePrivateCar(car: PrivateCar) {
//        car.run {
//            writeLong(id.value) //  8 Bytes
//            writeLong(owner.id.value) // 16 Bytes
//            writeInt(seats) //  4 Bytes
//            writeLong(mainUser?.id?.value ?: Long.MIN_VALUE) // 12 Bytes
//            writeInt(segment.code) // 16 Bytes
//            writeInt(engine.type.code) // 20 Bytes
////            encodeLocation(location) // 60 Bytes
//        }
//    }
//}
