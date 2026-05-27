@file:Suppress("TooManyFunctions")

package core.modelsteps.steps

import core.modelsteps.Context
import core.modelsteps.Validation
import core.modelsteps.resources.CsvResource
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Repository
import core.modelsteps.resources.Resource
import core.modelsteps.resources.validateNotSealed
import core.modelsteps.validation.validateCsvMetadata
import utils.Identifiable
import utils.csv.CsvParser
import utils.csv.SEMICOLON
import java.nio.file.Path

/**
 * A wrapper for [repositoryDependentStep] that represents a step intended to modify a [repository].
 *
 * In addition to checks for [dependentRepositories], this step validates that the target
 * [repository] is not already sealed.
 *
 * @receiver The context type.
 * @param C The context type.
 * @param E The type of entities in the repository.
 * @param I The type of entity IDs.
 * @param name The descriptive name of this step.
 * @param repository The mutable repository that will be modified.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param execution The logic to be executed in execution mode.
 */
fun <C : Context, E : Identifiable<I>, I> C.mutatingStep(
    name: String,
    repository: MutableRepository<E, I>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    execution: C.() -> Unit
) = repositoryDependentStep(
    name,
    dependentRepositories,
    validation + { validateNotSealed(repository, name) },
    execution
)

/**
 * A [mutatingStep] that adds elements from a [resource] to the given [repository].
 *
 * During validation, it ensures the repository is not sealed and optionally checks [dependentRepositories].
 * During execution, it adds all elements from the [resource] to the [repository].
 *
 * @receiver The context type.
 * @param C The context type.
 * @param E The type of entities to be added.
 * @param I The type of entity IDs.
 * @param name The descriptive name of this step.
 * @param repository The repository where elements will be added.
 * @param resource The resource providing the elements.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 */
fun <C : Context, E : Identifiable<I>, I> C.addResourceStep(
    name: String,
    repository: MutableRepository<E, I>,
    resource: Resource<E>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
) = mutatingStep(
    name,
    repository,
    dependentRepositories,
    validation
) {
    var count = 0
    repository.addElements("$name (from ${resource.name} [${resource.source}])", resource.elements.onEach { count++ })
    report.addNormalLog(name, "added $count elements to repo ${repository.name}")
}

/**
 * A [mutatingStep] that adds elements from a [CsvResource] to the given [repository].
 *
 * This step includes CSV metadata validation during the validation phase.
 *
 * @receiver The context type.
 * @param C The context type.
 * @param E The type of entities to be added.
 * @param I The type of entity IDs.
 * @param name The descriptive name of this step.
 * @param repository The repository where elements will be added.
 * @param resource The CSV resource providing the elements.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 */
fun <C : Context, E : Identifiable<I>, I> C.addCsvResourceStep(
    name: String,
    repository: MutableRepository<E, I>,
    resource: CsvResource<E>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
) = addResourceStep(
    name,
    repository,
    resource,
    dependentRepositories,
    validation + { validateCsvMetadata(name, resource) }
)

/**
 * Loads data from a CSV file at the specified [path] into the [repository].
 *
 * This is a convenience wrapper around [addCsvResourceStep].
 *
 * @receiver The context type.
 * @param C The context type.
 * @param E The type of entities to be added.
 * @param I The type of entity IDs.
 * @param repository The repository where elements will be added.
 * @param path The path to the CSV file.
 * @param parser The parser to convert CSV rows to entities.
 * @param delimiter The CSV delimiter (defaults to semicolon).
 * @param name The descriptive name of this step (defaults to "load <filename>").
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 */
@Suppress("LongParameterList")
fun <C : Context, E : Identifiable<I>, I> C.loadCsvStep(
    repository: MutableRepository<E, I>,
    path: Path,
    parser: CsvParser<E>,
    delimiter: String = SEMICOLON,
    name: String = "load ${path.fileName}",
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
) = addCsvResourceStep(
    name,
    repository,
    CsvResource(path, parser, delimiter),
    dependentRepositories,
    validation
)

/**
 * A [mutatingStep] that filters elements in the [repository] based on the [check] predicate.
 *
 * Elements that do not satisfy the predicate are removed from the repository.
 *
 * @receiver The context type.
 * @param C The context type.
 * @param E The type of entities in the repository.
 * @param I The type of entity IDs.
 * @param name The descriptive name of this step.
 * @param repository The repository to filter.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param check The predicate to determine which elements to keep.
 */
fun <C : Context, E : Identifiable<I>, I> C.filterStep(
    name: String,
    repository: MutableRepository<E, I>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    check: (E) -> Boolean,
) = mutatingStep(
    name,
    repository,
    dependentRepositories,
    validation
) {
    val sizeBefore = repository.size
    repository.filterElements(name, check)
    val sizeAfter = repository.size
    logNormal(
        "filter removed ${sizeBefore - sizeAfter} elements from repo '${repository.name}' (before: $sizeBefore, after: $sizeAfter)"
    )
}

/**
 * A [mutatingStep] that filters elements in the [repository] based on their IDs using the [check] predicate.
 *
 * Elements whose IDs do not satisfy the predicate are removed from the repository.
 *
 * @receiver The context type.
 * @param C The context type.
 * @param E The type of entities in the repository.
 * @param I The type of entity IDs.
 * @param name The descriptive name of this step.
 * @param repository The repository to filter.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param check The predicate to determine which IDs to keep.
 */
fun <C : Context, E : Identifiable<I>, I> C.filterIdsStep(
    name: String,
    repository: MutableRepository<E, I>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    check: (I) -> Boolean,
) = mutatingStep(
    name,
    repository,
    dependentRepositories,
    validation
) {
    val sizeBefore = repository.size
    repository.filterIds(name, check)
    val sizeAfter = repository.size
    logNormal(
        "filter removed ${sizeBefore - sizeAfter} elements by id from repo '${repository.name}' (before: $sizeBefore, after: $sizeAfter)"
    )
}

