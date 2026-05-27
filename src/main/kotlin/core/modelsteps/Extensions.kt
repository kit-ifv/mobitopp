package core.modelsteps

import utils.Identifiable
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import java.nio.file.Path

/**
 * Creates a [FilterStep] from this [MutatingStep] using the given [predicate].
 *
 * This avoids repeating repository and dependency definitions, since they are
 * inherited from the parent step. The returned step is a proper named
 * implementation, ensuring compliance with the step system
 *
 * @param predicate condition to test each element against
 * @return a new [FilterStep] which will apply the defined filter if executed.
 */
fun <E : Identifiable<I>, I> MutatingStep<E, I>.spawnFilterStep(predicate: (E) -> Boolean): FilterStep<E, I> =
    object : FilterStep<E, I>() {
        override fun check(element: E): Boolean = predicate(element)

        override val repository: MutableRepository<E, I> = this@spawnFilterStep.repository
        override val dependentRepositories: Set<Repository<*, *>> = this@spawnFilterStep.dependentRepositories
        override val name: String = "Filter spawned from ${this@spawnFilterStep.name}"

        override fun verifyInput(): Warning? = this@spawnFilterStep.verifyInput()
    }

/**
 * Wraps an [AbstractAddResourceStep] with binary caching support.
 */
fun <E : Identifiable<I>, I> AddResourceStep<E, I>.cached(
    binaryReader: BinaryReader<E>,
    binaryWriter: BinaryWriter<E>,
    sourcePath: Path,
    cacheRootPath: Path = Path.of("data"),
): CachedAddResourceStep<E, I> = CachedAddResourceStep(
    binaryReader,
    binaryWriter,
    this,
    originalSourcePath = sourcePath,
    cacheRootPath = cacheRootPath,
)
