package utils.binary

import domain.data.Household
import domain.data.MutableHousehold
import domain.data.MutablePrivateCar
import domain.data.Person
import domain.data.PlannedActivity
import domain.data.Zone
import domain.enums.ActivityType
import modeling.steps.Repository
import usecases.steps.LoadPersonsContext
import java.nio.file.Path

fun LoadPersonsContext.writePersonToBinary(path: Path) {
    val map = householdRepository.elements.associateBy { it.id }
    runStep {
        WriteBinaryPerson()
    }
    this.addStep(
        WriteBinaryPerson(map, repository = context.personRepository, path, context.simulationSeed)
    )
}

class WriteBinaryPerson(
    val households: Map<HouseholdId, MutableHousehold>,
    override val repository: Repository<Person, PersonId>,
    val path: Path,
    val seed: Long,

    ) : ForAllStep<Person, PersonId> {
    override val name: String = "Convert Simulation Person to binary format"
    override val dependentRepositories: Set<Repository<*, *>> = emptySet()
    override fun process(element: Collection<Person>) {
        println("Converting ${element.size} persons to binary.")
        PersonConverter(households, seed).toBinary(path, element)
    }
}

fun <S> S.writeActivityToBinary(path: Path) where S : ModelExecution<MatsimContext> {
    val map = context.zoneRepository.elements.associateBy { it.id }
    val persons = context.personRepository.elements.associateBy { it.id }
    this.addStep(
        WriteBinaryActivity(
            map,
            repository = context.plannedActivityRepository,
            context.simulationSeed,
            context.activityTypeCodes,
            persons,
            path
        )
    )
}

class WriteBinaryActivity(
    val zones: Map<ZoneId, Zone>,
    override val repository: Repository<PlannedActivity, ActivityId>,
    val contextSimulationSeed: Long,
    val activityPlan: CodePlan<ActivityType>,
    val personConverter: Map<PersonId, Person>,
    val path: Path
) : ForAllStep<PlannedActivity, ActivityId> {

    override val dependentRepositories: Set<Repository<*, *>> = emptySet()

    override val name: String = "Write Households to binary"

    override fun process(element: Collection<PlannedActivity>) {
        println("Converting ${element.size} planned activities to binary.")
        ActivityConverter(activityPlan, personConverter::getValue, contextSimulationSeed).toBinary(path, element)
    }

}

fun <S> S.writeCarToBinary(path: Path) where S : ModelExecution<MatsimContext> {
    val map = context.zoneRepository.elements.associateBy { it.id }
    val personConverter = context.personRepository.elements.associateBy { it.id }
    val householdConverter = context.householdRepository.elements.associateBy { it.id }
    this.addStep(
        WriteBinaryCars(map, repository = context.carRepository, personConverter, householdConverter, path)
    )
}

class WriteBinaryCars(
    val zones: Map<ZoneId, Zone>,
    override val repository: Repository<MutablePrivateCar, CarId>,
    val personConverter: Map<PersonId, Person>,
    val householdConverter: Map<HouseholdId, MutableHousehold>,
    val path: Path
) : ForAllStep<MutablePrivateCar, CarId> {

    override val dependentRepositories: Set<Repository<*, *>> = emptySet()

    override val name: String = "Write Households to binary"

    override fun process(element: Collection<MutablePrivateCar>) {
        println("Converting ${element.size} cars to binary.")
        CarConverter(householdConverter::getValue, personConverter::getValue).toBinary(path, element)
    }

}

fun <S> S.writeHouseholdToBinary(path: Path) where S : ModelExecution<MatsimContext> {
    val map = context.zoneRepository.elements.associateBy { it.id }
    this.addStep(
        WriteBinaryHousehold(map, repository = context.householdRepository, context.simulationSeed, path)
    )
}

class WriteBinaryHousehold(
    val zones: Map<ZoneId, Zone>,
    override val repository: Repository<Household, HouseholdId>,
    val contextSimulationSeed: Long,
    val path: Path
) : ForAllStep<Household, HouseholdId> {

    override val dependentRepositories: Set<Repository<*, *>> = emptySet()

    override val name: String = "Write Households to binary"

    override fun process(element: Collection<Household>) {
        println("Converting ${element.size} households to binary.")
        HouseholdConverter(zones, contextSimulationSeed).toBinary(path, element)
    }

}