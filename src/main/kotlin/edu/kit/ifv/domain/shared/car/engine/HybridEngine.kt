package edu.kit.ifv.domain.shared.car.engine
import edu.kit.ifv.units.Distance

interface HybridEngine :
    CombustionEngine,
    ElectricEngine {

    override val range: Distance
        get() = electricRange + combustionRange

    override val type: EngineType
        get() = EngineType.HYBRID
}
