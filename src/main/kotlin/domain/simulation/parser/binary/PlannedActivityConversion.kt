package domain.simulation.parser.binary

import domain.shared.enums.ActivityType
import domain.synthesis.data.ActivityId
import domain.synthesis.data.MutablePlannedActivity
import domain.synthesis.data.PlannedActivity
import domain.synthesis.data.person.PersonId
import utils.CodePlan
import utils.binary.BinaryReader
import utils.binary.DefaultBinaryWriter
import utils.units.sinceStart
import java.nio.ByteBuffer
import kotlin.time.DurationUnit
import kotlin.time.toDuration

// * 2) a [personConverter] to map [PersonId] -> [Person]

/**
 * This reader creates a list of [MutablePlannedActivity] from a file. There are 3 arguments that need to be provided in
 * order for a functional conversion.
 * 1) A [codeActivity] CodePlan to decipher [Int] -> [ActivityType]
 * 3) The [contextSimulationSeed]
 *
 */
@Suppress("MagicNumber")
class BinaryActivityReader(
    private val codeActivity: CodePlan<ActivityType>,
//    val personConverter: (PersonId) -> MutablePerson?,
    private val contextSimulationSeed: Long,
) : BinaryReader<MutablePlannedActivity> {

    override fun ByteBuffer.decode(stringLength: Int): MutablePlannedActivity? {
        val id = ActivityId(long)
        val person = PersonId(long) // personConverter(PersonId(long))
        val observedTripDuration = int.toDuration(DurationUnit.MINUTES)
        val startTime = long.toDuration(DurationUnit.MINUTES).sinceStart
        val duration = int.toDuration(DurationUnit.MINUTES)
        val activityType = codeActivity.decode(int)
        return person?.let {
            MutablePlannedActivity(id, person, contextSimulationSeed).apply {
                this.observedTripDuration = observedTripDuration
                this.startTime = startTime
                this.duration = duration
                this.activityType = activityType
            }
        }
    }
}

/**
 * Writes a planned activity to a binary file.
 */
class BinaryActivityWriter : DefaultBinaryWriter<PlannedActivity>() {

//    override fun operateStream(outStream: DataOutputStream, elements: Collection<PlannedActivity>) {
//        elements.forEach { outStream.encodeActivity(it) } // Write activities
//    }
//
//    private fun DataOutputStream.encodeActivity(act: PlannedActivity) {
//        writeLong(act.id.value)
//        writeLong(act.person.id.value)
//        writeInt(act.observedTripDuration.toInt(DurationUnit.MINUTES))
//        writeLong(act.startTime.minutesSinceStart)
//        writeInt(act.duration.toInt(DurationUnit.MINUTES))
//        writeInt(act.activityType.code)
//    }
}
