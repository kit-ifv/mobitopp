package usecases.steps

import domain.data.ActivityId
import domain.data.LegacyZone
import domain.data.MutablePlannedActivity
import domain.data.Person
import domain.data.PersonId
import domain.data.Zone
import domain.data.ZoneId
import domain.enums.ActivityType
import domain.location.Location
import domain.location.parseRoadPosition
import modeling.steps.Context
import modeling.steps.ModelStep
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.RepositoryDependentStep
import modeling.validation.Warning
import modeling.validation.validateScope
import utils.CodePlan
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.CsvReader
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.decodeName
import utils.csv.id
import utils.csv.long
import utils.csv.withFilter
import java.nio.file.Path

interface LoadFixedDestinationsContext : Context {
    val zoneRepository: Repository<Zone, ZoneId>
    val zoneColumnIndex: Map<Int, LegacyZone> // TODO legacy

    val personRepository: MutableRepository<out Person, PersonId>
    val plannedActivityRepository: MutableRepository<MutablePlannedActivity, ActivityId>

    val activityTypeCodes: CodePlan<ActivityType>

    val defaultFixedDestinationsPath: Path
        get() = demandFolder.resolve("demand-data").resolve("fixedDestination.csv")

    fun getZone(id: Long) = requireNotNull(
        zoneRepository[ZoneId(id)] ?: zoneColumnIndex[id.toInt()]
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

fun LoadFixedDestinationsContext.assignFixedDestinations(
    homeActivity: ActivityType,
    path: Path = defaultFixedDestinationsPath,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: FixedDestinationColumns = FixedDestinationColumns(),
    filter: FixedDestinationColumns.(Row, LoadFixedDestinationsContext) -> Boolean = { _, _ -> true }
) {
    val filterWrap: (Row) -> Boolean = { columns.filter(it, this) }

    val csvParser = CsvParser(errorHandling) { row ->
        val id: PersonId = row.id(columns.personOid)
        val p = personRepository[id]
        val activityType = row.decodeName(
            columns.activityType,
            activityTypeCodes
        )
        val zone = getZone(row.long(columns.zone))

        val location = row(columns.location, String::parseRoadPosition).withZone(zone)

        p?.let { person ->
            ActivityLocation(person, activityType, location)
        }
    }.withFilter(filterWrap)

    prepareFixedDestinationsFile(csvParser, homeActivity, path)
}

fun LoadFixedDestinationsContext.prepareFixedDestinationsFile(
    parser: CsvParser<ActivityLocation>,
    homeActivity: ActivityType,
    path: Path = defaultFixedDestinationsPath,
    delimiter: String = SEMICOLON,
) = runStep {
    LoadFixedDestinationsStep(this, parser, homeActivity, path, delimiter)
}

class LoadFixedDestinationsStep(
    private val context: LoadFixedDestinationsContext,
    private val parser: CsvParser<ActivityLocation>,
    private val homeActivity: ActivityType,
    private val path: Path = context.defaultFixedDestinationsPath,
    private val delimiter: String = SEMICOLON,
) : ModelStep, RepositoryDependentStep {
    override val name: String = "Load and assign fixed destinations in person schedules"

    override val repository: Repository<Person, PersonId> = context.personRepository
    override val dependentRepositories: Set<Repository<*, *>> = setOf(
        context.zoneRepository,
        context.plannedActivityRepository,
    )

    override fun execute() {
        val reader = CsvReader.of(path, delimiter)

        // apply fixed destinations
        parser.parse(reader).toList().forEach { (person, activityType, location) ->
            applyActivityLocation(person, activityType, location)
        }

        // apply home locations
        context.personRepository.elements.forEach { person ->
            applyActivityLocation(person, homeActivity, person.household.location)
        }
    }

    private fun applyActivityLocation(person: Person, activityType: ActivityType, location: Location) {
        person.plannedActivities.filter {
            it.activityType == activityType
        }.forEach {
            context.plannedActivityRepository.getById(it.id)?.location == location
        }
    }

    override fun verifyInput(): Warning? = validateScope {
        // TODO("Not yet implemented")
    }

    override fun mockBehavior(): Warning? = validateScope {
        // TODO("Not yet implemented")
    }
}
