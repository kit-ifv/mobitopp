package usecases.steps

import domain.data.PersonId
import domain.data.plus
import domain.enums.LegacyActivityType
import domain.location.parseRoadPosition
import modeling.steps.Context
import modeling.steps.CustomStep
import modeling.steps.ModelExecution
import modeling.steps.RepositoryState
import utils.csv.CsvParser
import utils.csv.CsvReader
import utils.csv.id
import java.io.File

// "personOid";"personNumber";"householdOid";"householdYear";"householdNumber";"activityType";"zoneId";"location";"locationX";"locationY"
// "31";"1";"30";"2017";"90090980";"WORK";"74";"(568000.2917658723,5933428.575922246: -843840888, 0.400201196600711)";"568000.2917658723";"5933428.575922246"

fun <S> S.assignFixedDestinations(
    file: File? = null,
    columns: FixedDestinationColumns = FixedDestinationColumns()
) where S : ModelExecution<LegacyContext> {
    val csvParser = CsvParser { row ->
        val id: PersonId = row.id(columns.personOid)
        val p = context.personRepository.getById(id)
        val activityType = LegacyActivityType.decode(row(columns.activityType))
        val zone =
            context.zoneRepository.getById(row.id(columns.zone)) ?: throw NoSuchElementException("Did not find zone")

        val location = zone + row(columns.location).parseRoadPosition()
        p?.let {
            val acts = it.schedule.activities().filter { act -> act.type == activityType }
            acts.forEach { it.location = location }
        }
    }
    prepareFixedDestinationsFile(csvParser, file)
}
data class FixedDestinationColumns(
    val personOid: String = "personOid",
    val activityType: String = "activityType",
    val location: String = "location",
    val zone: String = "zoneId",
)

fun <S, C> S.prepareFixedDestinationsFile(
    parser: CsvParser<*>,
    file: File? = null,
) where S : ModelExecution<C>, C : Context, C : LegacyZonesContext, C : PersonContext, C : ActivityContext {
    val fixedDestinationFile = file ?: File(this.context.demandFolder.path + "\\demand-data\\fixedDestination.csv")

//    val resource = CsvResource(householdFile, parser, delimiter)

    this.addStep(
        CustomStep(
            name = "assign fixed destinations",
            validation = {
                context.plannedActivityRepository.state == RepositoryState.FINISHED &&
                    context.personRepository.state == RepositoryState.FINISHED &&
                    context.zoneRepository.state == RepositoryState.FINISHED
            },
            exec = {
                val e = parser.parse(CsvReader.of(fixedDestinationFile)).toList()
                println("E : $e")
            }
        )
    )
}
