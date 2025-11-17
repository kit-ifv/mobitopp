package core.modelsteps

import utils.Identifiable
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import utils.files.PathChecksum
import utils.files.crc32
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.nameWithoutExtension
import kotlin.math.exp

/**
 * A resource-loading step that adds binary caching on top of an [AbstractAddResourceStep].
 *
 * On execution, this step attempts to reuse a previously written binary cache file
 * if it exists and its checksum matches the original source. If no valid cache is
 * found, the [originalStep] is executed and the results are serialized to disk
 * using [binaryWriter] for future runs.
 *
 * The cache is stored under a `data-cache` subdirectory of [cacheRootPath], with
 * file names derived from the [originalSourcePath].
 *
 * @param E the element type provided by the resource
 * @param I the identifier type of the element
 * @property binaryReader reader for deserializing elements from the cache
 * @property binaryWriter writer for serializing elements into the cache
 * @property originalStep the underlying step to fall back on when no valid cache exists
 * @property cacheRootPath base folder where cached data is stored
 * @property originalSourcePath the original file path used for cache keying and checksums
 */
class CachedAddResourceStep<E : Identifiable<I>, I>(
    binaryReader: BinaryReader<E>,
    binaryWriter: BinaryWriter<E>,

    val originalStep: AddResourceStep<E, I>,

    cacheRootPath: Path,
    originalSourcePath: Path,
) : BinaryCachedFileInput<E>(
    binaryReader,
    binaryWriter,

    cacheRootPath = cacheRootPath,
    originalSourcePath = originalSourcePath,
), AddResourceStep<E, I> {

    override val name: String = "load ${originalSourcePath.fileName} with background cache ${originalStep.name}"

    override val resource: Resource<E> by lazy {
        if (hasValidCacheEntry) cachedResource() else originalStep.resource
    }
    override val repository: MutableRepository<E, I> = originalStep.repository
    override val dependentRepositories: Set<Repository<*, *>> = originalStep.dependentRepositories

    override fun verifyInput(): Warning? = originalStep.verifyInput()

    override fun mockBehavior(): Warning? = originalStep.mockBehavior()

    override fun generateElementsForCacheWrite(): Collection<E> {
        return repository.elements.toList()
    }

    override fun execute() {
        runCached {
            super<AddResourceStep>.execute()
        }
    }

    private fun cachedResource(): Resource<E> {
        return BinaryFileResource(expectedCachePath, binaryReader)
    }
}

abstract class BinaryCachedFileInput<E>(
    val binaryReader: BinaryReader<E>,
    val binaryWriter: BinaryWriter<E>,
    cacheRootPath: Path,
    originalSourcePath: Path,
) : CachedFileInput(cacheRootPath, originalSourcePath) {
    override fun calculateChecksum(expectedCachePath: Path): PathChecksum {
        return binaryReader.checksum(expectedCachePath)
    }

    override fun onCacheMiss() {
        binaryWriter.toBinary(
            expectedCachePath,
            generateElementsForCacheWrite(),
            checksum = originalFileChecksum
        )
    }

    abstract fun generateElementsForCacheWrite(): Collection<E>
}

abstract class CachedFileInput(
    val cacheRootPath: Path,
    val originalSourcePath: Path,

) {
    protected val cacheFolder: Path by lazy {
        cacheRootPath.resolve("data-cache").apply { createDirectories() }
    }

    protected val expectedCachePath: Path = cacheFolder.resolve(originalSourcePath.nameWithoutExtension + ".bin")

    protected val hasValidCacheEntry: Boolean by lazy {
        hasValidCache()
    }
    protected val originalFileChecksum by lazy {
        originalSourcePath.crc32()
    }

    protected fun hasValidCache(): Boolean {

        println("Checking valid cache path $expectedCachePath")
        if (!expectedCachePath.exists()) return false
        val cachedChecksum = runCatching { calculateChecksum(expectedCachePath) }
        if (cachedChecksum.isFailure) return false
        return (cachedChecksum.getOrNull() == originalFileChecksum).also {
            println("Cache for ${expectedCachePath} is $it")
        }
    }

    abstract fun calculateChecksum(expectedCachePath: Path): PathChecksum
    abstract fun onCacheMiss()
    fun <R> runCached(execution: () -> R): R {
        val output = execution()
        if (!hasValidCacheEntry) {
            onCacheMiss()
        }
        return output
    }
}
