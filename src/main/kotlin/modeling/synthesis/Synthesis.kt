package modeling.synthesis

import Builder
import CodePlan
import Identifiable
import domain.data.EMobilityPersonDataBuilder
import domain.data.EconomicStatus
import domain.data.Employment
import domain.data.Graduation
import domain.data.HouseholdData
import domain.data.HouseholdDataBuilder
import domain.data.PersonData
import domain.data.Sex
import domain.data.ZoneData
import domain.data.ZoneDataBuilder
import domain.enums.AreaType
import domain.enums.Bbsr17
import units.CurrencyUnit
import usecases.finishPersons
import usecases.loadHouseholds
import usecases.loadZones
import usecases.prepareEmobilityPersons
import utils.ErrorHandling
import java.io.File

interface SynthesisStep {
    //TODO rename to ModelStep
    val name: String
    fun execute()
    fun validate(): Boolean
}


open class PrepareResourceStep<B, E>(
    override val name: String,
    protected val resource: Resource<B>,
    protected val repository: BuilderRepository<B, E>,
) : SynthesisStep where E:Identifiable<E>, B: Builder<E> {

    override fun execute() {
        repository.prepare(resource)
    }

    override fun validate() = validatePrepareResourceStep(repository, resource, this)

}

open class PrepareCsvStep<B, E>(
    name: String,
    protected val csv: CsvResource<B>,
    repository: BuilderRepository<B, E>,
): PrepareResourceStep<B, E>(name, csv, repository) where E:Identifiable<E>, B: Builder<E>{

    override fun validate() = validatePrepareCsvStep(repository, csv, this)
}

open class InitializeResourceStep<E>(
    override val name: String,
    protected val resource: Resource<E>,
    protected val repository: LateInitRepository<E>
) : SynthesisStep where E: Identifiable<E> {

    override fun execute() {
        repository.initialize(resource)
    }

    override fun validate() = validateInitializeResourceStep(repository, resource, this)
}

class InitializeCsvStep<E>(
    name: String,
    protected val csv: CsvResource<E>,
    repository: LateInitRepository<E>
): InitializeResourceStep<E>(name, csv, repository) where E: Identifiable<E> {

    override fun validate() = validateInitializeCsvStep(repository, csv, this)
}

class FilterStep<B, E>(
    override val name: String,
    val repository: BuilderRepository<B, E>,
    val predicate: (B) -> Boolean,
) : SynthesisStep where B: Builder<E>, E: Identifiable<E> {

    override fun execute() {
        repository.reduce(name, predicate)
    }

    override fun validate() = validateFilterStep(repository, this)

}

class UpdateStep<B, E>(
    override val name: String,
    protected val repository: BuilderRepository<B, E>,
    protected val transformation: (B) -> B?,
) : SynthesisStep where B: Builder<E>, E: Identifiable<E> {

    override fun execute() {
        repository.update(name, transformation)
    }

    override fun validate() = validateUpdateStep(repository, this)

}

class BuildStep<B, E> (
    override val name: String,
    val repository: BuilderRepository<B, E>,
) : SynthesisStep where B: Builder<E>, E: Identifiable<E>{

    override fun execute() {
        repository.build()
    }

    override fun validate() = validateBuildStep(repository, this)
}

open class MultiStep(
    override val name: String,
    vararg steps: SynthesisStep,
): SynthesisStep {
    private val steps = steps.toMutableList()

    fun addStep(step: SynthesisStep) {
        steps.add(step)
    }

    override fun execute() = steps.forEach { it.execute() }

    override fun validate() = steps.all { step ->
        step.validate().also {
            println("Step ${step.name} is " + (if (it) "valid" else "invalid") + "!")
        }
    }

}

//TODO add merge and mergeBuilders step


class Synthesis<C>(
    val context: C
): MultiStep(context.scenarioName) where C: Context

interface Context {
    val scenarioName: String
    val demandFolder: File

    val currencyUnit: CurrencyUnit

    val areaTypeCodes: CodePlan<AreaType>
    val economicalStatusCodes: CodePlan<EconomicStatus>
    val employmentCodes: CodePlan<Employment>
    val graduationCodes: CodePlan<Graduation>
    val sexCodes: CodePlan<Sex>

    val zoneRepository: BuilderRepository<ZoneDataBuilder, ZoneData>
    val householdRepository: BuilderRepository<HouseholdDataBuilder, HouseholdData>
    val personRepository: BuilderRepository<EMobilityPersonDataBuilder, PersonData>


    fun reset() {
        zoneRepository.reset()
        householdRepository.reset()
        personRepository.reset()
    }

}


fun <C> C.synthesis(lambda: Synthesis<C>.() -> Unit): C where C: Context {
    println("Validate before run!")
    val dummy = Synthesis(this)
    dummy.lambda()
    val isValid = dummy.validate()


    if (isValid) {
        println("Execute")
        this.reset()
        val synth = Synthesis(this)
        synth.lambda()
        synth.execute()
        return this
    } else {
        error("validation failed")
    }

}

data class BaseContext(
    override val scenarioName: String,
    override val demandFolder: File,
    override val areaTypeCodes: CodePlan<AreaType> = Bbsr17,
    override val economicalStatusCodes: CodePlan<EconomicStatus> = EconomicStatus,
    override val sexCodes: CodePlan<Sex> = Sex,
    override val graduationCodes: CodePlan<Graduation> = Graduation,
    override val employmentCodes: CodePlan<Employment> = Employment,
    override val currencyUnit: CurrencyUnit = CurrencyUnit.EUROS,
) : Context {

    override val zoneRepository: BuilderRepository<ZoneDataBuilder, ZoneData>
        = BuilderRepository()

    override val householdRepository: BuilderRepository<HouseholdDataBuilder, HouseholdData>
        = BuilderRepository()

    override val personRepository: BuilderRepository<EMobilityPersonDataBuilder, PersonData>
        = BuilderRepository()
}

fun main() {

    val context = BaseContext(
        scenarioName = "testSteps",
        areaTypeCodes = Bbsr17,
        demandFolder = File(
    "\\\\ifv-fs\\Forschung\\Projekte_intern\\mobitopp\\Output\\logiktram_rastatt_long-term-module\\rastatt"
        ),
        economicalStatusCodes = EconomicStatus
    )


    context.synthesis {

        loadZones()
        loadHouseholds()
        prepareEmobilityPersons(errorHandling = ErrorHandling.THROW)
        finishPersons()

    }

    println("zones: " +
        context.zoneRepository.elements.count()
    )

    println("households: " +
            context.householdRepository.elements.count()
    )

    println("persons: " +
            context.personRepository.elements.count()
    )

}
