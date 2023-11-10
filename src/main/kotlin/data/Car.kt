package data

/**
 * The generic interface for a car.
 * @property position It can always be expected for a car to have a location.
 * @property size
 * @property car Segment
 * @property motorType
 * @property
 */
interface Car {
    val position: Location

    fun drive(destination: Location)
}

object GHOSTCAR: Car {
    override val position: Location
        get() = NOWHERE

    override fun drive(destination: Location) {
        return
    }

}
