package utils.units

interface NumericUnit<SCALE : NumericUnitScale> {
    val rawValue: Number
    fun toDouble(unit: SCALE): Double
    fun toLong(unit: SCALE): Long
    fun toInt(unit: SCALE): Int

    operator fun unaryMinus(): NumericUnit<SCALE>

}

interface NumericUnitScale {
    val scale: Number
}
