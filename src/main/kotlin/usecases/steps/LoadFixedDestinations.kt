package usecases.steps

import domain.data.ActivityId
import domain.data.LegacyZone
import domain.data.Person
import domain.data.PersonId
import domain.data.PlannedActivity
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.location.Location
import domain.location.parseRoadPosition
import modeling.steps.Context
import modeling.steps.ModelExecution
import modeling.steps.ModelStep
import modeling.steps.Repository
import modeling.steps.RepositoryDependentStep
import modeling.validation.Warning
import modeling.validation.validateScope
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.CsvReader
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.decode
import utils.csv.id
import utils.csv.long
import utils.csv.withFilter
import java.io.File

// "personOid";"personNumber";"householdOid";"householdYear";"householdNumber";"activityType";"zoneId";"location";"locationX";"locationY"
// "31";"1";"30";"2017";"90090980";"WORK";"74";"(568000.2917658723,5933428.575922246: -843840888, 0.400201196600711)";"568000.2917658723";"5933428.575922246"

interface LoadFixedDestinationsContext : Context {
    val zoneRepository: Repository<Zone, ZoneId>
    val zoneColumnIndex: Map<Int, LegacyZone> // TODO legacy

    val personRepository: Repository<Person, PersonId>
    val plannedActivityRepository: Repository<PlannedActivity, ActivityId>

    val defaultFixedDestinationsFile: File
        get() = File(demandFolder.path + "\\demand-data\\fixedDestination.csv")

    fun getZone(id: Long) = requireNotNull(
        zoneRepository.getById(ZoneId(id)) ?: zoneColumnIndex[id.toInt()]
    ) {
        "Referenced ZoneId $id could not be found in zoneRepo:" +
            " ${zoneRepository.elements.map { it.id }.toList()}"
    }
}

data class FixedDestinationColumns(
    val personOid: String = "personOid",
    val activityType: String = "activityType",
    val location: String = "location",
    val zone: String = "zoneId",
)

data class ActivityLocation(val person: Person, val activityType: ActivityType, val location: Location)
// private const val DEMAND_DATA_FIXED_DESTINATION_CSV = "\\demand-data\\fixedDestination.csv"

fun <S, C> S.assignFixedDestinations(
    file: File = context.defaultFixedDestinationsFile,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: FixedDestinationColumns = FixedDestinationColumns(),
    filter: FixedDestinationColumns.(Row, C) -> Boolean = { _, _ -> true }
) where S : ModelExecution<C>, C : LoadFixedDestinationsContext {
    val filterWrap: (Row) -> Boolean = { columns.filter(it, context) }

    val csvParser = CsvParser(errorHandling) { row ->
        val id: PersonId = row.id(columns.personOid)
        val p = context.personRepository.getById(id)
        val activityType = row.decode(
            columns.activityType,
            LegacyActivityType
        ) // TODO should not be hardcoded to LegacyActivityType!
        val zone = context.getZone(row.long(columns.zone))

        val location = row(columns.location, String::parseRoadPosition).withZone(zone)

        p?.let { person ->
            ActivityLocation(person, activityType, location)
        }
    }.withFilter(filterWrap)

    prepareFixedDestinationsFile(csvParser, file)
}

fun <S, C> S.prepareFixedDestinationsFile(
    parser: CsvParser<ActivityLocation>,
    file: File = context.defaultFixedDestinationsFile,
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : LoadFixedDestinationsContext {
    this.addStep(
        LoadFixedDestinationsStep(context, parser, file, delimiter)
    )
}

class LoadFixedDestinationsStep(

    private val context: LoadFixedDestinationsContext,
    private val parser: CsvParser<ActivityLocation>,
    private val file: File = context.defaultFixedDestinationsFile,
    private val delimiter: String = SEMICOLON,
) : ModelStep, RepositoryDependentStep {
    override val name: String = "Load and assign fixed destinations in person schedules"

    override val repository: Repository<Person, PersonId> = context.personRepository
    override val dependentRepositories: Set<Repository<*, *>> = setOf(
        context.zoneRepository,
        context.plannedActivityRepository,
    )

    override fun execute() {
        val reader = CsvReader.of(file, delimiter)

        parser.parse(reader).toList().forEach { (person, activityType, location) ->
            val acts = person.schedule.activities().filter { act -> act.type == activityType }
            acts.forEach { it.location = location }
        }
    }

    override fun verifyInput(): Warning? = validateScope {
        // TODO("Not yet implemented")
    }

    override fun mockBehavior(): Warning? = validateScope {
        // TODO("Not yet implemented")
    }
}
