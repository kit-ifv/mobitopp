package domain.data

import Mutable
import domain.location.Location
import units.Distance
import units.Efficiency
import units.Energy
import units.Volume
import units.kilometers
import utils.Builder
import utils.Decodable
import utils.Encodable
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

    // TODO Debate with Jelle whether CAR should hold information and state or be separated.
    var location: Location

    var driver: Person?

    var passengers: MutableSet<Person>

    var keyHolder: Person?

    fun addDriver(person: Person) {
        driver = person
    }

    fun removeDriver() {
        driver = null
    }

    fun addPassenger(person: Person) {
        passengers.add(person)
    }

    fun removePassenger(person: Person) {
        passengers.remove(person)
    }
}

/**
 * A vehicle that is assigned to a specific household or user
 */
interface PrivateCar : Car {
    val owner: Household
    val mainUser: Person?

    var state: CarState

    enum class CarState {
        PARKED, IN_USE
    }
}

interface CarEngine {
    val type: EngineType
    val range: Distance
}

/**
 * Car segments are a classification seen in https://en.wikipedia.org/wiki/Euro_Car_Segment. If the need arises
 * to implement a different segment encoding it is up to the developer to extract an interface and provide a different
 * encoding.
 */
enum class CarSegment(private val code: Int) : Encodable {
    SMALL(1),
    MIDSIZE(2),
    LARGE(3);

    override fun encode() = this.code

    companion object : Decodable<CarSegment> {
        override fun decode(i: Int) = entries.first { it.code == i }
        override fun decode(s: String) = valueOf(s)
        override fun values(): Set<CarSegment> = CarSegment.entries.toSet()
    }
}

enum class EngineType(private val code: Int) : Encodable {
    COMBUSTION(1),
    ELECTRIC(2),
    HYBRID(3);

    override fun encode() = this.code

    companion object : Decodable<EngineType> {
        override fun decode(i: Int) = entries.first { it.code == i }
        override fun decode(s: String) = valueOf(s)
        override fun values(): Set<EngineType> = EngineType.entries.toSet()
    }
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

class PrivateCarBuilder(
    var segment: CarSegment? = null,
    var engine: EngineType? = null,
    var seats: Int? = null,
    var owner: Household? = null,
    var mainUser: Person? = null,
    var carEngineStatistics: CarEngineStatistics? = CarEngineStatistics()
) : Builder<PrivateCar> {
    companion object {
        private var idCount = 0L
    }

    override fun build() = object : PrivateCar {
        override val owner: Household = this@PrivateCarBuilder.owner!!
        override val mainUser: Person? = this@PrivateCarBuilder.mainUser
        override val segment: CarSegment = this@PrivateCarBuilder.segment!!
        override val seats: Int = this@PrivateCarBuilder.seats!!
        override val id: CarId = ID(idCount++)
        override val engine: CarEngine = buildEngine()
        override var location: Location = owner.location

        override var driver: Person? = null
        override var passengers: MutableSet<Person> = mutableSetOf()
        override var state = PrivateCar.CarState.PARKED

        override var keyHolder: Person? = null

        init {
            owner.addCar(this)
        }
    }

    fun buildEngine(): CarEngine {
        val stats = this.carEngineStatistics!!
        val segment = this.segment!!

        return when (val engine = this.engine!!) {
            EngineType.COMBUSTION -> object : CombustionEngine {
                override val fuelCapacity = stats.fuelCapacityOf(segment, engine)
                override val fuelConsumption100Km: Volume = stats.fuelConsumption100kmOf(segment)
            }

            EngineType.ELECTRIC -> object : ElectricEngine {
                override val batteryCapacity = stats.batteryCapacityOf(segment, engine)
                override val electricRange = stats.batteryRangeOf(segment, engine)
            }

            EngineType.HYBRID -> object : HybridEngine {
                override val fuelCapacity = stats.fuelCapacityOf(segment, engine)
                override val fuelConsumption100Km = stats.fuelConsumption100kmOf(segment)
                override val batteryCapacity = stats.batteryCapacityOf(segment, engine)
                override val electricRange = stats.batteryRangeOf(segment, engine)
            }
        }
    }
}

@Mutable
interface TestMut {
    val age: Int
    val name: String
}

@Mutable
abstract class SubTestMut(
    override val age: Int,
) : TestMut {

    abstract val foo: Car
}
