package application.steps.parser.csv

import application.steps.ActivityTypesConfig
import application.steps.HasPersonRepo
import application.steps.SourceFilesConfig
import application.steps.UnitConfig
import core.modelsteps.Config
import core.modelsteps.Context
import core.modelsteps.resources.BinaryCacheConfig
import core.modelsteps.resources.CsvResource
import core.modelsteps.resources.MapRepository
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Resource
import core.modelsteps.resources.cachedCsv
import core.modelsteps.scopes.addResourceStep
import core.modelsteps.scopes.mutableRepositoryScope
import core.modelsteps.scopes.updateEachStep
import domain.shared.datastructure.schedule.Activity
import domain.shared.datastructure.schedule.LinkedActivity
import domain.shared.datastructure.schedule.Schedule
import domain.shared.datastructure.schedule.plans.Dispatcher
import domain.shared.datastructure.schedule.plans.IDispatcher
import domain.shared.datastructure.schedule.plans.SingularDispatcher
import domain.simulation.behavior.toSchedule
import domain.synthesis.data.ActivityId
import domain.synthesis.data.MutablePlannedActivity
import domain.synthesis.data.PersonId
import domain.synthesis.data.PlannedActivity
import domain.synthesis.parser.ActivitiesColumns
import domain.synthesis.parser.ActivityCsvConfig
import domain.synthesis.parser.NoActivityStartShifter
import domain.synthesis.parser.binary.BinaryActivityReader
import domain.synthesis.parser.binary.BinaryActivityWriter
import domain.synthesis.parser.createActivityCsvParser
import utils.Identifiable
import utils.csv.CsvParser
import utils.random.StochasticActor
import java.nio.file.Path
import kotlin.time.Duration.Companion.minutes

interface HasPlannedActivities<A : Identifiable<ActivityId>> {
    val plannedActivities: MutableList<A>
}

interface HasMutableSchedule {
    var schedule: Schedule
}

/**
 * Provides a scope for configuring and assigning planned activities to persons.
 *
 * This step creates a temporary repository for planned activities, applies the [scope]
 * to populate it, and then assigns the activities to their respective persons in the
 * [repository].
 *
 * @receiver The simulation context [CTXT].
 * @param CTXT The context type. Must implement [Context].
 * @param P The person type. Must implement [HasPlannedActivities] for [PlannedActivity]
 *          and [Identifiable] for [PersonId].
 * @param repository The mutable repository of persons to update. Provided via context.
 * @param scope The configuration scope for populating planned activities.
 */
context(repository: MutableRepository<P, PersonId>)
fun <CTXT, P> CTXT.plannedActivities(
    scope: context(MutableRepository<MutablePlannedActivity, ActivityId>) CTXT.() -> Unit
) where CTXT : Context, P : HasPlannedActivities<PlannedActivity>, P : Identifiable<PersonId> {
    val plannedActivities = MapRepository<MutablePlannedActivity, ActivityId>("planned activities")

    mutableRepositoryScope<CTXT, MutablePlannedActivity, ActivityId>(
        { plannedActivities },
        sealed = true
    ) {
        scope()
    }

    val activitiesById: Map<PersonId, List<PlannedActivity>> =
        plannedActivities.elements.groupBy { it.person }

    updateEachStep<CTXT, P, PersonId>(
        name = "assign planned activites to persons",
        dependentRepositories = setOf(plannedActivities)
    ) {
        val activities = requireNotNull(activitiesById[it.id]) {
            "Could not find activities for person ${it.id}"
        }
        it.plannedActivities.addAll(activities.sortedBy { a -> a.startTime })
    }
}

// TODO do not use until build agents is removed, which currently performs schedule creation
// context(repository: MutableRepository<P, PersonId>, config: CFG)
// fun <CTXT, CFG : Config, P> CTXT.schedule(
//    dispatcher: IDispatcher = SingularDispatcher(),
//    repairStrategy: (Activity, LinkedActivity) -> Unit = oneMinuteGapFix,
//    scope: context(MutableRepository<MutablePlannedActivity, ActivityId>, CFG) CTXT.() -> Unit
// ) where CTXT : Context, P : HasPlannedActivities<PlannedActivity>, P : HasMutableSchedule, P : Identifiable<PersonId> {
//    plannedActivities(scope)
//    plannedActivitiesToSchedule(dispatcher, repairStrategy)
// }

val oneMinuteGapFix: (Activity, LinkedActivity) -> Unit = { prev, broken ->
    // TODO check and inform if triggered.
    broken.shiftStartTo(prev.endTime + 1.minutes)
}

// TODO do not use until build agents is removed, which currently performs schedule creation
// context(repository: MutableRepository<P, PersonId>, config: CFG)
// fun <CTXT, CFG : Config, P> CTXT.plannedActivitiesToSchedule(
//    dispatcher: IDispatcher = SingularDispatcher(),
//    repairStrategy: (Activity, LinkedActivity) -> Unit = oneMinuteGapFix
// ) where CTXT : Context, P : HasPlannedActivities<PlannedActivity>, P : HasMutableSchedule, P : Identifiable<PersonId> = updateEachStep(
//    name = "convert planned activities to schedule"
// ) {
//    it.schedule = it.plannedActivities.sortedBy { a -> a.startTime }.toSchedule(dispatcher, repairStrategy)
//    it.plannedActivities.clear()
// }

