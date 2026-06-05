package domain.shared.car.engine

import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.Efficiency
import edu.kit.ifv.units.Volume
import edu.kit.ifv.units.kilometers

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
