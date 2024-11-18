package synthesis

import domain.data.Sex
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.location.DistanceMetric
import domain.location.LOCATIONUNKNOWN
import domain.location.Location
import synthesis.BiMap.Companion.toBiMap
import units.Coordinate
import usecases.AttractivenessFromCsv
import usecases.AttractivenessModel
import utils.csv.DefaultCsvParser
import java.io.File
import kotlin.io.path.Path

data class PersonWithSchedule(
    private val person: SurveyPerson,
    private val household: SurveyHousehold,
    val homeLocation: Location,
    private val activitySchedule: ActivitySchedule
)


fun interface AssignFixedLocations {

    fun assign(person: PersonWithSchedule, distanceMetric: DistanceMetric, potentialLocations: Collection<Location>)
}


class TrivialLocations : AssignFixedLocations {
    override fun assign(
        person: PersonWithSchedule,
        distanceMetric: DistanceMetric,
        potentialLocations: Collection<Location>
    ) {
        TODO("Not yet implemented")
    }

}

data class CommuterInfo(val origin: CommunityNumber, val destination: CommunityNumber, val amount: Int)

fun readCommuters(file: File): Sequence<CommuterInfo> {
    val parser = DefaultCsvParser { row ->
        CommuterInfo(
            row("origin") { CommunityNumber.parse(it) },
            row("destination") { CommunityNumber.parse(it) },
            row("commuters").toInt(),

            )
    }

    return parser.parse(file)
}

@JvmInline
value class ZoneNumber(private val int: Int) {

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

fun readZoneToCommunity(file: File): BiMap<CommunityNumber, ZoneNumber> {
    val parser = DefaultCsvParser { row ->
        Pair(
            row("regionId") { CommunityNumber.parse(it) },
            row("partId") { ZoneNumber.parse(it) },

            )
    }

    return parser.parse(file).toMap().toBiMap()
}


class BiMap<K, V>(
    val forwardMap: MutableMap<K, V> = mutableMapOf(),
    val backwardMap: MutableMap<V, K> = mutableMapOf()
) {

    operator fun contains(element: K): Boolean {
        return element in forwardMap
    }

    fun containsValue(element: V): Boolean {
        return element in backwardMap
    }

    companion object {
        fun <K, V> Map<K, V>.toBiMap(): BiMap<K, V> {
            val backwardMap = this.entries.associate { (k, v) -> v to k }.toMutableMap()
            return BiMap(this.toMutableMap(), backwardMap)
        }
    }
}

private val attractivenessTypes = setOf(
    LegacyActivityType.WORK,
    LegacyActivityType.EDUCATION_PRIMARY,
    LegacyActivityType.EDUCATION_SECONDARY,
    LegacyActivityType.EDUCATION_TERTIARY,
    // TODO Sightseeing?
)

fun interface GenerateLocations {
    fun generateLocations(
        zone: FakeZone,
        activityType: ActivityType,
        attractivenessModel: AttractivenessModel
    ): Collection<Location>
}

data class FakeZone(
    val id: ZoneId,
    val centroid: Location
)

class TrivialLocation : GenerateLocations {
    override fun generateLocations(
        zone: FakeZone,
        activityType: ActivityType,
        attractivenessModel: AttractivenessModel
    ): Collection<Location> {
        return if (attractivenessModel.attractivenessFor(zone.id, activityType) > 0.0)
            listOf(zone.centroid) else emptyList()
    }

}

fun Collection<SynZone>.toLocatableZones(): List<FakeZone> = map {FakeZone(it.id.toZoneId(), LOCATIONUNKNOWN) }

fun interface FilterValidLocations {
    fun findValidTargets(person: PersonWithSchedule)
}

fun main() {
    val results = readCommuters(Path("src/test/resources/synthesis/commuters-rastatt.csv").toFile()).toList()
    val translator = readZoneToCommunity(Path("src/test/resources/synthesis/zone-to-community.csv").toFile())
    val targets = ZoneTarget.fromFile(Path("src/test/resources/synthesis/ZoneTargets.csv").toFile()).toList()
    val attractiveness: AttractivenessModel =
        AttractivenessFromCsv(
            file = Path("src/test/resources/synthesis/attractivities.csv").toFile(), activityTypes =
            attractivenessTypes
        )


    val targetKeys = targets.map { it.zoneId }
    val goodRegions = translator.backwardMap.filter { it.key in targetKeys }.values
    println(targets.sumOf { it.numberOfPeople() })
    val generationTargets = targets.map { it.zoneId }
    println(translator)
    println(results)
    println(results.map { it.origin }.associateWith { it in translator }.filter { !it.value })
    println(results.sumOf { it.amount })


}

class StepAssign() {

    fun ActivityType.assign(
        personWithSchedule: PersonWithSchedule,
        distanceMetric: DistanceMetric,
        potentialLocations: Collection<Location>
    ) {
        potentialLocations.map { distanceMetric.evaluate(it, personWithSchedule.homeLocation) }
    }
}

