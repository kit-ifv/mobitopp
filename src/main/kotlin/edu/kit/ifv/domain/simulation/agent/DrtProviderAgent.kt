package edu.kit.ifv.domain.simulation.agent
import edu.kit.ifv.core.statemachine.Message
import edu.kit.ifv.core.statemachine.StateBasedAgent
import edu.kit.ifv.core.statemachine.StateMachine
import edu.kit.ifv.core.statemachine.StateMachineFactory
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.zone.Zone
import edu.kit.ifv.domain.shared.location.zone.attributes.HasZoneId
import edu.kit.ifv.domain.simulation.data.drt.DrtProvider
import edu.kit.ifv.units.Currency
import edu.kit.ifv.utils.units.AbsoluteTime
import edu.kit.ifv.utils.units.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

interface DrtProviderMessage : Message

class DrtProviderAgent(
    val data: DrtProvider,
    val algorithm: DrtAlgorithm,
    stateMachineFactory: StateMachineFactory<DrtProviderAgent>,
) : DrtProvider by data,
    StateBasedAgent<DrtProviderMessage> {

    override val stateMachine: StateMachine = stateMachineFactory.create(AbsoluteTime.START, this)

    fun operatesAt(time: AbsoluteTime, origin: StandardLocation, destination: StandardLocation) = algorithm.operatesAt(
        time,
        origin,
        destination,
    )

    fun requestRide(request: DrtRequest): DrtOffer? = algorithm.requestRide(request)
}

interface DrtAlgorithm {

    fun operatesAt(time: AbsoluteTime, origin: StandardLocation, destination: StandardLocation): Boolean
    fun requestRide(request: DrtRequest): DrtOffer?

    // TODO Offers should always be cached by person in DrtAlg if it is called multiple times! (currently only once!)
    fun bookRide(drtOffer: DrtOffer): DrtRide?
    fun revokeOffer(offer: DrtOffer)

    fun nextActionTime(currentTime: AbsoluteTime): AbsoluteTime

    fun getPendingPickups(time: AbsoluteTime): List<DrtRide>
    fun getPendingArrivals(time: AbsoluteTime): List<DrtRide>
}

data class DrtRequest(
    val provider: DrtProviderAgent,
    val person: PersonAgent,
    val requestTime: AbsoluteTime,
    val departure: AbsoluteTime,
    val origin: StandardLocation,
    val destination: StandardLocation,
    val id: RequestId = RequestId.next(),
)

@JvmInline
value class RequestId private constructor(val value: Long) {

    companion object {
        private var idCounter: Long = 0L
        internal fun next() = RequestId(idCounter++)
    }
}

data class DrtOffer(
    val person: PersonAgent,
    val providerAgent: DrtProviderAgent,
    val origin: StandardLocation,
    val pickupAt: StandardLocation,
    val dropOffAt: StandardLocation,
    val destination: StandardLocation,
    val pickupTime: AbsoluteTime,
    val dropOffTime: AbsoluteTime, // TODO maybe min/max arrival time, guaranteed max time, but window for delays??
    val cost: Currency,
    val timeOfOffer: AbsoluteTime,
    val arrivalTimeAtDest: AbsoluteTime,
    val personsInVehicle: Int,
    val requestId: RequestId,
) {

    init {
        if (timeOfOffer > arrivalTimeAtDest) {
            println("Warning: arrival $arrivalTimeAtDest is before time of offer $timeOfOffer")
        }
    }

    val totalDuration: Duration get() = max(arrivalTimeAtDest - timeOfOffer, 1.minutes)
} // TODO derive access, wait, ride and egress time

data class DrtRide(val offer: DrtOffer) // TODO maybe add car in the future here

@Suppress("LongParameterList")
class SimpleMatrixDrtAlgorithm(
    private val impedance: Impedance,
    private val avgWaitTime: Duration,
    private val serviceArea: Collection<Zone<*>>,
    private val operationHours: Pair<Int, Int>,
    private val numVehicles: Int,
) : DrtAlgorithm {

    private val pendingPickUps: MutableMap<AbsoluteTime, MutableList<DrtRide>> = mutableMapOf()
    private val pendingDropOffs: MutableMap<AbsoluteTime, MutableList<DrtRide>> = mutableMapOf()

    private fun addPickup(ride: DrtRide) {
        pendingPickUps.getOrPut(ride.offer.pickupTime) {
            mutableListOf()
        }.add(ride)
    }

    private fun removePickups(time: AbsoluteTime) = pendingPickUps.remove(time)

    private fun addDropOff(ride: DrtRide) {
        pendingDropOffs.getOrPut(ride.offer.dropOffTime) {
            mutableListOf()
        }.add(ride)
    }

    private fun removeDropOff(absoluteTime: AbsoluteTime) = pendingDropOffs.remove(absoluteTime)

    private fun numPickUps() = pendingPickUps.values.flatten().size
    private fun numDropOffs() = pendingDropOffs.values.flatten().size
    private fun hasCapacity() = numPickUps() + numDropOffs() < numVehicles

    override fun operatesAt(time: AbsoluteTime, origin: StandardLocation, destination: StandardLocation) =
        serviceArea.any {
            (origin as HasZoneId) in it
        } &&
            serviceArea.any { (destination as HasZoneId) in it } &&
            timeInOperatingHours(time)

    private fun timeInOperatingHours(time: AbsoluteTime) = operationHours.let { (start, end) ->
        time.hour in start..end
    }

    override fun requestRide(request: DrtRequest): DrtOffer? = takeIf {
        hasCapacity()
    }?.let {
        request.run {
            val mode = provider.mode
            val pickupTime = departure + avgWaitTime
            val dropOffTime = pickupTime + impedance.duration(origin, destination, mode, pickupTime)
            val cost = impedance.cost(origin, destination, mode, pickupTime)

            DrtOffer(
                person,
                provider,
                origin,
                origin,
                destination,
                destination,
                pickupTime,
                dropOffTime,
                cost,
                departure,
                dropOffTime,
                1,
                request.id,
            )
        }
    }

    override fun bookRide(drtOffer: DrtOffer): DrtRide? = takeIf {
        hasCapacity()
    }?.let {
        DrtRide(drtOffer).also {
            addPickup(it)
        }
    }

    override fun revokeOffer(offer: DrtOffer) {
        // nothing to do
    }

    override fun nextActionTime(currentTime: AbsoluteTime): AbsoluteTime =
        (pendingPickUps.keys + pendingDropOffs.keys).minOrNull() ?: AbsoluteTime.INFINITY

    override fun getPendingPickups(time: AbsoluteTime): List<DrtRide> = removePickups(time)?.onEach {
        addDropOff(it) // move pickups to drop off map
    } ?: emptyList()

    override fun getPendingArrivals(time: AbsoluteTime): List<DrtRide> = removeDropOff(time) ?: emptyList()
}
