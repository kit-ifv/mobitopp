package domain.simulation.agent

import core.statemachine.Message
import core.statemachine.StateBasedAgent
import core.statemachine.StateMachine
import core.statemachine.StateMachineFactory
import domain.shared.location.Location
import domain.synthesis.data.DrtProvider
import domain.synthesis.data.IPerson
import domain.synthesis.data.ISharingProvider
import units.Currency
import utils.units.AbsoluteTime


interface DrtProviderMessage: Message

class DrtProviderAgent(
    val data: DrtProvider,
    val algorithm: DrtAlgorithm,
    stateMachineFactory: StateMachineFactory<DrtProviderAgent>,
): DrtProvider by data, StateBasedAgent<Message> {
    override val stateMachine: StateMachine = stateMachineFactory.create(AbsoluteTime.START, this)

    fun operatesAt(time: AbsoluteTime, origin: Location) = algorithm.operatesAt(time, origin)
    fun requestRide(request: DrtRequest): DrtOffer? = algorithm.requestRide(request)

}

interface DrtAlgorithm {

    fun operatesAt(time: AbsoluteTime, origin: Location): Boolean
    fun requestRide(request: DrtRequest): DrtOffer?
    //TODO Offers should always be cached by person in DrtAlg as it will be called multiple times!
    fun bookRide(drtOffer: DrtOffer): DrtRide?
    fun revokeOffer(offer: DrtOffer)

    fun nextActionTime(): AbsoluteTime

    fun getPendingPickups(time: AbsoluteTime): List<DrtRide>
    fun getPendingArrivals(time: AbsoluteTime): List<DrtRide>

}

data class DrtRequest(
    val person: IPerson,
    val departure: AbsoluteTime,
    val origin: Location,
    val destination: Location
)

data class DrtOffer(
    val person: IPerson,
    val providerAgent: DrtProviderAgent,
    val origin: Location,
    val pickupAt: Location,
    val dropOffAt: Location,
    val destination: Location,
    val pickupTime: AbsoluteTime,
    val dropOffTime: AbsoluteTime, //TODO maybe min/max arrrival time, guaranteed max time, but window for delays??
    val cost: Currency,
    val timeOfOffer: AbsoluteTime
) //TODO derive access, wait, ride and egress time

data class DrtRide(val offer: DrtOffer /*TODO: maybe add car in the future here*/)


