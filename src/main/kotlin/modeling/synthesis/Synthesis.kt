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
import usecases.finishPersons
import usecases.loadHouseholds
import usecases.loadZones
import usecases.prepareEmobilityPersons
import utils.ErrorHandling
import utils.units.CurrencyUnits
import java.io.File

interface SynthesisStep { // TODO: rename to ModelStep
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


class Synthesis<C>(
    val context: C
): MultiStep(context.scenarioName) where C: Context

interface Context {
    val scenarioName: String
    val demandFolder: File

    val currencyUnit: CurrencyUnits

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
    override val currencyUnit: CurrencyUnits = CurrencyUnits.EUROS,
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






//class Synthesis<C>(
//    val context: C
//) where C: Context {
//    // Todo : think about executing steps immediately instead of collecting all steps and then executing them
//    // Todo : pro collect: validation could be performed before execution
//    private val steps: MutableList<SynthesisStep> = mutableListOf()
//
//    fun addStep(step: SynthesisStep) = steps.add(step)
//
//    fun execute() {
//        steps.forEach{ s -> s.execute()}
//    }
//
//    operator fun invoke(lambda: Synthesis<C>.() -> Unit) {
//        this.apply { lambda() }
//    }
//
////    fun <E> addResource(
////        name: String,
////        resource: Resource<E>,
////        setter: (C, MutableRepository<E>) -> Unit
////    ): Synthesis<C> {
////        steps.add(InitResource(name, resource, setter))
////        return this
////    }
////
////    fun <E> addFinalResource(
////        name: String,
////        resource: Resource<E>,
////        setter: (C, Repository<E>) -> Unit
////    ): Synthesis<C> {
////        steps.add(InitializeResourceStep(name, resource, setter))
////        return this
////    }
////
////    fun <E> addIdResource(
////        name: String,
////        resource: Resource<E>,
////        setter: (C, MutableIdRepository<E>) -> Unit
////    ): Synthesis<C> where E: Identifiable<E> {
////        steps.add(InitIdResource(name, resource, setter))
////        return this
////    }
////
////    fun <E> addFinalIdResource(
////        name: String,
////        resource: Resource<E>,
////        setter: (C, IdRepository<E>) -> Unit
////    ): Synthesis<C> where E: Identifiable<E> {
////        steps.add(FinalIdResource(name, resource, setter))
////        return this
////    }
////
////    fun <E> addUpdate(
////        name: String,
////        transformation: (E) -> E?,
////        getter: (C) -> MutableRepository<E>?
////    ): Synthesis<C> {
////        steps.add(Update(name, transformation, wrapGetter(name, getter)))
////        return this
////    }
////
////    fun <B, E> addFinishStep(
////        name: String,
////        getter: (C) -> MutableRepository<B>?,
////        setter: (C, Repository<E>) -> Unit
////    ): Synthesis<C> where B: Builder<E> {
////        steps.add(BuildStep(name, wrapGetter(name, getter), setter))
////        return this
////    }
////
////    private fun <R> wrapGetter(
////        step: String,
////        getter: (C) -> R?
////    ): (C) -> R = {
////        context ->
////        requireNotNull(getter(context)) {
////            "Update step [$step] is not applicable since the required resource[$getter] has not been initialized."
////        }
////    }
//
//}
//interface Context {
//    val name: String
//}
//
//fun <C> C.synthesis(lambda: Synthesis<C>.() -> Unit): C where C: Context {
//    val synth = Synthesis<C>(this)
//    synth.lambda()
//    synth.execute(this)
//    return this
//}
//
//
//interface BaseContext : Context {
//    val demandFolder: File
//    val areaTypCodes: CodePlan<AreaType>
//    val economicalStatusCodes: CodePlan<EconomicStatus>
//
//
//    val zones: IdRepository<ZoneData>
//    var zoneBuilders: MutableIdRepository<ZoneDataBuilder>?
//
//    val households: IdRepository<HouseholdData>
//    var householdBuilders: MutableIdRepository<HouseholdDataBuilder>?
//
////    val persons: IdRepository<PersonData>
////    var personBuilders: MutableIdRepository<EMobilityPersonDataBuilder>?
//
////    var households: IdRepository<HouseholdData>
////    var cars: IdRepository<CarData>
////    var persons: IdRepository<PersonData>
////    var opportunities: IdRepository<OpportunityData>
//}
//
//class ExampleContext(override val name: String,
//                     override val demandFolder: File,
//                     override val areaTypCodes: CodePlan<AreaType> = ZoneAreaType,
//                     override val economicalStatusCodes: CodePlan<EconomicStatus> = EconomicStatus
//) : BaseContext {
//    override val zones: IdRepository<ZoneData>
//        get() = finishedZones ?: (
//               checkNotNull(zoneBuilders) {"Zone data builders has not yet been initialized or was already finished!"}
//                    .build()
//                    .also {
//                        finishedZones = it
//                        zoneBuilders = null
//                    }
//                )
//    override var zoneBuilders: MutableIdRepository<ZoneDataBuilder>? = null
//        set(value)  {
//            require((finishedZones == null) and (zoneBuilders == null) or (value == null)) {
//                "Cannot initialize zoneBuilders again, since zone were already initialized or finished!"
//            }
//
//            field = value
//        }
//
//    var finishedZones: IdRepository<ZoneData>? = null
//
//
//    //TODO reduce boilerplate/redundancy by refactoring mutable repository maybe new buildable repository
//    override val households: IdRepository<HouseholdData>
//        get() = finishedHouseholds ?: (
//                checkNotNull(householdBuilders) {
//                    "Household data builders has not yet been initialized or was already finished!"
//                }
//                    .build()
//                    .also {
//                        finishedHouseholds = it
//                        householdBuilders = null
//                    }
//                )
//    override var householdBuilders: MutableIdRepository<HouseholdDataBuilder>? = null
//        set(value)  {
//            require((finishedHouseholds == null) and (householdBuilders == null) or (value == null)) {
//                "Cannot initialize householdBuilders again, since zone were already initialized or finished!"
//            }
//
//            field = value
//        }
//
//    var finishedHouseholds: IdRepository<HouseholdData>? = null
//
////
////    //TODO reduce boilerplate/redundancy by refactoring mutable repository maybe new buildable repository
////    override val person: IdRepository<EMobilityPersonData>
////        get() = finishedPersons ?: (
////                checkNotNull(personBuilders) {
////                    "Person data builders has not yet been initialized or was already finished!"
////                }
////                    .build()
////                    .also {
////                        finishedPersons = it
////                        personBuilders = null
////                    }
////                )
////    override var personBuilders: MutableIdRepository<HouseholdDataBuilder>? = null
////        set(value)  {
////            require((finishedHouseholds == null) and (householdBuilders == null) or (value == null)) {
////                "Cannot initialize householdBuilders again, since zone were already initialized or finished!"
////            }
////
////            field = value
////        }
////
////    var finishedPersons: IdRepository<EMobilityPersonData>? = null
//
//}
//
//fun main() {
//    val context = ExampleContext(
//        name="test",
//        areaTypCodes = ZoneAreaType,
//        demandFolder = File(
//"\\\\ifv-fs\\Forschung\\Projekte_intern\\mobitopp\\Output\\logiktram_rastatt_long-term-module\\rastatt"
//        )
//    ).synthesis {
//
//        loadZoneCsv(
//            zoneParser(
//                idColumn = "id"
//            )
//        )
//
//        loadHouseholdCsv(
//            householdParser(
//                incomeUnit = CurrencyUnits.EUROS
//            )
//        )
//
//    }
//
//    println(context.zones.size)
//    println(context.households.size)
//
//}
