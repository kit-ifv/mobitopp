package synthesis.fixedDestinations

import domain.data.Zone
import domain.data.ZoneId
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.location.DistanceMetric
import domain.location.Location
import synthesis.CommuteDistance
import synthesis.domain.SynthesisPerson
import synthesis.fixedDestinations.BiMap.Companion.toBiMap
import units.Distance
import usecases.AttractivenessModel
import utils.csv.DefaultCsvParser
import java.nio.file.Path
import kotlin.io.path.Path

data class CommuterInfo(val origin: CommunityNumber, val destination: CommunityNumber, val amount: Int)



@JvmInline
private value class ZoneNumber(private val int: Int) {

    fun toZoneId(): ZoneId {
        return ZoneId(int.toLong())
    }

    companion object {
        fun parse(string: String): ZoneNumber {
            return ZoneNumber(string.toInt())
        }
    }
}

@JvmInline
value class CommunityNumber(private val int: Int) {
    companion object {
        fun parse(string: String): CommunityNumber {
            return CommunityNumber(string.toInt())
        }
    }
}



class BiMap<K, V>(
    val forwardMap: MutableMap<K, V> = mutableMapOf(),
    val backwardMap: MutableMap<V, List<K>> = mutableMapOf()
) {

    operator fun contains(element: K): Boolean {
        return element in forwardMap
    }

    fun containsValue(element: V): Boolean {
        return element in backwardMap
    }

    companion object {
        fun <K, V> Map<K, V>.toBiMap(): BiMap<K, V> {
            val backwardMap =
                this.entries.groupBy { it.value }.map { entry -> entry.key to entry.value.map { it.key } }.toMap()
            return BiMap(this.toMutableMap(), backwardMap.toMutableMap())
        }
    }
}



class CommuterMatrix(
    val translator: BiMap<ZoneId, CommunityNumber>,
    commuterInfo: List<CommuterInfo>,
    val zoneMapping: Map<ZoneId, Zone>,
) {
    val workZones = zoneMapping.values.filter { it.id in translator.forwardMap.keys }
    private val commuterTargets: Map<CommunityNumber, Map<CommunityNumber, Double>> =
        commuterInfo.groupBy { it.origin }
            .mapValues { targets ->
                targets.value.filter { it.amount > 0 }.associate { it.destination to it.amount.toDouble() }
            }
    val requests: Map<CommunityNumber, Double> = commuterTargets.entries.associate { it.key to it.value.values.sum() }
    val targetSize = commuterTargets.values.sumOf { it.values.sum() }
    fun commuterTargets(origin: Zone, destination: Zone): Double {
        val originCommunity = translator.forwardMap[origin.id] ?: throw NoSuchElementException("Did not find ${origin.id} in commuting relations")
        val destinationCommunity = translator.forwardMap[destination.id] ?: throw NoSuchElementException("Did not find ${destination.id} in commuting relations")
        val originCommuters = commuterTargets[originCommunity] ?: emptyMap()

        return originCommuters[destinationCommunity] ?: 0.0
    }

    fun getCommunityId(zone: Zone): CommunityNumber {
        return translator.forwardMap[zone.id]
            ?: throw NoSuchElementException("Cannot create community Id for $zone as it is not present in the translations ${translator.forwardMap.keys}")
    }

    companion object {
        fun parse(
            mappingFile: Path = Path("src/test/resources/synthesis/zone-to-community.csv"),
            commuterFile: Path = Path("src/test/resources/synthesis/commuters-rastatt.csv"),
            zoneMapping: Map<ZoneId, Zone>,
        ): CommuterMatrix {
            return CommuterMatrix(
                readZoneToCommunity(mappingFile),
                readCommuters(commuterFile).toList(),
                zoneMapping,
            )
        }

        private fun readZoneToCommunity(file: Path): BiMap<ZoneId, CommunityNumber> {
            val parser = DefaultCsvParser { row ->
                Pair(
                    row("partId") { ZoneNumber.parse(it).toZoneId() },
                    row("regionId") { CommunityNumber.parse(it) },

                    )
            }

            return parser.parse(file.toFile()).toMap().toBiMap()
        }

        private fun readCommuters(file: Path): Sequence<CommuterInfo> {
            val parser = DefaultCsvParser { row ->
                CommuterInfo(
                    row("origin") { CommunityNumber.parse(it) },
                    row("destination") { CommunityNumber.parse(it) },
                    row("commuters").toInt(),

                    )
            }

            return parser.parse(file.toFile())
        }
    }
}


