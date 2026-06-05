package edu.kit.ifv.domain.simulation.parser.binary

import edu.kit.ifv.domain.shared.data.activity.ActivityId
import edu.kit.ifv.domain.shared.data.person.PersonId
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.simulation.data.MutablePlannedActivity
import edu.kit.ifv.utils.CodePlan
import edu.kit.ifv.utils.binary.BinaryReader
import edu.kit.ifv.utils.units.sinceStart
import java.nio.ByteBuffer
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * This reader creates a list of [edu.kit.ifv.domain.simulation.data.MutablePlannedActivity] from a file. There are 3 arguments that need to be provided in
 * order for a functional conversion.
 * 1) A [codeActivity] CodePlan to decipher [Int] -> [edu.kit.ifv.domain.shared.enums.ActivityType]
 * 3) The [contextSimulationSeed]
 *
 */
@Suppress("MagicNumber")
class BinaryActivityReader(
    private val codeActivity: CodePlan<ActivityType>,
    private val contextSimulationSeed: Long,
) : BinaryReader<MutablePlannedActivity> {

    override fun ByteBuffer.decode(stringLength: Int): MutablePlannedActivity? {
        val id = ActivityId(long)
        val person = PersonId(long)
        val observedTripDuration = int.toDuration(DurationUnit.MINUTES)
        val startTime = long.toDuration(DurationUnit.MINUTES).sinceStart
        val duration = int.toDuration(DurationUnit.MINUTES)
        val activityType = codeActivity.decode(int)
        return person.let {
            MutablePlannedActivity(id, person, contextSimulationSeed).apply {
                this.observedTripDuration = observedTripDuration
                this.startTime = startTime
                this.duration = duration
                this.activityType = activityType
            }
        }
    }
}