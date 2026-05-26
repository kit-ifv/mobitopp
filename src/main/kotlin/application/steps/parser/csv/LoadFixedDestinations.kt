package application.steps.parser.csv

import application.steps.ActivityTypesConfig
import application.steps.HasPersonRepo
import application.steps.HasZoneRepo
import application.steps.SourceFilesConfig
import core.modelsteps.resources.BinaryCacheConfig
import core.modelsteps.resources.CsvResource
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Resource
import core.modelsteps.resources.cachedCsv
import core.modelsteps.scopes.updateEachStep
import core.modelsteps.steps.modelStep
import domain.shared.enums.ActivityType
import domain.shared.location.Zone
import domain.synthesis.data.ActivityId
import domain.synthesis.data.HasHousehold
import domain.synthesis.data.HasStandardLocation
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutablePlannedActivity
import domain.synthesis.data.PersonId
import domain.synthesis.parser.ActivityLocation
import domain.synthesis.parser.FixedDestinationColumns
import domain.synthesis.parser.FixedDestinationCsvConfig
import domain.synthesis.parser.binary.FixedDestinationReader
import domain.synthesis.parser.binary.FixedDestinationWriter
import domain.synthesis.parser.createFixedDestinationCsvParser
import utils.Identifiable
import utils.csv.CsvParser
import java.nio.file.Path

/**
 * Applies fixed destinations to planned activities of persons.
 *
 * This step loads fixed destination data from a [resource] and updates the locations
 * of matching planned activities. It also sets the location for home activities
 * based on the person's household location.
 *
 * @receiver The simulation context [CTXT].
 * @param CTXT The context type. Must implement [HasZoneRepo] for [Zone] and [HasPersonRepo].
 * @param CFG The configuration type. Must implement [ActivityTypesConfig] and [SourceFilesConfig].
 * @param P The person type. Must implement [Identifiable] for [PersonId] and [HasHousehold].
 * @param H The household type. Must implement [Identifiable] for [HouseholdId] and [HasStandardLocation].
 * @param repository The mutable repository of persons. Provided via context.
 * @param activityRepo The mutable repository of planned activities to update. Provided via context.
 * @param config The configuration. Provided via context.
 * @param homeActivity The [ActivityType] that represents being at home.
 * @param resource The resource containing fixed destination data. Defaults to [fixedDestinationCsv].
 */
context(repository: MutableRepository<P, PersonId>, activityRepo: MutableRepository<MutablePlannedActivity, ActivityId>, config: CFG)
fun <CTXT, CFG, P, H> CTXT.fixedDestinations(
    homeActivity: ActivityType,
    resource: Resource<ActivityLocation> = fixedDestinationCsv()
) where CTXT : HasZoneRepo<*, Zone>, CTXT : HasPersonRepo<*, *>, // TODO unify HasPersonRepo with P
      P : Identifiable<PersonId>, P : HasHousehold<H>,
      H : Identifiable<HouseholdId>, H : HasStandardLocation,
      CFG : ActivityTypesConfig, CFG : SourceFilesConfig {
    val fixedLocationsById: MutableMap<PersonId, Map<ActivityType, ActivityLocation>> = mutableMapOf()
    modelStep("load fixed destination csv") {
        fixedLocationsById.putAll(
            resource.elements.groupBy {
                it.personId
            }.mapValues { (_, locations) ->
                locations.associateBy { it.activityType }
            }
        )
    }

    updateEachStep<CTXT, MutablePlannedActivity, ActivityId>(
        name = "apply fixed destinations to planned activities",
    ) { activity ->

        fixedLocationsById[activity.person]?.get(activity.activityType)?.also {
            activity.location = it.location
        }

        if (activity.activityType == homeActivity) {
            val household = repository[activity.person]!!.household
            activity.location = household.location
        }
    }
}

/**
 * Creates a CSV resource for fixed destinations.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasPersonRepo] and [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SourceFilesConfig] and [ActivityTypesConfig].
 * @param config The configuration. Provided via context.
 * @param parser The CSV parser for fixed destinations. Defaults to [fixedDestinationCsvParser].
 * @param path The path to the fixed destination CSV file. Defaults to [config.sourceFiles.fixedDestinationCSV].
 * @param delimiter The CSV delimiter. Defaults to [config.sourceFiles.defaultCsvDelimiter].
 * @param binaryCache Optional configuration for binary caching. Defaults to [binaryFixedDestinationFormat].
 * @return A [Resource] representing the fixed destination CSV.
 */
context(config: CFG)
fun <C, CFG> C.fixedDestinationCsv(
    parser: CsvParser<ActivityLocation> = fixedDestinationCsvParser(),
    path: Path = config.sourceFiles.fixedDestinationCSV,
    delimiter: String = config.sourceFiles.defaultCsvDelimiter,
    binaryCache: BinaryCacheConfig<ActivityLocation>? = binaryFixedDestinationFormat() // TODO move binary format to load level?
): Resource<ActivityLocation> where C : HasPersonRepo<*, *>, C : HasZoneRepo<*, Zone>, CFG : SourceFilesConfig, CFG : ActivityTypesConfig =
    CsvResource(path, parser, delimiter).let { csv ->
        binaryCache?.let {
            csv.cachedCsv(it)
        } ?: csv
    } // TODO add csv validation

/**
 * Creates a CSV parser for fixed destinations.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasPersonRepo] and [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [ActivityTypesConfig].
 * @param config The configuration. Provided via context.
 * @param customizeCsvConfig Lambda to customize the [FixedDestinationCsvConfig].
 * @return A [CsvParser] for [ActivityLocation].
 */
context(config: CFG)
fun <C, CFG> C.fixedDestinationCsvParser(
    customizeCsvConfig: FixedDestinationCsvConfig.() -> Unit = {}
): CsvParser<ActivityLocation> where C : HasPersonRepo<*, *>, C : HasZoneRepo<*, Zone>, CFG : ActivityTypesConfig =
    createFixedDestinationCsvParser(
        FixedDestinationCsvConfig(
            columns = FixedDestinationColumns(),
            personsExists = personRepository::contains,
            activityTypes = config.activityTypes,
            zoneConverter = ::getZone,
            errorHandling = config.errorHandling,
        ).also {
            it.customizeCsvConfig()
        }
    )

/**
 * Creates a binary cache configuration for fixed destinations.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param CFG The configuration type. Must implement [SourceFilesConfig] and [ActivityTypesConfig].
 * @param config The configuration. Provided via context.
 * @return A [BinaryCacheConfig] instance.
 */
context(config: CFG)
fun <C, CFG> C.binaryFixedDestinationFormat(): BinaryCacheConfig<ActivityLocation>
    where C : HasZoneRepo<*, Zone>, CFG : SourceFilesConfig, CFG : ActivityTypesConfig {
    return BinaryCacheConfig<ActivityLocation>(
        cacheRootPath = config.cachePath,
        binaryReader = FixedDestinationReader(
            activityTypeConverter = config.activityTypes,
            zoneConverter = zoneRepository::getValue,
        ),

        binaryWriter = FixedDestinationWriter()
    )
}
