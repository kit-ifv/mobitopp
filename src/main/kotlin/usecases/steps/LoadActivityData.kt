package usecases.steps

import domain.data.ActivityId
import domain.data.MutablePlannedActivity
import domain.data.Person
import domain.data.PersonId
import domain.data.PlannedActivity
import domain.enums.ActivityType
import modeling.steps.Context
import modeling.steps.LoadCsvStep
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
import utils.random.StochasticActor
import utils.units.AbsoluteTime
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.toDuration

interface LoadPlannedActivitiesContext : Context {
    val plannedActivityRepository: MutableRepository<PlannedActivity, ActivityId>
    val personRepository: Repository<Person, PersonId>
    val activityTypeCodes: CodePlan<ActivityType>

    val defaultActivityFile: File
        get() = File(demandFolder.path + "\\demand-data\\activity.csv")

    fun getPerson(row: Row, personColumn: String) = requireNotNull(
        personRepository[row.id(personColumn)]
    ) {
        "Referenced person id ${row(personColumn)} could not be found in personRepo:" +
            " ${personRepository.elements.map { it.id }.toList()}"
    }
}

data class ActivitiesColumns(
    val personColumn: String = "personId",
    val activityTypeColumn: String = "activityType",
    val tripDurationColumn: String = "observedTripDuration",
    val startColumn: String = "startTime",
    val durationColumn: String = "duration",
)

@Suppress("LongParameterList")
fun LoadPlannedActivitiesContext.prepareActivities(
    file: File = defaultActivityFile,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: ActivitiesColumns = ActivitiesColumns(),
    durationUnit: DurationUnit = timeUnit,
    filter: ActivitiesColumns.(Row, LoadPlannedActivitiesContext) -> Boolean = { _, _ -> true },
    shiftActivityStart: ActivityStartShifter = QuarterHourShifter.cached(),
) {
    val parser = CsvParser<MutablePlannedActivity>(errorHandling) { row ->

        MutablePlannedActivity(
            id = ActivityId(row.index.toLong()),
            seed = simulationSeed
        ) {
            val shift = shiftActivityStart(this)

            person = getPerson(row, columns.personColumn)
            observedTripDuration = row.int(columns.tripDurationColumn).toDuration(durationUnit)
            startTime = AbsoluteTime.START + row.int(columns.startColumn).toDuration(durationUnit) + shift
            duration = row.int(columns.durationColumn).toDuration(durationUnit)
            activityType = row.decode(columns.activityTypeColumn, activityTypeCodes)
        }
    }

    this.prepareActivitiesFile(parser.withFilter { columns.filter(it, this) }, file, delimiter)
}

fun LoadPlannedActivitiesContext.prepareActivitiesFile(
    parser: CsvParser<PlannedActivity>,
    file: File = defaultActivityFile,
    delimiter: String = SEMICOLON,
) = runStep {
    LoadCsvStep<PlannedActivity, ActivityId>(
        file = file,
        name = "Load planned activities from csv",
        parser = parser,
        delimiter = delimiter,
        repository = plannedActivityRepository,
        dependentRepositories = setOf(personRepository),
        validationMock = listOf() // TODO
    )
}

fun LoadPlannedActivitiesContext.finishActivities() = runStep {
    SealStep(plannedActivityRepository)
}

fun LoadPlannedActivitiesContext.loadActivities() {
    this.prepareActivities(errorHandling = ErrorHandling.THROW)
    this.finishActivities()
}

fun interface ActivityStartShifter {
    operator fun invoke(actor: StochasticActor): Duration
    fun cached() = CachedActivityStartShifter(this)
}

@Suppress("MagicNumber")
object QuarterHourShifter : ActivityStartShifter {
    override operator fun invoke(actor: StochasticActor) = actor.random.nextInt(-7, 7).minutes
}

object NoActivityStartShifter : ActivityStartShifter {
    override operator fun invoke(actor: StochasticActor) = Duration.ZERO
}

class CachedActivityStartShifter(
    private val shifter: ActivityStartShifter
) : ActivityStartShifter {
    private val shifts: MutableMap<StochasticActor, Duration> = mutableMapOf()
    override operator fun invoke(actor: StochasticActor) = shifts.computeIfAbsent(actor) { shifter(actor) }
}
