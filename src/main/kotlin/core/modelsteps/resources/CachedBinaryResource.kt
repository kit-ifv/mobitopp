package core.modelsteps.resources

import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import utils.files.PathChecksum
import utils.files.crc32
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.nameWithoutExtension

/**
 * Decorates a [Resource] with binary caching.
 *
 * @receiver The original resource.
 * @param E the element type
 * @param path the path to the original source file (used for cache keying)
 * @param binaryConfig the configuration for binary caching
 * @return a [CachedBinaryResource] instance
 */
fun <E> Resource<E>.cached(
    path: Path,
    binaryConfig: BinaryCacheConfig<E>,
): CachedBinaryResource<E> = CachedBinaryResource(path, binaryConfig) { this }

/**
 * Decorates a [CsvResource] with binary caching.
 *
 * @receiver The original CSV resource.
 * @param E the element type
 * @param binaryConfig the configuration for binary caching
 * @return a [CachedBinaryResource] instance
 */
fun <E> CsvResource<E>.cachedCsv(
    binaryConfig: BinaryCacheConfig<E>,
): CachedBinaryResource<E> = CachedBinaryResource(path, binaryConfig) { this }

/**
 * A [Resource] that attempts to load its elements from a binary cache file.
 *
 * If a valid cache file exists and its checksum matches the original source file,
 * elements are loaded from the cache using [BinaryReader]. Otherwise, elements
 * are loaded from the [defaultResource] and then written to the cache for future use.
 *
 * @param E the type of elements provided by the resource
 * @property path the path to the original source file
 * @param binaryConfig configuration for cache root, reading, and writing
 * @param defaultResource factory function to create the fallback resource
 */
class CachedBinaryResource<E>(
    private val path: Path,
    binaryConfig: BinaryCacheConfig<E>,
    private val defaultResource: (Path) -> Resource<E>,
) : BinaryCachedFileInput<E>(
    binaryConfig.binaryReader,
    binaryConfig.binaryWriter,
    binaryConfig.cacheRootPath,
    path,
), Resource<E> {

    /** The name of the resource, prefixed with 'cached' if loaded from cache. */
    override val name: String by lazy {
        if (hasValidCacheEntry) {
            "cached ${path.nameWithoutExtension}.bin"
        } else { path.fileName.toString() }
    }

    /** The source path, pointing to the .bin file if loaded from cache. */
    override val source: String by lazy {
        if (hasValidCacheEntry) {
            path.absolutePathString().replaceAfterLast(".", "bin")
        } else { path.fileName.toString() }
    }

    /**
     * A sequence of elements, either loaded from cache or computed from the default resource.
     */
    override val elements: Sequence<E> // TODO check for memory leak
        get() = if (data != null) {
            data!!.asSequence()
        } else {
            if (hasValidCacheEntry) {
                data = loadFromCache().toList()
            } else {
                data = defaultResource(path).elements.toList()
                onCacheMiss()
            }
            data!!.asSequence()
        }

    private var data: List<E>? = null

    /**
     * @return the collection of elements currently held by the resource.
     */
    override fun getData(): Collection<E> = data!!.toList()
}

/**
 * Configuration for binary caching of resources.
 *
 * @param E the type of elements to be cached
 * @property binaryReader the reader used to deserialize elements from the cache
 * @property binaryWriter the writer used to serialize elements into the cache
 * @property cacheRootPath the base directory where cache files are stored
 */
data class BinaryCacheConfig<E>(
    val binaryReader: BinaryReader<E>,
    val binaryWriter: BinaryWriter<E>,
    val cacheRootPath: Path
)

/**
 * Base class for binary cached file inputs.
 *
 * @param E the type of elements
 * @property binaryReader reader for deserializing elements
 * @property binaryWriter writer for serializing elements
 * @param cacheRootPath base folder for cache storage
 * @param originalSourcePath the path to the original data source
 */
abstract class BinaryCachedFileInput<E>(
    val binaryReader: BinaryReader<E>,
    val binaryWriter: BinaryWriter<E>,
    cacheRootPath: Path,
    originalSourcePath: Path,
) : CachedFileInput(cacheRootPath, originalSourcePath) {

    /**
     * Computes the checksum of the cached file.
     *
     * @param expectedCachePath the path to the cache file
     * @return the computed [PathChecksum]
     */
    override fun cachedChecksum(expectedCachePath: Path): PathChecksum {
        return binaryReader.checksum(expectedCachePath)
    }

    /**
     * Called when the cache is missing or invalid. Writes the current data to the cache.
     */
    override fun onCacheMiss() {
        binaryWriter.toBinary(
            expectedCachePath,
            getData(),
            checksum = originalFileChecksum
        )
    }

    /**
     * @return the collection of data to be cached.
     */
    abstract fun getData(): Collection<E>

    /**
     * @return a sequence of elements loaded from the cache.
     */
    fun loadFromCache(): Sequence<E> = binaryReader.fromBinary(expectedCachePath).asSequence()
}

/**
 * Abstract base class for handling file-based caching logic.
 *
 * @property cacheRootPath the root directory for cache storage
 * @property originalSourcePath the path to the original source file
 */
abstract class CachedFileInput(
    val cacheRootPath: Path,
    val originalSourcePath: Path,
) {
    /** The directory where cache files are stored. */
    protected val cacheFolder: Path by lazy { cacheRootPath.resolve("data-cache").apply { createDirectories() } }

    /** The expected path to the binary cache file. */
    protected val expectedCachePath: Path = cacheFolder.resolve(originalSourcePath.nameWithoutExtension + ".bin")

    /** Whether a valid cache entry exists. */
    protected val hasValidCacheEntry: Boolean by lazy { hasValidCache() }

    /** The CRC32 checksum of the original source file. */
    protected val originalFileChecksum by lazy { originalSourcePath.crc32() }

    /**
     * Checks if the cache exists and is valid by comparing checksums.
     *
     * @return true if the cache is valid, false otherwise.
     */
    protected fun hasValidCache(): Boolean {
        println("Checking valid cache path $expectedCachePath")
        if (!expectedCachePath.exists()) return false
        val cachedChecksum = runCatching { cachedChecksum(expectedCachePath) }
        if (cachedChecksum.isFailure) return false
        return (cachedChecksum.getOrNull() == originalFileChecksum).also {
            println("Cache for $expectedCachePath is $it")
        }
    }

    /**
     * Abstract method to compute the checksum of the cached file.
     *
     * @param expectedCachePath the path to the cache file
     * @return the computed [PathChecksum]
     */
    abstract fun cachedChecksum(expectedCachePath: Path): PathChecksum

    /**
     * Called when a cache miss occurs to handle data persistence.
     */
    abstract fun onCacheMiss()
}
