package utils.units

interface NumericUnit<N: NumericUnitScale> {
    val rawValue: Number
    fun toDouble(unit: N): Double
    fun toLong(unit: N): Long
    fun toInt(unit: N): Int

    operator fun unaryMinus(): NumericUnit<N>

}

interface NumericUnitScale {
    val scale: Number
}