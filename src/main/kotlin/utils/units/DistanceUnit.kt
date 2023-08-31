package utils.units

import java.io.DataInput

public enum class DistanceUnit {
    MILLIMETERS,
    CENTIMETERS,
    DECIMETERS,
    METERS,
    KILOMETERS
}

internal fun DistanceUnit.shortName(): String = when (this) {
    DistanceUnit.MILLIMETERS -> "mm"
    DistanceUnit.CENTIMETERS -> "cm"
    DistanceUnit.DECIMETERS -> "dm"
    DistanceUnit.METERS -> "m"
    DistanceUnit.KILOMETERS -> "km"
    else -> error("Unknown unit $this")
}