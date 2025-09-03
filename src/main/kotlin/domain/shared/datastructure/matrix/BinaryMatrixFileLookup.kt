package domain.shared.datastructure.matrix

import domain.shared.datastructure.matrix.binary.MatrixDoubleFormat
import domain.shared.datastructure.matrix.binary.StandardMatrixBinaryFormat
import domain.shared.datastructure.matrix.yaml.YamlInfo
import utils.files.crc32
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteIfExists
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
    private val defaultCreation: StandardMatrixCreation = VisumMatrixCreator,
) : ZoneMatrixCreation {

    private val internalFolder by lazy {
        rootCachePath.resolve("binary-cache").apply { createDirectories() }
    }

    override fun createMatrix(config: YamlInfo): ZoneIdMatrix {
        val (_, path) = config
        return findCachedBinaryFile(path) ?: run {
            val matrix = defaultCreation.createMatrix(config)
            format.serialize(
                path.crc32(),
                matrix,
                internalFolder.resolve(path.nameWithoutExtension + format.fileExtension)
            )
            matrix
        }
    }

    fun listCachedFiles() = internalFolder.listDirectoryEntries()

    fun deleteDirectory() {
        internalFolder.listDirectoryEntries().forEach {
            it.deleteIfExists()
        }
        internalFolder.deleteIfExists()
    }

    private fun findCachedBinaryFile(path: Path): StandardMatrix? {
        val fileName = path.nameWithoutExtension
        // Create the hash value of the content found at the path.
        val originalHash = path.crc32()
        val target = internalFolder.listDirectoryEntries().find { it.nameWithoutExtension == fileName }
        if (target == null) return null
        val cachedHash = format.hashCode(target)
        if (cachedHash != originalHash) return null
        return format.deserialize(target)
    }
}
