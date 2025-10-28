package application.steps.model

import core.modelsteps.Context
import core.modelsteps.ForEachStep
import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.Warning
import domain.synthesis.data.DrtProviderData
import domain.synthesis.data.DrtProviderId
import domain.synthesis.data.IPerson
import domain.synthesis.data.MutablePerson
import domain.synthesis.data.PersonId

interface AddDrtMembershipContext : Context {
    val drtProviderRepository: Repository<DrtProviderData, DrtProviderId>
    val personRepository: MutableRepository<MutablePerson, PersonId>
}

fun AddDrtMembershipContext.addDrtMemberships(
    predicate: (IPerson, DrtProviderData) -> Boolean
) = runStep {
    AddDrtMembershipsStep(this, predicate)
}

val everyoneIsMember: (IPerson, DrtProviderData) -> Boolean = { p, d -> true }

class AddDrtMembershipsStep(
    private val context: AddDrtMembershipContext,
    private val predicate: (IPerson, DrtProviderData) -> Boolean,
) : ForEachStep<MutablePerson, PersonId>() {

    override val name = "Add drt memberships to persons"
    override val repository = context.personRepository

    override fun process(element: MutablePerson) {
        for (provider in context.drtProviderRepository.elements) {
            if (predicate(element, provider)) {
                element.drtMemberships.add(provider)
            }
        }
    }

    override fun verifyInput(): Warning? = null

    override fun validate(validationPrefix: Warning.() -> Unit): Warning? {
        return super.validate(validationPrefix)
    }

    override val dependentRepositories: Set<Repository<*, *>> = setOf(
        context.drtProviderRepository
    )
}
