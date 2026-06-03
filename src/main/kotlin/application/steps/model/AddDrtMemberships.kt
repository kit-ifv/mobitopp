package application.steps.model

import application.steps.HasDrtProviderRepo
import core.modelsteps.Config
import core.modelsteps.resources.MutableRepository
import core.modelsteps.scopes.updateEachStep
import domain.simulation.data.person.MutablePerson

// TODO generalize MutablePerson to P: HasMutableDrtMemberships

/**
 * Adds DRT memberships to persons based on a predicate.
 *
 * This step iterates over all persons in the [repository] and all DRT providers in the
 * [HasDrtProviderRepo.drtProviderRepository] of the context [C]. For each pair of person and provider,
 * it evaluates the [predicate]. If the predicate returns `true`, the provider is added to the person's
 * DRT memberships.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasDrtProviderRepo] for [domain.simulation.data.DrtProvider].
 * @param CFG The configuration type. Must implement [Config].
 * @param repository The mutable repository of persons to update. Provided via context.
 * @param predicate The condition to determine if a person should be a member of a DRT provider.
 *                  Evaluated in the context of [CFG] and [C].
 */
context(repository: MutableRepository<MutablePerson, domain.simulation.data.person.PersonId>, _: CFG)
fun <C, CFG : Config> C.addDrtMembershipsIf(
    predicate: context(CFG) C.(domain.simulation.data.person.IPerson, domain.simulation.data.DrtProvider) -> Boolean,
) where C : HasDrtProviderRepo<*, domain.simulation.data.DrtProvider> = updateEachStep(
    name = "add drt memberships to each person",
    dependentRepositories = setOf(drtProviderRepository),
) { person ->
    for (provider in drtProviderRepository.elements) {
        if (predicate(person, provider)) {
            person.drtMemberships.add(provider)
        }
    }
}
