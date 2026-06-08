package edu.kit.ifv.domain.shared.datastructure.matrix.optimized
import edu.kit.ifv.units.Distance

fun interface DoubleToDistance {
    fun from(x: Double): Distance
}
