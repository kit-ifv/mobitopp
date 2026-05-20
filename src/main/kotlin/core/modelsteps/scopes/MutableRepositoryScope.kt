package core.modelsteps.scopes

import core.modelsteps.Context
import core.modelsteps.resources.CsvResource
import core.modelsteps.resources.MapRepository
import core.modelsteps.resources.MutableRepository
import core.modelsteps.resources.Repository
import core.modelsteps.steps.seal
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import utils.Identifiable
import utils.csv.CsvParser
import kotlin.io.path.Path
import kotlin.reflect.KMutableProperty0

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
 * @param CTXT The context type.
 * @param CFG The configuration type.
 * @param E The entity type in the repository.
 * @param I The ID type of the entities.
 * @param getter A function to retrieve the [MutableRepository] from the context.
 * @param sealed Whether to seal the repository after the scope finishes.
 * @param scope The block of code to execute within the repository context.
 */
context(_: CFG)
fun <CTXT : Context, CFG, E : Identifiable<I>, I> CTXT.mutableRepositoryScope(
    getter: CTXT.() -> MutableRepository<E, I>,
    sealed: Boolean = false,
    scope: context(MutableRepository<E, I>, CFG) CTXT.() -> Unit
) {
    val mutableRepo = getter()

    context(mutableRepo) {
        scope()
    }

    if (sealed) {
        seal(mutableRepo)
    }
}
