package core.modelsteps

import utils.Identifiable
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import utils.files.crc32
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.nameWithoutExtension

/**
 * A resource-loading step that adds binary caching on top of an [AddResourceStep].
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
class CachedIntegration<E : Identifiable<I>, I>(
    val binaryReader: BinaryReader<E>,
    val binaryWriter: BinaryWriter<E>,

    val originalStep: AddResourceStep<E, I>,

    private val cacheRootPath: Path,
    private val originalSourcePath: Path,
) : AddResourceStep<E, I>() {

    override val repository: MutableRepository<E, I> = originalStep.repository
    override val dependentRepositories: Set<Repository<*, *>> = originalStep.dependentRepositories
    override val name: String = "load ${originalSourcePath.fileName} with background cache${originalStep.name}"
    private val cacheFolder by lazy {
        cacheRootPath.resolve("data-cache").apply { createDirectories() }
    }

    val hasValidCacheEntry: Boolean by lazy {
        hasValidCache()
    }
    private val expectedCachePath = cacheFolder.resolve(originalSourcePath.nameWithoutExtension + ".bin")

    private fun hasValidCache(): Boolean {
        if (!expectedCachePath.exists()) return false
        val originalChecksum = originalSourcePath.crc32()
        val cachedChecksum = binaryReader.checksum(expectedCachePath)
        return cachedChecksum == originalChecksum
    }

    override val resource: Resource<E> by lazy {
        if (hasValidCacheEntry) cachedResource() else originalStep.resource
    }

    override fun execute() {
        super.execute()
        if (!hasValidCacheEntry) {
            binaryWriter.toBinary(
                expectedCachePath,
                repository.elements.toList(),
                checksum = originalSourcePath.crc32()
            )
        }
    }

    override fun mockElementsForValidation(): List<E> {
        return emptyList()
    }

    fun cachedResource(): Resource<E> {
        return BinaryFileResource(expectedCachePath, binaryReader)
    }

    override fun verifyInput(): Warning? {
        return null // TODO
    }
}
