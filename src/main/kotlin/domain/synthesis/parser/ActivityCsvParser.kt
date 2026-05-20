package domain.synthesis.parser

import com.fasterxml.jackson.annotation.JsonIdentityReference
import domain.shared.enums.ActivityType
import domain.synthesis.data.ActivityBinaryRecord
import domain.synthesis.data.ActivityId
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.MutablePlannedActivity
import domain.synthesis.data.PersonId
import utils.CodePlan
import utils.ErrorHandling
import utils.Identifiable
import utils.csv.CsvParser
import utils.csv.decode
import utils.csv.int
import utils.csv.long
import utils.csv.withFilter
import utils.random.StochasticActor
import utils.units.AbsoluteTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class ActivitiesColumns(
    val personColumn: String = "personId",
    val activityTypeColumn: String = "activityType",
    val tripDurationColumn: String = "observedTripDuration",
    val startColumn: String = "startTime",
    val durationColumn: String = "duration",
)

data class ActivityCsvConfig<P>(
//    var path: Path,
//    var delimiter: String = SEMICOLON,
    var columns: ActivitiesColumns = ActivitiesColumns(),
    var personExists: (PersonId) -> Boolean,
    var personProvider: (PersonId) -> P,
    var durationUnit: DurationUnit,
    var activityTypes: CodePlan<ActivityType>,
//    var filter: ActivitiesColumns.(Row, LoadPlannedActivitiesContext) -> Boolean = { _, _ -> true },
    var shiftActivityStart: ActivityStartShifter = QuarterHourShifter.cached(),
    var errorHandling: ErrorHandling = ErrorHandling.WARNING,
    val seed: Long
) where P : Identifiable<PersonId>, P : StochasticActor

fun <P> createActivityCsvParser(
    csvConfig: ActivityCsvConfig<P>,
//    errorHandling: ErrorHandling,
//    columns: ActivitiesColumns,
// //    shiftActivityStart: ActivityStartShifter,
//    durationUnit: DurationUnit,
// //    personProvider: (PersonId) -> MutablePerson,
//    seed: Long,
//    activityTypes: CodePlan<ActivityType>
): CsvParser<MutablePlannedActivity> where P : Identifiable<PersonId>, P : StochasticActor = csvConfig.run {
    CsvParser<MutablePlannedActivity>(errorHandling) { row ->
        val person = personProvider(PersonId(row.long(columns.personColumn)))

        MutablePlannedActivity(
            id = ActivityId(row.index.toLong()),
            person = person.id,
            seed = seed
        ) {
            val shift = shiftActivityStart(
                this
            ) // TODO this is broken! cache should use person instead of activity for reuse

            observedTripDuration = row.int(columns.tripDurationColumn).toDuration(durationUnit)
            startTime = AbsoluteTime.START + row.int(columns.startColumn).toDuration(durationUnit) + shift
            duration = row.int(columns.durationColumn).toDuration(durationUnit)
            activityType = row.decode(columns.activityTypeColumn, activityTypes)
        }
    }.withFilter { row ->
        personExists(PersonId(row.long(columns.personColumn)))
    }
}

fun activityBinaryCsvParser(
    columns: ActivitiesColumns = ActivitiesColumns(),
    errorHandling: ErrorHandling = ErrorHandling.WARNING
) =
    CsvParser(errorHandling) { row ->
        ActivityBinaryRecord(
            row.index.toLong(),
            row.long(columns.personColumn),
            row.int(columns.tripDurationColumn),
            row.long(columns.startColumn),
            duration = row.int(columns.durationColumn),
            activityCode = row.int(columns.activityTypeColumn),

        )
    }
