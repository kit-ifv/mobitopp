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
 * Add additional non sealed repository check as well as [dependentRepositories] checks to validation.
 *
 * @param E the generic type of entities in the repository
 * @param I the generic entity id type
 */
fun <C: Context, E: Identifiable<I>, I> C.mutatingStep(
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
 * Add a [core.modelsteps.resources.Resource] of elements to the given [MutableRepository].
 * Add additional checks of [mutatingStep] to validation.
 *
 * @param E the generic type of entities to be added
 * @param I the generic entity id type
 */
fun <C: Context, E: Identifiable<I>, I> C.addResourceStep(
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
    repository.addElements("$name (from ${resource.name} [${resource.source}])", resource.elements)
}

/**
 * Add a [core.modelsteps.resources.CsvResource] of elements to the given [MutableRepository].
 * Add additional csv metadata check as well as checks of [mutatingStep] to validation.
 *
 * @param E the generic type of entities to be added
 * @param I the generic entity id type
 */
fun <C: Context, E: Identifiable<I>, I> C.addCsvResourceStep(
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
 * Load a csv from the given [java.nio.file.Path], parse and add to the given [MutableRepository].
 * Add checks of [addCsvResourceStep] in validation mode.
 *
 * @param E the generic type of entities to be added
 * @param I the generic entity id type
 */
fun <C: Context, E: Identifiable<I>, I> C.loadCsvStep(
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
 * A FilterStep is a model step that filters the elements of a given [MutableRepository]
 * using a given predicate [check].
 * This removes elements from the repository if applying the predicates evaluates to false.
 * Adds additional checks of [mutatingStep] to validation.
 *
 * @param E the generic type of entities to be filtered
 * @param I the generic id type of entities
 */
fun <C: Context, E: Identifiable<I>, I> C.filterStep(
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
    repository.filterElements(name, check)
}

/**
 * Filters the elements of a given [MutableRepository] by id
 * using a given predicate [check].
 * This removes elements from the repository if applying the predicates evaluates to false.
 * Adds additional checks of [mutatingStep] to validation.
 *
 * @param E the generic type of entities to be filtered
 * @param I the generic id type of entities
 */
fun <C: Context, E: Identifiable<I>, I> C.filterIdsStep(
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
    repository.filterIds(name, check)
}

/**
 * Modify / update the internal state of elements in a given repository
 * by applying an action to each element in the repository.
 * This action may alter state variables of the element.
 *
 * Unlike [updateBulkStep] where all current elements in the repository
 * are passed as a collection to the update function,
 * [updateEachStep] applies the [update] function to each element individually.
 *
 * Adds additional checks of [mutatingStep] to validation.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @param repository the repository in which each element should be updated
 */
fun <C: Context, E: Identifiable<I>, I> C.updateEachStep(
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
 * Modify / update the internal state of all elements in a given repository
 * by applying an action to all element in bulk.
 * This action may alter state variables of the element.
 * This can be used if the state update of the elements are not isolated but interdependent.
 *
 * Unlike [updateEachStep] where the action is applied to each element individually,
 * are passed as a collection to the update function,
 * [updateBulkStep] passes all current elements in the repository as a collection to the [update] function.
 *
 * Adds additional checks of [mutatingStep] to validation.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @param repository the repository in which each element should be updated
 */
fun <C: Context, E: Identifiable<I>, I> C.updateBulkStep(
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
 * Modify / update elements in a given repository
 * by applying a transformation (mapping) to each element in the repository,
 * replacing the respective previous entity/data in the repository.
 * The transformation might evaluate to null, which removes the element from the repository.
 *
 * Unlike [updateEachStep] or [updateBulkStep] where internal state of repository entities are updated,
 * [transformEachStep] allows replacing the original entity in the repo ith an updated copy
 * (e.g. in case of immutable data objects).
 *
 * Unlike [transformBulkStep] where the new elements are computed from data of all current elements in the repository,
 * [transformEachStep] maps each current element to a new element or null.
 *
 * Adds additional checks of [mutatingStep] to validation.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @param repository the repository in which each element should be transformed
 */
fun <C: Context, E: Identifiable<I>, I> C.transformEachStep(
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
 * Modify / update all elements in a given repository
 * by applying a transformation (mapping) to all element in bulk,
 * replacing the repository content by new/derived entities.
 *
 * Unlike [updateEachStep] or [updateBulkStep] where internal state of repository entities are updated,
 * [transformBulkStep] allows replacing the original entity in the repo ith an updated copy
 * (e.g. in case of immutable data objects).
 *
 * Unlike [transformEachStep] where each element is mapped individually to a new element or null,
 * [transformBulkStep] computes the new elements from data of all current elements in the repository.
 *
 * Adds additional checks of [mutatingStep] to validation.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @param repository the repository in which each element should be transformed
 */
fun <C: Context, E: Identifiable<I>, I> C.transformBulkStep(
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
 * Applies a (non mutating) action to each element of the [repository].
 *
 * Unlike [forAllStep] where all current elements of the [repository]
 * are passed as a collection to the processAll action,
 * [forEachStep] applies the [process] action to each element individually.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @param repository the repository in which each element should be processed
 */
fun <C: Context, E: Identifiable<I>, I> C.forEachStep(
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
 * Applies a (non mutating) action to each element of the [repository].
 *
 * Unlike [forEachStep] where all current elements of the [repository]
 * are passed as a collection to the process action,
 * [forEachStep] applies the [processAll] action to each element individually.
 *
 * @param E the generic type of entities to be built
 * @param I the generic id type of entities
 * @param repository the repository in which all elements should be processed
 */
fun <C: Context, E: Identifiable<I>, I> C.forAllStep(
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
 * Seals the given [repository] denying any future modification.
 *
 * @param C the generic type of the context
 * @param E the generic type of entities in the repository
 * @param I the generic id type of entities
 * @param repository the repository to be sealed
 * @param name name of ths seal step for logging
 * @param validation checks to be performed during validation
 */
fun <C: Context, E: Identifiable<I>, I> C.seal(
    repository: MutableRepository<E, I>,
    name: String = "seal ${repository.name}",
    validation: Validation<C> = emptyList()
) = mutatingStep(
    name,
    repository,
    emptySet(),
    validation
) {
    repository.seal()
    println("Sealed ${repository.name} repo: ${repository.size} elements. This repo can no longer be updated!")
}