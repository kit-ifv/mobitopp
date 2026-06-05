package edu.kit.ifv.domain.shared.car.engine
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.Efficiency
import edu.kit.ifv.units.Energy

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
