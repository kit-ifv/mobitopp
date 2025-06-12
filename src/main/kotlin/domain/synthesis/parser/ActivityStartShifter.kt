package domain.synthesis.parser

import utils.random.StochasticActor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

fun interface ActivityStartShifter {
    operator fun invoke(actor: StochasticActor): Duration
    fun cached() = CachedActivityStartShifter(this)
}

@Suppress("MagicNumber")
object QuarterHourShifter : ActivityStartShifter {
    override operator fun invoke(actor: StochasticActor) = actor.random.nextDouble(-7.5, 7.5).minutes
}

object NoActivityStartShifter : ActivityStartShifter {
    override operator fun invoke(actor: StochasticActor) = Duration.ZERO
}

class CachedActivityStartShifter(
    private val shifter: ActivityStartShifter
) : ActivityStartShifter {
    private val shifts: MutableMap<StochasticActor, Duration> = mutableMapOf()
    override operator fun invoke(actor: StochasticActor) = shifts.computeIfAbsent(actor) { shifter(actor) }
}
