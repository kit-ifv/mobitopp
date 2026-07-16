package edu.kit.ifv.domain.simulation.parser.binary

import edu.kit.ifv.binary.ElementWiseBinaryReader
import edu.kit.ifv.domain.shared.car.CarId
import edu.kit.ifv.domain.shared.car.CarSegment
import edu.kit.ifv.domain.shared.car.engine.CarEngineStatistics
import edu.kit.ifv.domain.shared.car.engine.EngineType
import edu.kit.ifv.domain.shared.car.engine.buildEngine
import edu.kit.ifv.domain.shared.data.household.HouseholdId
import edu.kit.ifv.domain.shared.data.person.PersonId
import edu.kit.ifv.domain.simulation.data.car.MutablePrivateCar
import edu.kit.ifv.domain.simulation.data.household.MutableHousehold
import edu.kit.ifv.domain.simulation.data.person.Person
import java.nio.ByteBuffer

@Suppress("MagicNumber")
class BinaryCarReader(
    val householdConverter: (HouseholdId) -> MutableHousehold?,
    val personConverter: (PersonId) -> Person?,
    private val carEngineStatistics: CarEngineStatistics = CarEngineStatistics(),
) : ElementWiseBinaryReader<MutablePrivateCar> {

    override fun ByteBuffer.decode(stringLength: Int): MutablePrivateCar? {
        val id = CarId(long)
        val household = householdConverter(HouseholdId(long))
        val seats = int
        val personId = PersonId(long)
        val segment = CarSegment.Companion.decode(int)
        val engineType = EngineType.Companion.decode(int)

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