// TODO scope to add directly to schedule no intermediate planned activities list?

/**
 * Loads planned activities from a resource.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [Context].
 * @param repository The mutable repository of planned activities to populate. Provided via context.
 * @param resource The resource (e.g., CSV) to load activities from.
 */
context(repository: MutableRepository<MutablePlannedActivity, ActivityId>)
fun <C> C.loadActivities(
    resource: Resource<MutablePlannedActivity>,
) where C : Context = addResourceStep<C, MutablePlannedActivity, ActivityId>(
    name = "load planned activities from ${resource.name}",
    resource = resource,
)

/**
 * Creates a CSV resource for planned activities.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [Context] and [HasPersonRepo] for [P].
 * @param CFG The configuration type. Must implement [SourceFilesConfig], [UnitConfig],
 *            and [ActivityTypesConfig].
 * @param P The person type. Must implement [StochasticActor] and [Identifiable] for [PersonId].
 * @param config The configuration. Provided via context.
 * @param parser The CSV parser for planned activities. Defaults to [plannedActivityCsvParser].
 * @param path The path to the activity CSV file. Defaults to [config.sourceFiles.activityCSV].
 * @param delimiter The CSV delimiter. Defaults to [config.sourceFiles.defaultCsvDelimiter].
 * @param binaryCache Optional configuration for binary caching. Defaults to [binaryPlannedActivityFormat].
 * @return A [Resource] representing the planned activity CSV.
 */
context(config: CFG)
fun <C, CFG, P> C.plannedActivityCsv(
    parser: CsvParser<MutablePlannedActivity> = plannedActivityCsvParser(),
    path: Path = config.sourceFiles.activityCSV,
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<MutablePlannedActivity>? = binaryPlannedActivityFormat() // TODO move binary format to load level?
): Resource<MutablePlannedActivity>
    where C : Context, CFG : SourceFilesConfig, CFG : UnitConfig, CFG : ActivityTypesConfig, C : HasPersonRepo<*, P>, P : StochasticActor, P : Identifiable<PersonId> = CsvResource(
    path,
    parser,
    delimiter
).let { csv ->
    binaryCache?.let {
        csv.cachedCsv(it)
    } ?: csv
}

/**
 * Creates a CSV parser for planned activities.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [Context] and [HasPersonRepo] for [P].
 * @param CFG The configuration type. Must implement [ActivityTypesConfig] and [UnitConfig].
 * @param P The person type. Must implement [StochasticActor] and [Identifiable] for [PersonId].
 * @param config The configuration. Provided via context.
 * @param customizeCsvConfig Lambda to customize the [ActivityCsvConfig].
 * @return A [CsvParser] for [MutablePlannedActivity].
 */
context(config: CFG)
fun <C, CFG, P> C.plannedActivityCsvParser(
    customizeCsvConfig: ActivityCsvConfig<P>.() -> Unit = {}
): CsvParser<MutablePlannedActivity>
    where C : Context, CFG : ActivityTypesConfig, CFG : UnitConfig, C : HasPersonRepo<*, P>, P : StochasticActor, P : Identifiable<PersonId> = createActivityCsvParser(
    ActivityCsvConfig(
        columns = ActivitiesColumns(),
        personExists = personRepository::contains,
        personProvider = personRepository::getValue,
        durationUnit = config.durationUnit,
        activityTypes = config.activityTypes,
        errorHandling = config.errorHandling,
        shiftActivityStart = NoActivityStartShifter,
        seed = config.seed
    ).also {
        it.customizeCsvConfig()
    }
)

/**
 * Creates a binary cache configuration for planned activities.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [Context] and [HasPersonRepo].
 * @param CFG The configuration type. Must implement [SourceFilesConfig] and [ActivityTypesConfig].
 * @param config The configuration. Provided via context.
 * @return A [BinaryCacheConfig] instance.
 */
context(config: CFG)
fun <C, CFG> C.binaryPlannedActivityFormat(): BinaryCacheConfig<MutablePlannedActivity>
    where C : Context, CFG : SourceFilesConfig, CFG : ActivityTypesConfig {
    return BinaryCacheConfig<MutablePlannedActivity>(
        cacheRootPath = config.cachePath,
        binaryReader = BinaryActivityReader(
            codeActivity = config.activityTypes,
//            personConverter = converter,
            contextSimulationSeed = config.seed
        ),
        binaryWriter = BinaryActivityWriter()
    )
}

