package utils.units

enum class DistanceUnit(internal val scale: Long) {
    MICROMETERS(1L),
    MILLIMETERS(1000L),
    CENTIMETERS(10_000L),
    INCH(25_400L),
    DECIMETERS(100_000L),
    YARD(914_400L),
    METERS(1_000_000L),
    KILOMETERS(1_000_000_000L),
    MILE(1_609_340_000L),
    SEA_MILE(1_852_000_000L);

    companion object {
        /**
         * The smallest possible unit of distance represented by this enumeration
         */
        fun atomic(): DistanceUnit {
            val atomic = MICROMETERS
            assert(atomic.scale == 1L)
            return atomic
        }
    }
}

internal fun DistanceUnit.shortName(): String = when (this) {
    DistanceUnit.MICROMETERS -> "µm"
    DistanceUnit.MILLIMETERS -> "mm"
    DistanceUnit.CENTIMETERS -> "cm"
    DistanceUnit.DECIMETERS -> "dm"
    DistanceUnit.METERS -> "m"
    DistanceUnit.KILOMETERS -> "km"
    else -> error("Unknown unit $this")
}

private fun convert(d: Long, dst: Long, src: Long): Long {

    if (src == dst)
        return d;
    else if (src < dst)
        return d / (dst / src)

    val r = src / dst
    val m = Long.MAX_VALUE / r
    if (d > m)
        return Long.MAX_VALUE;
    else if (d < -m)
        return Long.MIN_VALUE;
    else
        return d * r;
}

internal fun convertDistanceUnit(value: Long, sourceUnit: DistanceUnit, targetUnit: DistanceUnit): Long {
    return convert(value, targetUnit.scale, sourceUnit.scale)
}

internal fun convertDistanceUnit(value: Double, sourceUnit: DistanceUnit, targetUnit: DistanceUnit): Double {
    val sInT = convert(1, targetUnit.scale, sourceUnit.scale)
    if(sInT > 0) {
        return value * sInT
    }
    val oInThis = convert(1, sourceUnit.scale, targetUnit.scale)
    return value / oInThis
}