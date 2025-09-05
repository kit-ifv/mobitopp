package domain.simulation.events

import MessageCalled
import StateCalled
import core.statemachine.builder.BaseStateData
import core.statemachine.builder.on
import core.statemachine.builder.stateMachine
import domain.shared.location.Location
import domain.simulation.agent.DrtAlgorithm
import domain.simulation.agent.DrtOffer
import domain.simulation.agent.DrtProviderAgent
import domain.simulation.agent.DrtProviderMessage
import domain.simulation.agent.DrtRequest
import domain.synthesis.data.IPerson
import utils.units.AbsoluteTime

@StateCalled("StartDrtProvider")
class DrtProviderStartState(
    time: AbsoluteTime,
    override val agent: DrtProviderAgent
): BaseStateData(time), DrtAlgorithm by agent.algorithm {
    val self get() = agent
}

@MessageCalled("RequestRide")
data class RequestMessage(val request: DrtRequest): DrtProviderMessage {
    constructor(
        person: IPerson,
        departure: AbsoluteTime,
        origin: Location,
        destination: Location
    ): this(DrtRequest(person, departure, origin, destination))
}

@MessageCalled("BookRide")
data class BookRideMessage(val offer: DrtOffer): DrtProviderMessage

@MessageCalled("RevokeOffer")
data class RevokeOfferMessage(val offer: DrtOffer): DrtProviderMessage

@MessageCalled("PickupDropOffPersons")
class InteractPersonsMessage: DrtProviderMessage



val drtProviderStateMachine = stateMachine<DrtProviderAgent>("DrtProviderStateMachine") {

    start(StartDrtProvider, ::startDrtProvider) {
        // TODO logic on startup if needed
        //
    }.on(RequestRide) { message, send ->
        val offer = requestRide(message.request)
        //send offer to person

        send(pickupDropOffPersons(), self, nextActionTime())
        //
    }.on(BookRide) { message, send ->
        val ride = bookRide(message.offer)
        //send ride to person

        send(pickupDropOffPersons(), self, nextActionTime())
        //
    }.on(RevokeOffer){ message, send ->
        revokeOffer(message.offer)

        send(pickupDropOffPersons(), self, nextActionTime())
        //
    }.on(PickupDropOffPersons) { message, send ->
        for (ride in getPendingPickups(time)) {
            //TODO send pickup to person
        }

        for (ride in getPendingArrivals(time)) {
            //TODO send arrive to person
        }

    }

}