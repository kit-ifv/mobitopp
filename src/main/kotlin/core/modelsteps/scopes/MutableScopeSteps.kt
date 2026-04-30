package core.modelsteps.scopes

import core.modelsteps.Context
import core.modelsteps.Validation
import core.modelsteps.resources.CsvResource
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Repository
import core.modelsteps.resources.Resource
import core.modelsteps.steps.addCsvResourceStep
import core.modelsteps.steps.addResourceStep
import core.modelsteps.steps.filterIdsStep
import core.modelsteps.steps.filterStep
import core.modelsteps.steps.forAllStep
import core.modelsteps.steps.forEachStep
import core.modelsteps.steps.loadCsvStep
import core.modelsteps.steps.mutatingStep
import core.modelsteps.steps.transformBulkStep
import core.modelsteps.steps.transformEachStep
import core.modelsteps.steps.updateBulkStep
import core.modelsteps.steps.updateEachStep
import utils.Identifiable
import utils.csv.CsvParser
import utils.csv.SEMICOLON
import java.nio.file.Path

/**
 * [mutableRepositoryScope] version of [core.modelsteps.steps.mutatingStep]
 */
context(repository: MutableRepository<E, I>)
fun <C: Context, E: Identifiable<I>, I> C.mutatingStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    execution: C.() -> Unit
) = mutatingStep(name, repository, dependentRepositories, validation, execution)

/**
 * [mutableRepositoryScope] version of [core.modelsteps.steps.addResourceStep]
 */
context(repository: MutableRepository<E, I>)
fun <C: Context, E: Identifiable<I>, I> C.addResourceStep(
    name: String,
    resource: Resource<E>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
) = addResourceStep(name, repository, resource, dependentRepositories, validation)

/**
 * [mutableRepositoryScope] version of [core.modelsteps.steps.addCsvResourceStep]
 */
context(repository: MutableRepository<E, I>)
fun <C: Context, E: Identifiable<I>, I> C.addCsvResourceStep(
    name: String,
    resource: CsvResource<E>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
) = addCsvResourceStep(name, repository, resource, dependentRepositories, validation)

/**
 * [mutableRepositoryScope] version of [core.modelsteps.steps.loadCsvStep]
 */
context(repository: MutableRepository<E, I>)
fun <C: Context, E: Identifiable<I>, I> C.loadCsvStep(
    path: Path,
    parser: CsvParser<E>,
    delimiter: String = SEMICOLON,
    name: String = "load ${path.fileName}",
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
) = loadCsvStep(repository, path, parser, delimiter, name, dependentRepositories, validation)

/**
 * [mutableRepositoryScope] version of [core.modelsteps.steps.filterStep]
 */
context(repository: MutableRepository<E, I>)
fun <C: Context, E: Identifiable<I>, I> C.filterStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    check: (E) -> Boolean,
) = filterStep(name, repository, dependentRepositories, validation, check)

/**
 * [mutableRepositoryScope] version of [core.modelsteps.steps.filterIdsStep]
 */
context(repository: MutableRepository<E, I>)
fun <C: Context, E: Identifiable<I>, I> C.filterIdsStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    check: (I) -> Boolean,
) = filterIdsStep(name, repository, dependentRepositories, validation, check)

/**
 * [mutableRepositoryScope] version of [core.modelsteps.steps.updateEachStep]
 */
context(repository: MutableRepository<E, I>)
fun <C: Context, E: Identifiable<I>, I> C.updateEachStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    update: (E) -> Unit,
) = updateEachStep(name, repository, dependentRepositories, validation, update)

/**
 * [mutableRepositoryScope] version of [core.modelsteps.steps.updateBulkStep]
 */
context(repository: MutableRepository<E, I>)
fun <C: Context, E: Identifiable<I>, I> C.updateBulkStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    update: (Collection<E>) -> Unit,
) = updateBulkStep(name, repository, dependentRepositories, validation, update)

/**
 * [mutableRepositoryScope] version of [core.modelsteps.steps.transformEachStep]
 */
context(repository: MutableRepository<E, I>)
fun <C: Context, E: Identifiable<I>, I> C.transformEachStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    transform: (E) -> E?,
) = transformEachStep(name, repository, dependentRepositories, validation, transform)

/**
 * [mutableRepositoryScope] version of [core.modelsteps.steps.transformBulkStep]
 */
context(repository: MutableRepository<E, I>)
fun <C: Context, E: Identifiable<I>, I> C.transformBulkStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    transform: (E) -> E?,
) = transformBulkStep(name, repository, dependentRepositories, validation, transform)

/**
 * [mutableRepositoryScope] version of [core.modelsteps.steps.forEachStep]
 */
context(repository: Repository<E, I>)
fun <C: Context, E: Identifiable<I>, I> C.forEachStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    process: (E) -> Unit,
) = forEachStep(name, repository, dependentRepositories, validation, process)

/**
 * [mutableRepositoryScope] version of [core.modelsteps.steps.forAllStep]
 */
context(repository: Repository<E, I>)
fun <C: Context, E: Identifiable<I>, I> C.forAllStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    processAll: (Collection<E>) -> Unit,
) = forAllStep(name, repository, dependentRepositories, validation, processAll)
