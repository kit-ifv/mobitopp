package benchmark

import BIELEFELD
import domain.data.LegacyZone
import domain.data.Zone
import domain.data.ZoneId
import domain.data.point
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import generateActivities
import generateHouseholds
import generateZones
import syntheticsim.ControllableImpedance
import usecases.AttractivenessModel
import usecases.LegacyMode
import usecases.choicemodels.ILegacyDestinationChoice
import usecases.choicemodels.LegacyDestinationChoice
import usecases.choicemodels.NoFilter
import usecases.choicemodels.destinationchoice.ModernizedDestinationChoice
import usecases.legacyChoiceModelModes
import utils.collections.cartesianProduct
import utils.units.sinceStart
import kotlin.random.Random
import kotlin.time.Duration.Companion.hours

fun main() {
    val controllableImpedance = ControllableImpedance()
    val zones = generateZones(100)
    val attractiveness = zones.spawnAttractiveness()
    val households = zones.generateHouseholds(10)
    val persons = households.flatMap { it.members }
    val activities = zones.generateActivities(10)
    controllableImpedance.run {
        zones.generateRandomValues(1 to 360, 0.1 to 100.2, 0.0 to 50.0)
    }
    val destinationChoice: ILegacyDestinationChoice =
        legacyDestinationChoice(controllableImpedance, attractiveness, zones)
    val destinationChoice2: ILegacyDestinationChoice =
        modernizedDestinationChoice(controllableImpedance, attractiveness, zones)
    val amount = 10000000
    val random = Random(42)
    repeat(amount) {
        val zone1 = zones.random(random).point(BIELEFELD)
        val person = persons.random(random)
        val zone2 = zones.random(random).point(BIELEFELD)
        val zone3 = zones.random(random).point(BIELEFELD)
        val act = activities.random(random)

        destinationChoice.calculateU_destination(
            zone1,
            person,
            zone2,
            zone3,
            act,
            0.hours.sinceStart,
            LegacyMode.entries,
            0.5

        )
    }
    repeat(amount) {
        val zone1 = zones.random(random).point(BIELEFELD)
        val person = persons.random(random)
        val zone2 = zones.random(random).point(BIELEFELD)
        val zone3 = zones.random(random).point(BIELEFELD)
        val act = activities.random(random)
        destinationChoice2.calculateU_destination(
            zone1,
            person,
            zone2,
            zone3,
            act,
            0.hours.sinceStart,
            LegacyMode.entries,
            0.5
        )
    }
}

private fun legacyDestinationChoice(
    controllableImpedance: ControllableImpedance,
    attractiveness: ControllableAttractiveness,
    zones: List<LegacyZone>
) = LegacyDestinationChoice(
    controllableImpedance,
    attractiveness,
    umlands = { false },
    zones.toSet(),
    legacyChoiceModelModes,
    NoFilter

)

private fun modernizedDestinationChoice(
    controllableImpedance: ControllableImpedance,
    attractiveness: ControllableAttractiveness,
    zones: Collection<Zone>
) = ModernizedDestinationChoice(
    controllableImpedance,
    attractiveness,
    { false },
    zones.toSet(),
    legacyChoiceModelModes,
)

class ControllableAttractiveness(zones: Collection<Zone>) : AttractivenessModel {
    constructor() : this(emptyList())
    val attractivenessMap: MutableMap<Pair<ZoneId, ActivityType>, Double> =
        zones.map { it.id }.cartesianProduct(LegacyActivityType.entries).associateWith { 1.0 }.toMutableMap()

    override fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Double {
        return attractivenessMap[Pair(zone, activityType)] ?: 0.0
    }

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
