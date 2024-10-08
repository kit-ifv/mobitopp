package usecases.steps

import domain.data.PersonId
import domain.data.ZoneId
import domain.data.plus
import domain.enums.LegacyActivityType
import domain.location.parseRoadPosition
import modeling.steps.Context
import modeling.steps.CustomStep
import modeling.steps.ModelExecution
import modeling.steps.RepositoryState
import modeling.steps.repairFinishedState
import modeling.steps.subValidateState
import modeling.validation.validateScope
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.CsvReader
import utils.csv.SEMICOLON
import utils.csv.id
import utils.csv.long
import java.io.File

// "personOid";"personNumber";"householdOid";"householdYear";"householdNumber";"activityType";"zoneId";"location";"locationX";"locationY"
// "31";"1";"30";"2017";"90090980";"WORK";"74";"(568000.2917658723,5933428.575922246: -843840888, 0.400201196600711)";"568000.2917658723";"5933428.575922246"

fun <S, C> S.assignFixedDestinations(
    file: File? = null,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: FixedDestinationColumns = FixedDestinationColumns()

) where S : ModelExecution<C>, C : Context, C : PersonContext, C : LegacyZonesContext, C : ActivityContext {
    val csvParser = CsvParser(errorHandling) { row ->
        val id: PersonId = row.id(columns.personOid)
        val p = context.personRepository.getById(id)
        val activityType = LegacyActivityType.decode(row(columns.activityType))
        val zone = context.getZone(row.long(columns.zone))

        val location = zone + row(columns.location).parseRoadPosition()
        p?.let { person ->
            val acts = person.schedule.activities().filter { act -> act.type == activityType }
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
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : Context, C : LegacyZonesContext, C : PersonContext, C : ActivityContext {
    val fixedDestinationFile = file ?: File(this.context.demandFolder.path + "\\demand-data\\fixedDestination.csv")

//    val resource = CsvResource(householdFile, parser, delimiter)

    this.addStep(
        CustomStep(
            name = "assign fixed destinations",
            validation = {
                validateScope(
                    "Validate assign fixed destinations produced warnings:"
                ) {
                    val step = this@prepareFixedDestinationsFile
                    subValidateState(context.plannedActivityRepository, RepositoryState.FINISHED, step)
                    subValidateState(context.personRepository, RepositoryState.FINISHED, step)
                    subValidateState(context.zoneRepository, RepositoryState.FINISHED, step)

                    repairFinishedState(context.plannedActivityRepository, step)
                    repairFinishedState(context.personRepository, step)
                    repairFinishedState(context.zoneRepository, step)
                }
            },
            exec = {
                parser.parse(CsvReader.of(fixedDestinationFile, delimiter)).toList()
            }
        )
    )
}

// data class FixedDestinationColumns(
//    val personOid: String = "personOid",
//    val activityType: String = "activityType",
//    val location: String = "location",
//    val zone: String = "zoneId",
// )

private fun <C> C.getZone(id: Long) where C : LegacyZonesContext = requireNotNull(
    zoneRepository.getById(ZoneId(id)) ?: zoneColumnIndex[id.toInt()]
) {
    "Referenced ZoneId $id could not be found in zoneRepo:" +
        " ${zoneRepository.elements.map { it.id }.toList()}"
}
