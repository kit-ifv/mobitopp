package domain.data

import units.Distance
import units.DistanceUnit
import units.Energy
import units.EnergyUnit
import units.kilometers
import units.kilowatthours
import units.toEnergy
import utils.Builder
import utils.Decodable
import utils.Encodable
import utils.ID
import utils.Identifiable
import utils.drawId
import kotlin.math.roundToInt

typealias CarId = ID<CarData>

/**
 * The generic interface for a car.
 * @property segment Segment as described in [CarSegment]
 * @property engine: The type of Engine
 * @property seats the amount of people that can travel inside the car including the driver
 */
interface CarData : Identifiable<CarId> {
    val segment: CarSegment
    val engine: CarEngine
    val seats: Int
}

/**
 * A vehicle that is assigned to a specific household or user
 */
interface PrivateCarData : CarData {
    val owner: HouseholdData
    val mainUser: PersonData
}

interface CarEngine {
    val type: EngineType
    val range: Distance
}

interface CombustionEngine : CarEngine { // TODO refactor combustion car with liters as unit of energy
    val fuelCapacity: Int // TODO unit volume
    val fuelEfficiency: Double // TODO unit volume over distance

    val combustionRange: Distance
        get() = (fuelCapacity * fuelEfficiency).kilometers // TODO

    override val range: Distance
        get() = combustionRange

    override val type: EngineType
        get() = EngineType.COMBUSTION
}

interface ElectricEngine : CarEngine {
    val electricRange: Distance
    val batteryCapacity: Energy

    val batteryEfficiency: Double // TODO energy over distance
        get() = batteryCapacity.toDouble(EnergyUnit.KILOWATTHOUR) / range.toDouble(DistanceUnit.KILOMETERS)

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
    val smallBatteryCarCapacity: Energy = 30.toEnergy(EnergyUnit.KILOWATTHOUR),

    val midsizeBatteryCarRange: Distance = 350.kilometers,
    val midsizeBatteryCarCapacity: Energy = 60.toEnergy(EnergyUnit.KILOWATTHOUR),

    val largeBatteryCarRange: Distance = 550.kilometers,
    val largeBatteryCarCapacity: Energy = 125.toEnergy(EnergyUnit.KILOWATTHOUR),

    val smallHybridCarBatteryRange: Distance = 50.kilometers,
    val smallHybridCarTotalRange: Distance = 300.kilometers,
    val smallHybridCarBatteryCapacity: Energy = 9.toEnergy(EnergyUnit.KILOWATTHOUR),

    val midsizeHybridCarBatteryRange: Distance = 90.kilometers,
    val midsizeHybridCarTotalRange: Distance = 300.kilometers,
    val midsizeHybridCarBatteryCapacity: Energy = 19.toEnergy(EnergyUnit.KILOWATTHOUR),

    val largeHybridCarBatteryRange: Distance = 90.kilometers,
    val largeHybridCarTotalRange: Distance = 300.kilometers,
    val largeHybridCarBatteryCapacity: Energy = 19.toEnergy(EnergyUnit.KILOWATTHOUR),

    val smallCombustionCarFuelCapacity: Int = 50,
    val midsizeCombustionCarFuelCapacity: Int = 60,
    val largeCombustionCarFuelCapacity: Int = 70,

    val smallCombustionCarFuelEfficiency: Double = 6.0,
    val midsizeCombustionCarFuelEfficiency: Double = 7.0,
    val largeCombustionCarFuelEfficiency: Double = 8.0,
) {

    fun batteryCapacityOf(segment: CarSegment, engine: EngineType): Energy =
        engine.batteryCapacityOf(segment, this)

    fun batteryRangeOf(segment: CarSegment, engine: EngineType): Distance =
        engine.batteryRangeOf(segment, this)

    fun fuelCapacityOf(segment: CarSegment, engine: EngineType): Int =
        engine.fuelCapacityOf(segment, this)

    fun fuelEfficiencyOf(segment: CarSegment): Double =
        segment.fuelEfficiency(this)
}

