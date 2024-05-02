package usecases

import domain.data.ActivityDataBuilder
import domain.enums.ActivityType
import modeling.steps.ActivityContext
import modeling.steps.BuildStep
import modeling.steps.Context
import modeling.steps.CsvResource
import modeling.steps.ModelExecution
import modeling.steps.PersonContext
import modeling.steps.PrepareCsvStep
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.SEMICOLON
import utils.csv.decode
import utils.csv.id
import utils.csv.int
import java.io.File
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Suppress("LongParameterList")
fun <S, C> S.prepareActivities(
    file: File? = null,
    delimiter: String = SEMICOLON,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    personColumn: String = "personId",
    activityTypeColumn: String = "activityType",
    activityTypeCodes: CodePlan<ActivityType>? = null,
    tripDurationColumn: String = "observedTripDuration",
    startColumn: String = "startTime",
    durationColumn: String = "duration",
    durationUnit: DurationUnit? = null
) where S : ModelExecution<C>, C : Context, C: ActivityContext, C : PersonContext<*,*> {

    val timeUnit = durationUnit ?: context.timeUnit
    val personRepo = { context.personRepository }
    val activityTpeCodePlan = activityTypeCodes ?: context.activityTypeCodes


    val parser = CsvParser(errorHandling) { row ->
        ActivityDataBuilder(
            id = row.index.toLong(),
            person = personRepo().getById(row.id(personColumn)),
            observedTripDuration = row.int(tripDurationColumn).toDuration(timeUnit),
            startTime = row.int(startColumn).toDuration(timeUnit),
            duration = row.int(durationColumn).toDuration(timeUnit),
            activityType = row.decode(activityTypeColumn, activityTpeCodePlan)
        )
    }

    this.prepareActivitiesFile(parser, file, delimiter)
}

fun <S, C> S.prepareActivitiesFile(
    parser: CsvParser<ActivityDataBuilder>,
    file: File? = null,
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : Context, C : ActivityContext {
    val activityFile = file ?: File(this.context.demandFolder.path + "\\demand-data\\activity.csv")

    val resource = CsvResource(activityFile, parser, delimiter)

    this.addStep(
        PrepareCsvStep(
            name = "load activity csv",
            csv = resource,
            repository = context.activityRepository
        )
    )
}

fun <S, C> S.finishActivities() where S : ModelExecution<C>, C : ActivityContext {
    this.addStep(BuildStep("finish activities", context.activityRepository))
}

fun <S, C> S.loadActivities() where S : ModelExecution<C>, C : Context, C : PersonContext<*,*>, C : ActivityContext {
    this.prepareActivities(errorHandling = ErrorHandling.THROW)
    this.finishActivities()
}