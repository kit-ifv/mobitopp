package core.modelsteps

import utils.Identifiable
import java.nio.file.Path

/**
 * Wrapper class to encapsulate a step that depends on a file, and thus a source path.
 */
data class FileBasedAddResourceStep<E : Identifiable<I>, I>(
    val source: Path,
    val step: AddResourceStep<E, I>,
)
