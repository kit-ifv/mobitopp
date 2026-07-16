package edu.kit.ifv.domain.simulation.data
import edu.kit.ifv.Mutable
import edu.kit.ifv.binary.Simplifiable
import edu.kit.ifv.domain.shared.data.activity.ActivityBinaryRecord
import edu.kit.ifv.domain.shared.data.activity.ActivityId
import edu.kit.ifv.domain.shared.data.person.PersonId
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.utils.Identifiable
import edu.kit.ifv.utils.random.StochasticActor
import edu.kit.ifv.utils.units.AbsoluteTime
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
