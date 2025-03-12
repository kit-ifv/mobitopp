package utils.binary

import domain.data.*
import domain.enums.ActivityType
import utils.CodePlan
import utils.units.sinceStart
import java.io.DataOutputStream
import java.nio.MappedByteBuffer
import java.nio.file.Path
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * This reader creates a list of [MutablePlannedActivity] from a file. There are 3 arguments that need to be provided in
 * order for a functional conversion.
 * 1) A [codeActivity] CodePlan to decipher [Int] -> [ActivityType]
 * 2) a [personConverter] to map [PersonId] -> [Person]
 * 3) The [contextSimulationSeed]
 *
 */
class BinaryActivityReader(
    private val codeActivity: CodePlan<ActivityType>,
    val personConverter: (PersonId) -> Person,
    private val contextSimulationSeed: Long
) : BinaryReader<MutablePlannedActivity> {

    override fun fromBinary(path: Path): List<MutablePlannedActivity> {
        return path.operateOnMemoryFile {
            val size = this.getInt(0)
            val idArray = Array(size) {
                ActivityId(-1L)
            }

            for (i in 0 until size) {
                idArray[i] = extractIds(this, i * idByteSize + 4)
            }
            val activities = idArray.map {
                MutablePlannedActivity(
                    it,
                    contextSimulationSeed
                )
            }
            for (i in 0 until size) {
                extractContent(this, i * attributesByteSize + 4 + size * idByteSize, activities[i])
            }

            activities
        }
    }

    private fun extractIds(buffer: MappedByteBuffer, at: Int): ActivityId {
        return ActivityId(buffer.getLong(at))
    }

    private val idByteSize = 8

    private fun extractContent(buffer: MappedByteBuffer, at: Int, target: MutablePlannedActivity) {
        TrackingBuffer(buffer, at).run {
            target.apply {
                val personId = PersonId(nextLong)
                person = personConverter(personId)
                observedTripDuration = nextInt.toDuration(DurationUnit.MINUTES)
                startTime = nextLong.toDuration(DurationUnit.MINUTES).sinceStart
                duration = nextInt.toDuration(DurationUnit.MINUTES)
                activityType = codeActivity.decode(nextInt)
            }
        }
    }

    private val attributesByteSize = 28
}

/**
 * Writes a planned activity to a binary file.
 */
class BinaryActivityWriter : BinaryWriter<PlannedActivity> {

    override fun operateStream(outStream: DataOutputStream, elements: Collection<PlannedActivity>) {
        val size = elements.size
        outStream.writeInt(size) // Write the amount of activities that are expected to be found in this file

        elements.forEach { outStream.encodeID(it) } // Write the id of the activity
        elements.forEach { outStream.encodeAttributes(it) } // write all the other information.
    }

    private fun DataOutputStream.encodeID(act: PlannedActivity) {
        writeLong(act.id.value) //  8 Bytes
    }

    private fun DataOutputStream.encodeAttributes(act: PlannedActivity) {
        writeLong(act.person.id.value) //  8 Bytes
        writeInt(act.observedTripDuration.toInt(DurationUnit.MINUTES)) // 12 Bytes
        writeLong(act.startTime.minutesSinceStart) // 20 Bytes
        writeInt(act.duration.toInt(DurationUnit.MINUTES)) // 24 Bytes
        writeInt(act.activityType.encode()) // 28 Bytes
    }
}
