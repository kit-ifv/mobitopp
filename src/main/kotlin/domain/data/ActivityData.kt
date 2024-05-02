package domain.data

import domain.enums.ActivityType
import utils.Builder
import utils.ID
import utils.Identifiable
import kotlin.random.Random
import kotlin.time.Duration

typealias ActivityId = ID<HouseholdData>

interface ActivityData : Identifiable<ActivityId> {

    val person: PersonData
    val activityType: ActivityType
    val observedTripDuration: Duration
    val startTime: Duration
    val duration: Duration

    val random: Random
}

class ActivityDataBuilder(
    val id: Long? = null,
    val person: PersonData? = null,
    val activityType: ActivityType? = null,
    val observedTripDuration: Duration? = null,
    val startTime: Duration? = null,
    val duration: Duration? = null
) : Builder<ActivityData> {

    override fun build() = object : ActivityData {
        override val activityType = this@ActivityDataBuilder.activityType!!
        override val person = this@ActivityDataBuilder.person!!
        override val duration = this@ActivityDataBuilder.duration!!
        override val startTime = this@ActivityDataBuilder.startTime!!
        override val observedTripDuration = this@ActivityDataBuilder.observedTripDuration!!

        override val id: ActivityId = ID(this@ActivityDataBuilder.id!!)

        override val random = Random(id.id)

        init {
            person.addActivity(this)
        }
    }

}