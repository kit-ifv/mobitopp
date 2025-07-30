package domain.synthesis.parser

import domain.shared.config.SynthesisContext
import domain.synthesis.data.ActivityId
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.MutablePlannedActivity
import domain.synthesis.data.PersonId
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.DefaultCsvParser
import utils.csv.decode
import utils.csv.id
import utils.csv.int
import utils.units.AbsoluteTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun SynthesisContext.activityCsvParser(
    errorHandling: ErrorHandling,
    columns: ActivitiesColumns,
    shiftActivityStart: ActivityStartShifter,
    durationUnit: DurationUnit,
    personProvider: (PersonId) -> MutablePerson,
): DefaultCsvParser<MutablePlannedActivity> = CsvParser<MutablePlannedActivity>(errorHandling) { row ->

    val person = personProvider(PersonId(row.invoke(columns.personColumn).toLong()))

    MutablePlannedActivity(
        id = ActivityId(row.index.toLong()),
        person = person,
        seed = simulationSeed
    ) {
        val shift = shiftActivityStart(this)

        observedTripDuration = row.int(columns.tripDurationColumn).toDuration(durationUnit)
        startTime = AbsoluteTime.Companion.START + row.int(columns.startColumn).toDuration(durationUnit) + shift
        duration = row.int(columns.durationColumn).toDuration(durationUnit)
        activityType = row.decode(columns.activityTypeColumn, activityTypes)
    }
}

data class ActivitiesColumns(
    val personColumn: String = "personId",
    val activityTypeColumn: String = "activityType",
    val tripDurationColumn: String = "observedTripDuration",
    val startColumn: String = "startTime",
    val durationColumn: String = "duration",
)
