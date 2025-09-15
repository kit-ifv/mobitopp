package application.steps.parser.csv

import core.modelsteps.ModelStep
import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.RepositoryDependentStep
import core.modelsteps.Warning
import core.modelsteps.validateScope
import domain.shared.enums.ActivityType
import domain.shared.location.LegacyZone
import domain.shared.location.Location
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.shared.location.parseRoadPosition
import domain.simulation.config.DemandSimContext
import domain.synthesis.data.ActivityId
import domain.synthesis.data.MutablePlannedActivity
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import domain.synthesis.parser.ActivityLocation
import utils.ErrorHandling
import utils.csv.CsvParser
import utils.csv.CsvReader
import utils.csv.Row
import utils.csv.SEMICOLON
import utils.csv.decodeName
import utils.csv.long
import utils.csv.withFilter
import java.nio.file.Path

interface LoadFixedDestinationsContext : DemandSimContext {
    val zoneRepository: Repository<Zone, ZoneId>
    val zoneColumnIndex: Map<Int, LegacyZone> // TODO legacy

    val personRepository: MutableRepository<out Person, PersonId>
    val plannedActivityRepository: MutableRepository<MutablePlannedActivity, ActivityId>

    val defaultFixedDestinationsPath: Path
        get() = dataFolder.resolve("demand-data").resolve("fixedDestination.csv")

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

fun LoadFixedDestinationsContext.assignFixedDestinations(
    homeActivity: ActivityType,
    path: Path = defaultFixedDestinationsPath,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: FixedDestinationColumns = FixedDestinationColumns(),
    filter: FixedDestinationColumns.(Row, LoadFixedDestinationsContext) -> Boolean = { _, _ -> true }
) {
    val filterWrap: (Row) -> Boolean = { columns.filter(it, this) }

    val csvParser = CsvParser(errorHandling) { row ->
        val id = PersonId(row.long(columns.personOid))
        val p = personRepository[id]
        val activityType = row.decodeName(
            columns.activityType,
            activityTypes
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
            context.plannedActivityRepository.find(it.id)?.location == location
        }
    }

    override fun verifyInput(): Warning? = validateScope {
        // TODO("Not yet implemented")
    }

    override fun mockBehavior(): Warning? = validateScope {
        // TODO("Not yet implemented")
    }
}
