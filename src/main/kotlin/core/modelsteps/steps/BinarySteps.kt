package core.modelsteps.steps

import core.modelsteps.Context
import core.modelsteps.resources.LazyResource
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Repository
import core.modelsteps.Validation
import core.modelsteps.validation.validateFileReadAccess
import utils.Identifiable
import utils.binary.BinaryReader
import utils.binary.BinaryWriter
import java.nio.file.Path
import kotlin.io.path.absolutePathString

fun <C: Context, E: Identifiable<I>, I> C.loadBinary(
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
    validation + { validateFileReadAccess(path, true, "binary cache file ${path.fileName}") }
)

fun <C: Context, E: Identifiable<I>, I> C.writeBinary(
    path: Path,
    writer: BinaryWriter<E>,
    repository: Repository<E, I>,
    name: String = "write repo content ${repository.name} to binary ${path.fileName}",
    validation: Validation<C> = emptyList(),
) = forAllStep(
    name,
    repository,
    emptySet(),
    validation + { validateFileReadAccess(path, true, "binary cache file ${path.fileName}") }
) { elements ->
    writer.toBinary(path, elements)
}
