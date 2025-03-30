package usecases.steps.binary

import domain.data.ActivityId
import domain.data.MutablePlannedActivity
import domain.data.Person
import domain.data.PersonId
import domain.data.PlannedActivity
import domain.enums.ActivityType
import utils.CodePlan
import utils.units.sinceStart
import java.io.DataInputStream
import java.io.DataOutputStream
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
@Suppress("MagicNumber")
class BinaryActivityReader(
    private val codeActivity: CodePlan<ActivityType>,
    val personConverter: (PersonId) -> Person,
    private val contextSimulationSeed: Long
) : BinaryReader<MutablePlannedActivity> {

    override fun DataInputStream.decode(stringLength: Int): MutablePlannedActivity {
        return MutablePlannedActivity(
            ActivityId(readLong()),
            contextSimulationSeed
        ).apply {
            val personId = PersonId(readLong())
            person = personConverter(personId)
            observedTripDuration = readInt().toDuration(DurationUnit.MINUTES)
            startTime = readLong().toDuration(DurationUnit.MINUTES).sinceStart
            duration = readInt().toDuration(DurationUnit.MINUTES)
            activityType = codeActivity.decode(readInt())
        }
    }
}

/**
 * Writes a planned activity to a binary file.
 */
class BinaryActivityWriter : BinaryWriter<PlannedActivity> {

    override fun operateStream(outStream: DataOutputStream, elements: Collection<PlannedActivity>) {
        val size = elements.size
        outStream.writeInt(size) // Write the amount of activities that are expected to be found in this file
        outStream.writeInt(0) // Write string length as required by standard format.
        elements.forEach { outStream.encodeActivity(it) } // Write activities
    }

    private fun DataOutputStream.encodeActivity(act: PlannedActivity) {
        writeLong(act.id.value)
        writeLong(act.person.id.value)
        writeInt(act.observedTripDuration.toInt(DurationUnit.MINUTES))
        writeLong(act.startTime.minutesSinceStart)
        writeInt(act.duration.toInt(DurationUnit.MINUTES))
        writeInt(act.activityType.encode())
    }
}
