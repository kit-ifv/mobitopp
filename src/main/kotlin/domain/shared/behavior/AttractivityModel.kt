package domain.shared.behavior

import domain.shared.enums.ActivityType
import domain.shared.location.zone.MaximalZone
import domain.shared.location.zone.ZoneId
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.DefaultMapCsvParser
import utils.csv.Row
import utils.csv.commaDouble
import utils.csv.long
import java.nio.file.Path
import kotlin.math.abs

// TODO Debate with Jelle, There is a more generalized version of attractiveness, which takes in a location, rather than
//  a zoneID
interface AttractivenessModel {
    fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Attractiveness
    fun isAttractive(zone: ZoneId, activityType: ActivityType): Boolean =
        attractivenessFor(zone, activityType).value > .0

    // TODO Extract work/private visit, as they are only required to compute parkdruck/parkingpressure
    // Create new interface ParkingPressureModel and an implementation that wraps attractiveness model and knows about work/private visit
    val work: ActivityType
    val privateVisit: ActivityType
}

fun AttractivenessModel.sumAttractiveness(zone: ZoneId, vararg activityTypes: ActivityType): Double =
    activityTypes.sumOf {
        attractivenessFor(zone, it).value
    }

@Suppress("MagicNumber")
fun AttractivenessModel.parkingPressure(target: MaximalZone): Double {
    val attractiveness = sumAttractiveness(target.id, work, privateVisit)
    if (target.parkingPlaces == 0) {
        return if (abs(attractiveness) < 1e-6) 0.0 else 999.0
    }
    return attractiveness / target.parkingPlaces
}

@Deprecated(
    "This class needs to be reworked: switch to fastCSV or Jackson parsing and log warnings to report instead of console",
)
class AttractivenessFromCsv(
    private val path: Path,
    delimiter: String = ";",
    zoneColumn: String = "zoneId",
    override val work: ActivityType,
    override val privateVisit: ActivityType,
    private val activityTypes: Set<ActivityType>,
) : AttractivenessModel {

    private val attractivenessMap: Map<ZoneId, Map<ActivityType, Attractiveness>>

    init {
        var filteredActivityTypes: Set<ActivityType>? = null

        val parser = DefaultMapCsvParser(
            CsvParser(errorHandling = ErrorHandling.THROW) { row ->
                // TODO error level as config param
                filteredActivityTypes = filteredActivityTypes ?: activityTypes.filterExistingColumns(row)
                ZoneId(row.long(zoneColumn)) to
                    activityMapOf(row, filteredActivityTypes)
            },
        )

        attractivenessMap = parser.parseMap(path, separator = delimiter)
    }

    private val warned: MutableMap<ZoneId, MutableList<ActivityType>> = mutableMapOf()
    private val warnedSet = mutableSetOf<ActivityType>()

    override fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Attractiveness =
        attractivenessMap[zone]?.let {
            it[activityType]
        } ?: Attractiveness.DEFAULT.also {
            val activities = warned.getOrPut(zone) { mutableListOf() }
            if (activityType !in activities && activityType !in warnedSet) {
                println(
                    "Warning: could not find attractiveness for ZoneId $zone and activity $activityType in lookup " +
                        "(Source $path)! Using 1.0 instead!",
                )
                activities.add(activityType)
                warnedSet.add(activityType)
            }
        }
}

private fun activityMapOf(row: Row, activityTypes: Set<ActivityType>) = activityTypes.associateWith { act ->
    row.commaDouble(act.columnString).asAttractiveness()
}

fun String.capitalizeWithUnderscores() = this.split("_").joinToString("_") { part ->
    part.lowercase().replaceFirstChar { it.uppercase() }
}

fun Set<ActivityType>.filterExistingColumns(row: Row) = this.filter { act ->
    row.hasColumn(act.columnString)
}.toSet()

private val ActivityType.columnString: String get() = "Attractivity:${description.capitalizeWithUnderscores()}"
