package edu.kit.ifv.domain.shared.behavior

import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.zone.ZoneId

/**
 * Caches Attractivities of zones for a given activity. Calculation is lazy, so first get for an activity is expensive
 * and cheap afterward.
 *
 * @param attractivenessModel Some model modeling attractiveness of zones for different ActivityTypes. Should be able to
 * generate attractivities for all [zoneIds] and all activities this cacher gets called with.
 * @param zoneIds All zoneIDs the cacher generates attractivities for.
 */
class AttractivenessCacher(
    private val attractivenessModel: AttractivenessModel,
    private val zoneIds: List<ZoneId>,
) {
    private val map: MutableMap<ActivityType, DoubleArray> = mutableMapOf()

    /**
     * Returns a DoubleArray which encodes the attractiveness for the given [activity]. The attractivity of a given zone
     * is at the index of the zone, that was provided in [zoneIds].
     * @param activity
     */
    operator fun get(activity: ActivityType): DoubleArray {
        return map.getOrPut(activity) {
            DoubleArray(zoneIds.size) {
                attractivenessModel.attractivenessFor(zoneIds[it], activity).value
            }
        }
    }
}