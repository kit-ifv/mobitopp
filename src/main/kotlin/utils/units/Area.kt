package utils.units


@JvmInline
value class Area internal constructor(override val rawValue: Double): FloatUnit<AreaUnits> {
    companion object {
        /**
         * Create the area defined by two distances spanning a rectangle
         */
        fun ofRectangle(a: Distance, b: Distance): Area {
            return Area(a.toDouble(DistanceUnit.METERS) * b.toDouble(DistanceUnit.METERS))
        }
    }

    override fun plus(other: NumericUnit<AreaUnits>): Area {
        return Area(this.rawValue + other.rawValue.toDouble())
    }

    override fun minus(other: NumericUnit<AreaUnits>): Area {
        return this + (-other)
    }

    override fun unaryMinus(): Area {
        return Area(-this.rawValue)
    }
}

fun Int.toArea(units: AreaUnits): Area {
    return Area(this * units.scale)
}

fun Long.toArea(units: AreaUnits): Area {
    return Area(this * units.scale)
}

fun Double.toArea(units: AreaUnits): Area {
    return Area(this * units.scale)
}

enum class AreaUnits(override val scale: Double): FloatUnitScale {
    SQUARE_METERS(1.0),
    SQUARE_INCH(0.00064516),
    SQUARE_KILOMETERS(1000.0 * 1000.0)
}
