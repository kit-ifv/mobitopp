package edu.kit.ifv.domain.shared.datastructure.matrix.optimized
import kotlin.time.Duration

fun interface DoubleToDuration {
    fun from(x: Double): Duration
}
