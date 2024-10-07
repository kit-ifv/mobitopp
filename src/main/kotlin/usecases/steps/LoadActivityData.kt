package usecases.steps

import domain.data.ActivityId
import domain.data.Person
import domain.data.PersonBuilder
import domain.data.PersonId
import domain.data.PlannedActivityBuilder
import domain.enums.ActivityType
import modeling.steps.AddCsvStep
import modeling.steps.BuildStep
import modeling.steps.Context
import modeling.steps.CsvResource
import modeling.steps.ModelExecution
import modeling.steps.RepositoryBuilder
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.decode
import utils.csv.id
import utils.csv.int
import utils.csv.withFilter
import utils.units.AbsoluteTime
import java.io.File
import kotlin.random.Random
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Suppress("LongParameterList")
fun <S, C> S.prepareActivities(
    file: File? = null,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    activitiesColumns: ActivitiesColumns = ActivitiesColumns(),
    activityTypeCodes: CodePlan<ActivityType>? = null,
    durationUnit: DurationUnit? = null,
    filter: ActivitiesColumns.(Row) -> Boolean = { true }
) where S : ModelExecution<C>, C : Context, C : ActivityContext, C : PersonContext {
    val timeUnit = durationUnit ?: context.timeUnit
    val personRepo = { context.personRepository }
    val activityTpeCodePlan = activityTypeCodes ?: context.activityTypeCodes

    val parser = CsvParser(errorHandling) { row ->
        PlannedActivityBuilder().apply {
            id = ActivityId(row.index.toLong())
            person = getPerson(personRepo, row, activitiesColumns)
            observedTripDuration = row.int(activitiesColumns.tripDurationColumn).toDuration(timeUnit)
            startTime = AbsoluteTime.START + row.int(activitiesColumns.startColumn).toDuration(timeUnit)
            duration = row.int(activitiesColumns.durationColumn).toDuration(timeUnit)
            activityType = row.decode(activitiesColumns.activityTypeColumn, activityTpeCodePlan)
            // TODO someone should check that this is useful
            random = Random(row.index.toLong())
        }
    }

    this.prepareActivitiesFile(parser.withFilter { activitiesColumns.filter(it) }, file, delimiter)
}

private fun getPerson(
    personRepo: () -> RepositoryBuilder<PersonBuilder, Person, PersonId>,
    row: Row,
    activitiesColumns: ActivitiesColumns
): Person = row.id<Person>(activitiesColumns.personColumn).let {
    val repo = personRepo()

    return checkNotNull(
        repo.getById(it)
    ) {
        "Person[$it] does not exist in the repository: ${repo.name}. (Maybe filters were applied?)\n" +
            "    Repo ${repo.name}: ${repo.source}"
    }
}

data class ActivitiesColumns(
    val personColumn: String = "personId",
    val activityTypeColumn: String = "activityType",
    val tripDurationColumn: String = "observedTripDuration",
    val startColumn: String = "startTime",
    val durationColumn: String = "duration",
)

fun <S, C> S.prepareActivitiesFile(
    parser: CsvParser<PlannedActivityBuilder>,
    file: File? = null,
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : Context, C : ActivityContext {
    val path = this.context.demandFolder.path + "\\demand-data\\activity.csv"
    val activityFile = file ?: File(path)

    val resource = CsvResource(activityFile, parser, delimiter)

    this.addStep(
        AddCsvStep(
            name = "load activity csv",
            csv = resource,
            repository = context.plannedActivityRepository
        )
    )
}

fun <S, C> S.finishActivities() where S : ModelExecution<C>, C : ActivityContext {
    this.addStep(BuildStep("finish activities", context.plannedActivityRepository))
}

fun <S, C> S.loadActivities()
    where S : ModelExecution<C>, C : Context, C : PersonContext, C : ActivityContext {
    this.prepareActivities(errorHandling = ErrorHandling.THROW)
    this.finishActivities()
}
