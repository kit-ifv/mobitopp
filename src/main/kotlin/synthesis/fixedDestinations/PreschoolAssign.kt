package synthesis.fixedDestinations

import datastructure.ReadOnlyKDTree
import datastructure.WithMetric
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.location.DistanceMetric
import domain.location.LOCATIONUNKNOWN
import domain.location.Location
import domain.roadnetwork.toUTM
import modeling.discreteChoice.AllocatedLogit
import modeling.discreteChoice.ChoiceSituation
import modeling.discreteChoice.DiscreteChoiceModel
import synthesis.CommuteDistance
import synthesis.domain.SynthesisPerson
import units.Distance
import units.DistanceUnit
import units.kilometers
import units.toDistance
import usecases.AttractivenessModel
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.math.ln
import kotlin.math.pow

fun interface LocationFinder<T : Any> {

    fun find(person: SynthesisPerson<out T>, activityType: ActivityType): Location
}
fun interface GroupLocationFinder<T> {
    fun find(persons: Collection<SynthesisPerson<out T>>, activityType: ActivityType): Collection<Pair<SynthesisPerson<out T>, Location>>
}

class TrivialGroupLocationFinder<T: Any>(val original: LocationFinder<T>): GroupLocationFinder<T> {
    override fun find(
        persons: Collection<SynthesisPerson<out T>>,
        activityType: ActivityType
    ): Collection<Pair<SynthesisPerson<out T>, Location>> {
        return persons.map { it to original.find(it, activityType) }
    }
}
class UseClosestLocation(potentialLocations: List<Location>) : LocationFinder<Any>, GroupLocationFinder<Any> {
    private val locationTree =
        ReadOnlyKDTree(potentialLocations, { it.coordinate.toUTM().e }, { it.coordinate.toUTM().n })

    override fun find(
        person: SynthesisPerson<out Any>,
        activityType: ActivityType
    ): Location {
        return locationTree.nearestNeighbor(person.homeLocation) {
            doubleArrayOf(
                it.coordinate.toUTM().e,
                it.coordinate.toUTM().n
            )
        }
    }

    override fun find(
        persons: Collection<SynthesisPerson<out Any>>,
        activityType: ActivityType
    ): Collection<Pair<SynthesisPerson<out Any>, Location>> {
        return  persons.map { it to find(it, activityType) }
    }

}

data class BandwidthParameters(
    val poleDistance: Distance = 10.kilometers, // TODO this is an attribute of the person that needs to be extracted from the panel data
    val poleRadius: Distance = 4.kilometers,
    val bDistance: Double = 0.5,
    val aDistance: Double = 5.0 // TODO sehr wahrscheinlich kilometer statt meter. Rausfinden
)


class LocationKDTree(locations: List<Location>) {
    private val tree = ReadOnlyKDTree(locations, { it.coordinate.toUTM().e }, { it.coordinate.toUTM().n })

    fun sequenceFor(location: Location): Sequence<WithMetric<Location, Distance>> {
        return tree.findUntil(
            location,
            { doubleArrayOf(it.coordinate.toUTM().e, it.coordinate.toUTM().n) },
            { it.toDistance(DistanceUnit.METERS) })
    }
}

data class LocationSituation(
    override val choice: Location,
    val distance: Distance,
    val activityType: ActivityType
) : ChoiceSituation<Location>()

class UseBandwidthLocation(
    private val potentialLocations: List<Location>, val attractivenessModel: AttractivenessModel,
    val parameters: BandwidthParameters = BandwidthParameters()
) : LocationFinder<Any>, GroupLocationFinder<Any> {
    private val locationTree = LocationKDTree(potentialLocations)
    private val model =
        DiscreteChoiceModel<Location, LocationSituation, BandwidthParameters>(
            AllocatedLogit.create(potentialLocations) {
                ruleForAll {
                    val zoneId = it.choice.zone?.id

                    val attractiveness =
                        zoneId?.let { zId -> attractivenessModel.attractivenessFor(zId, it.activityType) }
                            ?: 0.000001.also { System.err.println("Cannot find attractiveness for location") }

                    ln(attractiveness) / (bDistance * it.distance.toDouble(DistanceUnit.KILOMETERS).pow(aDistance))

                }
            }
        )

    override fun find(
        person: SynthesisPerson<out Any>,
        activityType: ActivityType
    ): Location {
        var validTargets =
            locationTree.sequenceFor(
                person.homeLocation,
            )
                .dropWhile { it.item.distance(person.homeLocation) <= parameters.poleDistance - parameters.poleRadius }
                .takeWhile { it.item.distance(person.homeLocation) <= parameters.poleDistance + parameters.poleRadius }
                .toList()
        if (validTargets.isEmpty()) {
            validTargets = potentialLocations.sortedBy { it.distance(person.homeLocation) }
                .map { WithMetric(it, it.distance(person.homeLocation)) }
        }
        val converted = validTargets.map { LocationSituation(it.item, it.metric, activityType) }.toSet()
        return model.select(converted, parameters)

    }

    override fun find(
        persons: Collection<SynthesisPerson<out Any>>,
        activityType: ActivityType
    ): Collection<Pair<SynthesisPerson<out Any>, Location>> {
        return persons.map { it to find(it, activityType) }
    }
}

