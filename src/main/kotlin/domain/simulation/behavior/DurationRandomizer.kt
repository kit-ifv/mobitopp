package domain.simulation.behavior

import domain.shared.datastructure.schedule.Activity
import domain.simulation.agent.PersonAgent
import domain.simulation.config.DemandSimContext
import utils.random.getGaussian
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.times

abstract class ActivityDurationRandomizer {
    fun randomizeAll(person: PersonAgent) {
        val schedule = person.schedule
        for (i in 0 until schedule.activities().size) {
            val act = schedule.activities().toList()[i]
            act.duration = randomizeDuration(act, act.duration, person.random)
        }
    }

    abstract fun randomizeDuration(activity: Activity, currentDuration: Duration, rand: Random): Duration
}

@Suppress("MagicNumber")
class GaussianActivityDurationRandomizer(
    private val min: Duration = 1.minutes,
    private val max: Duration = 7.days,
) : ActivityDurationRandomizer() {
    override fun randomizeDuration(activity: Activity, currentDuration: Duration, rand: Random): Duration {
        val gaussian: Double = rand.getGaussian(0.0, 1.0)
        val deviation: Duration = (gaussian * currentDuration) / 20.0

        return (currentDuration + deviation).coerceIn(min, max)
    }
}

// TODO move to other file knowing about mobitopp when restructuring packages

fun DemandSimContext.gaussianDurationRandomizer() = GaussianActivityDurationRandomizer(
    max = this.simulationEnd.minus(this.simulationStart),
)

object NoDurationRandomizer : ActivityDurationRandomizer() {
    override fun randomizeDuration(activity: Activity, currentDuration: Duration, rand: Random) = currentDuration
}
