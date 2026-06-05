package edu.kit.ifv.domain.synthesis
import edu.kit.ifv.domain.shared.behavior.Attractiveness
import edu.kit.ifv.domain.shared.behavior.AttractivenessModel
import edu.kit.ifv.domain.shared.behavior.asAttractiveness
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.enums.LegacyActivityType
import edu.kit.ifv.domain.shared.location.zone.MaximalZone
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.utils.collections.cartesianProduct

class ControllableAttractiveness(zones: Collection<MaximalZone>) : AttractivenessModel {
    constructor() : this(emptyList())

    val attractivenessMap: MutableMap<Pair<ZoneId, ActivityType>, Attractiveness> =
        zones.map { it.id }.cartesianProduct(
            LegacyActivityType.entries,
        ).associateWith { Attractiveness.DEFAULT }.toMutableMap()

    override fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Attractiveness = attractivenessMap[
        Pair(
            zone,
            activityType,
        ),
    ] ?: 0.0.asAttractiveness()

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
