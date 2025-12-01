package domain.shared.datastructure.matrix

import domain.shared.datastructure.matrix.binary.MatrixDoubleFormat
import domain.shared.datastructure.matrix.binary.StandardMatrixBinaryFormat
import domain.shared.datastructure.matrix.yaml.YamlInfo
import utils.files.crc32
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.nameWithoutExtension

/**
 * This class builds a local file cache at [rootCachePath] / "binary-cache". Whenever a matrix is requested to be created, this class
 * looks whether it finds a similarly named file in the storage, checks against the hash code of the original file
 * content (to avoid the cache returning a stale element). If the class finds a suitable cache candidate it is parsed
 * with the corresponding binary format, which is faster than string based parsing by a factor of 100x. If the
 * file has no suitable cache candidate, the normal parser is used, and the matrix is stored in the internal storage.
 */
class BinaryMatrixFileLookup(
    val rootCachePath: Path,
    val format: StandardMatrixBinaryFormat = MatrixDoubleFormat,
    private val defaultCreation: ZoneMatrixCreation = VisumMatrixCreator,
) : ZoneMatrixCreation {

    private val internalFolder by lazy {
        rootCachePath.resolve("binary-cache").apply { createDirectories() }
    }

    fun readAllMatchingMatrices(): Map<String, StandardMatrix> {
        return rootCachePath.filter { it.extension == format.fileExtension }.associate {
            it.nameWithoutExtension to format.deserialize(it)
        }
    }

    override fun createMatrix(config: YamlInfo): ZoneIdMatrix {
        val (_, path) = config
        return findCachedBinaryFile(path) ?: run {
            val matrix = defaultCreation.createMatrix(config)
            if (matrix is StandardMatrix) {
                format.serialize(
                    path.crc32(),
                    matrix,
                    internalFolder.resolve(path.nameWithoutExtension + format.fileExtension)
                )
            }

            matrix
        }
    }

    fun listCachedFiles() = internalFolder.listDirectoryEntries()

    fun deleteDirectory() {
        clearDirectory()
        internalFolder.deleteIfExists()
    }
    fun clearDirectory() {
        if (!internalFolder.exists()) return
        internalFolder.listDirectoryEntries().forEach {
            it.deleteIfExists()
        }
    }

    @Suppress("ReturnCount")
    private fun findCachedBinaryFile(path: Path): StandardMatrix? {
        if (!path.exists()) return null
        val fileName = path.nameWithoutExtension
        // Create the hash value of the content found at the path.
        val originalChecksum = path.crc32()
        val target = internalFolder.listDirectoryEntries().find { it.nameWithoutExtension == fileName }
        if (target == null) return null
        val cachedChecksum = format.checksum(target)
        if (cachedChecksum != originalChecksum) return null

        return format.deserialize(target)
    }
}

/**
 * Returns a cached matrix creator if rootCachePath is not null, otherwise no caching is done.
 * @param rootCachePath Null or a path to a directory, where binary cache files can be stored.
 * @param defaultCreation The appropriate method for parsing the matrix format this creator is later used for.
 * @return If `rootCachePath` is not null, a BinaryMatrixFileLookup with both params is returned
 * else `defaultCreation` is returned.
 */
fun optionalCachedMatrixCreator(rootCachePath: Path?, defaultCreation: ZoneMatrixCreation): ZoneMatrixCreation {
    return if (rootCachePath != null) {
        BinaryMatrixFileLookup(rootCachePath, defaultCreation = defaultCreation)
    } else {
        defaultCreation
    }
}
