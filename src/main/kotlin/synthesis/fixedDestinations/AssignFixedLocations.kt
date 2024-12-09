package synthesis.fixedDestinations

import domain.data.ZoneId
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.location.DistanceMetric
import domain.location.Location
import synthesis.ActivitySchedule
import synthesis.SurveyHousehold
import synthesis.SurveyPerson
import synthesis.SynZone
import synthesis.fixedDestinations.BiMap.Companion.toBiMap
import usecases.AttractivenessModel
import utils.csv.DefaultCsvParser
import java.io.File
import java.nio.file.Path

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
        potentialLocations.map {distanceMetric.evaluate(person.homeLocation, it)}
    }

}

data class CommuterInfo(val origin: CommunityNumber, val destination: CommunityNumber, val amount: Int)

fun readCommuters(file: Path): Sequence<CommuterInfo> {
    val parser = DefaultCsvParser { row ->
        CommuterInfo(
            row("origin") { CommunityNumber.parse(it) },
            row("destination") { CommunityNumber.parse(it) },
            row("commuters").toInt(),

            )
    }

    return parser.parse(file.toFile())
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

fun readZoneToCommunity(file: Path): BiMap<ZoneId, CommunityNumber> {
    val parser = DefaultCsvParser { row ->
        Pair(
            row("partId") { ZoneNumber.parse(it).toZoneId() },
            row("regionId") { CommunityNumber.parse(it) },

            )
    }

    return parser.parse(file.toFile()).toMap().toBiMap()
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

private val attractivenessTypes = setOf(
    LegacyActivityType.WORK,
    LegacyActivityType.EDUCATION_PRIMARY,
    LegacyActivityType.EDUCATION_SECONDARY,
    LegacyActivityType.EDUCATION_TERTIARY,
    // TODO Sightseeing?
)

fun interface GenerateLocations {
    fun generateLocations(
        zone: SynZone,
        activityType: ActivityType,
        attractivenessModel: AttractivenessModel
    ): Collection<Location>
}

data class FakeZone(
    val id: ZoneId,
    val centroid: Location
)

/**
 * Generate a location for a given activity type at the center of a zone, if and only if the attractiveness is nonzero
 */
object TrivialLocation : GenerateLocations {
    override fun generateLocations(
        zone: SynZone,
        activityType: ActivityType,
        attractivenessModel: AttractivenessModel
    ): Collection<Location> {
        return if (attractivenessModel.attractivenessFor(zone.id, activityType) > 0.0)
            listOf(zone.centroid) else emptyList()
    }

}
