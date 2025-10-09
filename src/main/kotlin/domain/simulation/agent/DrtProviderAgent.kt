package domain.simulation.agent

import core.statemachine.Message
import core.statemachine.StateBasedAgent
import core.statemachine.StateMachine
import core.statemachine.StateMachineFactory
import domain.shared.enums.Mode
import domain.shared.location.Location
import domain.shared.location.Metrics
import domain.shared.location.Zone
import domain.simulation.events.PersonBehavior
import domain.synthesis.data.DrtProvider
import units.Currency
import utils.units.AbsoluteTime
import utils.units.Time
import kotlin.time.Duration

interface DrtProviderMessage : Message

class DrtProviderAgent(
    val data: DrtProvider,
    val algorithm: DrtAlgorithm,
    stateMachineFactory: StateMachineFactory<DrtProviderAgent>,
) : DrtProvider by data, StateBasedAgent<Message> {

    override val stateMachine: StateMachine = stateMachineFactory.create(AbsoluteTime.START, this)

    fun operatesAt(time: AbsoluteTime, origin: Location, destination: Location) =
        algorithm.operatesAt(time, origin, destination)

    fun requestRide(request: DrtRequest): DrtOffer? = algorithm.requestRide(request)
}

interface DrtAlgorithm {

    fun operatesAt(time: AbsoluteTime, origin: Location, destination: Location): Boolean
    fun requestRide(request: DrtRequest): DrtOffer?

    // TODO Offers should always be cached by person in DrtAlg if it is called multiple times! (currently only once!)
    fun bookRide(drtOffer: DrtOffer): DrtRide?
    fun revokeOffer(offer: DrtOffer)

    fun nextActionTime(): AbsoluteTime

    fun getPendingPickups(time: AbsoluteTime): List<DrtRide>
    fun getPendingArrivals(time: AbsoluteTime): List<DrtRide>
}

data class DrtRequest(
    val provider: DrtProviderAgent,
    val person: PersonAgent,
    val departure: AbsoluteTime,
    val origin: Location,
    val destination: Location
)

data class DrtOffer(
    val person: PersonAgent,
    val providerAgent: DrtProviderAgent,
    val origin: Location,
    val pickupAt: Location,
    val dropOffAt: Location,
    val destination: Location,
    val pickupTime: AbsoluteTime,
    val dropOffTime: AbsoluteTime, // TODO maybe min/max arrival time, guaranteed max time, but window for delays??
    val cost: Currency,
    val timeOfOffer: AbsoluteTime,
    val arrivalTimeAtDest: AbsoluteTime
) {

    val totalDuration: Duration get() = arrivalTimeAtDest - timeOfOffer
} // TODO derive access, wait, ride and egress time

data class DrtRide(val offer: DrtOffer) // TODO maybe add car in the future here

// TODO temporary solution to get offer information into characteristics without creating whole new set of ModeChoiceCharacteristics sub class
data class DrtImpedance(
    private val impedance: Metrics,
    private val offer: DrtOffer,
    private val drtMode: Mode
) : Metrics by impedance {

    override fun cost(from: Location, to: Location, mode: Mode, time: Time): Currency =
        if (mode == drtMode) {
            offer.cost
        } else {
            super.cost(from, to, mode, time)
        }

    override fun duration(from: Location, to: Location, mode: Mode, time: Time): Duration =
        if (mode == drtMode) {
            offer.totalDuration
        } else {
            super.duration(from, to, mode, time)
        }
}

fun PersonBehavior.withDrtImpedance(offer: DrtOffer, drtMode: Mode) =
    this.copy(impedance = DrtImpedance(this.impedance, offer, drtMode))

@Suppress("LongParameterList")
class DummyDrtAlgorithm(
    private val impedance: Metrics,
    private val avgWaitTime: Duration,
    private val serviceArea: Collection<Zone>,
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

    private fun removePickups(time: AbsoluteTime) =
        pendingPickUps.remove(time)

    private fun addDropOff(ride: DrtRide) {
        pendingDropOffs.getOrPut(ride.offer.dropOffTime) {
            mutableListOf()
        }.add(ride)
    }

    private fun removeDropOff(absoluteTime: AbsoluteTime) =
        pendingDropOffs.remove(absoluteTime)

    private fun numPickUps() = pendingPickUps.values.flatten().size
    private fun numDropOffs() = pendingDropOffs.values.flatten().size
    private fun hasCapacity() = numPickUps() + numDropOffs() < numVehicles

    override fun operatesAt(time: AbsoluteTime, origin: Location, destination: Location) =
        serviceArea.any { origin in it } &&
            serviceArea.any { destination in it } &&
            timeInOperatingHours(time)

    private fun timeInOperatingHours(time: AbsoluteTime) =
        operationHours.let { (start, end) -> start <= time.hour && time.hour <= end }

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
                dropOffTime
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

    override fun nextActionTime(): AbsoluteTime =
        (pendingPickUps.keys + pendingDropOffs.keys).minOrNull() ?: AbsoluteTime.INFINITY

    override fun getPendingPickups(time: AbsoluteTime): List<DrtRide> =
        removePickups(time)?.onEach {
            addDropOff(it) // move pickups to drop off map
        } ?: emptyList()

    override fun getPendingArrivals(time: AbsoluteTime): List<DrtRide> =
        removeDropOff(time) ?: emptyList()
}
