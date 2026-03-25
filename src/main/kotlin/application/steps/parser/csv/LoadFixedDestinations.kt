package application.steps.parser.csv

import core.modelsteps.BinaryCachedFileInput
import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.RepositoryDependentStep
import core.modelsteps.Warning
import core.modelsteps.validateFileReadAccess
import core.modelsteps.validateScope
import domain.shared.enums.ActivityType
import domain.shared.location.attributes.HasRoadAccess
import domain.shared.location.LegacyZone
import domain.shared.location.StandardLocation
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.shared.location.parseRoadPositionWGS
import domain.simulation.config.DemandSimContext
import domain.synthesis.data.ActivityId
import domain.synthesis.data.MutablePlannedActivity
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import domain.synthesis.parser.ActivityLocation
import domain.synthesis.parser.binary.FixedDestinationReader
import domain.synthesis.parser.binary.FixedDestinationWriter
import utils.CodePlan
import utils.ErrorHandling
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import utils.csv.CsvParser
import utils.csv.CsvReader
import utils.csv.FilterRowCsvParser
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

class FixedDestinationsBuilder(
    personConverter: (PersonId) -> Person?,
    activityTypeConverter: CodePlan<ActivityType>,
    zoneConverter: (ZoneId) -> Zone,
    val cacheRootPath: Path,
    val sourcePath: Path,
) {
    val reader = FixedDestinationReader(
        personConverter,
        activityTypeConverter,
        zoneConverter,
    )
    val writer = FixedDestinationWriter()

    lateinit var fallback: GenerateFixedDestinationLocations

    fun build(): GenerateFromCache {
        return GenerateFromCache(fallback, reader, writer, cacheRootPath, sourcePath)
    }
}

data class FixedDestinationColumns(
    val personOid: String = "personOid",
    val activityType: String = "activityType",
    val location: String = "location",
    val zone: String = "zoneId",
)

data class FixedDestinationCsvConfig(
    var path: Path,
    var errorHandling: ErrorHandling = ErrorHandling.WARNING,
    var columns: FixedDestinationColumns = FixedDestinationColumns(),
    var filter: FixedDestinationColumns.(Row, LoadFixedDestinationsContext) -> Boolean = { row, context ->
        PersonId(row.long(this.personOid)) in context.personRepository
    },
    var delimiter: String = SEMICOLON
)

fun LoadFixedDestinationsContext.fixedDestinations(
    homeActivity: ActivityType,
    cacheRootPath: Path = Path.of("data"),
    lambda: FixedDestinationsBuilder.() -> Unit,
) {
    val builder = FixedDestinationsBuilder(
        personRepository::get,
        activityTypes,
        zoneRepository::getValue,
        cacheRootPath,
        this.defaultFixedDestinationsPath
    )
    builder.apply(lambda)
    runStep {
        LoadFixedDestinationsStep(this, builder.build(), homeActivity)
    }
}
fun LoadFixedDestinationsContext.fromCSV(
    lambda: FixedDestinationCsvConfig.() -> Unit
): GenerateFixedDestinationLocations {
    val config = FixedDestinationCsvConfig(path = defaultFixedDestinationsPath)
    config.apply(lambda)
    return GenerateFromCSV(csvParser(config), config.path, config.delimiter)
}

fun LoadFixedDestinationsContext.csvParser(
    lambda: FixedDestinationCsvConfig.() -> Unit,
): FilterRowCsvParser<ActivityLocation> {
    val config = FixedDestinationCsvConfig(path = defaultFixedDestinationsPath)
    config.apply(lambda)
    return csvParser(config)
}

fun LoadFixedDestinationsContext.csvParser(
    csvConfig: FixedDestinationCsvConfig,
): FilterRowCsvParser<ActivityLocation> {
    return csvConfig.run {
        val filterWrap: (Row) -> Boolean = { columns.filter(it, this@csvParser) }

        CsvParser(errorHandling) { row ->
            val id = PersonId(row.long(columns.personOid))
            val p = personRepository[id]
            val activityType = row.decodeName(
                columns.activityType,
                activityTypes
            )
            val zone = getZone(row.long(columns.zone))

            val coordinate: HasRoadAccess = row(columns.location, String::parseRoadPositionWGS)
            val location = StandardLocation(coordinate.position, zone, coordinate.roadAccess)
            p?.let { person ->
                ActivityLocation(person, activityType, location)
            }
        }.withFilter(filterWrap)
    }
}

