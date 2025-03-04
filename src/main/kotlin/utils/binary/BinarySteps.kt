package utils.binary

import domain.data.Household
import domain.data.HouseholdId
import domain.data.Person
import domain.data.PersonId
import domain.data.Zone
import domain.data.ZoneId
import modeling.steps.AddResourceStep
import modeling.steps.Context
import modeling.steps.ForAllStep
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.Resource
import modeling.steps.asResource
import modeling.validation.Warning
import usecases.steps.LoadPersonsContext
import usecases.steps.LoadPlannedActivitiesContext
import usecases.steps.legacyData.LoadHouseholdContext
import utils.Identifiable
import java.nio.file.Path
import kotlin.io.path.name

fun LoadPersonsContext.loadPersonFromBinary(path: Path) {
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
    val converter = BinaryHouseholdReader(zoneRepository.elements.associateBy { it.id }::getValue, simulationSeed)
    runStep {
        LoadBinaryStep(path, converter, householdRepository, setOf(zoneRepository))
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

//fun LoadFixedDestinationsContext.loadFixedDestinationsFromBinary(path: Path) {
//    val converter = FixedDestinationReader(
//        { personRepository.getById(it) ?: throw NoSuchElementException("No person of id $it in personRepository") },
//        activityTypeCodes,
//        { zoneRepository.getById(it) ?: throw NoSuchElementException("No zone of id $it in repository") }
//    )
//    runStep {
//        object : UpdateEachStep<Person, PersonId>() {
//            val fixedDestinations = converter.fromBinary(path).groupBy { it.person }
//            override fun update(element: Person) {
//                val activities = element.schedule.activities()
//                fixedDestinations[element]?.let { entry ->
//                    entry.forEach { actLoc ->
//                        activities.filter { act -> act.type == actLoc.activityType }.forEach {
//                            it.location = actLoc.location
//                        }
//                    }
//                }
//            }
//
//            override val repository: MutableRepository<Person, PersonId> = personRepository
//            override val dependentRepositories: Set<Repository<*, *>> = emptySet()
//
//            override val name: String = "I hate the step system"
//
//            override fun verifyInput(): Warning? {
//                return null //TODO("Not yet implemented")
//            }
//
//        }
//    }
//}

class WriteBinaryStep<READONLY : Identifiable<ID>, ID>(
    val path: Path,
    val writer: BinaryWriter<READONLY>,
    override val repository: Repository<READONLY, ID>,
    override val dependentRepositories: Set<Repository<*, *>>,
) : ForAllStep<READONLY, ID>() {
    override val name: String = "Write Binary"

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
        return emptyList() //TODO I WILL NOT WRTIE A BINARY FILE ON MY OWN
    }

    override fun verifyInput(): Warning? {
        return null // TODO some reasonable validation.
    }

}

interface ReadonlyPersonContext : Context {
    val personRepository: Repository<Person, PersonId>

}

fun ReadonlyPersonContext.writePersonBinary(path: Path) {

    runStep {
        WriteBinaryStep<Person, PersonId>(path, BinaryPersonWriter(), personRepository, emptySet())
    }

}

interface ReadonlyHouseholdContext: Context {
    val householdRepository: Repository<Household, HouseholdId>
}

fun ReadonlyHouseholdContext.writeHouseholdBinary(path: Path) {
    runStep {
        WriteBinaryStep(path, BinaryHouseholdWriter(), householdRepository, emptySet())
    }
}
interface ReadonlyZoneContext: Context {
    val zoneRepository: Repository<Zone, ZoneId>
}
fun ReadonlyZoneContext.writeZonesBinary(path: Path) {
    runStep {
        WriteBinaryStep(path, BinaryZoneWriter(), zoneRepository, emptySet())
    }
}