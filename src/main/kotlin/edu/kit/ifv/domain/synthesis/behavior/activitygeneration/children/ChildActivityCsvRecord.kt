package edu.kit.ifv.domain.synthesis.behavior.activitygeneration.children

import com.fasterxml.jackson.annotation.JsonProperty
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Activity
import edu.kit.ifv.domain.shared.datastructure.schedule.action.RawActivity
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.utils.units.sinceStart
import kotlin.time.Duration.Companion.minutes

data class ChildActivityCsvRecord(
    @field:JsonProperty("pers_id")
    val personId: Long,
    @field:JsonProperty("tagestyp")
    val dayTypeCode: Int,
    @field:JsonProperty("start_min")
    val activityStartMinutes: Int,
    @field:JsonProperty("dauer_min")
    val activityDurationMinutes: Int,
    @field:JsonProperty("acttyp")
    val actType: String,
    @field:JsonProperty("agegroup")
    val ageGroupCode: Int,

) {
    val ageGroup get() = AgeGroup.decode(ageGroupCode)
    val dayType get() = DayType.decode(dayTypeCode)
    val startTime get() = activityStartMinutes.minutes.sinceStart
    val duration get() = activityDurationMinutes.minutes.coerceAtLeast(1.minutes)
    val endTime get() = startTime + duration
    fun toActivity(decoder: (String) -> ActivityType): Activity = RawActivity(
        location = StandardLocation.Companion.LOCATIONUNKNOWN,
        startTime = startTime,
        endTime = endTime,
        type = decoder(actType.uppercase()),
    )
}
