package usecases

import domain.data.Zone
import domain.data.ZoneId
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.DefaultMapCsvParser
import utils.csv.Row
import utils.csv.commaDouble
import utils.csv.long
import java.io.File
import kotlin.math.abs

// Can be "fun" when implementing only one function
// TODO Debate with Jelle, There is a more generalized version of attractiveness, which takes in a location, rather than
//  a zoneID
fun interface AttractivenessModel {
    fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Double
}

fun AttractivenessModel.sumAttractiveness(zone: ZoneId, vararg activityTypes: ActivityType): Double =
    activityTypes.sumOf { attractivenessFor(zone, it) }

@Suppress("MagicNumber")
fun AttractivenessModel.parkingPressure(target: Zone): Double {
    val attractiveness = sumAttractiveness(target.id, LegacyActivityType.WORK, LegacyActivityType.PRIVATE_VISIT)
    if (target.parkingPlaces == 0) {
        return if (abs(attractiveness) < 1e-6) 0.0 else 999.0
    }
    return attractiveness / target.parkingPlaces
}

class AttractivenessFromCsv(
    private val file: File,
    delimiter: String = ";",
    zoneColumn: String = "zoneId",
    activityTypes: Set<ActivityType>,
) : AttractivenessModel {

    private val attractivenessMap: Map<ZoneId, Map<ActivityType, Double>>

    init {

        val parser = DefaultMapCsvParser(
            CsvParser(errorHandling = ErrorHandling.THROW) { row -> // TODO error level as config param
                ZoneId(row.long(zoneColumn)) to
                    activityMapOf(row, activityTypes)
            }
        )

        attractivenessMap = parser.parseMap(file, separator = delimiter)
    }

    override fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Double =
        attractivenessMap[zone]?.let { it[activityType] } ?: 1.0.also {
            println(
                "Warning: could not find attractiveness for ZoneId $zone and activity $activityType in lookup " +
                    "(Source $file)! Using 1.0 instead!"
            )
        }
}

private fun activityMapOf(row: Row, activityTypes: Set<ActivityType>) =
    activityTypes.associateWith { act ->
        row.commaDouble("Attractivity:${act.description.capitalizeWithUnderscores()}")
    }

private fun String.capitalizeWithUnderscores() =
    this.split("_").joinToString("_") { part ->
        part.lowercase().replaceFirstChar { it.uppercase() }
    }
