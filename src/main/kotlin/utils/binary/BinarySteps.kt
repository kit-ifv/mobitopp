package utils.binary

import modeling.steps.AddResourceStep
import modeling.steps.ForAllStep
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.Resource
import modeling.steps.asResource
import modeling.validation.Warning
import usecases.steps.LoadPersonsContext
import usecases.steps.LoadPlannedActivitiesContext
import usecases.steps.legacyData.LoadHouseholdContext
import usecases.steps.legacyData.LoadPrivateCarsContext
import usecases.steps.legacyData.LoadZonesContext
import utils.Identifiable
import java.nio.file.Path
import kotlin.io.path.name

fun LoadPersonsContext.loadPersonsFromBinary(path: Path) {
    val converter = BinaryPersonReader(householdRepository.elements.associateBy { it.id }::getValue, simulationSeed)
    runStep {
        LoadBinaryStep(
            path,
            parser = converter,
            repository = personRepository,
            dependentRepositories = setOf(householdRepository)
        )
    }
}

fun LoadHouseholdContext.loadHouseholdFromBinary(path: Path) {
    runStep {
        val converter = BinaryHouseholdReader(zoneRepository.elements.associateBy { it.id }::getValue, simulationSeed)
        LoadBinaryStep(path, converter, householdRepository, setOf(zoneRepository))
    }
}

fun LoadZonesContext.loadZonesFromBinary(path: Path) {
    val converter = BinaryZoneReader(simulationSeed, areaTypeCodes)
    runStep {
        LoadBinaryStep(path, converter, zoneRepository, emptySet())
    }
}

fun LoadPrivateCarsContext.loadCarsFromBinary(path: Path) {
    val converter = BinaryCarReader(
        householdRepository.elements.associateBy { it.id }::getValue,
        personRepository.elements.associateBy { it.id }::getValue,
    ) {
        it.owner.location
    }
    runStep {
        LoadBinaryStep(path, converter, carRepository, emptySet())
    }
}

fun LoadPlannedActivitiesContext.loadActivitiesFromBinary(path: Path) {
    val converter = BinaryActivityReader(
        activityTypeCodes,
        { personRepository.getById(it) ?: throw NoSuchElementException("No person of id $it in personRepository") },
        simulationSeed
    )
    runStep {
        LoadBinaryStep(path, converter, plannedActivityRepository, setOf(personRepository))
    }
}

class WriteBinaryStep<READONLY : Identifiable<ID>, ID>(
    val path: Path,
    val writer: BinaryWriter<READONLY>,
    override val repository: Repository<READONLY, ID>,

) : ForAllStep<READONLY, ID>() {
    override val name: String = "Write Binary ${repository.name}"
    override val dependentRepositories: Set<Repository<*, *>> =
        emptySet() // There is no need for dependent repositories, the objects are already there

    override fun processAll(element: Collection<READONLY>) {
        writer.toBinary(path, element)
    }

    override fun verifyInput(): Warning? {
        return null // TODO("Not yet implemented")
    }

    override fun mockBehavior(): Warning? {
        return null
    }
}

class LoadBinaryStep<MUTABLE : Identifiable<ID>, ID>(
    path: Path,
    parser: BinaryReader<MUTABLE>,
    override val repository: MutableRepository<MUTABLE, ID>,
    override val dependentRepositories: Set<Repository<*, *>>,

) : AddResourceStep<MUTABLE, ID>() {

    override val name: String = "load ${path.fileName}"
    override val resource: Resource<MUTABLE> = parser.fromBinary(path).asResource(name, path.name)

    override fun mockElementsForValidation(): List<MUTABLE> {
        return emptyList() // TODO I WILL NOT WRTIE A BINARY FILE ON MY OWN
    }

    override fun verifyInput(): Warning? {
        return null // TODO some reasonable validation.
    }
}

/* TODO There is no reason to require the LoadHouseholdContext or any other of the predefined context, but sadly writing
     a readonly interface also requires adding the interface to the underlying context, as the interfaces do not specify
     what they require. The correct procedure would be i.e. that LoadHouseholdContext is a : ReadonlyZonesContext,
     MutableHouseholdContext, etc. If that would be the case, this extension method could be built upon a readonly
     household context. Which would be better, because you can write a household repository to binary, even if your
     context does not fulfill LoadHouseholdContext because Zones are missing (or sth else)
 */

fun LoadHouseholdContext.writeHouseholdBinary(path: Path) {
    runStep {
        WriteBinaryStep(path, BinaryHouseholdWriter(), householdRepository)
    }
}

fun LoadZonesContext.writeZonesBinary(path: Path) {
    runStep {
        WriteBinaryStep(path, BinaryZoneWriter(), zoneRepository)
    }
}

fun LoadPersonsContext.writePersonsBinary(path: Path) {
    runStep {
        WriteBinaryStep(path, BinaryPersonWriter(), personRepository)
    }
}

fun LoadPlannedActivitiesContext.writeActivitiesBinary(path: Path) {
    runStep {
        WriteBinaryStep(path, BinaryActivityWriter(), plannedActivityRepository)
    }
}

fun LoadPrivateCarsContext.writeCarsBinary(path: Path) {
    runStep {
        WriteBinaryStep(path, BinaryCarWriter(), carRepository)
    }
}
