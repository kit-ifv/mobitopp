package domain.synthesis.data

import Mutable
import domain.jackson.BinaryWritable
import domain.jackson.Simplifiable
import domain.shared.enums.ActivityType
import domain.shared.location.StandardLocation
import domain.synthesis.data.person.PersonId
import kotlinx.serialization.Serializable
import utils.Identifiable
import utils.random.StochasticActor
import utils.units.AbsoluteTime
import java.io.DataOutputStream
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.DurationUnit

@Serializable
@JvmInline
value class ActivityId(val value: Long) : Comparable<ActivityId> {
    /**
     * Compares this object with the specified object for order. Returns zero if this object is equal
     * to the specified [other] object, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: ActivityId): Int = value.compareTo(other.value)

    /**
     * @return the next higher id.
     */
    fun next(): ActivityId = ActivityId(value + 1)
}

@Mutable
abstract class PlannedActivity(
    override val id: ActivityId,
    val person: PersonId, // MutablePerson,
    seed: Long,
) : StochasticActor,
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
        person.value, // person.id.value,
        observedTripDuration.toInt(DurationUnit.MINUTES),
        startTime.minutesSinceStart,
        duration.toInt(DurationUnit.MINUTES),
        activityType.code,
    )
    override fun toString(): String =
        "${activityType.description.first()}(${activityType.code}) start=$startTime duration=$duration"
}

data class ActivityBinaryRecord(
    val id: Long,
    val personId: Long,
    val observedTripDuration: Int,
    val startTime: Long,
    val duration: Int,
    val activityCode: Int,
) : BinaryWritable {
    override fun writeTo(outStream: DataOutputStream) {
        outStream.run {
            writeLong(id)
            writeLong(personId)
            writeInt(observedTripDuration)
            writeLong(startTime)
            writeInt(duration)
            writeInt(activityCode)
        }
    }
}