/**
 * A [mutatingStep] that applies an [update] function to each element in the [repository].
 *
 * This is used for in-place modifications of entities.
 *
 * @receiver The context type.
 * @param C The context type.
 * @param E The type of entities in the repository.
 * @param I The type of entity IDs.
 * @param name The descriptive name of this step.
 * @param repository The repository where elements will be updated.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param update The function to apply to each element.
 */
fun <C : Context, E : Identifiable<I>, I> C.updateEachStep(
    name: String,
    repository: MutableRepository<E, I>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    update: (E) -> Unit,
) = mutatingStep(
    name,
    repository,
    dependentRepositories,
    validation
) {
    repository.updateEach(name, update)
}

/**
 * A [mutatingStep] that applies an [update] function to all elements in the [repository] as a collection.
 *
 * This is useful when updates are interdependent and require access to all elements at once.
 *
 * @receiver The context type.
 * @param C The context type.
 * @param E The type of entities in the repository.
 * @param I The type of entity IDs.
 * @param name The descriptive name of this step.
 * @param repository The repository where elements will be updated.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param update The function to apply to the collection of all elements.
 */
fun <C : Context, E : Identifiable<I>, I> C.updateBulkStep(
    name: String,
    repository: MutableRepository<E, I>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    update: (Collection<E>) -> Unit,
) = mutatingStep(
    name,
    repository,
    dependentRepositories,
    validation
) {
    repository.updateAll(name, update)
}

/**
 * A [mutatingStep] that transforms each element in the [repository] using the [transform] function.
 *
 * This allows replacing entities (e.g., for numerical data objects).
 * If the [transform] function returns null, the element is removed from the repository.
 *
 * @receiver The context type.
 * @param C The context type.
 * @param E The type of entities in the repository.
 * @param I The type of entity IDs.
 * @param name The descriptive name of this step.
 * @param repository The repository where elements will be transformed.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param transform The transformation function to apply to each element.
 */
fun <C : Context, E : Identifiable<I>, I> C.transformEachStep(
    name: String,
    repository: MutableRepository<E, I>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    transform: (E) -> E?,
) = mutatingStep(
    name,
    repository,
    dependentRepositories,
    validation
) {
    repository.transformEach(name, transform)
}

/**
 * A [mutatingStep] that transforms the [repository] elements by applying the [transform] function to each.
 *
 * This is similar to [transformEachStep] but often used for bulk operations where the result
 * might depend on other elements (though the current implementation here uses `transformEach`).
 *
 * @receiver The context type.
 * @param C The context type.
 * @param E The type of entities in the repository.
 * @param I The type of entity IDs.
 * @param name The descriptive name of this step.
 * @param repository The repository where elements will be transformed.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param transform The transformation function to apply.
 */
fun <C : Context, E : Identifiable<I>, I> C.transformBulkStep(
    name: String,
    repository: MutableRepository<E, I>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    transform: (Collection<E>) -> Collection<E>,
) = mutatingStep(
    name,
    repository,
    dependentRepositories,
    validation
) {
    repository.transformAll(name, transform)
}

/**
 * A [repositoryDependentStep] that performs a read-only action [process] on each element in the [repository].
 *
 * @receiver The context type.
 * @param C The context type.
 * @param E The type of entities in the repository.
 * @param I The type of entity IDs.
 * @param name The descriptive name of this step.
 * @param repository The repository to iterate over.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param process The action to perform on each element.
 */
fun <C : Context, E : Identifiable<I>, I> C.forEachStep(
    name: String,
    repository: Repository<E, I>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    process: (E) -> Unit,
) = repositoryDependentStep(
    name,
    dependentRepositories,
    validation
) {
    repository.elements.forEach {
        process(it)
    }
}

/**
 * A [repositoryDependentStep] that performs a read-only action [processAll] on all elements in the [repository] at once.
 *
 * @receiver The context type.
 * @param C The context type.
 * @param E The type of entities in the repository.
 * @param I The type of entity IDs.
 * @param name The descriptive name of this step.
 * @param repository The repository to process.
 * @param dependentRepositories A set of repositories that this step depends on.
 * @param validation Additional validation checks.
 * @param processAll The action to perform on the collection of all elements.
 */
fun <C : Context, E : Identifiable<I>, I> C.forAllStep(
    name: String,
    repository: Repository<E, I>,
    dependentRepositories: Set<Repository<*, *>> = emptySet(),
    validation: Validation<C> = emptyList(),
    processAll: (Collection<E>) -> Unit,
) = repositoryDependentStep(
    name,
    dependentRepositories,
    validation
) {
    processAll(repository.elements.toList())
}

/**
 * A [mutatingStep] that seals the given [repository], preventing any further modifications.
 *
 * Once sealed, future steps that attempt to modify this repository (via [mutatingStep])
 * will report a validation failure.
 *
 * @receiver The context type.
 * @param C The context type.
 * @param E The type of entities in the repository.
 * @param I The type of entity IDs.
 * @param repository The repository to seal.
 * @param name The descriptive name of this step.
 * @param validation Additional validation checks.
 */
fun <C : Context, E : Identifiable<I>, I> C.seal(
    repository: MutableRepository<E, I>,
    name: String = "seal ${repository.name}",
    validation: Validation<C> = emptyList()
) = modelStep(
    name,
    validation + listOf { repository.seal(); true } //seal repo also in validation mode, for subsequent checks
) {
    repository.seal()
    logNormal("sealed ${repository.name} with ${repository.size} elements. This repo can no longer be updated!")
}
