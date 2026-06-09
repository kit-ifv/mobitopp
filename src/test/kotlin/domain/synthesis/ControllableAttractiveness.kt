package domain.synthesis

import domain.shared.behavior.Attractiveness
import domain.shared.behavior.AttractivenessModel
import domain.shared.behavior.ChoiceModelPurposes
import domain.shared.behavior.asAttractiveness
import domain.shared.enums.ActivityType
import domain.shared.enums.LegacyActivityType
import domain.shared.enums.legacyChoiceModelPurposes
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import utils.collections.cartesianProduct

class ControllableAttractiveness(zones: Collection<Zone>) : AttractivenessModel {
    constructor() : this(emptyList())

    val attractivenessMap: MutableMap<Pair<ZoneId, ActivityType>, Attractiveness> =
        zones.map { it.id }.cartesianProduct(
            LegacyActivityType.entries
        ).associateWith { Attractiveness.DEFAULT }.toMutableMap()

    override fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Attractiveness {
        return attractivenessMap[Pair(zone, activityType)] ?: 0.0.asAttractiveness()
    }

    operator fun set(zone: ZoneId, activityType: ActivityType, value: Double) {
        attractivenessMap[Pair(zone, activityType)] = value.asAttractiveness()
    }

    operator fun set(zone: ZoneId, value: Double) {
        LegacyActivityType.entries.forEach {
            this[zone, it] = value
        }
    }
}

fun Collection<Zone>.spawnAttractiveness() = ControllableAttractiveness(this)
