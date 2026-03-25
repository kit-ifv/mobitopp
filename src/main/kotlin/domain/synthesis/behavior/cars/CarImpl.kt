package domain.synthesis.behavior.cars

import domain.synthesis.data.Car
import domain.synthesis.data.CarEngine
import domain.synthesis.data.CarEngineStatistics
import domain.synthesis.data.CarId
import domain.synthesis.data.CarSegment
import domain.synthesis.data.EngineType
import domain.synthesis.data.buildEngine
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

data class CarImpl(
    override val engine: CarEngine,
    override val segment: CarSegment,
    override val seats: Int = segment.toSeats(),

    ): Car {

    override val id: CarId = nextId



    companion object {
        @Suppress("MagicNumber") // Seat size is a number
        private fun CarSegment.toSeats(): Int {
            return when (this) {
                CarSegment.SMALL -> 4
                CarSegment.MIDSIZE -> 5
                CarSegment.LARGE -> 5
            }
        }

        fun fromEngineType(
            engineType: EngineType,
            segment: CarSegment,
            seats: Int? = null,
        ): Car {
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