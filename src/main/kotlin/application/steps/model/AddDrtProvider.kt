package application.steps.model

import core.modelsteps.Context
import core.modelsteps.resources.MutableRepository
import core.modelsteps.scopes.mutatingStep
import domain.shared.location.Impedance
import domain.shared.location.zone.Zone
import domain.simulation.agent.SimpleMatrixDrtAlgorithm
import domain.simulation.data.DrtProviderId
import domain.synthesis.data.MutableDrtProviderData
import domain.simulation.parser.GlobalDrtProviderIdCounter
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
 * @param idProvider A function that provides the [domain.simulation.data.DrtProviderId] for the new provider.
 *                   Defaults to [domain.simulation.parser.GlobalDrtProviderIdCounter].
 * @param scope A lambda to configure the newly created [MutableDrtProviderData].
 */
context(repository: MutableRepository<MutableDrtProviderData, domain.simulation.data.DrtProviderId>)
fun <C : Context> C.newDrtProvider(
    idProvider: () -> domain.simulation.data.DrtProviderId = _root_ide_package_.domain.simulation.parser.GlobalDrtProviderIdCounter,
    scope: MutableDrtProviderData.() -> Unit,
) = mutatingStep(
    "add single new drt provider",
) {
    val newDrtProvider = MutableDrtProviderData(id = idProvider())
    newDrtProvider.scope()
    repository.addElements(
        "add single drt provider '${newDrtProvider.name}'",
        listOf(newDrtProvider),
    )
}

// TODO add step for alg

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
    serviceArea: Collection<Zone<*>>,
    numVehicles: Int = serviceArea.size,
    avgWaitingTime: Duration = 4.minutes,
    operationHours: Pair<Int, Int> = allDay,
) = SimpleMatrixDrtAlgorithm(
    impedance,
    avgWaitingTime,
    serviceArea,
    operationHours,
    numVehicles,
)