// private const val ERROR_OUTPUT_SIZE = 5
//
// @Suppress("LongParameterList")
// fun LoadPlannedActivitiesContext.prepareActivities(
//    path: Path = defaultActivityPath,
//    delimiter: String = SEMICOLON,
//    errorHandling: ErrorHandling = ErrorHandling.WARNING,
//    columns: ActivitiesColumns = ActivitiesColumns(),
//    durationUnit: DurationUnit = timeUnit,
//    filter: ActivitiesColumns.(Row, LoadPlannedActivitiesContext) -> Boolean = { _, _ -> true },
//    shiftActivityStart: ActivityStartShifter = QuarterHourShifter.cached(),
// ) {
//    val (_, step) = activitiesCsvConfig(path) {
//        this.delimiter = delimiter
//        this.errorHandling = errorHandling
//        this.columns = columns
//        this.durationUnit = durationUnit
//        this.filter = filter
//        this.shiftActivityStart = shiftActivityStart
//    }
//    this.runStep(step)
// }

// class ConvertedCsvResource<X, E>(
//    val path: Path,
//    val parser: CsvParser<X>,
//    val delimiter: String = SEMICOLON,
//    private val reusable: Boolean = false,
//    val converter: (X) -> E
//
// ) : Resource<E> {
//    override val name = path.fileName.toString()
//    override val source: String = path.toString()
//    override val elements: Sequence<E>
//        get() = rowSequence.elements
//    private val rowSequence by lazy {
//        parser.parse(CsvReader.of(path, delimiter)).map { converter(it) }
//            .asResource(name, source).let {
//                if (reusable) {
//                    it.reusable()
//                } else {
//                    it
//                }
//            }
//    }
// }
//
// fun LoadPlannedActivitiesContext.activitiesCsvConfig(
//    path: Path = defaultActivityPath,
//    lambda: ActivityCsvConfig.() -> Unit,
// ): FileBasedAddResourceStep<MutablePlannedActivity, ActivityId> {
//    val config = ActivityCsvConfig(path = path, durationUnit = this.timeUnit)
//    config.apply(lambda)
//    return config.run {
//        val parser = createActivityCsvParser(errorHandling, columns, shiftActivityStart, durationUnit) {
//            getPerson(it)
//        }
//        val filteredParser = parser.withFilter { columns.filter(it, this@activitiesCsvConfig) }
//        val step = LoadCsvStep(
//            path = path,
//            name = "Load planned activities from csv",
//            parser = filteredParser,
//            delimiter = delimiter,
//            repository = plannedActivityRepository,
//            dependentRepositories = setOf(personRepository),
//            validationMock = listOf() // TODO
//        )
//        FileBasedAddResourceStep(path, step)
//    }
// }

// fun LoadPlannedActivitiesContext.runStep(
//    step: AbstractAddResourceStep<MutablePlannedActivity, ActivityId>,
//
// ) = runStep {
//    step
// }

// fun LoadPlannedActivitiesContext.activities(lambda: ActivityBuild.() -> Unit) {
//    val builder = ActivityBuild(simulationSeed, personRepository::get, activityTypes)
//    builder.apply(lambda)
//    builder.executeOn(this)
//    finishActivities()
// }

// class ActivityBuild(
//    val seed: Long,
//    val converter: (PersonId) -> MutablePerson?,
//    activityCodes: CodePlan<ActivityType>,
// ) : GroupedStepBuilder<MutablePlannedActivity, ActivityId>() {
//    override val reader: BinaryReader<MutablePlannedActivity> = BinaryActivityReader(
//        codeActivity = activityCodes,
//        personConverter = converter,
//        contextSimulationSeed = seed
//    )
//    override val writer: BinaryWriter<MutablePlannedActivity> = BinaryActivityWriter()
//
// //    override fun fromCSV(
// //        source: Path,
// //        lambda: context(Path) () -> AbstractAddResourceStep<MutablePlannedActivity, ActivityId>,
// //    ): FileBasedAddResourceStep<MutablePlannedActivity, ActivityId> {
// //        return context(source) {
// //            FileBasedAddResourceStep(source, lambda())
// //        }
// //    }
// }
//
// fun LoadPlannedActivitiesContext.finishActivities() = runStep {
//    SealStep(plannedActivityRepository)
// }
//
// fun LoadPlannedActivitiesContext.loadActivities() {
//    this.prepareActivities(errorHandling = ErrorHandling.THROW)
//    this.finishActivities()
// }
//
// interface LoadPlannedActivitiesContext : DemandSimContext {
//    val plannedActivityRepository: MutableRepository<MutablePlannedActivity, ActivityId>
//    val personRepository: MutableRepository<MutablePerson, PersonId>
//
//    val defaultActivityPath: Path
//        get() = dataFolder.resolve("demand-data").resolve("activity.csv")
//
//    fun getPerson(personId: PersonId) = requireNotNull(
//        personRepository[personId]
//    ) {
//        "Referenced person id $personId could not be found in personRepo:" +
//            " ${
//                personRepository.elements.map { it.id }.toList()
//                    .sortedBy { abs(it.value - personId.value) }.take(ERROR_OUTPUT_SIZE)
//            }"
//    }
// }
