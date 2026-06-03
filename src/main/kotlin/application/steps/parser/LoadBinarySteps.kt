package application.steps.parser

import application.config.ShortTermConfig
import application.steps.ActivityTypesConfig
import application.steps.HasCarRepo
import application.steps.HasDrtProviderRepo
import application.steps.HasHouseholdRepo
import application.steps.HasPersonRepo
import application.steps.HasSharingProviderRepo
import application.steps.HasZoneRepo
import application.steps.RegionCodesConfig
import application.steps.SourceFilesConfig
import core.modelsteps.Context
import core.modelsteps.resources.MutableRepository
import core.modelsteps.steps.forAllStep
import core.modelsteps.steps.loadBinary
import core.modelsteps.steps.writeBinary
import core.modelsteps.validation.validateFileReadAccess
import domain.shared.car.CarId
import domain.shared.location.parser.BinaryZoneReader
import domain.shared.location.parser.BinaryZoneWriter
import domain.shared.location.zone.MaximalZone
import domain.shared.location.zone.Zone
import domain.shared.location.zone.ZoneId
import domain.shared.location.zone.attributes.HasRegionType
import domain.simulation.data.ActivityId
import domain.simulation.data.MutablePlannedActivity
import domain.simulation.data.SharingProvider
import domain.simulation.data.car.MutablePrivateCar
import domain.simulation.data.car.PrivateCar
import domain.simulation.data.drt.DrtProvider
import domain.simulation.data.household.Household
import domain.simulation.data.household.HouseholdId
import domain.simulation.data.household.MutableHousehold
import domain.simulation.data.person.MutablePerson
import domain.simulation.data.person.Person
import domain.simulation.data.person.PersonId
import domain.simulation.parser.binary.BinaryActivityReader
import domain.simulation.parser.binary.BinaryActivityWriter
import domain.simulation.parser.binary.BinaryCarReader
import domain.simulation.parser.binary.BinaryCarWriter
import domain.simulation.parser.binary.BinaryHouseholdReader
import domain.simulation.parser.binary.BinaryHouseholdWriter
import domain.simulation.parser.binary.BinaryPersonReader
import domain.simulation.parser.binary.BinaryPersonWriter
import java.nio.file.Path

/**
 * Loads persons from a binary file.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement:
 *   - [HasHouseholdRepo] for [MutableHousehold]
 *   - [HasSharingProviderRepo] for [SharingProvider]
 *   - [HasDrtProviderRepo] for [DrtProvider]
 * @param repository The mutable repository of persons to populate. Provided via context.
 * @param config The short-term configuration. Provided via context. Must implement [ShortTermConfig].
 * @param path The path to the binary file.
 */
context(
    repository: MutableRepository<MutablePerson, PersonId>,
    config: ShortTermConfig<*>
)
fun <C> C.loadPersonsFromBinary(
    path: Path,
)
    where C : HasHouseholdRepo<MutableHousehold, *>,
          C : HasSharingProviderRepo<*, SharingProvider>,
          C : HasDrtProviderRepo<*, DrtProvider> {
    val converter = BinaryPersonReader(
        ::getMutableHousehold,
        ::getSharingProvider,
        ::getDrtProvider,
        config.seed,
    )

    loadBinary(
        path,
        converter,
        repository,
        dependentRepositories = setOf(householdRepository),
    )
}

/**
 * Loads households from a binary file.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasZoneRepo] for [Zone].
 * @param repository The mutable repository of households to populate. Provided via context.
 * @param config The short-term configuration. Provided via context. Must implement [ShortTermConfig].
 * @param path The path to the binary file.
 */
context(
    repository: MutableRepository<MutableHousehold, HouseholdId>,
    config: ShortTermConfig<*>
)
fun <C> C.loadHouseholdFromBinary(path: Path)
    where C : HasZoneRepo<*, Zone<HasRegionType>> {
    val converter = BinaryHouseholdReader(
        ::getZone,
        config.seed,
    )

    loadBinary(
        path,
        converter,
        repository,
        dependentRepositories = setOf(zoneRepository),
    )
}