class AssignFromCommuterMatrix(
    private val commuterMatrix: CommuterMatrix,
    val metric: DistanceMetric,
    val attractivenessModel: AttractivenessModel,
    val findProperWorkspace: FindProperWorkspace,
    val zoneLocationAssigner: DetermineLocationInZone
) : GroupActivityLocator<CommuteDistance> {

    private val legacyActivityType = LegacyActivityType.WORK
    private val helper: Map<Zone, Collection<WorkDistances>> = unrollCommutermatrix()
    private val assignedPeeps: MutableMap<Pair<CommunityNumber, CommunityNumber>, Int> = mutableMapOf()
    init {
        println("The commuter matrix wants  ${commuterMatrix.targetSize} workers assigned")
    }

    fun access(origin: Zone, destination: Zone): Int {
        val target = commuterMatrix.getCommunityId(origin) to commuterMatrix.getCommunityId(destination)
        return assignedPeeps.getOrPut(target) { 0 }
    }

    private fun increment(origin: Zone, destination: Zone) {
        val target = commuterMatrix.getCommunityId(origin) to commuterMatrix.getCommunityId(destination)
        assignedPeeps[target] = assignedPeeps.getOrPut(target) { 0 } + 1
    }

    /**
     * There is no distance evaluable between communities, only zones, so we need to unroll the entirety to get
     * approximate distances for actually assigning a work location based on the survey data.
     */
    private fun unrollCommutermatrix(): Map<Zone, Collection<WorkDistances>> {
        return commuterMatrix.workZones.associateWith { origin ->
            commuterMatrix.workZones.map { destination ->
                WorkDistances(
                    origin = origin,
                    destination = destination,
                    attractiveness = attractivenessModel.attractivenessFor(destination.id, legacyActivityType),
                    distance = metric.evaluate(origin.centroid, destination.centroid),
                    communityCommutersDesired = commuterMatrix.commuterTargets(origin, destination),
                    originCommunity = commuterMatrix.getCommunityId(origin),
                    destinationCommunity = commuterMatrix.getCommunityId(destination),
                    tracker = ::access
                )
            }
        }
    }

    fun find(person: SynthesisPerson<out CommuteDistance>, activityType: ActivityType): Location {
        return location(person)
    }

    private fun location(person: SynthesisPerson<out CommuteDistance>): Location {
        val homeZone = person.homeLocation.requireZone()

        val workies = helper[homeZone]
            ?: throw NoSuchElementException("It appears that there are no associated work commute rules for the given home zone $homeZone . $helper")
        val targetCommunities = workies.mapNotNull { commuterMatrix.translator.forwardMap[it.destination.id] }.toSet()
        val destinationZone = findProperWorkspace.assign(workies, person.info.distanceWork).destination
        increment(homeZone, destinationZone)
        return zoneLocationAssigner.getLocation(
            destinationZone
        )
    }

    override fun find(
        group: Collection<SynthesisPerson<out CommuteDistance>>,
        activityType: ActivityType
    ): Collection<Pair<SynthesisPerson<out CommuteDistance>, Location>> {
        val targets = group.groupBy { commuterMatrix.getCommunityId(it.homeLocation.requireZone()) }
        val canIMeetTheRequirements = targets.entries.associate { it.key to (it.value.size to commuterMatrix.requests[it.key]) }
        return group.map { it to find(it, activityType) }
    }
}

/**
 * The data of flows between zones
 */
class WorkDistances(
    val origin: Zone,
    val destination: Zone,
    val attractiveness: Double,
    val distance: Distance,
    val communityCommutersDesired: Double,
    val originCommunity: CommunityNumber,
    val destinationCommunity: CommunityNumber,
    val tracker: (Zone, Zone) -> Int
) {
    private fun communityCommutersAssigned(): Int {
        return tracker(origin, destination)
    }

    val communityCommuters get() = communityCommutersAssigned()
    fun isUndersaturated(): Boolean {
        return communityCommutersAssigned() < communityCommutersDesired
    }

    override fun toString(): String {
        return "${origin.id}, ${destination.id}: Current: $communityCommuters Target: $communityCommutersDesired"
    }
}

fun interface FindProperWorkspace {

    fun assign(workies: Collection<WorkDistances>, personWorkDistance: Distance): WorkDistances
}

/**
 * This workspace assignment strategy simply tries to take the first zone where the distance of the centroid exceeds
 * the distance requirement of the person. The attractiveness is ignored. The saturation of previous assignments is
 * considered. The algorithm tries to avoid assigning any work locations which are already completely saturated based
 * on the conditions defined in the commuter relations. For this reason there exist 2 fallback solutions:
 *
 * 1) If all targets with a distance >= #targetDistance are completely saturated, the target with the largest remaining
 * distance is considered.
 * 2) If all targets are saturated, no filter will be applied anymore.
 */
val GREEDY_BY_DISTANCE = FindProperWorkspace { targets, distance ->

    val potentialTargets = if (targets.any { it.isUndersaturated() }) {
        targets.filter { it.isUndersaturated() }
        // Fallback solution, if all destinations are saturated, use the original set again
    } else {
        targets
    }

    val sortedBy = potentialTargets.sortedBy { it.distance }
    // Fallback solution, if no zone with sufficient commuter distance is available, use the largest remaining zone instead
    sortedBy.firstOrNull { it.distance >= distance } ?: sortedBy.last()
}

val GREEDY_BY_DISTANCE_PROPORTIONAL = FindProperWorkspace { targets, distance ->

    val potentialTargets = if (targets.any { it.isUndersaturated() }) {
        targets.filter { it.isUndersaturated() }
        // Fallback solution, if all destinations are saturated, use the original set again
    } else {
        targets
    }

    val sortedBy = potentialTargets.sortedBy { it.distance }
    // Fallback solution, if no zone with sufficient commuter distance is available, use the largest remaining zone instead
    sortedBy.filter { it.distance >= distance }.maxBy { it.communityCommutersDesired }
}

val WarnSaturation: WarnOnce = WarnOnce()
class WarnOnce {
    private var triggered = false
    fun print(message: () -> String) {
        if (triggered) return
        println(message())
        triggered = true
    }
}
