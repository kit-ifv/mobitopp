package domain.shared.datastructure.schedule.replanning

import domain.shared.datastructure.schedule.StationaryAction
import domain.shared.location.attributes.HasZoneID
import utils.units.AbsoluteTime

/**
 * This is a conflict, you have a set of readonly actions, that you want to perform, but a time window, that currently
 * does not support all the actions.
 */
data class Conflict(
    val startTime: AbsoluteTime,
    val endTime: AbsoluteTime,
    val startLocation: HasZoneID?,
    val endLocation: HasZoneID?,
    val actions: List<StationaryAction>
)
