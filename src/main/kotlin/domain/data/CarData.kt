package domain.data

import units.Distance
import units.DistanceUnit
import units.Energy
import units.EnergyUnit
import units.kilometers
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
    val smallBevRange: Distance = 250.kilometers,
    val smallBevBattery: Energy = 30.toEnergy(EnergyUnit.KILOWATTHOUR),

    val midBevRange: Distance = 350.kilometers,
    val midBevBattery: Energy = 60.toEnergy(EnergyUnit.KILOWATTHOUR),

    val largeBevRange: Distance = 550.kilometers,
    val largeBevBattery: Energy = 125.toEnergy(EnergyUnit.KILOWATTHOUR),

    val smallErevBatteryRange: Distance = 50.kilometers,
    val smallErevTotalRange: Distance = 300.kilometers,
    val smallErevBattery: Energy = 9.toEnergy(EnergyUnit.KILOWATTHOUR),

    val midErevBatteryRange: Distance = 90.kilometers,
    val midErevTotalRange: Distance = 300.kilometers,
    val midErevBattery: Energy = 19.toEnergy(EnergyUnit.KILOWATTHOUR),

    val largeErevBatteryRange: Distance = 90.kilometers,
    val largeErevTotalRange: Distance = 300.kilometers,
    val largeErevBattery: Energy = 19.toEnergy(EnergyUnit.KILOWATTHOUR),

    val smallCombustionFuelCapacity: Int = 50,
    val midCombustionFuelCapacity: Int = 60,
    val largeCombustionFuelCapacity: Int = 70,

    val smallCombustionFuelEfficiency: Double = 6.0,
    val midCombustionFuelEfficiency: Double = 7.0,
    val largeCombustionFuelEfficiency: Double = 8.0,
) {

    fun batteryCapacityOf(segment: CarSegment, engine: EngineType): Energy = when (segment to engine) {
        (CarSegment.SMALL to EngineType.ELECTRIC) -> smallBevBattery
        (CarSegment.MIDSIZE to EngineType.ELECTRIC) -> midBevBattery
        (CarSegment.LARGE to EngineType.ELECTRIC) -> largeBevBattery

        (CarSegment.SMALL to EngineType.HYBRID) -> smallErevBattery
        (CarSegment.MIDSIZE to EngineType.HYBRID) -> midErevBattery
        (CarSegment.LARGE to EngineType.HYBRID) -> largeErevBattery

        else -> { 0.toEnergy(EnergyUnit.KILOWATTHOUR) }
    }

    fun batteryRangeOf(segment: CarSegment, engine: EngineType): Distance = when (segment to engine) {
        (CarSegment.SMALL to EngineType.ELECTRIC) -> smallBevRange
        (CarSegment.MIDSIZE to EngineType.ELECTRIC) -> midBevRange
        (CarSegment.LARGE to EngineType.ELECTRIC) -> largeBevRange

        (CarSegment.SMALL to EngineType.HYBRID) -> smallErevBatteryRange
        (CarSegment.MIDSIZE to EngineType.HYBRID) -> midErevBatteryRange
        (CarSegment.LARGE to EngineType.HYBRID) -> largeErevBatteryRange

        else -> { 0.kilometers }
    }

    fun fuelCapacityOf(segment: CarSegment, engine: EngineType): Int = when (segment to engine) {
        (CarSegment.SMALL to EngineType.COMBUSTION) -> smallCombustionFuelCapacity
        (CarSegment.MIDSIZE to EngineType.COMBUSTION) -> midCombustionFuelCapacity
        (CarSegment.LARGE to EngineType.COMBUSTION) -> largeCombustionFuelCapacity

        (CarSegment.SMALL to EngineType.HYBRID) ->
            ((smallErevTotalRange - smallErevBatteryRange).rawValue * smallCombustionFuelEfficiency).roundToInt()

        (CarSegment.MIDSIZE to EngineType.HYBRID) ->
            ((midErevTotalRange - midErevBatteryRange).rawValue * midCombustionFuelEfficiency).roundToInt()

        (CarSegment.LARGE to EngineType.HYBRID) ->
            ((largeErevTotalRange - largeErevBatteryRange).rawValue * largeCombustionFuelEfficiency).roundToInt()

        else -> { 0 }
    }

    fun fuelEfficiencyOf(segment: CarSegment): Double = when (segment) {
        CarSegment.SMALL -> smallCombustionFuelEfficiency
        CarSegment.MIDSIZE -> midCombustionFuelEfficiency
        CarSegment.LARGE -> largeCombustionFuelEfficiency
    }
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

@Suppress("LongParameterList")
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
