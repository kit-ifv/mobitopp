package edu.kit.ifv.domain.shared.datastructure.schedule.replanning
import edu.kit.ifv.domain.shared.datastructure.schedule.action.StationaryAction
import edu.kit.ifv.domain.shared.location.zone.attributes.HasZoneId
import edu.kit.ifv.utils.units.AbsoluteTime

/**
 * This is a conflict, you have a set of readonly actions, that you want to perform, but a time window, that currently
 * does not support all the actions.
 */
data class Conflict(
    val startTime: AbsoluteTime,
    val endTime: AbsoluteTime,
    val startLocation: HasZoneId?,
    val endLocation: HasZoneId?,
    val actions: List<StationaryAction>,
)
