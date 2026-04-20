package application.steps.model

import core.modelsteps.AddResourceStep
import core.modelsteps.Context
import core.modelsteps.LateInit
import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.Resource
import core.modelsteps.SealStep
import core.modelsteps.Warning
import core.modelsteps.asResource
import domain.shared.location.Impedance
import domain.shared.location.Zone
import domain.simulation.agent.SimpleMatrixDrtAlgorithm
import domain.synthesis.data.DrtProviderId
import domain.synthesis.data.MutableDrtProviderData
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

interface AddDrtProviderContext : Context {

    val drtProviderRepository: MutableRepository<MutableDrtProviderData, DrtProviderId>
    val impedance: LateInit<Impedance>
}

class DrtProviderCollector {
    private val providers: MutableList<MutableDrtProviderData> = mutableListOf()

    fun drtProvider(id: DrtProviderId, scope: MutableDrtProviderData.() -> Unit) {
        val p = MutableDrtProviderData(id)
        p.scope()
        providers.add(p)
    }

    fun drtProvider(scope: MutableDrtProviderData.() -> Unit) {
        val p = MutableDrtProviderData(DrtProviderId(providerIdCounter++))
        p.scope()
        providers.add(p)
    }

    internal fun getProviders(): List<MutableDrtProviderData> = providers.toList()
}

private var providerIdCounter = 0L

fun AddDrtProviderContext.newDrtProvider(scope: MutableDrtProviderData.() -> Unit) = runStep {
    val provider = MutableDrtProviderData(DrtProviderId(providerIdCounter++))
    provider.scope()
    AddDrtProviderStep(this, listOf(provider))
}

fun AddDrtProviderContext.finishDrtProviders() = runStep {
    SealStep(drtProviderRepository)
}

fun AddDrtProviderContext.addDrtProvider(drtProvider: () -> MutableDrtProviderData) = runStep {
    val provider = drtProvider()
    validateId(provider)

    AddDrtProviderStep(this, listOf(drtProvider()))
}

private fun validateId(provider: MutableDrtProviderData) {
    require(provider.id.value >= providerIdCounter) {
        "Ids up to (no including) $providerIdCounter were already used for DrtProviders.\n" +
            "Cannot create new provider with id: ${provider.id.value}!"
    }
    providerIdCounter = provider.id.value + 1
}

fun AddDrtProviderContext.addMultipleDrtProvider(scope: DrtProviderCollector.() -> Unit) = runStep {
    val collector = DrtProviderCollector()
    collector.scope()
    val providers = collector.getProviders().sortedBy { it.id.value }.onEach { validateId(it) }
    AddDrtProviderStep(this, providers)
}

@Suppress("MagicNumber")
private val allDay = 0 to 24

fun AddDrtProviderContext.dummyDrtAlgorithm(
    serviceArea: Collection<Zone>,
    numVehicles: Int = serviceArea.size,
    avgWaitingTime: Duration = 4.minutes,
    operationHours: Pair<Int, Int> = allDay,
) = SimpleMatrixDrtAlgorithm(
    impedance.value,
    avgWaitingTime,
    serviceArea,
    operationHours,
    numVehicles
)

class AddDrtProviderStep(
    context: AddDrtProviderContext,
    providers: List<MutableDrtProviderData>
) : AddResourceStep<MutableDrtProviderData, DrtProviderId> {
    override val name = "Add DrtProviders"

    override val resource: Resource<MutableDrtProviderData> = providers.asSequence().asResource(
        "newDrtProviders",
        "AddDrtProviderStep"
    )

//    override fun mockElementsForValidation() = emptyList<MutableDrtProviderData>()

    override val repository = context.drtProviderRepository

    override val dependentRepositories = emptySet<Repository<*, *>>()

    override fun verifyInput(): Warning? = null

    override fun mockBehavior() = null
}
