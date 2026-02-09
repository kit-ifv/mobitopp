package domain.shared.datastructure.schedule.replanning

/**
 * Resolves a temporal conflict by proposing a sequence of changes to the involved activities.
 *
 * A [ConflictResolver] receives a fully specified [Conflict] and returns a list of proposed
 * modifications in the same order as the conflict’s activity list.
 *
 * Each element in the returned list represents the resolution for the corresponding activity:
 * - a non-null [Change] updates the activity’s start time and/or duration
 * - `null` indicates that the activity should be removed from the schedule
 *
 * The resolver itself does not apply the changes; it merely proposes them. Interpretation
 * and execution of the returned changes (including validation and fallback behavior) is the
 * responsibility of the caller.
 *
 * Implementations are free to use any strategy (compression, shifting, deletion, etc.),
 * but should ensure that mandatory activities are not removed unless explicitly permitted
 * by the caller.
 */
fun interface ConflictResolver {
    fun resolveConflict(conflict: Conflict): List<Change?>
}
