package domain.shared.car.engine

import edu.kit.ifv.units.Distance

interface CarEngine {
    val type: EngineType
    val range: Distance

    @Deprecated("Do we use identical as comparison anywhere, would it not be better to go over equals.")
    fun identical(other: CarEngine) = type == other.type && range == other.range
}
