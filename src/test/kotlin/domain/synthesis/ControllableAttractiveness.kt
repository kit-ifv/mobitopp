package domain.synthesis

import domain.shared.behavior.AttractivenessModel
import domain.shared.enums.ActivityType
import domain.simulation.behavior.ChoiceModelPurposes
import domain.simulation.behavior.LegacyActivityType
import domain.simulation.behavior.legacyChoiceModelPurposes
import domain.synthesis.data.Zone
import domain.synthesis.data.ZoneId
import utils.collections.cartesianProduct

class ControllableAttractiveness(zones: Collection<Zone>) : AttractivenessModel {
    constructor() : this(emptyList())
    val attractivenessMap: MutableMap<Pair<ZoneId, ActivityType>, Double> =
        zones.map { it.id }.cartesianProduct(LegacyActivityType.entries).associateWith { 1.0 }.toMutableMap()

    override fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Double {
        return attractivenessMap[Pair(zone, activityType)] ?: 0.0
    }

    override val purposes: ChoiceModelPurposes = legacyChoiceModelPurposes

    operator fun set(zone: ZoneId, activityType: ActivityType, value: Double) {
        attractivenessMap[Pair(zone, activityType)] = value
    }

    operator fun set(zone: ZoneId, value: Double) {
        LegacyActivityType.entries.forEach {
            this[zone, it] = value
        }
    }
}

fun Collection<Zone>.spawnAttractiveness() = ControllableAttractiveness(this)
