package core.modelsteps.steps

import core.modelsteps.Context
import core.modelsteps.Validation
import core.modelsteps.resources.LazyResource
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Repository
import core.modelsteps.validation.validateFileReadAccess
import utils.Identifiable
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import java.nio.file.Path
import kotlin.io.path.absolutePathString

/**
 * Adds a step to load entities from a binary file into a repository.
 * The entities are loaded lazily when the repository is first accessed.
 *
 * @param C the generic type of the context
 * @param E the generic type of entities to be loaded
 * @param I the type of the identifier for entities
 * @param path the path to the binary file to load from
 * @param reader the [BinaryReader] used to deserialize entities
 * @param repository the repository where the loaded entities will be stored
 * @param name the name of the model step
 * @param dependentRepositories a set of repositories that this step depends on
 * @param validation additional validation logic for this step
 */
@Suppress("LongParameterList")
fun <C : Context, E : Identifiable<I>, I> C.loadBinary(
    path: Path,
    reader: BinaryReader<E>,
    repository: MutableRepository<E, I>,
    name: String = "load binary ${path.fileName}",
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
) = addResourceStep(
    name,
    repository,
    LazyResource("binary ${path.fileName}", path.absolutePathString()) {
        reader.fromBinary(path).asSequence()
    },
    dependentRepositories,
    validation + { validateFileReadAccess(path, true, "source binary cache file ${path.fileName}") },
)

/**
 * Adds a step to write the content of a repository to a binary file.
 *
 * @param C the generic type of the context
 * @param E the generic type of entities to be written
 * @param I the type of the identifier for entities
 * @param path the path to the binary file to write to
 * @param writer the [BinaryWriter] used to serialize entities
 * @param repository the repository whose content will be written
 * @param name the name of the model step
 * @param validation additional validation logic for this step
 */
fun <C : Context, E : Identifiable<I>, I> C.writeBinary(
    path: Path,
    writer: BinaryWriter<E>,
    repository: Repository<E, I>,
    name: String = "write repo content ${repository.name} to binary ${path.fileName}",
    validation: Validation<C> = emptyList(),
) = forAllStep(
    name,
    repository,
    emptySet(),
    validation + { validateFileReadAccess(path, true, "target binary cache file ${path.fileName}") },
) { elements ->
    writer.toBinary(path, elements)
}
