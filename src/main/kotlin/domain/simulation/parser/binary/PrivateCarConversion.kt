package domain.simulation.parser.binary

import domain.shared.car.CarId
import domain.shared.car.CarSegment
import domain.shared.car.engine.CarEngineStatistics
import domain.shared.car.engine.EngineType
import domain.shared.car.engine.buildEngine
import domain.shared.data.household.HouseholdId
import domain.shared.data.person.PersonId
import domain.simulation.data.car.MutablePrivateCar
import domain.simulation.data.car.PrivateCar
import domain.simulation.data.household.MutableHousehold
import domain.simulation.data.person.Person
import utils.binary.BinaryReader
import utils.binary.DefaultBinaryWriter
import java.nio.ByteBuffer

@Suppress("MagicNumber")
class BinaryCarReader(
    val householdConverter: (HouseholdId) -> MutableHousehold?,
    val personConverter: (PersonId) -> Person?,
    private val carEngineStatistics: CarEngineStatistics = CarEngineStatistics(),
) : BinaryReader<MutablePrivateCar> {

    override fun ByteBuffer.decode(stringLength: Int): MutablePrivateCar? {
        val id = CarId(long)
        val household = householdConverter(HouseholdId(long))
        val seats = int
        val personId = PersonId(long)
        val segment = CarSegment.decode(int)
        val engineType = EngineType.decode(int)

        return household?.let {
            MutablePrivateCar(id, it).apply {
                this.seats = seats
                this.mainUser = if (personId != PersonId(Long.MIN_VALUE)) personConverter(personId) else null
                this.segment = segment
                this.engine = carEngineStatistics.buildEngine(segment, engineType)
            }
        }
    }
}

class BinaryCarWriter : DefaultBinaryWriter<PrivateCar>()
