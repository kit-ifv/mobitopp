package core.modelsteps.scopes

import core.modelsteps.Context
import core.modelsteps.resources.MutableRepository
import core.modelsteps.steps.seal
import utils.Identifiable

/**
 * Creates a scope where a [MutableRepository] is provided as a context.
 *
 * This function allows executing model steps that require a specific repository via context
 * instead of a function parameter. It simplifies the definition of sequences of operations
 * on the same repository.
 *
 * If [sealed] is true, the repository will be sealed after the [scope] is executed.
 *
 * @receiver The simulation context.
 * @param C The context type.
 * @param E The entity type in the repository.
 * @param I The ID type of the entities.
 * @param getter A function to retrieve the [MutableRepository] from the context.
 * @param sealed Whether to seal the repository after the scope finishes.
 * @param scope The block of code to execute within the repository context.
 */
fun <C : Context, E : Identifiable<I>, I> C.mutableRepositoryScope(
    getter: C.() -> MutableRepository<E, I>,
    sealed: Boolean = false,
    scope: context(MutableRepository<E, I>) C.() -> Unit
) {
    val mutableRepo = getter()

    context(mutableRepo) {
        scope()
    }

    if (sealed) {
        seal(mutableRepo)
    }
}
