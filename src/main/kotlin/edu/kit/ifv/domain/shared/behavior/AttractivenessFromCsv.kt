package edu.kit.ifv.domain.shared.behavior

import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.utils.ErrorHandling
import edu.kit.ifv.utils.csv.CsvParser
import edu.kit.ifv.utils.csv.DefaultMapCsvParser
import edu.kit.ifv.utils.csv.Row
import edu.kit.ifv.utils.csv.commaDouble
import edu.kit.ifv.utils.csv.long
import java.nio.file.Path

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
            CsvParser.Companion(errorHandling = ErrorHandling.THROW) { row ->
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

@Deprecated("This function should either be in util or not used by loadattractiveness step")
internal fun String.capitalizeWithUnderscores() = this.split("_").joinToString("_") { part ->
    part.lowercase().replaceFirstChar { it.uppercase() }
}

private fun Set<ActivityType>.filterExistingColumns(row: Row) = this.filter { act ->
    row.hasColumn(act.columnString)
}.toSet()

private val ActivityType.columnString: String get() = "Attractivity:${description.capitalizeWithUnderscores()}"
