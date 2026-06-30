package edu.kit.ifv.domain.shared.car
import edu.kit.ifv.domain.shared.car.engine.CarEngine
import edu.kit.ifv.utils.Identifiable

/**
 * The generic interface for a car.
 * @property segment Segment as described in [CarSegment]
 * @property engine: The type of Engine
 * @property seats the amount of people that can travel inside the car including the driver
 */
interface Car : Identifiable<CarId> {
    val segment: CarSegment
    val engine: CarEngine
    val seats: Int
}
