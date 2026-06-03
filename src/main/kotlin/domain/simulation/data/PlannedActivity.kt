package domain.simulation.data

import Mutable
import utils.binary.Simplifiable
import domain.shared.enums.ActivityType
import domain.shared.location.StandardLocation
import domain.simulation.data.person.PersonId
import kotlinx.serialization.Serializable
import domain.synthesis.data.person.PersonId
import utils.Identifiable
import utils.random.StochasticActor
import utils.units.AbsoluteTime
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.DurationUnit

@Mutable
abstract class PlannedActivity(override val id: ActivityId, val person: PersonId, seed: Long) :
    StochasticActor,
    Identifiable<ActivityId>,
    Simplifiable<ActivityBinaryRecord> {

    final override val random: Random by lazy { Random(id.value + seed) }

    abstract val activityType: ActivityType
    abstract val observedTripDuration: Duration
    abstract val startTime: AbsoluteTime
    abstract val duration: Duration
    abstract val location: StandardLocation?

    val endTime: AbsoluteTime
        get() = startTime + duration

    override fun simplify(): ActivityBinaryRecord = ActivityBinaryRecord(
        id.value,
        person.value,
        observedTripDuration.toInt(DurationUnit.MINUTES),
        startTime.minutesSinceStart,
        duration.toInt(DurationUnit.MINUTES),
        activityType.code,
    )
    override fun toString(): String =
        "${activityType.description.first()}(${activityType.code}) start=$startTime duration=$duration"
}

