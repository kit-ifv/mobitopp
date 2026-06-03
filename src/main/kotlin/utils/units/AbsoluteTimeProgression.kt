package utils.units

import kotlin.time.Duration

class AbsoluteTimeProgression(
    override val start: AbsoluteTime,
    override val endInclusive: AbsoluteTime,
    val step: Duration,
) : Iterable<AbsoluteTime>,
    ClosedRange<AbsoluteTime> {

    init {
        require(step != Duration.Companion.ZERO && !step.isNegative()) {
            "Step must be positive, but was $step"
        }
    }

    override fun iterator(): Iterator<AbsoluteTime> = object : Iterator<AbsoluteTime> {
        private var current = start

        override fun hasNext(): Boolean = current <= endInclusive

        override fun next(): AbsoluteTime {
            if (!hasNext()) {
                throw NoSuchElementException(
                    "Nex increment step ${current + step} is out of range: [$start, $endInclusive]",
                )
            }

            val result = current
            current += step
            return result
        }
    }

    infix fun step(newStep: Duration): AbsoluteTimeProgression = AbsoluteTimeProgression(start, endInclusive, newStep)
}
