package core.modelsteps

import utils.Identifiable
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import java.nio.file.Path

/**
 * Builds a collection of steps that operate on a shared mutable repository within the simulation. The idea
 * is to encapsulate resource creation, mutation and finalization in one call so that later application developers
 * do not get confused by operation order.
 */
abstract class GroupedStepBuilder<E : Identifiable<I>, I> {

    abstract val reader: BinaryReader<E>
    abstract val writer: BinaryWriter<E>

    // Expose only one source field. there should be one and only one AddResourceStep (though this could theoretically be
    // by additionalSteps. However we need at least one and this field should hold that.
    lateinit var source: AddResourceStep<E, I>

    // Coollect Additional steps if something should happen on our mutable entity E before closing.
    private val additionalSteps: MutableList<MutatingStep<E, I>> = mutableListOf()

    // Similar to source, there should reasonably only be one filter. BUt here I am open to discussion
    var filter: IDFilter<I>? = null

    // For DSL addition of steps
    operator fun MutatingStep<E, I>.unaryPlus() {
        additionalSteps.add(this)
    }

//    // Entry function for source if source is file based on a CSV file found at source.
//    abstract fun fromCSV(
//        source: Path,
//        lambda: context(Path) () -> AbstractAddResourceStep<E, I>,
//    ): FileBasedAddResourceStep<E, I>

    // If you dont want to use a binary cache.
    fun FileBasedAddResourceStep<E, I>.disableCache(): AddResourceStep<E, I> {
        return step
    }

    // Enable the binary cache at the cacheRootPath.
    fun FileBasedAddResourceStep<E, I>.enableCache(cacheRootPath: Path = Path.of("data")): AddResourceStep<E, I> {
        return step.cacheInternally(cacheRootPath = cacheRootPath, sourcePath = source)
    }
    private fun AbstractAddResourceStep<E, I>.cacheInternally(cacheRootPath: Path, sourcePath: Path): AddResourceStep<E, I> {
        return this.cached(
            reader,
            writer,
            cacheRootPath = cacheRootPath,
            sourcePath = sourcePath

        )
    }

    /**
     * Executes the pipeline: [source] → optional [filter] → [additionalSteps].
     */
    fun executeOn(target: Context) {
        target.runStep {
            source
        }
        filter?.let { filter->
            target.runStep { source.spawnFilterStep { filter.accept(it.id)} }
        }
        additionalSteps.forEach {
            target.runStep { it }
        }
    }
}

fun interface IDFilter  <I> {
    fun accept(id: I): Boolean
}