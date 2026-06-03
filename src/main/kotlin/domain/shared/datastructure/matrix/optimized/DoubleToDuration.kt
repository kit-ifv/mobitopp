package domain.shared.datastructure.matrix.optimized

import kotlin.time.Duration

fun interface DoubleToDuration {
    fun from(x: Double): Duration
}
