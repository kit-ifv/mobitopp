package domain.synthesis.data

import Mutable
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.Efficiency
import edu.kit.ifv.units.Energy
import edu.kit.ifv.units.Volume
import edu.kit.ifv.units.kilometers
import kotlinx.serialization.Serializable
import utils.Encodable
import utils.EnumDecodable
import utils.Identifiable

@Serializable
@JvmInline
value class CarId(val value: Long) : Comparable<CarId> {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: CarId): Int {
        return value.compareTo(other.value)
    }

    /**
     * Robin: I added a method to iterate over ids, I want to use this feature for generating autoincrementing ids
     * in the test cases
     *
     * @return the next higher id.
     */
    fun next(): CarId {
        return CarId(value + 1)
    }
}

/**
 * The generic interface for a car.
 * @property segment Segment as described in [CarSegment]
 * @property engine: The type of Engine
 * @property seats the amount of people that can travel inside the car including the driver
 */
interface Car : Identifiable<CarId> {
    val segment: CarSegment
    val engine: CarEngine
    val seats: Int
}

interface IPrivateCar : Car {
    val owner: IHousehold
    val mainUser: IPerson?
}

/**
 * A vehicle that is assigned to a specific household or user
 */
@Mutable
abstract class PrivateCar(
    final override val id: CarId,
    override val owner: MutableHousehold,
) : IPrivateCar {

    abstract override val mainUser: Person?

    init {
        registerCarOwner()
    }

    private fun registerCarOwner() {
        owner.cars.add(this)
    }
}

/**
 * Car segments are a classification seen in https://en.wikipedia.org/wiki/Euro_Car_Segment. If the need arises
 * to implement a different segment encoding it is up to the developer to extract an interface and provide a different
 * encoding.
 */
enum class CarSegment(
    override val code: Int
) : Encodable {
    SMALL(1),
    MIDSIZE(2),
    LARGE(3);

    override val description: String = name

    companion object : EnumDecodable<CarSegment>(CarSegment::class)
}

interface CarEngine {
    val type: EngineType
    val range: Distance
}

fun CarEngine.identical(other: CarEngine): Boolean {
    return type == other.type && range == other.range
}

enum class EngineType(override val code: Int) : Encodable {
    COMBUSTION(1),
    ELECTRIC(2),
    HYBRID(3);

    override val description: String = name

    companion object : EnumDecodable<EngineType>(EngineType::class)
}

interface CombustionEngine : CarEngine {
    val fuelCapacity: Volume
    val fuelConsumption100Km: Volume
    val fuelEfficiency: Efficiency
        get() = fuelConsumption100Km.benzene.div(100.kilometers)

    val combustionRange: Distance
        get() = fuelCapacity.benzene.div(fuelEfficiency)

    override val range: Distance
        get() = combustionRange

    override val type: EngineType
        get() = EngineType.COMBUSTION
}

interface ElectricEngine : CarEngine {
    val electricRange: Distance
    val batteryCapacity: Energy

    val batteryEfficiency: Efficiency
        get() = batteryCapacity.div(electricRange)

    override val range: Distance
        get() = electricRange

    override val type: EngineType
        get() = EngineType.ELECTRIC
}

interface HybridEngine : CombustionEngine, ElectricEngine {

    override val range: Distance
        get() = electricRange + combustionRange

    override val type: EngineType
        get() = EngineType.HYBRID
}
