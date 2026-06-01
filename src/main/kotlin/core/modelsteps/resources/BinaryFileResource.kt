package core.modelsteps.resources

import utils.binary.BinaryReader
import java.nio.file.Path

/**
 * A [Resource] backed by a binary file on disk.
 *
 * Uses the provided [reader] to lazily decode elements of type [E]
 * from the given [path]. The file path serves as both the resource
 * [name] (file name only) and [source] (full path).
 *
 * Loading is deferred until [elements] or [content] are accessed.
 *
 * @param E the type of elements contained in the resource
 * @property path the binary file to read from
 * @property reader the decoder for deserializing elements from the file
 */
class BinaryFileResource<E>(
    private val path: Path,
    private val reader: BinaryReader<E>,
) : Resource<E> {
    /** The file name of the binary resource. */
    override val name: String
        get() = path.fileName.toString()

    /** The absolute path of the binary resource. */
    override val source: String
        get() = path.toString()

    /** A sequence of elements decoded from the binary file. */
    override val elements: Sequence<E>
        get() = content.asSequence()

    /** The lazily loaded content of the binary file. */
    val content by lazy {
        reader.fromBinary(path)
    }
}