@Suppress("LongParameterList")
fun LoadFixedDestinationsContext.assignFixedDestinations(
    homeActivity: ActivityType,
    path: Path = defaultFixedDestinationsPath,
    errorHandling: ErrorHandling = ErrorHandling.WARNING,
    columns: FixedDestinationColumns = FixedDestinationColumns(),
    delimiter: String = SEMICOLON,
    filter: FixedDestinationColumns.(Row, LoadFixedDestinationsContext) -> Boolean = { _, _ -> true },
) {
    val csvParser = csvParser {
        this.path = path
        this.errorHandling = errorHandling
        this.columns = columns
        this.filter = filter
    }
    val generation = GenerateFromCSV(csvParser, path, delimiter)
    prepareFixedDestinationsFile(generation, homeActivity)
}

fun LoadFixedDestinationsContext.prepareFixedDestinationsFile(
    fixedDestinationGenerator: GenerateFixedDestinationLocations,
    homeActivity: ActivityType,
) = runStep {
    LoadFixedDestinationsStep(this, fixedDestinationGenerator, homeActivity)
}

fun interface GenerateFixedDestinationLocations {
    fun generate(): Collection<ActivityLocation>

    fun validate(): Warning? = null
}

class GenerateFromCSV(
    private val parser: CsvParser<ActivityLocation>,
    private val path: Path,
    private val delimiter: String,
) : GenerateFixedDestinationLocations {
    override fun generate(): Collection<ActivityLocation> {
        val reader = CsvReader.of(path, delimiter)
        return parser.parse(reader).toList()
    }

    override fun validate(): Warning? = validateFileReadAccess(path, fileDescription = "fixed destinations csv file")
}

class GenerateFromCache(
    val fallback: GenerateFixedDestinationLocations,
    binaryReader: BinaryReader<ActivityLocation>,
    binaryWriter: BinaryWriter<ActivityLocation>,
    cacheRootPath: Path,
    originalSourcePath: Path,
) :
    BinaryCachedFileInput<ActivityLocation>(
        binaryReader,
        binaryWriter,
        cacheRootPath,
        originalSourcePath,
    ), GenerateFixedDestinationLocations {
    override fun generate(): Collection<ActivityLocation> {
        return if (hasValidCacheEntry) {
            binaryReader.fromBinary(expectedCachePath)
        } else {
            runCached {
                generateElementsForCacheWrite()
            }
        }
    }

    override fun generateElementsForCacheWrite(): Collection<ActivityLocation> {
        return fallback.generate()
    }
}

class LoadFixedDestinationsStep(
    private val context: LoadFixedDestinationsContext,
    private val fixedDestinationGenerator: GenerateFixedDestinationLocations,
    private val homeActivity: ActivityType,

) : RepositoryDependentStep {
    override val name: String = "Load and assign fixed destinations in person schedules"

    override val repository: Repository<Person, PersonId> = context.personRepository
    override val dependentRepositories: Set<Repository<*, *>> = setOf(
        context.zoneRepository,
        context.plannedActivityRepository,
    )

    override fun execute() {
        // apply fixed destinations
        fixedDestinationGenerator.generate().forEach { (person, activityType, location) ->
            applyActivityLocation(person, activityType, location)
        }

        // apply home locations
        context.personRepository.elements.forEach { person ->
            applyActivityLocation(person, homeActivity, person.household.location)
        }
    }

    private fun applyActivityLocation(person: Person, activityType: ActivityType, location: StandardLocation) {
        person.plannedActivities.filter {
            it.activityType == activityType
        }.forEach {
            context.plannedActivityRepository.find(it.id)?.location = location
        }
    }

    override fun verifyInput(): Warning? = fixedDestinationGenerator.validate()

    override fun mockBehavior(): Warning? = validateScope {
        // TODO("Not yet implemented")
    }
}
