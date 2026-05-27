@file:Suppress("TooManyFunctions")

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
 * A version of [core.modelsteps.steps.mutatingStep] that uses a [MutableRepository] from the context.
 *
 * This function is intended to be used within a [mutableRepositoryScope].
 *
 * @receiver The simulation context.
 * @param C The context type.
 * @param E The entity type.
 * @param I The ID type.
 * @param name The descriptive name of this step.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param execution The logic to be executed in execution mode.
 */
context(repository: MutableRepository<E, I>)
fun <C : Context, E : Identifiable<I>, I> C.mutatingStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    execution: C.() -> Unit
) = mutatingStep(name, repository, dependentRepositories, validation, execution)

/**
 * A version of [core.modelsteps.steps.addResourceStep] that uses a [MutableRepository] from the context.
 *
 * @receiver The simulation context.
 * @param C The context type.
 * @param E The entity type.
 * @param I The ID type.
 * @param name The descriptive name of this step.
 * @param resource The resource providing the elements to add.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 */
context(repository: MutableRepository<E, I>)
fun <C : Context, E : Identifiable<I>, I> C.addResourceStep(
    name: String,
    resource: Resource<E>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
) = addResourceStep(name, repository, resource, dependentRepositories, validation)

/**
 * A version of [core.modelsteps.steps.addCsvResourceStep] that uses a [MutableRepository] from the context.
 *
 * @receiver The simulation context.
 * @param C The context type.
 * @param E The entity type.
 * @param I The ID type.
 * @param name The descriptive name of this step.
 * @param resource The CSV resource providing the elements.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 */
context(repository: MutableRepository<E, I>)
fun <C : Context, E : Identifiable<I>, I> C.addCsvResourceStep(
    name: String,
    resource: CsvResource<E>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
) = addCsvResourceStep(name, repository, resource, dependentRepositories, validation)

/**
 * A version of [core.modelsteps.steps.loadCsvStep] that uses a [MutableRepository] from the context.
 *
 * @receiver The simulation context.
 * @param C The context type.
 * @param E The entity type.
 * @param I The ID type.
 * @param path The path to the CSV file.
 * @param parser The parser to convert CSV rows to entities.
 * @param delimiter The CSV delimiter.
 * @param name The descriptive name of this step.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 */
context(repository: MutableRepository<E, I>)
@Suppress("LongParameterList")
fun <C : Context, E : Identifiable<I>, I> C.loadCsvStep(
    path: Path,
    parser: CsvParser<E>,
    delimiter: String = SEMICOLON,
    name: String = "load ${path.fileName}",
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
) = loadCsvStep(repository, path, parser, delimiter, name, dependentRepositories, validation)

/**
 * A version of [core.modelsteps.steps.filterStep] that uses a [MutableRepository] from the context.
 *
 * @receiver The simulation context.
 * @param C The context type.
 * @param E The entity type.
 * @param I The ID type.
 * @param name The descriptive name of this step.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param check The predicate to determine which elements to keep.
 */
context(repository: MutableRepository<E, I>)
fun <C : Context, E : Identifiable<I>, I> C.filterStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    check: (E) -> Boolean,
) = filterStep(name, repository, dependentRepositories, validation, check)

/**
 * A version of [core.modelsteps.steps.filterIdsStep] that uses a [MutableRepository] from the context.
 *
 * @receiver The simulation context.
 * @param C The context type.
 * @param E The entity type.
 * @param I The ID type.
 * @param name The descriptive name of this step.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param check The predicate to determine which IDs to keep.
 */
context(repository: MutableRepository<E, I>)
fun <C : Context, E : Identifiable<I>, I> C.filterIdsStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    check: (I) -> Boolean,
) = filterIdsStep(name, repository, dependentRepositories, validation, check)

/**
 * A version of [core.modelsteps.steps.updateEachStep] that uses a [MutableRepository] from the context.
 *
 * @receiver The simulation context.
 * @param C The context type.
 * @param E The entity type.
 * @param I The ID type.
 * @param name The descriptive name of this step.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param update The function to apply to each element.
 */
context(repository: MutableRepository<E, I>)
fun <C : Context, E : Identifiable<I>, I> C.updateEachStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    update: (E) -> Unit,
) = updateEachStep(name, repository, dependentRepositories, validation, update)

/**
 * A version of [core.modelsteps.steps.updateBulkStep] that uses a [MutableRepository] from the context.
 *
 * @receiver The simulation context.
 * @param C The context type.
 * @param E The entity type.
 * @param I The ID type.
 * @param name The descriptive name of this step.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param update The function to apply to the collection of all elements.
 */
context(repository: MutableRepository<E, I>)
fun <C : Context, E : Identifiable<I>, I> C.updateBulkStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    update: (Collection<E>) -> Unit,
) = updateBulkStep(name, repository, dependentRepositories, validation, update)

/**
 * A version of [core.modelsteps.steps.transformEachStep] that uses a [MutableRepository] from the context.
 *
 * @receiver The simulation context.
 * @param C The context type.
 * @param E The entity type.
 * @param I The ID type.
 * @param name The descriptive name of this step.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param transform The transformation function to apply to each element.
 */
context(repository: MutableRepository<E, I>)
fun <C : Context, E : Identifiable<I>, I> C.transformEachStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    transform: (E) -> E?,
) = transformEachStep(name, repository, dependentRepositories, validation, transform)

/**
 * A version of [core.modelsteps.steps.transformBulkStep] that uses a [MutableRepository] from the context.
 *
 * @receiver The simulation context.
 * @param C The context type.
 * @param E The entity type.
 * @param I The ID type.
 * @param name The descriptive name of this step.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param transform The transformation function to apply.
 */
context(repository: MutableRepository<E, I>)
fun <C : Context, E : Identifiable<I>, I> C.transformBulkStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    transform: (Collection<E>) -> Collection<E>,
) = transformBulkStep(name, repository, dependentRepositories, validation, transform)

/**
 * A version of [core.modelsteps.steps.forEachStep] that uses a [Repository] from the context.
 *
 * @receiver The simulation context.
 * @param C The context type.
 * @param E The entity type.
 * @param I The ID type.
 * @param name The descriptive name of this step.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param process The action to perform on each element.
 */
context(repository: Repository<E, I>)
fun <C : Context, E : Identifiable<I>, I> C.forEachStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    process: (E) -> Unit,
) = forEachStep(name, repository, dependentRepositories, validation, process)

/**
 * A version of [core.modelsteps.steps.forAllStep] that uses a [Repository] from the context.
 *
 * @receiver The simulation context.
 * @param C The context type.
 * @param E The entity type.
 * @param I The ID type.
 * @param name The descriptive name of this step.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param processAll The action to perform on the collection of all elements.
 */
context(repository: Repository<E, I>)
fun <C : Context, E : Identifiable<I>, I> C.forAllStep(
    name: String,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    processAll: (Collection<E>) -> Unit,
) = forAllStep(name, repository, dependentRepositories, validation, processAll)
