package domain.synthesis

import domain.shared.behavior.Attractiveness
import domain.shared.behavior.AttractivenessModel
import domain.shared.behavior.asAttractiveness
import domain.shared.enums.ActivityType
import domain.shared.enums.LegacyActivityType
import domain.shared.location.ZoneId
import domain.shared.location.zone.MaximalZone
import utils.collections.cartesianProduct

class ControllableAttractiveness(zones: Collection<MaximalZone>) : AttractivenessModel {
    constructor() : this(emptyList())

    val attractivenessMap: MutableMap<Pair<ZoneId, ActivityType>, Attractiveness> =
        zones.map { it.id }.cartesianProduct(
            LegacyActivityType.entries,
        ).associateWith { Attractiveness.DEFAULT }.toMutableMap()

    override fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Attractiveness =
        attractivenessMap[Pair(zone, activityType)] ?: 0.0.asAttractiveness()

    override val work: ActivityType = LegacyActivityType.WORK
    override val privateVisit: ActivityType = LegacyActivityType.PRIVATE_VISIT

    operator fun set(zone: ZoneId, activityType: ActivityType, value: Double) {
        attractivenessMap[Pair(zone, activityType)] = value.asAttractiveness()
    }

    operator fun set(zone: ZoneId, value: Double) {
        LegacyActivityType.entries.forEach {
            this[zone, it] = value
        }
    }
}

fun Collection<MaximalZone>.spawnAttractiveness() = ControllableAttractiveness(this)
