package usecases.steps

import domain.data.ActivityId
import domain.data.Person
import domain.data.PersonId
import domain.data.PlannedActivity
import domain.enums.ActivityType
import modeling.steps.Context
import modeling.steps.LoadCsvStep
import modeling.steps.ModelExecution
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.SealStep
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.withFilter
import java.io.File
import kotlin.time.DurationUnit

interface LoadPlannedActivitiesContext : Context {
    val plannedActivityRepository: MutableRepository<PlannedActivity, ActivityId>
    val personRepository: Repository<Person, PersonId>
    val activityTypeCodes: CodePlan<ActivityType>

    val defaultActivityFile: File
        get() = File(demandFolder.path + "\\demand-data\\activity.csv")
}

data class ActivitiesColumns(
    val personColumn: String = "personId",
    val activityTypeColumn: String = "activityType",
    val tripDurationColumn: String = "observedTripDuration",
    val startColumn: String = "startTime",
    val durationColumn: String = "duration",
)

@Suppress("LongParameterList", "UnusedParameter")
fun <S, C> S.prepareActivities(
    file: File = context.defaultActivityFile,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    activitiesColumns: ActivitiesColumns = ActivitiesColumns(),
    activityTypeCodes: CodePlan<ActivityType>? = null,
    durationUnit: DurationUnit? = null,
    filter: ActivitiesColumns.(Row, C) -> Boolean = { _, _ -> true }
) where S : ModelExecution<C>, C : LoadPlannedActivitiesContext {
//    val timeUnit = durationUnit ?: context.timeUnit
//    val personRepo = { context.personRepository }
//    val activityTpeCodePlan = activityTypeCodes ?: context.activityTypeCodes

    val parser = CsvParser<PlannedActivity>(errorHandling) { row ->
        null
//        PlannedActivity().apply { // TODO
//            id = ActivityId(row.index.toLong())
//            person = getPerson(personRepo, row, activitiesColumns)
//            observedTripDuration = row.int(activitiesColumns.tripDurationColumn).toDuration(timeUnit)
//            startTime = AbsoluteTime.START + row.int(activitiesColumns.startColumn).toDuration(timeUnit)
//            duration = row.int(activitiesColumns.durationColumn).toDuration(timeUnit)
//            activityType = row.decode(activitiesColumns.activityTypeColumn, activityTpeCodePlan)
//            // TODO someone should check that this is useful
//            random = Random(row.index.toLong())
//        }
    }

    this.prepareActivitiesFile(parser.withFilter { activitiesColumns.filter(it, context) }, file, delimiter)
}

fun <S, C> S.prepareActivitiesFile(
    parser: CsvParser<PlannedActivity>,
    file: File = context.defaultActivityFile,
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : LoadPlannedActivitiesContext {
    this.addStep(
        LoadCsvStep<PlannedActivity, ActivityId>(
            file = file,
            name = "Load planned activities from csv",
            parser = parser,
            delimiter = delimiter,
            repository = context.plannedActivityRepository,
            dependentRepositories = setOf(context.personRepository),
            validationMock = listOf() // TODO
        )
    )
}

fun <S, C> S.finishActivities() where S : ModelExecution<C>, C : LoadPlannedActivitiesContext {
    this.addStep(SealStep(context.plannedActivityRepository))
}

fun <S, C> S.loadActivities()
    where S : ModelExecution<C>, C : LoadPlannedActivitiesContext {
    this.prepareActivities(errorHandling = ErrorHandling.THROW)
    this.finishActivities()
}

// private fu, "UnusedParameter"
