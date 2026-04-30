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

///**
// * A resource-loading step that adds binary caching on top of an [AbstractAddResourceStep].
// *
// * On execution, this step attempts to reuse a previously written binary cache file
// * if it exists and its checksum matches the original source. If no valid cache is
// * found, the [originalStep] is executed and the results are serialized to disk
// * using [binaryWriter] for future runs.
// *
// * The cache is stored under a `data-cache` subdirectory of [cacheRootPath], with
// * file names derived from the [originalSourcePath].
// *
// * @param E the element type provided by the resource
// * @param I the identifier type of the element
// * @property binaryReader reader for deserializing elements from the cache
// * @property binaryWriter writer for serializing elements into the cache
// * @property originalStep the underlying step to fall back on when no valid cache exists
// * @property cacheRootPath base folder where cached data is stored
// * @property originalSourcePath the original file path used for cache keying and checksums
// */


fun <E> Resource<E>.cached(
    path: Path,
    binaryConfig: BinaryCacheConfig<E>,
): CachedBinaryResource<E> = CachedBinaryResource(path, binaryConfig) { this }

fun <E> CsvResource<E>.cachedCsv(
    binaryConfig: BinaryCacheConfig<E>,
): CachedBinaryResource<E> = CachedBinaryResource(path, binaryConfig) { this }

class CachedBinaryResource<E>(
    private val path: Path,
    binaryConfig: BinaryCacheConfig<E>,
    private val defaultResource: (Path) -> Resource<E>,
): BinaryCachedFileInput<E>(
    binaryConfig.binaryReader,
    binaryConfig.binaryWriter,
    binaryConfig.cacheRootPath,
    path,
), Resource<E> {

    override val name: String by lazy {
        if (hasValidCacheEntry) {
            "cached ${path.nameWithoutExtension}.bin"
        } else { path.fileName.toString() }
    }

    override val source: String by lazy {
        if (hasValidCacheEntry) {
            path.absolutePathString().replaceAfterLast(".", "bin")
        } else { path.fileName.toString() }
    }

    override val elements: Sequence<E> by lazy {
        ifCachedElse(::loadFromCache) {
            defaultResource(path).elements
        }
    }

    override fun getData(): Collection<E> = elements.toList()

}


data class BinaryCacheConfig<E>(
    val binaryReader: BinaryReader<E>,
    val binaryWriter: BinaryWriter<E>,
    val cacheRootPath: Path
)

abstract class BinaryCachedFileInput<E>(
    val binaryReader: BinaryReader<E>,
    val binaryWriter: BinaryWriter<E>,
    cacheRootPath: Path,
    originalSourcePath: Path,
) : CachedFileInput(cacheRootPath, originalSourcePath) {

    override fun cachedChecksum(expectedCachePath: Path): PathChecksum {
        return binaryReader.checksum(expectedCachePath)
    }

    override fun onCacheMiss() {
        binaryWriter.toBinary(
            expectedCachePath,
            getData(),
            checksum = originalFileChecksum
        )
    }

    abstract fun getData(): Collection<E>
    fun loadFromCache(): Sequence<E> = binaryReader.fromBinary(expectedCachePath).asSequence()

    //best practice: cache result of getData
    fun loadCachedOrCompute() = ifCachedElse(::loadFromCache) {
        getData()
    }


}

abstract class CachedFileInput(
    val cacheRootPath: Path,
    val originalSourcePath: Path,
) {
    protected val cacheFolder: Path by lazy { cacheRootPath.resolve("data-cache").apply { createDirectories() } }
    protected val expectedCachePath: Path = cacheFolder.resolve(originalSourcePath.nameWithoutExtension + ".bin")
    protected val hasValidCacheEntry: Boolean by lazy { hasValidCache() }
    protected val originalFileChecksum by lazy { originalSourcePath.crc32() }

    protected fun hasValidCache(): Boolean {
        println("Checking valid cache path $expectedCachePath")
        if (!expectedCachePath.exists()) return false
        val cachedChecksum = runCatching { cachedChecksum(expectedCachePath) }
        if (cachedChecksum.isFailure) return false
        return (cachedChecksum.getOrNull() == originalFileChecksum).also {
            println("Cache for $expectedCachePath is $it")
        }
    }

    abstract fun cachedChecksum(expectedCachePath: Path): PathChecksum
    abstract fun onCacheMiss()

    fun <R> ifCachedElse(cached: () -> R, otherwise: () -> R): R =
        if (hasValidCacheEntry) {
            cached()
        } else {
            otherwise().also { onCacheMiss() }
        }

}
