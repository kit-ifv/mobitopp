package usecases

import domain.data.Zone
import domain.data.ZoneId
import domain.enums.ActivityType
import usecases.choicemodels.destinationchoice.parameters.ChoiceModelPurposes
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.DefaultMapCsvParser
import utils.csv.Row
import utils.csv.commaDouble
import utils.csv.long
import java.io.File
import kotlin.math.abs

// Can be "fun" when implementing only one function
interface AttractivenessModel {
    fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Double
    val purposes: ChoiceModelPurposes
}

fun AttractivenessModel.sumAttractiveness(zone: ZoneId, vararg activityTypes: ActivityType): Double =
    activityTypes.sumOf { attractivenessFor(zone, it) }

@Suppress("MagicNumber")
fun AttractivenessModel.parkingPressure(target: Zone): Double {
    val attractiveness = sumAttractiveness(target.id, purposes.work, purposes.privateVisit)
    if (target.parkingPlaces == 0) {
        return if (abs(attractiveness) < 1e-6) 0.0 else 999.0
    }
    return attractiveness / target.parkingPlaces
}

class AttractivenessFromCsv(
    private val file: File,
    delimiter: String = ";",
    zoneColumn: String = "zoneId",
    override val purposes: ChoiceModelPurposes,
) : AttractivenessModel {

    private val activityTypes: Set<ActivityType> = purposes.typesWithAttractivity

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

    private val warned: MutableMap<ZoneId, MutableList<ActivityType>> = mutableMapOf()

    override fun attractivenessFor(zone: ZoneId, activityType: ActivityType): Double =
        attractivenessMap[zone]?.let { it[activityType] } ?: 1.0.also {
            val activities = warned.getOrPut(zone) { mutableListOf() }
            if (activityType !in activities) {
                println(
                    "Warning: could not find attractiveness for ZoneId $zone and activity $activityType in lookup " +
                        "(Source $file)! Using 1.0 instead!"
                )
                activities.add(activityType)
            }
        }
}

private fun activityMapOf(row: Row, activityTypes: Set<ActivityType>) =
    activityTypes.associateWith { act ->
        row.commaDouble("Attractivity:${act.description.capitalizeWithUnderscores()}")
    }

fun String.capitalizeWithUnderscores() =
    this.split("_").joinToString("_") { part ->
        part.lowercase().replaceFirstChar { it.uppercase() }
    }
