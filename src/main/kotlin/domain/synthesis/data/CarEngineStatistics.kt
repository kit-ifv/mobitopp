package domain.synthesis.data

import units.Distance
import units.Energy
import units.Volume
import units.kilometers
import units.kilowatthours
import units.liters

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

fun CarEngineStatistics.buildEngine(segment: CarSegment, engine: EngineType): CarEngine {
    val stats = this

    return when (engine) {
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
