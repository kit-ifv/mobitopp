package usecases.steps

import domain.data.ActivityId
import domain.data.LegacyZone
import domain.data.Person
import domain.data.PersonId
import domain.data.PlannedActivity
import domain.data.Zone
import domain.data.ZoneId
import domain.data.plus
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.location.Location
import domain.location.parseRoadPosition
import modeling.steps.Context
import modeling.steps.ModelExecution
import modeling.steps.MutableRepository
import modeling.steps.MutatingStep
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

private const val DEMAND_DATA_FIXED_DESTINATION_CSV = "\\demand-data\\fixedDestination.csv"

fun <S, C> S.assignFixedDestinations(
    file: File = File(context.demandFolder.path + DEMAND_DATA_FIXED_DESTINATION_CSV),
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
//            val acts = person.schedule.activities().filter { act -> act.type == activityType }
//            acts.forEach { it.location = location }
        }
    }.withFilter(filterWrap)

    prepareFixedDestinationsFile(csvParser, file)
}

data class ActivityLocation(val person: Person, val activityType: ActivityType, val location: Location)

data class FixedDestinationColumns(
    val personOid: String = "personOid",
    val activityType: String = "activityType",
    val location: String = "location",
    val zone: String = "zoneId",
)

@Suppress("UnusedParameter")
fun <S, C> S.prepareFixedDestinationsFile(
    parser: CsvParser<*>,
    file: File = File(context.demandFolder.path + DEMAND_DATA_FIXED_DESTINATION_CSV),
    delimiter: String = SEMICOLON,
) where S : ModelExecution<C>, C : LoadFixedDestinationsContext {
    //this.addStep()
}

interface LoadFixedDestinationsContext : Context {
    val zoneRepository: Repository<Zone, ZoneId>
    val zoneColumnIndex: Map<Int, LegacyZone> // TODO legacy

    val personRepository: MutableRepository<Person, PersonId>
    val plannedActivityRepository: Repository<PlannedActivity, ActivityId>

    fun getZone(id: Long) = requireNotNull(
        zoneRepository.getById(ZoneId(id)) ?: zoneColumnIndex[id.toInt()]
    ) {
        "Referenced ZoneId $id could not be found in zoneRepo:" +
            " ${zoneRepository.elements.map { it.id }.toList()}"
    }
}

class LoadFixedDestinationsStep(
    private val context: LoadFixedDestinationsContext,
    private val file: File = File(context.demandFolder.path + DEMAND_DATA_FIXED_DESTINATION_CSV),
    private val errorHandling: ErrorHandling = ErrorHandling.WARNING,
    private val delimiter: String = SEMICOLON,
    private val columns: FixedDestinationColumns = FixedDestinationColumns(),
    private val filter: FixedDestinationColumns.(Row, Context) -> Boolean = { _, _ -> true }
) : MutatingStep<Person, PersonId>, RepositoryDependentStep {
    override val name: String = "Load and assign fixed destinations in person schedules"

    override val repository: MutableRepository<Person, PersonId> = context.personRepository
    override val dependentRepositories: Set<Repository<*, *>> = setOf(
        context.zoneRepository,
        context.plannedActivityRepository,
    )

    override fun execute() {
        val filterWrap: (Row) -> Boolean = { columns.filter(it, context) }

        val csvParser = CsvParser(errorHandling) { row ->
            val id: PersonId = row.id(columns.personOid)
            val p = context.personRepository.getById(id)
            val activityType = row.decode(
                columns.activityType,
                LegacyActivityType
            ) // TODO should not be hardcoded to LegacyActivityType!
            val zone = context.getZone(row.long(columns.zone))

            val location = zone + row(columns.location).parseRoadPosition()
            p?.let { person ->
                val acts = person.schedule.activities().filter { act -> act.type == activityType }
                acts.forEach { it.location = location }
            }
        }.withFilter(filterWrap)

        csvParser.parse(CsvReader.of(file, delimiter)).toList()
    }

    override fun verifyInput(): Warning? = validateScope {
        // TODO("Not yet implemented")
    }

    override fun mockBehavior(): Warning? = validateScope {
        // TODO("Not yet implemented")
    }
}
