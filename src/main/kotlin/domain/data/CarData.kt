package domain.data

import Buildable
import Decodable
import Encodable
import Identifiable
import utils.units.Distance
import utils.units.DistanceUnit
import utils.units.Energy
import utils.units.EnergyUnits
import utils.units.toEnergy

/**
 * The generic interface for a car.
 * @property segment Segment as desribed in [CarSegment]
 * @property engine: The type of Engine
 * @property seats the amount of people that can travel inside the car including the driver
 */
interface CarData : Identifiable<CarData> {
    val segment: CarSegment
    val engine: EngineType
    val seats: Int
    val range: Distance
    val energyCapacity: Energy

    val efficiency: Double //TODO energy over distance
        get() = energyCapacity.toDouble(EnergyUnits.KILOWATTHOUR) / range.toDouble(DistanceUnit.KILOMETERS)

}

/**
 * A vehicle that is assigned to a specific household or user
 */
interface PersonalCarData: CarData {
    val owner: HouseholdData
    val mainUser: PersonData
}

interface CombustionCarData: CarData {
    val fuelCapacity: Int //TODO unit volume
    val kwhPerLiter: Double //TODO unit energy per volume

    override val energyCapacity: Energy
        get() = (kwhPerLiter * fuelCapacity).toEnergy(EnergyUnits.KILOWATTHOUR)

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
        get() = (kwhPerLiter * fuelCapacity).toEnergy(EnergyUnits.KILOWATTHOUR)

    val electricEfficiency: Double
        get() = batteryCapacity.toDouble(EnergyUnits.KILOWATTHOUR) / electricRange.toDouble(DistanceUnit.KILOMETERS)

    val combustionEfficiency: Double
        get() = combustionCapacity.toDouble(EnergyUnits.KILOWATTHOUR) /
                combustionRange.toDouble(DistanceUnit.KILOMETERS)

    override val engine: EngineType
        get() = EngineType.HYBRID
}

@Buildable
interface PersonalCombustionCarData: PersonalCarData, CombustionCarData

@Buildable
interface PersonalElectricCarData: PersonalCarData, ElectricCarData

@Buildable
interface PersonalHybridCarData: PersonalCarData, HybridCarData

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
        override fun decode(i: Int) = CarSegment.values().first { it.code == i }
    }
}

enum class EngineType(private val code: Int): Encodable {
    COMBUSTION(1),
    ELECTRIC(2),
    HYBRID(3);

    override fun encode() = this.code

    companion object: Decodable<EngineType> {
        override fun decode(i: Int) = EngineType.values().first { it.code == i }
    }
}
