package utils.units


import kotlin.math.roundToLong

@JvmInline
value class Distance (override val rawValue: Long): Helper<Distance, DistanceUnit> {
    override fun create(value: Long): Distance {
        return Distance(value)
    }

    val inWholeMillimeters: Long
        get() = toLong(DistanceUnit.MILLIMETERS)

    val inWholeCentimeters: Long
        get() = toLong(DistanceUnit.CENTIMETERS)

    val inWholeMeters: Long
        get() = toLong(DistanceUnit.METERS)

    companion object {
        val INFINITE = Distance(Long.MAX_VALUE)
        fun ofMeters(i: Int): Distance {
            return i.toDistance(DistanceUnit.METERS)
        }
        fun ofKilometers(i: Int): Distance {
            return i.toDistance(DistanceUnit.KILOMETERS)
        }
        fun ofKilometers(d: Double): Distance {
            return d.toDistance(DistanceUnit.KILOMETERS)
        }

    }
}

enum class DistanceUnit(override val scale: Long) : ConvertableUnit {
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



/**
 *
 * If the need for smaller representation than [DistanceUnit.MICROMETERS] ever arises and different range
 * representations as in the [kotlin.time.Duration] are necessary this method is the entry point for all
 * conversions to allow simple alteration
 */
fun Double.toDistance(unit: DistanceUnit): Distance {
    val millis = this.roundToLong()
    return Distance(convertUnit(millis, unit.scale))
}

fun Long.toDistance(unit: DistanceUnit): Distance {
    return Distance(convertUnit(this, unit.scale))
}

fun Int.toDistance(unit: DistanceUnit): Distance {
    return toLong().toDistance(unit)
}

val Int.meters: Distance
    get() =  this.toDistance(DistanceUnit.METERS)

