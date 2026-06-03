package domain.shared.datastructure.matrix.optimized

import edu.kit.ifv.units.Currency

fun interface DoubleToCurrency {
    fun from(x: Double): Currency
}
