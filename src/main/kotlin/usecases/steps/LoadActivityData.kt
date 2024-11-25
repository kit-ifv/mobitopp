package usecases.steps

import domain.data.ActivityId
import domain.data.Person
import domain.data.PersonId
import domain.data.PlannedActivity
import domain.enums.ActivityType
import modeling.steps.AddCsvStep
import modeling.steps.Context
import modeling.steps.CsvResource
import modeling.steps.ModelExecution
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.SealStep
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

interface PlannedActivitiesContext : Context {
    val activitiesRepository: MutableRepository<PlannedActivity, ActivityId>
    val personRepository: Repository<Person, PersonId>

    val activityTypeCodes: CodePlan<ActivityType>
}

@Suppress("LongParameterList")
fun <S, C> S.prepareActivities(
    file: File? = null,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    activitiesColumns: ActivitiesColumns = ActivitiesColumns(),
    activityTypeCodes: CodePlan<ActivityType>? = null,
    durationUnit: DurationUnit? = null,
    filter: ActivitiesColumns.(Row, C) -> Boolean = { _, _ -> true }
) where S : ModelExecution<C>, C : PlannedActivitiesContext {
    val timeUnit = durationUnit ?: context.timeUnit
    val personRepo = { context.personRepository }
    val activityTpeCodePlan = activityTypeCodes ?: context.activityTypeCodes

    val parser = CsvParser(errorHandling) { row ->
        PlannedActivity().apply { // TODO
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

    this.prepareActivitiesFile(parser.withFilter { activitiesColumns.filter(it, context) }, file, delimiter)
}

private fun getPerson(
    personRepo: () -> Repository<Person, PersonId>,
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
    parser: CsvParser<PlannedActivity>,
    file: File? = null,
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : PlannedActivitiesContext {
    val step = if (file != null) {
        ReadActivityCsv(context, parser, delimiter, file)
    } else {
        ReadActivityCsv(context, parser, delimiter = delimiter)
    }

    this.addStep(step)
}

class ReadActivityCsv(
    context: PlannedActivitiesContext,
    parser: CsvParser<PlannedActivity>,
    delimiter: String = SEMICOLON,
    file: File = File(context.demandFolder.path + "\\demand-data\\activity.csv"),
) : AddCsvStep<PlannedActivity, ActivityId>(
    csv = CsvResource(file, parser, delimiter)
) {
    override val name: String = "Reade planned activity csv: ${file.name}"

    override val repository: MutableRepository<PlannedActivity, ActivityId> = context.activitiesRepository
    override val dependentRepositories: Set<Repository<*, *>> = setOf(context.personRepository)

    override fun mockElementsForValidation(): List<PlannedActivity> {
        // TODO("Not yet implemented")
        return emptyList()
    }
}

fun <S, C> S.finishActivities() where S : ModelExecution<C>, C : PlannedActivitiesContext {
    this.addStep(SealStep(context.activitiesRepository))
}

fun <S, C> S.loadActivities()
    where S : ModelExecution<C>, C : PlannedActivitiesContext {
    this.prepareActivities(errorHandling = ErrorHandling.THROW)
    this.finishActivities()
}
