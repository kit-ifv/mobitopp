package domain.data

import domain.location.Location
import units.Distance
import units.Efficiency
import units.Energy
import units.Volume
import units.kilometers
import units.kilowatthours
import units.liters
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

/**
 * Properties for different car engine types imported from legacy mobiTopp.
 * For bev (battery electric car) we define range and battery capacity.
 * For erev (hybrid car) we define battery capacity and differentiate the range by battery and total (combined).
 * For combustion cars we define fuel capacity and fuel efficiency.
 *
 * The values are defined for the three car segments: small, midsize and large.
 */
data class CarEngineStatistics(
    val smallBatteryCarRange: Distance = 250.kilometers,
    val smallBatteryCarCapacity: Energy = 30.kilowatthours,

    val midsizeBatteryCarRange: Distance = 350.kilometers,
    val midsizeBatteryCarCapacity: Energy = 60.kilowatthours,

    val largeBatteryCarRange: Distance = 550.kilometers,
    val largeBatteryCarCapacity: Energy = 125.kilowatthours,

    val smallHybridCarBatteryRange: Distance = 50.kilometers,
    val smallHybridCarTotalRange: Distance = 300.kilometers,
    val smallHybridCarBatteryCapacity: Energy = 9.kilowatthours,

    val midsizeHybridCarBatteryRange: Distance = 90.kilometers,
    val midsizeHybridCarTotalRange: Distance = 300.kilometers,
    val midsizeHybridCarBatteryCapacity: Energy = 19.kilowatthours,

    val largeHybridCarBatteryRange: Distance = 90.kilometers,
    val largeHybridCarTotalRange: Distance = 300.kilometers,
    val largeHybridCarBatteryCapacity: Energy = 19.kilowatthours,

    val smallCombustionCarFuelCapacity: Volume = 50.liters,
    val midsizeCombustionCarFuelCapacity: Volume = 60.liters,
    val largeCombustionCarFuelCapacity: Volume = 70.liters,

    val smallCombustionCarFuelConsumption100km: Volume = 6.liters,
    val midsizeCombustionCarFuelConsumption100km: Volume = 7.liters,
    val largeCombustionCarFuelConsumption100km: Volume = 8.liters,
) {

    fun batteryCapacityOf(segment: CarSegment, engine: EngineType): Energy =
        engine.batteryCapacityOf(segment, this)

    fun batteryRangeOf(segment: CarSegment, engine: EngineType): Distance =
        engine.batteryRangeOf(segment, this)

    fun fuelCapacityOf(segment: CarSegment, engine: EngineType): Volume =
        engine.fuelCapacityOf(segment, this)

    fun fuelConsumption100kmOf(segment: CarSegment): Volume =
        segment.fuelConsumption100km(this)
}

fun CarSegment.fuelConsumption100km(data: CarEngineStatistics): Volume = when (this) {
    CarSegment.SMALL -> data.smallCombustionCarFuelConsumption100km
    CarSegment.MIDSIZE -> data.midsizeCombustionCarFuelConsumption100km
    CarSegment.LARGE -> data.largeCombustionCarFuelConsumption100km
}

fun EngineType.batteryCapacityOf(segment: CarSegment, data: CarEngineStatistics): Energy = when (this) {
    EngineType.COMBUSTION -> 0.kilowatthours

    EngineType.ELECTRIC -> when (segment) {
        CarSegment.SMALL -> data.smallBatteryCarCapacity
        CarSegment.MIDSIZE -> data.midsizeBatteryCarCapacity
        CarSegment.LARGE -> data.largeBatteryCarCapacity
    }

    EngineType.HYBRID -> when (segment) {
        CarSegment.SMALL -> data.smallHybridCarBatteryCapacity
        CarSegment.MIDSIZE -> data.midsizeHybridCarBatteryCapacity
        CarSegment.LARGE -> data.largeHybridCarBatteryCapacity
    }
}

fun EngineType.batteryRangeOf(segment: CarSegment, data: CarEngineStatistics): Distance = when (this) {
    EngineType.COMBUSTION -> 0.kilometers

    EngineType.ELECTRIC -> when (segment) {
        CarSegment.SMALL -> data.smallBatteryCarRange
        CarSegment.MIDSIZE -> data.midsizeBatteryCarRange
        CarSegment.LARGE -> data.largeBatteryCarRange
    }

    EngineType.HYBRID -> when (segment) {
        CarSegment.SMALL -> data.smallHybridCarBatteryRange
        CarSegment.MIDSIZE -> data.midsizeHybridCarBatteryRange
        CarSegment.LARGE -> data.largeHybridCarBatteryRange
    }
}

fun EngineType.totalRangeOf(segment: CarSegment, data: CarEngineStatistics): Distance = when (this) {
    EngineType.COMBUSTION ->
        100.kilometers *
            this.fuelCapacityOf(segment, data).div(segment.fuelConsumption100km(data))

    EngineType.ELECTRIC -> batteryRangeOf(segment, data)

    EngineType.HYBRID -> when (segment) {
        CarSegment.SMALL -> data.smallHybridCarTotalRange
        CarSegment.MIDSIZE -> data.midsizeHybridCarTotalRange
        CarSegment.LARGE -> data.largeHybridCarTotalRange
    }
}

fun EngineType.fuelCapacityOf(segment: CarSegment, data: CarEngineStatistics): Volume = when (this) {
    EngineType.ELECTRIC -> 0.liters

    EngineType.COMBUSTION -> when (segment) {
        CarSegment.SMALL -> data.smallCombustionCarFuelCapacity
        CarSegment.MIDSIZE -> data.midsizeCombustionCarFuelCapacity
        CarSegment.LARGE -> data.largeCombustionCarFuelCapacity
    }

    EngineType.HYBRID -> segment.fuelConsumption100km(data) *
        (this.totalRangeOf(segment, data) - this.batteryRangeOf(segment, data)).div(100.kilometers)
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
