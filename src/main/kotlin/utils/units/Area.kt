package utils.units


@JvmInline
value class Area internal constructor(private val rawValue: Double) {
    companion object {
        fun of(a: Distance, b: Distance): Area {
            return Area(a.toDouble(DistanceUnit.METERS) * b.toDouble(DistanceUnit.METERS))
        }
    }
}

enum class AreaUnits {

}