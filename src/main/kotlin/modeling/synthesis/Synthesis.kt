package modeling.synthesis

import Builder
import Identifiable

interface SynthesisStep {
    val name: String
    fun execute()

    fun validate(): ValidateStep

}


open class NewResource<B, E>(
    override val name: String,
    val resource: Resource<B>,
    val repository: BuilderRepository<B, E>,
) : SynthesisStep where E:Identifiable<E>, B: Builder<E> {

    override fun execute() {
        repository.initializeBuilders(resource)
    }

    override fun validate() = ValidateNewResource(this)

}

class FinalResource<E>(
    override val name: String,
    val resource: Resource<E>,
    val repository: LateInitRepository<E>
) : SynthesisStep where E: Identifiable<E> {

    override fun execute() {
        repository.initialize(resource)
    }

    override fun validate() = ValidateFinalResource(this)
}

class UpdateStep<B, E>(
    override val name: String,
    val predicate: (B) -> Boolean,
    val repository: BuilderRepository<B, E>,
) : SynthesisStep where B: Builder<E>, E: Identifiable<E> {

    override fun execute() {
        repository.reduce(name, predicate)
    }

    override fun validate() = SimpleValidate(this)
}

class FilterStep<B, E>(
    override val name: String,
    protected val transformation: (B) -> B?,
    protected val repository: BuilderRepository<B, E>,
) : SynthesisStep where B: Builder<E>, E: Identifiable<E> {

    override fun execute() {
        repository.update(name, transformation)
    }

    override fun validate() = SimpleValidate(this)
}

class BuildRepository<B, E> (
    override val name: String,
    val repository: BuilderRepository<B, E>,
) : SynthesisStep where B: Builder<E>, E: Identifiable<E>{

    override fun execute() {
        repository.build()
    }

    override fun validate() = ValidateBuild(this)
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
////        steps.add(FinalResource(name, resource, setter))
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
////        steps.add(BuildRepository(name, wrapGetter(name, getter), setter))
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
//                checkNotNull(zoneBuilders) {"Zone data builders has not yet been initialized or was already finished!"}
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
