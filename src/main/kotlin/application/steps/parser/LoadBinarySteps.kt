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
import domain.shared.location.MutableZone
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.synthesis.data.ActivityId
import domain.synthesis.data.CarId
import domain.synthesis.data.DrtProvider
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutableHousehold
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.MutablePlannedActivity
import domain.synthesis.data.MutablePrivateCar
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import domain.synthesis.data.PrivateCar
import domain.synthesis.data.SharingProvider
import domain.synthesis.parser.binary.BinaryActivityReader
import domain.synthesis.parser.binary.BinaryActivityWriter
import domain.synthesis.parser.binary.BinaryCarReader
import domain.synthesis.parser.binary.BinaryCarWriter
import domain.synthesis.parser.binary.BinaryHouseholdReader
import domain.synthesis.parser.binary.BinaryHouseholdWriter
import domain.synthesis.parser.binary.BinaryPersonReader
import domain.synthesis.parser.binary.BinaryPersonWriter
import domain.synthesis.parser.binary.BinaryZoneReader
import domain.synthesis.parser.binary.BinaryZoneWriter
import java.nio.file.Path

context(repository: MutableRepository<MutablePerson, PersonId>, config: ShortTermConfig<*>)
fun <C> C.loadPersonsFromBinary(path: Path)
where C: HasHouseholdRepo<MutableHousehold>,
      C: HasSharingProviderRepo<SharingProvider>,
      C: HasDrtProviderRepo<DrtProvider>
{
    val converter = BinaryPersonReader(
        householdRepository.elements.associateBy { it.id }::getValue,
        sharingProviderRepository.elements.associateBy { it.id }::getValue,
        drtProviderRepository.elements.associateBy { it.id }::getValue,
        config.seed
    )

    loadBinary(
        path, converter, repository,
        dependentRepositories = setOf(householdRepository)
    )
}

context(repository: MutableRepository<MutableHousehold, HouseholdId>, config: ShortTermConfig<*>)
fun <C> C.loadHouseholdFromBinary(path: Path)
where C: HasZoneRepo<Zone>
{
    val converter = BinaryHouseholdReader(
        zoneRepository.elements.associateBy { it.id }::getValue,
        config.seed
    )

    loadBinary(
        path, converter, repository,
        dependentRepositories = setOf(zoneRepository)
    )
}

context(repository: MutableRepository<MutableZone, ZoneId>, config: CFG)
fun <CFG> Context.loadZonesFromBinary(path: Path)
where CFG: RegionCodesConfig, CFG: SourceFilesConfig
{
    val converter = BinaryZoneReader(config.seed, config.regionTypeCodes)
    loadBinary(
        path, converter, repository,
        dependentRepositories = emptySet()
    )
}

context(repository: MutableRepository<MutablePrivateCar, CarId>)
fun <C> C.loadCarsFromBinary(path: Path)
where C: HasHouseholdRepo<MutableHousehold>,
      C: HasPersonRepo<Person>
{
    val converter = BinaryCarReader(
        householdRepository.elements.associateBy { it.id }::getValue,
        personRepository.elements.associateBy { it.id }::getValue,
    )

    loadBinary(path, converter, repository,
        dependentRepositories = emptySet()
    )
}

context(repository: MutableRepository<MutablePlannedActivity, ActivityId>, config: CFG)
fun <C, CFG> C.loadActivitiesFromBinary(path: Path)
where C: HasPersonRepo<MutablePerson>, CFG: ShortTermConfig<*>, CFG: ActivityTypesConfig //TODO mutable person required
{
    val converter = BinaryActivityReader(
        config.activityTypes,
        ::getPerson,
        config.seed
    )

    loadBinary(
        path, converter, repository,
        dependentRepositories = setOf(personRepository)
    )
}

fun HasHouseholdRepo<Household>.writeHouseholdBinary(path: Path) = writeBinary(
    path = path,
    writer = BinaryHouseholdWriter(),
    repository = householdRepository
)

fun HasZoneRepo<Zone>.writeZoneBinary(path: Path) = writeBinary(
    path = path,
    writer = BinaryZoneWriter(),
    repository = zoneRepository
)

fun HasPersonRepo<Person>.writePersonBinary(path: Path) = writeBinary(
    path = path,
    writer = BinaryPersonWriter(),
    repository = personRepository
)


//TODO no longer use activity repository!

fun HasPersonRepo<Person>.writeActivitiesBinary(path: Path) = forAllStep(
    "write activities of persons tto binary ${path.fileName}",
    personRepository,
    emptySet(),
    validation = listOf { validateFileReadAccess(path, true, "binary cache file ${path.fileName}") }
) { elements ->
    BinaryActivityWriter().toBinary(path, elements.flatMap { it.plannedActivities })
}

fun HasCarRepo<PrivateCar>.writePersonBinary(path: Path) = writeBinary(
    path = path,
    writer = BinaryCarWriter(),
    repository = carRepo
)