fun CarSegment.fuelEfficiency(data: CarEngineStatistics): Double = when (this) {
    CarSegment.SMALL -> data.smallCombustionCarFuelEfficiency
    CarSegment.MIDSIZE -> data.midsizeCombustionCarFuelEfficiency
    CarSegment.LARGE -> data.largeCombustionCarFuelEfficiency
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
    EngineType.COMBUSTION -> (
        this.fuelCapacityOf(segment, data) * segment.fuelEfficiency(data)
        ).kilometers

    EngineType.ELECTRIC -> batteryRangeOf(segment, data)

    EngineType.HYBRID -> when (segment) {
        CarSegment.SMALL -> data.smallHybridCarTotalRange
        CarSegment.MIDSIZE -> data.midsizeHybridCarTotalRange
        CarSegment.LARGE -> data.largeHybridCarTotalRange
    }
}

fun EngineType.fuelCapacityOf(segment: CarSegment, data: CarEngineStatistics): Int = when (this) {
    EngineType.ELECTRIC -> 0

    EngineType.COMBUSTION -> when (segment) {
        CarSegment.SMALL -> data.smallCombustionCarFuelCapacity
        CarSegment.MIDSIZE -> data.midsizeCombustionCarFuelCapacity
        CarSegment.LARGE -> data.largeCombustionCarFuelCapacity
    }
    EngineType.HYBRID -> (
        (this.totalRangeOf(segment, data) - this.batteryRangeOf(segment, data)).rawValue
            * segment.fuelEfficiency(data)
        ).roundToInt()
}

/**
 * Car segments are a classification seen in https://en.wikipedia.org/wiki/Euro_Car_Segment. If the need arises
 * to implement a different segment encoding it is up to the developer to extract an interface and provide a different
 * encoding
 */
enum class CarSegment(private val code: Int) : Encodable {
    SMALL(1),
    MIDSIZE(2),
    LARGE(3);

    override fun encode() = this.code
    companion object : Decodable<CarSegment> {
        override fun decode(i: Int) = entries.first { it.code == i }
        override fun decode(s: String) = valueOf(s)
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
    }
}

class PrivateCarBuilder(
    var segment: CarSegment? = null,
    var engine: EngineType? = null,
    var seats: Int? = null,
    var owner: HouseholdData? = null,
    var mainUser: PersonData? = null,
    var carEngineStatistics: CarEngineStatistics? = CarEngineStatistics()
) : Builder<PrivateCarData> {

    override fun build() = object : PrivateCarData {
        override val owner: HouseholdData = this@PrivateCarBuilder.owner!!
        override val mainUser: PersonData = this@PrivateCarBuilder.mainUser!!
        override val segment: CarSegment = this@PrivateCarBuilder.segment!!
        override val seats: Int = this@PrivateCarBuilder.seats!!
        override val id: CarId = drawId()
        override val engine: CarEngine = buildEngine()
    }

    fun buildEngine(): CarEngine {
        val stats = this.carEngineStatistics!!
        val segment = this.segment!!

        return when (val engine = this.engine!!) {
            EngineType.COMBUSTION -> object : CombustionEngine {
                override val fuelCapacity = stats.fuelCapacityOf(segment, engine)
                override val fuelEfficiency = stats.fuelEfficiencyOf(segment)
            }
            EngineType.ELECTRIC -> object : ElectricEngine {
                override val batteryCapacity = stats.batteryCapacityOf(segment, engine)
                override val electricRange = stats.batteryRangeOf(segment, engine)
            }
            EngineType.HYBRID -> object : HybridEngine {
                override val fuelCapacity = stats.fuelCapacityOf(segment, engine)
                override val fuelEfficiency = stats.fuelEfficiencyOf(segment)
                override val batteryCapacity = stats.batteryCapacityOf(segment, engine)
                override val electricRange = stats.batteryRangeOf(segment, engine)
            }
        }
    }
}
