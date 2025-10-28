package application.steps.parser.csv

import core.modelsteps.AbstractAddResourceStep
import core.modelsteps.FileBasedAddResourceStep
import core.modelsteps.GroupedStepBuilder
import core.modelsteps.LoadCsvStep
import core.modelsteps.MutableRepository
import core.modelsteps.Resource
import core.modelsteps.SealStep
import core.modelsteps.asResource
import core.modelsteps.reusable
import domain.shared.enums.ActivityType
import domain.simulation.config.DemandSimContext
import domain.synthesis.data.ActivityId
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.MutablePlannedActivity
import domain.synthesis.data.PersonId
import domain.synthesis.parser.ActivitiesColumns
import domain.synthesis.parser.ActivityStartShifter
import domain.synthesis.parser.QuarterHourShifter
import domain.synthesis.parser.activityCsvParser
import domain.synthesis.parser.binary.BinaryActivityReader
import domain.synthesis.parser.binary.BinaryActivityWriter
import utils.CodePlan
import utils.ErrorHandling
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import utils.csv.CsvParser
import utils.csv.CsvReader
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.withFilter
import java.nio.file.Path
import kotlin.math.abs
import kotlin.time.DurationUnit

private const val ERROR_OUTPUT_SIZE = 5

@Suppress("LongParameterList")
fun LoadPlannedActivitiesContext.prepareActivities(
    path: Path = defaultActivityPath,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: ActivitiesColumns = ActivitiesColumns(),
    durationUnit: DurationUnit = timeUnit,
    filter: ActivitiesColumns.(Row, LoadPlannedActivitiesContext) -> Boolean = { _, _ -> true },
    shiftActivityStart: ActivityStartShifter = QuarterHourShifter.cached(),
) {
    val (_, step) = activitiesCsvConfig(path) {
        this.delimiter = delimiter
        this.errorHandling = errorHandling
        this.columns = columns
        this.durationUnit = durationUnit
        this.filter = filter
        this.shiftActivityStart = shiftActivityStart
    }
    this.runStep(step)
}

class ConvertedCsvResource<X, E>(
    val path: Path,
    val parser: CsvParser<X>,
    val delimiter: String = SEMICOLON,
    private val reusable: Boolean = false,
    val converter: (X) -> E

) : Resource<E> {
    override val name = path.fileName.toString()
    override val source: String = path.toString()
    override val elements: Sequence<E>
        get() = rowSequence.elements
    private val rowSequence by lazy {
        parser.parse(CsvReader.of(path, delimiter)).map { converter(it) }
            .asResource(name, source).let {
                if (reusable) {
                    it.reusable()
                } else {
                    it
                }
            }
    }
}

fun LoadPlannedActivitiesContext.activitiesCsvConfig(
    path: Path = defaultActivityPath,
    lambda: ActivityCsvConfig.() -> Unit,
): FileBasedAddResourceStep<MutablePlannedActivity, ActivityId> {
    val config = ActivityCsvConfig(path = path, durationUnit = this.timeUnit)
    config.apply(lambda)
    return config.run {
        val parser = activityCsvParser(errorHandling, columns, shiftActivityStart, durationUnit) {
            getPerson(it)
        }
        val filteredParser = parser.withFilter { columns.filter(it, this@activitiesCsvConfig) }
        val step = LoadCsvStep(
            path = path,
            name = "Load planned activities from csv",
            parser = filteredParser,
            delimiter = delimiter,
            repository = plannedActivityRepository,
            dependentRepositories = setOf(personRepository),
            validationMock = listOf() // TODO
        )
        FileBasedAddResourceStep(path, step)
    }
}

data class ActivityCsvConfig(
    var path: Path,
    var delimiter: String = SEMICOLON,
    var errorHandling: ErrorHandling = ErrorHandling.WARNING,
    var columns: ActivitiesColumns = ActivitiesColumns(),
    var durationUnit: DurationUnit,
    var filter: ActivitiesColumns.(Row, LoadPlannedActivitiesContext) -> Boolean = { _, _ -> true },
    var shiftActivityStart: ActivityStartShifter = QuarterHourShifter.cached(),
)

fun LoadPlannedActivitiesContext.runStep(
    step: AbstractAddResourceStep<MutablePlannedActivity, ActivityId>,

) = runStep {
    step
}

fun LoadPlannedActivitiesContext.activities(lambda: ActivityBuild.() -> Unit) {
    val builder = ActivityBuild(simulationSeed, personRepository::get, activityTypes)
    builder.apply(lambda)
    builder.executeOn(this)
    finishActivities()
}

class ActivityBuild(
    val seed: Long,
    val converter: (PersonId) -> MutablePerson?,
    activityCodes: CodePlan<ActivityType>,
) : GroupedStepBuilder<MutablePlannedActivity, ActivityId>() {
    override val reader: BinaryReader<MutablePlannedActivity> = BinaryActivityReader(
        codeActivity = activityCodes,
        personConverter = converter,
        contextSimulationSeed = seed
    )
    override val writer: BinaryWriter<MutablePlannedActivity> = BinaryActivityWriter()

//    override fun fromCSV(
//        source: Path,
//        lambda: context(Path) () -> AbstractAddResourceStep<MutablePlannedActivity, ActivityId>,
//    ): FileBasedAddResourceStep<MutablePlannedActivity, ActivityId> {
//        return context(source) {
//            FileBasedAddResourceStep(source, lambda())
//        }
//    }
}

fun LoadPlannedActivitiesContext.finishActivities() = runStep {
    SealStep(plannedActivityRepository)
}

fun LoadPlannedActivitiesContext.loadActivities() {
    this.prepareActivities(errorHandling = ErrorHandling.THROW)
    this.finishActivities()
}

interface LoadPlannedActivitiesContext : DemandSimContext {
    val plannedActivityRepository: MutableRepository<MutablePlannedActivity, ActivityId>
    val personRepository: MutableRepository<MutablePerson, PersonId>

    val defaultActivityPath: Path
        get() = dataFolder.resolve("demand-data").resolve("activity.csv")

    fun getPerson(personId: PersonId) = requireNotNull(
        personRepository[personId]
    ) {
        "Referenced person id $personId could not be found in personRepo:" +
            " ${
                personRepository.elements.map { it.id }.toList()
                    .sortedBy { abs(it.value - personId.value) }.take(ERROR_OUTPUT_SIZE)
            }"
    }
}
