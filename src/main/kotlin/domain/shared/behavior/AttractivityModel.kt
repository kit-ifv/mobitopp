package domain.shared.behavior

import domain.shared.enums.ActivityType
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.synthesis.results.Attractiveness
import domain.synthesis.results.asAttractiveness
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

    val purposes: ChoiceModelPurposes
}

fun AttractivenessModel.sumAttractiveness(zone: ZoneId, vararg activityTypes: ActivityType): Double =
    activityTypes.sumOf { attractivenessFor(zone, it).value }

@Suppress("MagicNumber")
fun AttractivenessModel.parkingPressure(target: Zone): Double {
    val attractiveness = sumAttractiveness(target.id, purposes.work, purposes.privateVisit)
    if (target.parkingPlaces == 0) {
        return if (abs(attractiveness) < 1e-6) 0.0 else 999.0
    }
    return attractiveness / target.parkingPlaces
}

class AttractivenessFromCsv(
    private val path: Path,
    delimiter: String = ";",
    zoneColumn: String = "zoneId",
    override val purposes: ChoiceModelPurposes,
) : AttractivenessModel {

    private val activityTypes: Set<ActivityType> = purposes.typesWithAttractivity

    private val attractivenessMap: Map<ZoneId, Map<ActivityType, Attractiveness>>

    init {

        val parser = DefaultMapCsvParser(
            CsvParser(errorHandling = ErrorHandling.THROW) { row -> // TODO error level as config param
                ZoneId(row.long(zoneColumn)) to
                        activityMapOf(row, activityTypes)
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
        row.commaDouble("Attractivity:${act.description.capitalizeWithUnderscores()}").asAttractiveness()
    }

fun String.capitalizeWithUnderscores() =
    this.split("_").joinToString("_") { part ->
        part.lowercase().replaceFirstChar { it.uppercase() }
    }
