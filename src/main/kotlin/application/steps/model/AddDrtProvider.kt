package application.steps.model

import core.modelsteps.AddResourceStep
import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.Resource
import core.modelsteps.Warning
import core.modelsteps.asResource
import domain.synthesis.data.DrtProviderId
import domain.synthesis.data.MutableDrtProviderData

interface AddDrtProviderContext {

    val drtProviderRepository : MutableRepository<MutableDrtProviderData, DrtProviderId>

}

class DrtProviderCollector {
    private val providers : MutableList<MutableDrtProviderData> = mutableListOf()

    fun newDrtProvider(id: DrtProviderId, scope: MutableDrtProviderData.() -> Unit) {
        val p = MutableDrtProviderData(id)
        p.scope()
        providers.add(p)
    }

    internal fun getProviders(): List<MutableDrtProviderData> = providers.toList()

}

fun AddDrtProviderContext.addDrtProvider(drtProvider : () -> MutableDrtProviderData) = run {
    AddDrtProviderStep(this, listOf(drtProvider()))
}

fun AddDrtProviderContext.addMultipleDrtProvider(scope : DrtProviderCollector.() -> Unit) = run {
    val collector = DrtProviderCollector()
    collector.scope()
    AddDrtProviderStep(this, collector.getProviders())
}



class AddDrtProviderStep(context: AddDrtProviderContext, providers: List<MutableDrtProviderData>): AddResourceStep<MutableDrtProviderData, DrtProviderId>() {
    override val name = "Add DrtProviders"

    override val resource: Resource<MutableDrtProviderData> = providers.asSequence().asResource("newDrtProviders", "AddDrtProviderStep")

    override fun mockElementsForValidation() = emptyList<MutableDrtProviderData>()

    override val repository = context.drtProviderRepository

    override val dependentRepositories = emptySet<Repository<*,*>>()

    override fun verifyInput(): Warning? = validate {

    }

}