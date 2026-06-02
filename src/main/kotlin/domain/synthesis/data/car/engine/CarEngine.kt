package domain.synthesis.data.car.engine

import edu.kit.ifv.units.Distance

interface CarEngine {
    val type: EngineType
    val range: Distance

    fun identical(other: CarEngine) = type == other.type && range == other.range
}
