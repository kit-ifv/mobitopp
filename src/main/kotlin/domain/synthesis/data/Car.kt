package domain.synthesis.data

import Mutable
import units.Distance
import units.Efficiency
import units.Energy
import units.Volume
import units.kilometers
import utils.Encodable
import utils.EnumDecodable
import utils.ID
import utils.Identifiable

typealias CarId = ID<Car>

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
