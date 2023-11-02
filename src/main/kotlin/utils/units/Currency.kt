package utils.units

@JvmInline
value class Currency(override val rawValue: Double) : FloatUnit<CurrencyUnits> {
    override fun plus(other: FloatUnit<CurrencyUnits>): FloatUnit<CurrencyUnits> {
        return Currency(this.rawValue + other.rawValue)
    }

    override fun minus(other: FloatUnit<CurrencyUnits>): FloatUnit<CurrencyUnits> {
        return this + (-other)
    }

    override fun unaryMinus(): FloatUnit<CurrencyUnits> {
        return Currency(-rawValue)
    }

}


enum class CurrencyUnits(override val scale: Double) : FloatUnitScale {
    EUROS(1.0)
}
