package domain.simulation.events

import MessageCalled
import StateCalled
import core.statemachine.Send
import core.statemachine.builder.BaseStateData
import core.statemachine.builder.on
import core.statemachine.builder.stateMachine
import domain.simulation.agent.DrtAlgorithm
import domain.simulation.agent.DrtProviderAgent
import domain.simulation.agent.DrtProviderMessage
import utils.units.AbsoluteTime

@StateCalled("StartDrtProvider")
class DrtProviderStartState(
    time: AbsoluteTime,
    override val agent: DrtProviderAgent,
    var lastSentUpdateTime: AbsoluteTime = AbsoluteTime.START
) : BaseStateData(time), DrtAlgorithm by agent.algorithm {
    val self get() = agent
}

// TODO classes for detailed message protocol (when no longer using mutex locks!)
// @MessageCalled("RequestRide", StartingTripState::class)
// data class RequestMessage(val request: DrtRequest): DrtProviderMessage {
//    constructor(
//        person: PersonAgent,
//        departure: AbsoluteTime,
//        origin: Location,
//        destination: Location
//    ): this(DrtRequest(person, departure, origin, destination))
// }
//
// @MessageCalled("BookRide")
// data class BookRideMessage(val offer: DrtOffer): DrtProviderMessage
//
// @MessageCalled("RevokeOffer")
// data class RevokeOfferMessage(val offer: DrtOffer): DrtProviderMessage

@MessageCalled("PickupDropOffPersons")
class InteractPersonsMessage : DrtProviderMessage

val drtProviderStateMachine = stateMachine<DrtProviderAgent>("DrtProviderStateMachine") {

    start(StartDrtProvider, { t, a -> startDrtProvider(t, a, AbsoluteTime.START) }) {
        // TODO logic on startup if needed
        //
    }.on(PickupDropOffPersons) { message, send ->

        synchronized(agent) {
//            println("Update of Drt ${agent.name} at time $time (next action time: ${nextActionTime(time)})")

//            if (time < nextActionTime(time)) {
//                sendNextActionUpdate(send)
//                return@on
//            }

            for (ride in getPendingPickups(time)) {
                send.now(pickupByDrt(ride), ride.offer.person)
            }

            for (ride in getPendingArrivals(time)) {
                send.now(dropOffByDrt(ride), ride.offer.person)
            }

            sendNextActionUpdate(send)
        }
    }

// TODO logic for detailed message protocol (when no longer using mutex locks!)

//    .on(RequestRide) { message, send ->
//        val offer = requestRide(message.request)
//        //send offer to person
//
//        send(pickupDropOffPersons(), self, nextActionTime())
//        //
//    }.on(BookRide) { message, send ->
//        val ride = bookRide(message.offer)
//        //send ride to person
//
//        send(pickupDropOffPersons(), self, nextActionTime())
//        //
//    }.on(RevokeOffer){ message, send ->
//        revokeOffer(message.offer)
//
//        send(pickupDropOffPersons(), self, nextActionTime())
//        //
//    }
}

fun DrtProviderStartState.sendNextActionUpdate(send: Send) {
    nextActionTime(time).takeIf {
        it != AbsoluteTime.INFINITY && it > lastSentUpdateTime
    }?.also { updateTime ->
        lastSentUpdateTime = updateTime
        send(pickupDropOffPersons(), self, updateTime)
    }
}
