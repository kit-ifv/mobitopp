package core.modelsteps.scopes

import core.modelsteps.Context
import core.modelsteps.resources.CsvResource
import core.modelsteps.resources.MapRepository
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Repository
import core.modelsteps.steps.seal
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import utils.Identifiable
import utils.csv.CsvParser
import kotlin.io.path.Path
import kotlin.reflect.KMutableProperty0


private typealias Setter<E> = (E) -> Unit


context(_: CFG)
fun <CTXT: Context, CFG, E: Identifiable<I>, I> CTXT.mutableRepositoryScope(
    name: String,
    setter: (Repository<E, I>) -> Unit,
    scope: context(MutableRepository<E, I>, CFG) CTXT.() -> Unit
) {
    val mutableRepo = MapRepository<E, I>(name)

    context(mutableRepo) {
        scope()
    }

    seal(mutableRepo)
    setter(mutableRepo)
}







//Example

abstract class ExamplePersonContext: Context {
    lateinit var personRepo: Repository<Person, PersonId>

    init {

        context(ConfigRequirement(51L)) {
            persons {
                //steps for persons here

                addCsvResourceStep("load person.csv", CsvResource(Path("test.csv"), CsvParser { row -> null}))

                filterIdsStep("filter person ids 10%") {
                        id -> id.value.toInt() % 10 == 0
                }

                updateEachStep("update person data") { person ->
                    person.age += 1
                }

                doSomething("sdgsdgsgsfdgs")


            }
        }



    }


}

context(config: CFG)
fun <CTXT: Context, CFG, E: Identifiable<I>, I> CTXT.setUpEntities(
    name: String,
    repository: KMutableProperty0<in Repository<E, I>>,
    scope: context(MutableRepository<E, I>, CFG) CTXT.() -> Unit
) = mutableRepositoryScope<CTXT, CFG, E, I>(name, repository::set, scope)

//fun <C: Context, E: Identifiable<I>, I> C.setUpEntities(
//    name: String,
//    setter: (Repository<E, I>) -> Unit,
//    scope: context(MutableRepository<E, I>) C.() -> Unit
//) = mutableRepositoryScope<C, E, I>(name, setter, scope)

class ConfigRequirement(val seed: Long)

context(config: CFG)
fun <C: ExamplePersonContext, CFG> C.persons(scope: context(MutableRepository<MutablePerson, PersonId>, CFG) C.() -> Unit) =
    setUpEntities<C, CFG, MutablePerson, PersonId>(
        "persons", this::personRepo, scope
    )

context(repository: MutableRepository<MutablePerson, PersonId>, config: ConfigRequirement)
fun <C: ExamplePersonContext> C.doSomething(bla: String) {
    println("$bla ${config.seed} ${repository.name}")
}


//
///**
// * Builds a collection of steps that operate on a shared mutable repository within the simulation. The idea
// * is to encapsulate resource creation, mutation and finalization in one call so that later application developers
// * do not get confused by operation order.
// */
//abstract class GroupedStepBuilder<E : Identifiable<I>, I> {
//
//    abstract val reader: BinaryReader<E>
//    abstract val writer: BinaryWriter<E>
//
//    // Expose only one source field. there should be one and only one AddResourceStep (though this could theoretically be
//    // by additionalSteps. However we need at least one and this field should hold that.
//    lateinit var source: AddResourceStep<E, I>
//
//    // Coollect Additional steps if something should happen on our mutable entity E before closing.
//    private val additionalSteps: MutableList<MutatingStep<E, I>> = mutableListOf()
//
//    // Similar to source, there should reasonably only be one filter. BUt here I am open to discussion
//    var filter: IDFilter<I>? = null
//
//    // For DSL addition of steps
//    operator fun MutatingStep<E, I>.unaryPlus() {
//        additionalSteps.add(this)
//    }
//
////    // Entry function for source if source is file based on a CSV file found at source.
////    abstract fun fromCSV(
////        source: Path,
////        lambda: context(Path) () -> AbstractAddResourceStep<E, I>,
////    ): FileBasedAddResourceStep<E, I>
//
//    // If you dont want to use a binary cache.
//    fun FileBasedAddResourceStep<E, I>.disableCache(): AddResourceStep<E, I> {
//        return step
//    }
//
//    /**
//     * Enables binary caching (using `enableCache`) if the cacheRootPath is not null.
//     * Otherwise, no caching is done.
//     */
//    fun FileBasedAddResourceStep<E, I>.optionalCache(cacheRootPath: Path?): AddResourceStep<E, I> {
//        return if (cacheRootPath != null) {
//            enableCache(cacheRootPath)
//        } else {
//            disableCache()
//        }
//    }
//
//    // Enable the binary cache at the cacheRootPath.
//    fun FileBasedAddResourceStep<E, I>.enableCache(cacheRootPath: Path = Path.of("data")): AddResourceStep<E, I> {
//        return step.cacheInternally(cacheRootPath = cacheRootPath, sourcePath = source)
//    }
//    private fun AbstractAddResourceStep<E, I>.cacheInternally(
//        cacheRootPath: Path,
//        sourcePath: Path
//    ): AddResourceStep<E, I> {
//        return this.cached(
//            reader,
//            writer,
//            cacheRootPath = cacheRootPath,
//            sourcePath = sourcePath
//
//        )
//    }
//
//    /**
//     * Executes the pipeline: [source] → optional [filter] → [additionalSteps].
//     */
//    fun executeOn(target: Context) {
//        target.runStep {
//            source
//        }
//        filter?.let { filter ->
//            target.runStep { source.spawnFilterStep { filter.accept(it.id) } }
//        }
//        additionalSteps.forEach {
//            target.runStep { it }
//        }
//    }
//}
//
//fun interface IDFilter<I> {
//    fun accept(id: I): Boolean
//}