/**
 * Loads zones from a binary file.
 *
 * @receiver The simulation context [Context].
 * @param CFG The configuration type. Must implement [RegionCodesConfig] and [SourceFilesConfig].
 * @param repository The mutable repository of zones to populate. Provided via context.
 * @param config The configuration. Provided via context.
 * @param path The path to the binary file.
 */
context(repository: MutableRepository<MaximalZone, ZoneId>, config: CFG)
fun <CFG> Context.loadZonesFromBinary(path: Path)
    where CFG : RegionCodesConfig, CFG : SourceFilesConfig {
    val converter = BinaryZoneReader(config.seed, config.regionTypeCodes)
    loadBinary(
        path,
        converter,
        repository,
        dependentRepositories = emptySet(),
    )
}

/**
 * Loads cars from a binary file.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement:
 *   - [HasHouseholdRepo] for [MutableHousehold]
 *   - [HasPersonRepo] for [domain.simulation.data.person.Person]
 * @param repository The mutable repository of private cars to populate. Provided via context.
 * @param path The path to the binary file.
 */
context(repository: MutableRepository<MutablePrivateCar, CarId>)
fun <C> C.loadCarsFromBinary(
    path: Path,
)
    where C : HasHouseholdRepo<MutableHousehold, *>,
          C : HasPersonRepo<*, Person> {
    val converter = BinaryCarReader(
        ::getMutableHousehold,
        ::getPerson,
    )

    loadBinary(
        path,
        converter,
        repository,
        dependentRepositories = emptySet(),
    )
}

/**
 * Loads activities from a binary file.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasPersonRepo] for [MutablePerson].
 * @param CFG The configuration type. Must implement [ShortTermConfig] and [ActivityTypesConfig].
 * @param repository The mutable repository of planned activities to populate. Provided via context.
 * @param config The configuration. Provided via context.
 * @param path The path to the binary file.
 */
context(repository: MutableRepository<MutablePlannedActivity, ActivityId>, config: CFG)
fun <C, CFG> C.loadActivitiesFromBinary(
    path: Path,
)
    where C : HasPersonRepo<MutablePerson, *>, CFG : ShortTermConfig<*>, CFG : ActivityTypesConfig {
    val converter = BinaryActivityReader(
        config.activityTypes,
        config.seed,
    )

    loadBinary(
        path,
        converter,
        repository,
        dependentRepositories = setOf(personRepository),
    )
}

/**
 * Writes households to a binary file.
 *
 * @param path The path to the output binary file.
 */
fun HasHouseholdRepo<*, Household>.writeHouseholdBinary(path: Path) = writeBinary(
    path = path,
    writer = BinaryHouseholdWriter(),
    repository = householdRepository,
)

/**
 * Writes zones to a binary file.
 *
 * @param path The path to the output binary file.
 */
fun HasZoneRepo<*, MaximalZone>.writeZoneBinary(path: Path) = writeBinary(
    path = path,
    writer = BinaryZoneWriter(),
    repository = zoneRepository,
)

/**
 * Writes persons to a binary file.
 *
 * @param path The path to the output binary file.
 */
fun HasPersonRepo<*, Person>.writePersonBinary(path: Path) = writeBinary(
    path = path,
    writer = BinaryPersonWriter(),
    repository = personRepository,
)

// TODO no longer use activity repository!

/**
 * Writes activities of all persons to a binary file.
 *
 * @param path The path to the output binary file.
 */
fun HasPersonRepo<*, Person>.writeActivitiesBinary(path: Path) = forAllStep(
    "write activities of persons tto binary ${path.fileName}",
    personRepository,
    emptySet(),
    validation = listOf { validateFileReadAccess(path, true, "binary cache file ${path.fileName}") },
) { elements ->
    BinaryActivityWriter().toBinary(
        path,
        elements.flatMap { it.plannedActivities },
    )
}

/**
 * Writes private cars to a binary file.
 *
 * @param path The path to the output binary file.
 */
fun HasCarRepo<*, PrivateCar>.writeCarsBinary(path: Path) = writeBinary(
    path = path,
    writer = BinaryCarWriter(),
    repository = carRepository,
)
