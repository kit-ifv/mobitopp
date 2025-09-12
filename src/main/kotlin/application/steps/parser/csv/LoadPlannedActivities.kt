package application.steps.parser.csv

import core.modelsteps.AddResourceStep
import core.modelsteps.FileBasedResourceStep
import core.modelsteps.GroupedStepBuilder
import core.modelsteps.LoadCsvStep
import core.modelsteps.MutableRepository
import core.modelsteps.SealStep
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
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.withFilter
import java.nio.file.Path
import kotlin.time.DurationUnit

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
    val parser = activityCsvParser(errorHandling, columns, shiftActivityStart, durationUnit) {
        getPerson(it)
    }
    this.prepareActivitiesFile(parser.withFilter { columns.filter(it, this) }, path, delimiter)
}

context(source: Path)
fun LoadPlannedActivitiesContext.activitiesCsvConfig(
    lambda: ActivityCsvConfig.() -> Unit
): AddResourceStep<MutablePlannedActivity, ActivityId> {
    val config = ActivityCsvConfig(path = source, durationUnit = this.timeUnit)
    config.apply(lambda)
    return config.run {
        val parser = activityCsvParser(errorHandling, columns, shiftActivityStart, durationUnit) {
            getPerson(it)
        }
        val filteredParser = parser.withFilter { columns.filter(it, this@activitiesCsvConfig) }
        LoadCsvStep(
            path = path,
            name = "Load planned activities from csv",
            parser = filteredParser,
            delimiter = delimiter,
            repository = plannedActivityRepository,
            dependentRepositories = setOf(personRepository),
            validationMock = listOf() // TODO
        )
    }
}

data class ActivityCsvConfig(
    val path: Path,
    val delimiter: String = SEMICOLON,
    val errorHandling: ErrorHandling = ErrorHandling.WARNING,
    val columns: ActivitiesColumns = ActivitiesColumns(),
    val durationUnit: DurationUnit,
    val filter: ActivitiesColumns.(Row, LoadPlannedActivitiesContext) -> Boolean = { _, _ -> true },
    val shiftActivityStart: ActivityStartShifter = QuarterHourShifter.cached(),
)

fun LoadPlannedActivitiesContext.prepareActivitiesFile(
    parser: CsvParser<MutablePlannedActivity>,
    path: Path = defaultActivityPath,
    delimiter: String = SEMICOLON,
) = runStep {
    LoadCsvStep<MutablePlannedActivity, ActivityId>(
        path = path,
        name = "Load planned activities from csv",
        parser = parser,
        delimiter = delimiter,
        repository = plannedActivityRepository,
        dependentRepositories = setOf(personRepository),
        validationMock = listOf() // TODO
    )
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

    override fun fromCSV(
        source: Path,
        lambda: context(Path) () -> AddResourceStep<MutablePlannedActivity, ActivityId>,
    ): FileBasedResourceStep<MutablePlannedActivity, ActivityId> {
        return context(source) {
            FileBasedResourceStep(source, lambda())
        }
    }
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
            " ${personRepository.elements.map { it.id }.toList()}"
    }
}
