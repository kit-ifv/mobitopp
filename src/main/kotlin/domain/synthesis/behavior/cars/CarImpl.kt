package domain.synthesis.behavior.cars

import domain.simulation.data.car.Car
import domain.simulation.data.car.CarId
import domain.simulation.data.car.CarSegment
import domain.simulation.data.car.engine.CarEngine
import domain.simulation.data.car.engine.CarEngineStatistics
import domain.simulation.data.car.engine.EngineType
import domain.simulation.data.car.engine.buildEngine
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

data class CarImpl(
    override val engine: CarEngine,
    override val segment: CarSegment,
    override val seats: Int = segment.toSeats(),
) : Car {

    override val id: CarId = nextId

    companion object {
        @Suppress("MagicNumber") // Seat size is a number
        private fun CarSegment.toSeats(): Int = when (this) {
            CarSegment.SMALL -> 4
            CarSegment.MIDSIZE -> 5
            CarSegment.LARGE -> 5
        }

        fun fromEngineType(engineType: EngineType, segment: CarSegment, seats: Int? = null): Car {
            val actualSeats = seats ?: segment.toSeats()
            return CarImpl(
                engine = CarEngineStatistics().buildEngine(segment, engineType),
                segment = segment,
                seats = actualSeats,
            )
        }

        @OptIn(ExperimentalAtomicApi::class)
        private var idCounter: AtomicLong = AtomicLong(0L)

        @OptIn(ExperimentalAtomicApi::class)
        private val nextId: CarId get() = CarId(idCounter.incrementAndFetch())
    }
}