class CommuterMatrix(
    val translator: BiMap<ZoneId, CommunityNumber>,
    commuterInfo: List<CommuterInfo>,
    val zoneMapping: Map<ZoneId, Zone>,
) {
    val workZones = zoneMapping.values.filter{it.id in translator.forwardMap.keys}
    private val commuterTargets: Map<CommunityNumber, Map<CommunityNumber, Double>> =
        commuterInfo.groupBy { it.origin }
            .mapValues { targets ->
                targets.value.filter { it.amount > 0 }.associate { it.destination to it.amount.toDouble() }
            }
    val requests: Map<CommunityNumber, Double> = commuterTargets.entries.associate { it.key to it.value.values.sum() }
    val targetSize = commuterTargets.values.sumOf { it.values.sum() }
    fun commuterTargets(origin: Zone, destination: Zone): Double {
        val originCommunity = translator.forwardMap[origin.id]?: throw NoSuchElementException("Did not find ${origin.id} in commuting relations")
        val destinationCommunity = translator.forwardMap[destination.id]?: throw NoSuchElementException("Did not find ${destination.id} in commuting relations")
        val originCommuters = commuterTargets[originCommunity]?: emptyMap()

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
    }
}

class CommuterAssignee<T : Any>(
    private val commuterMatrix: CommuterMatrix,
    val locateFromCommuterMatrix: LocationFromCommuterMatrix<T>
) : LocationFinder<T> {
    override fun find(person: SynthesisPerson<out T>, activityType: ActivityType): Location {
        return locateFromCommuterMatrix.locate(commuterMatrix, person, activityType)


    }
}

class MetricCommAssign(
    private val commuterMatrix: CommuterMatrix,
    val metric: DistanceMetric,
    val attractivenessModel: AttractivenessModel,
    val findProperWorkspace: FindProperWorkspace,
    val zoneLocationAssigner: DetermineLocationInZone
) : GroupLocationFinder<CommuteDistance> {

    private val legacyActivityType = LegacyActivityType.WORK
    val helper: Map<Zone, Collection<WorkDistances>> = unrollCommutermatrix()
    val assignedPeeps: MutableMap<Pair<CommunityNumber, CommunityNumber>, Int> = mutableMapOf()
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
                    originCommunity = commuterMatrix.getCommunityId(origin) ,
                    destinationCommunity = commuterMatrix.getCommunityId(destination) ,
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
        persons: Collection<SynthesisPerson<out CommuteDistance>>,
        activityType: ActivityType
    ): Collection<Pair<SynthesisPerson<out CommuteDistance>, Location>> {
        val targets = persons.groupBy { commuterMatrix.getCommunityId(it.homeLocation.requireZone()) }
        val canIMeetTheRequirements = targets.entries.associate { it.key to ((it.value.size to commuterMatrix.requests[it.key]) ?: 0.0) }
        return persons.map { it to find(it, activityType) }
    }

}

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

    val potentialTargets = if (targets.any { it.isUndersaturated()}) {
        targets.filter { it.isUndersaturated() }
        // Fallback solution, if all destinations are saturated, use the original set again
    } else {
        targets
    }

    val sortedBy = potentialTargets.sortedBy { it.distance }
    //Fallback solution, if no zone with sufficient commuter distance is available, use the largest remaining zone instead
    sortedBy.firstOrNull { it.distance >= distance } ?:sortedBy.last()
}

val GREEDY_BY_DISTANCE_PROPORTIONAL = FindProperWorkspace { targets, distance ->

    val potentialTargets = if (targets.any { it.isUndersaturated()}) {
        targets.filter { it.isUndersaturated() }
        // Fallback solution, if all destinations are saturated, use the original set again
    } else {
        targets
    }

    val sortedBy = potentialTargets.sortedBy { it.distance }
    //Fallback solution, if no zone with sufficient commuter distance is available, use the largest remaining zone instead
    sortedBy.filter { it.distance >= distance }.maxBy { it.communityCommutersDesired }
}

val WarnSaturation: WarnOnce = WarnOnce()
class WarnOnce {
    private var triggered = false
    fun print(message: () -> String) {
        if(triggered) return
        println(message())
        triggered = true
    }
}
fun interface LocationFromCommuterMatrix<T : Any> : LocationFinder<T> {

    override fun find(person: SynthesisPerson<out T>, activityType: ActivityType): Location {
        return LOCATIONUNKNOWN
    }

    fun locate(commuterMatrix: CommuterMatrix, person: SynthesisPerson<out T>, activityType: ActivityType): Location
}



private fun Location.distance(other: Location) = coordinate.distance(other.coordinate)

