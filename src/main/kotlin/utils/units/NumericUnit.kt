package utils.units

interface NumericUnit<N: NumericUnitScale> {
    fun toDouble(unit: N): Double
    fun toLong(unit: N): Long
    fun toInt(unit: N): Int
}

interface NumericUnitScale {
    val scale: Number
}