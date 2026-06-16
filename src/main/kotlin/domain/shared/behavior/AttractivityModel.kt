package domain.shared.behavior

import domain.shared.enums.ActivityType
import domain.shared.location.ZoneId
import utils.ErrorHandling
import utils.csv.*
import java.nio.file.Path

// TODO Debate with Jelle, There is a more generalized version of attractiveness, which takes in a location, rather than
//  a zoneID
interface AttractivenessModel {
    fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Attractiveness
    fun isAttractive(zone: ZoneId, activityType: ActivityType): Boolean =
        attractivenessFor(zone, activityType).value > .0
}

fun AttractivenessModel.sumAttractiveness(zone: ZoneId, vararg activityTypes: ActivityType): Double =
    activityTypes.sumOf { attractivenessFor(zone, it).value }

@Deprecated("This class needs to be reworked: switch to fastCSV or Jackson parsing and log warnings to report instead of console")
class AttractivenessFromCsv(
    private val path: Path,
    delimiter: String = ";",
    zoneColumn: String = "zoneId",
    private val activityTypes: Set<ActivityType>,
    errorHandling: ErrorHandling = ErrorHandling.THROW,
) : AttractivenessModel {

    private val attractivenessMap: Map<ZoneId, Map<ActivityType, Attractiveness>>

    init {
        var filteredActivityTypes: Set<ActivityType>? = null

        val parser = DefaultMapCsvParser(
            CsvParser(errorHandling = errorHandling) { row ->
                filteredActivityTypes = filteredActivityTypes ?: activityTypes.filterExistingColumns(row)
                ZoneId(row.long(zoneColumn)) to
                    activityMapOf(row, filteredActivityTypes)
            }
        )

        attractivenessMap = parser.parseMap(path, separator = delimiter)
    }

    private val warned: MutableMap<ZoneId, MutableList<ActivityType>> = mutableMapOf()
    private val warnedSet = mutableSetOf<ActivityType>()

    override fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Attractiveness =
        attractivenessMap[zone]?.let { it[activityType] } ?: Attractiveness.DEFAULT.also {
            val activities = warned.getOrPut(zone) { mutableListOf() }
            if (activityType !in activities && activityType !in warnedSet) {
                println(
                    "Warning: could not find attractiveness for ZoneId $zone and activity $activityType in lookup " +
                        "(Source $path)! Using 1.0 instead!"
                )
                activities.add(activityType)
                warnedSet.add(activityType)
            }
        }
}

private fun activityMapOf(row: Row, activityTypes: Set<ActivityType>) =
    activityTypes.associateWith { act ->
        row.commaDouble(act.columnString).asAttractiveness()
    }

fun String.capitalizeWithUnderscores() =
    this.split("_").joinToString("_") { part ->
        part.lowercase().replaceFirstChar { it.uppercase() }
    }

fun Set<ActivityType>.filterExistingColumns(row: Row) = this.filter { act ->
    row.hasColumn(act.columnString)
}.toSet()

private val ActivityType.columnString: String get() = "Attractivity:${description.capitalizeWithUnderscores()}"
