package application.steps.model

import core.modelsteps.Context
import core.modelsteps.resources.MutableRepository
import core.modelsteps.scopes.mutatingStep
import domain.shared.location.Impedance
import domain.shared.location.Zone
import domain.simulation.agent.SimpleMatrixDrtAlgorithm
import domain.synthesis.data.DrtProviderId
import domain.synthesis.data.MutableDrtProviderData
import domain.synthesis.parser.GlobalDrtProviderIdCounter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Creates and adds a new DRT provider to the repository.
 *
 * This step creates a [MutableDrtProviderData] instance using the provided [idProvider],
 * applies the [scope] to configure it, and then adds it to the [repository] from the context.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [Context].
 * @param repository The mutable repository of DRT providers to which the new provider will be added.
 *                   Provided via context.
 * @param idProvider A function that provides the [DrtProviderId] for the new provider.
 *                   Defaults to [GlobalDrtProviderIdCounter].
 * @param scope A lambda to configure the newly created [MutableDrtProviderData].
 */
context(repository: MutableRepository<MutableDrtProviderData, DrtProviderId>)
fun <C: Context> C.newDrtProvider(
    idProvider: () -> DrtProviderId = GlobalDrtProviderIdCounter,
    scope: MutableDrtProviderData.() -> Unit
) = mutatingStep(
    "add single new drt provider",
) {
    val newDrtProvider = MutableDrtProviderData(id = idProvider())
    newDrtProvider.scope()
    repository.addElements(
        "add single drt provider '${newDrtProvider.name}'",
        listOf(newDrtProvider)
    )
}



//interface AddDrtProviderContext : Context {
//
//    val drtProviderRepository: MutableRepository<MutableDrtProviderData, DrtProviderId>
//    val impedance: LateInit<Impedance>
//}
//
//class DrtProviderCollector {
//    private val providers: MutableList<MutableDrtProviderData> = mutableListOf()
//
//    fun drtProvider(id: DrtProviderId, scope: MutableDrtProviderData.() -> Unit) {
//        val p = MutableDrtProviderData(id)
//        p.scope()
//        providers.add(p)
//    }
//
//    fun drtProvider(scope: MutableDrtProviderData.() -> Unit) {
//        val p = MutableDrtProviderData(DrtProviderId(providerIdCounter++))
//        p.scope()
//        providers.add(p)
//    }
//
//    internal fun getProviders(): List<MutableDrtProviderData> = providers.toList()
//}
//
//private var providerIdCounter = 0L
//
//fun AddDrtProviderContext.newDrtProvider(scope: MutableDrtProviderData.() -> Unit) = runStep {
//    val provider = MutableDrtProviderData(DrtProviderId(providerIdCounter++))
//    provider.scope()
//    AddDrtProviderStep(this, listOf(provider))
//}
//
//fun AddDrtProviderContext.finishDrtProviders() = runStep {
//    SealStep(drtProviderRepository)
//}
//
//fun AddDrtProviderContext.addDrtProvider(drtProvider: () -> MutableDrtProviderData) = runStep {
//    val provider = drtProvider()
//    validateId(provider)
//
//    AddDrtProviderStep(this, listOf(drtProvider()))
//}
//
//private fun validateId(provider: MutableDrtProviderData) {
//    require(provider.id.value >= providerIdCounter) {
//        "Ids up to (no including) $providerIdCounter were already used for DrtProviders.\n" +
//            "Cannot create new provider with id: ${provider.id.value}!"
//    }
//    providerIdCounter = provider.id.value + 1
//}
//
//fun AddDrtProviderContext.addMultipleDrtProvider(scope: DrtProviderCollector.() -> Unit) = runStep {
//    val collector = DrtProviderCollector()
//    collector.scope()
//    val providers = collector.getProviders().sortedBy { it.id.value }.onEach { validateId(it) }
//    AddDrtProviderStep(this, providers)
//}


//TODO add step for alg

@Suppress("MagicNumber")
private val allDay = 0 to 24

/**
 * Configures a simple DRT algorithm.
 *
 * @param impedance The impedance model for distance and duration calculations.
 * @param serviceArea The zones where the DRT service is available.
 * @param numVehicles The number of vehicles in the DRT fleet. Defaults to the number of zones in the service area.
 * @param avgWaitingTime The average waiting time for the DRT service. Defaults to 4 minutes.
 * @param operationHours The hours of operation for the DRT service (start hour to end hour). Defaults to 0-24.
 * @return A [SimpleMatrixDrtAlgorithm] configured with the provided parameters.
 */
fun simpleDrtAlgorithm(
    impedance: Impedance,
    serviceArea: Collection<Zone>,
    numVehicles: Int = serviceArea.size,
    avgWaitingTime: Duration = 4.minutes,
    operationHours: Pair<Int, Int> = allDay,
) = SimpleMatrixDrtAlgorithm(
    impedance,
    avgWaitingTime,
    serviceArea,
    operationHours,
    numVehicles
)

//class AddDrtProviderStep(
//    context: AddDrtProviderContext,
//    providers: List<MutableDrtProviderData>
//) : AddResourceStep<MutableDrtProviderData, DrtProviderId> {
//    override val name = "Add DrtProviders"
//
//    override val resource: Resource<MutableDrtProviderData> = providers.asSequence().asResource(
//        "newDrtProviders",
//        "AddDrtProviderStep"
//    )
//
////    override fun mockElementsForValidation() = emptyList<MutableDrtProviderData>()
//
//    override val repository = context.drtProviderRepository
//
//    override val dependentRepositories = emptySet<Repository<*, *>>()
//
//    override fun verifyInput(): Warning? = null
//
//    override fun mockBehavior() = null
//}
