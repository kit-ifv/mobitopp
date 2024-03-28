package domain.data

import Buildable
import Builder
import Decodable
import Encodable
import units.Distance
import units.DistanceUnit
import units.Energy
import units.EnergyUnit
import units.toEnergy
import utils.ID
import utils.Identifiable
import utils.drawId

typealias CarId = ID<CarData>

/**
 * The generic interface for a car.
 * @property segment Segment as described in [CarSegment]
 * @property engine: The type of Engine
 * @property seats the amount of people that can travel inside the car including the driver
 */
interface CarData : Identifiable<CarId> {
    val segment: CarSegment
    val engine: EngineType
    val seats: Int
    val range: Distance
    val energyCapacity: Energy

    val efficiency: Double //TODO energy over distance
        get() = energyCapacity.toDouble(EnergyUnit.KILOWATTHOUR) / range.toDouble(DistanceUnit.KILOMETERS)

}

/**
 * A vehicle that is assigned to a specific household or user
 */
interface PrivateCarData: CarData {
    val owner: HouseholdData
    val mainUser: PersonData
}

interface CombustionCarData: CarData {
    val fuelCapacity: Int //TODO unit volume
    val kwhPerLiter: Double //TODO unit energy per volume

    override val energyCapacity: Energy
        get() = (kwhPerLiter * fuelCapacity).toEnergy(EnergyUnit.KILOWATTHOUR)

    override val engine: EngineType
        get() = EngineType.COMBUSTION
}

interface ElectricCarData: CarData {
    val batteryCapacity: Energy
        get() = energyCapacity

    override val engine: EngineType
        get() = EngineType.ELECTRIC
}

interface HybridCarData: CombustionCarData, ElectricCarData {
    val electricRange: Distance
    val combustionRange: Distance

    override val range: Distance
        get() = electricRange + combustionRange

    override val energyCapacity: Energy
        get() = batteryCapacity + combustionCapacity

    override val efficiency: Double
        get() = throw UnsupportedOperationException("General efficiency is not defined for hybrid cars.")

    val combustionCapacity: Energy
        get() = (kwhPerLiter * fuelCapacity).toEnergy(EnergyUnit.KILOWATTHOUR)

    val electricEfficiency: Double
        get() = batteryCapacity.toDouble(EnergyUnit.KILOWATTHOUR) / electricRange.toDouble(DistanceUnit.KILOMETERS)

    val combustionEfficiency: Double
        get() = combustionCapacity.toDouble(EnergyUnit.KILOWATTHOUR) /
                combustionRange.toDouble(DistanceUnit.KILOMETERS)

    override val engine: EngineType
        get() = EngineType.HYBRID
}

@Buildable
interface PrivateCombustionCarData: PrivateCarData, CombustionCarData

@Buildable
interface PrivateElectricCarData: PrivateCarData, ElectricCarData

@Buildable
interface PrivateHybridCarData: PrivateCarData, HybridCarData

/**
 * Car segments are a classification seen in https://en.wikipedia.org/wiki/Euro_Car_Segment. If the need arises
 * to implement a different segment encoding it is up to the developer to extract an interface and provide a different
 * encoding
 */
enum class CarSegment(private val code: Int): Encodable {
    SMALL(1),
    MIDSIZE(2),
    LARGE(3);

    override fun encode() = this.code
    companion object : Decodable<CarSegment> {
        override fun decode(i: Int) = entries.first { it.code == i }
        override fun decode(s: String) = valueOf(s)
    }
}

enum class EngineType(private val code: Int): Encodable {
    COMBUSTION(1),
    ELECTRIC(2),
    HYBRID(3);

    override fun encode() = this.code

    companion object: Decodable<EngineType> {
        override fun decode(i: Int) = entries.first { it.code == i }
        override fun decode(s: String) = valueOf(s)
    }
}


@Suppress("LongParameterList")
class PrivateCarBuilder(
    var segment: CarSegment? = null,
    var engine: EngineType? = null,
    var seats: Int? = null,
    var range: Distance? = null,
    var owner: HouseholdData? = null,
    var mainUser: PersonData? = null,
    var fuelCapacity: Int? = null, //TODO unit volume
    var kwhPerLiter: Double? = null, //TODO unit energy per volume
    val electricRange: Distance? = null,
    val combustionRange: Distance? = null,
): Builder<PrivateCarData> {

    override fun build() = when(engine!!) {
        EngineType.COMBUSTION -> buildCombunstionCar()
        EngineType.ELECTRIC -> TODO()
        EngineType.HYBRID -> TODO()
    }

    private fun buildCombunstionCar() = object:PrivateCombustionCarData {
        override val owner: HouseholdData = this@PrivateCarBuilder.owner!!
        override val mainUser: PersonData = this@PrivateCarBuilder.mainUser!!
        override val segment: CarSegment = this@PrivateCarBuilder.segment!!
        override val seats: Int = this@PrivateCarBuilder.seats!!
        override val range: Distance = this@PrivateCarBuilder.range!!
        override val id: CarId = drawId()
        override val fuelCapacity: Int = this@PrivateCarBuilder.fuelCapacity!!
        override val kwhPerLiter: Double = this@PrivateCarBuilder.kwhPerLiter!!
    }

}