package edu.kit.ifv.domain.simulation.events
import edu.kit.ifv.MessageCalled
import edu.kit.ifv.StateCalled
import edu.kit.ifv.application.steps.HasAttractivenessModel
import edu.kit.ifv.application.steps.HasChoiceModelModes
import edu.kit.ifv.application.steps.HasDestinationChoiceModel
import edu.kit.ifv.application.steps.HasImpedance
import edu.kit.ifv.application.steps.HasModeAvailabilityModel
import edu.kit.ifv.application.steps.HasModeChoiceModel
import edu.kit.ifv.application.steps.HasReplanningStrategy
import edu.kit.ifv.application.steps.HasSpawnDestinationCharacteristics
import edu.kit.ifv.application.steps.HasSpawnModeCharacteristics
import edu.kit.ifv.core.statemachine.Send
import edu.kit.ifv.core.statemachine.StateMachineFactory
import edu.kit.ifv.core.statemachine.builder.BaseStateData
import edu.kit.ifv.core.statemachine.builder.stateMachine
import edu.kit.ifv.domain.shared.behavior.ChoiceModelModes
import edu.kit.ifv.domain.shared.datastructure.schedule.Agenda
import edu.kit.ifv.domain.shared.datastructure.schedule.LinkTrip
import edu.kit.ifv.domain.shared.datastructure.schedule.Representative
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Leg
import edu.kit.ifv.domain.shared.datastructure.schedule.action.LinkedAction
import edu.kit.ifv.domain.shared.datastructure.schedule.action.StationaryAction
import edu.kit.ifv.domain.shared.datastructure.schedule.alternateByImpedance
import edu.kit.ifv.domain.shared.enums.MODEUNKOWN
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.DrtRide
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.agent.PersonMessage
import edu.kit.ifv.domain.simulation.behavior.availability.ModeAvailabilityModel
import edu.kit.ifv.domain.simulation.behavior.availability.ModeResource
import edu.kit.ifv.domain.simulation.behavior.availability.PoolingResource
import edu.kit.ifv.domain.simulation.behavior.modechoice.ModeChoiceCharacteristics
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.utils.concurrent.synchronizeAll
import edu.kit.ifv.utils.units.AbsoluteTime

abstract class PersonState(time: AbsoluteTime, override val agent: PersonAgent, doStep: Boolean) : BaseStateData(time) {

    init {
        if (doStep) {
            person.schedule.step()
        }
    }

    val block: Representative<out LinkedAction>?
        get() = person.schedule.nextBlock()

    // TODO modes only necessary here until dispattch: mode > nested state machine can be defined outside of PersonStates
    val self: PersonAgent
        get() = agent

    val person: PersonAgent
        get() = self

    override fun toString() = "P" + person.id.value.toString()
}

abstract class ActivityState(val agenda: Agenda, time: AbsoluteTime, agent: PersonAgent, doStep: Boolean) :
    PersonState(
        time,
        agent,
        doStep,
    ) {
    constructor(agenda: Agenda, state: PersonState, doStep: Boolean) : this(agenda, state.time, state.agent, doStep)
}

abstract class TripState(val trip: LinkTrip, time: AbsoluteTime, agent: PersonAgent, doStep: Boolean) :
    PersonState(
        time,
        agent,
        doStep,
    ) {
    constructor(trip: LinkTrip, state: PersonState, doStep: Boolean) : this(trip, state.time, state.agent, doStep)

    val origin: StandardLocation
        get() = trip.origin

    val destination: StandardLocation
        get() = trip.destination
}

// typealias AfterLegAction = (PersonAgent, AbsoluteTime) -> Unit
fun interface AfterLegAction {
    fun execute(person: PersonAgent, time: AbsoluteTime)
}

// Person Messages
@MessageCalled("FirstActivity", PersonStartState::class)
data class FirstActivityMessage(val activity: StationaryAction) : PersonMessage

@MessageCalled("EndActivity", PerformingActivityState::class)
data class EndActivityMessage(val activity: StationaryAction) : PersonMessage

@MessageCalled("EndLeg", PerformLegState::class)
data class EndLegMessage(val leg: Leg) : PersonMessage

// Person States
@StateCalled("StartPerson")
class PersonStartState(time: AbsoluteTime, agent: PersonAgent) : PersonState(time, agent, doStep = false)

@StateCalled(
    "PerformingActivity",
    PersonStartState::class,
    PerformingActivityState::class,
    PerformLegState::class,
    FinishDrtTripState::class,
)
class PerformingActivityState(agenda: Agenda, val activity: StationaryAction, state: PersonState) :
    ActivityState(agenda, activity.startTime, state.agent, doStep = true) {
    val location: StandardLocation
        get() = activity.location
}

@StateCalled("StartingTrip", PerformingActivityState::class)
class StartingTripState constructor(trip: LinkTrip, state: PersonState) : TripState(trip, state, doStep = false)

@StateCalled(
    "PerformLeg",
    StartingTripState::class,
    PerformLegState::class,
)
class PerformLegState(trip: LinkTrip, val leg: Leg, state: PersonState) : TripState(trip, state, doStep = true)

// @StateCalled("StartingCarTrip", StartingTripState::class)
// class StartingCarTripState(trip: LinkTrip, state: PersonState) : TripState(trip, state, doStep = false)
//
// @StateCalled("StartingBikeSharingTrip", StartingTripState::class)
// class StartingBikeSharingTripState(trip: LinkTrip, state: PersonState) : TripState(trip, state, doStep = false)

@StateCalled("FinishedPerson", PerformLegState::class, PerformingActivityState::class, FinishDrtTripState::class)
class FinishedPersonState(state: PersonState) : PersonState(state.time, state.agent, doStep = false)

// DRT messages

@MessageCalled("PickupByDrt", DrtProviderStartState::class)
data class PickupByDrtMessage(val ride: DrtRide) : PersonMessage

@MessageCalled("DropOffByDrt", DrtProviderStartState::class)
data class DropOffByDrtMessage(val ride: DrtRide) : PersonMessage

@MessageCalled("FinishDrtEgress", OnDrtEgressState::class)
data class FinishDrtEgressMessage(val ride: DrtRide) : PersonMessage

// DRT states
@StateCalled("WaitingForPickup", StartingTripState::class)
class WaitingForPickupState constructor(state: PersonState, val trip: LinkTrip, val drtRide: DrtRide) :
    PersonState(
        state.time,
        state.agent,
        doStep = false,
    )

@StateCalled("WaitingForDropOff", WaitingForPickupState::class)
class WaitingForDropOffState(state: PersonState, val trip: LinkTrip, val drtRide: DrtRide) :
    PersonState(
        state.time,
        state.agent,
        doStep = false,
    )

@StateCalled("OnDrtEgress", WaitingForDropOffState::class)
class OnDrtEgressState(state: PersonState, val trip: LinkTrip, val drtRide: DrtRide) :
    PersonState(
        state.time,
        state.agent,
        doStep = false,
    )

@StateCalled("FinishDrtTrip", OnDrtEgressState::class)
class FinishDrtTripState(state: PersonState, val trip: LinkTrip, val drtRide: DrtRide) :
    PersonState(
        state.time,
        state.agent,
        doStep = false,
    )

// TODO for DTR
// add person messages sent by drt provider agent (see DrtProviderStateMachine):
// - DrtOffer(offer)
// - DrtRide(ride)
// - PickupByDrt(ride)
// - DropOffByDrt(ride)
// add person self messages and states
// - start drt trip
// - cancel drt trip (if drt ride is null)
// - arrive at pickup
// - State: waiting for pickup
// - (on pickup) >
// - State: waiting for dropoff
// (on dropoff) >
// State: walking to dest
// - send self: finish drt trip

val <C> C.personStateMachine: StateMachineFactory<PersonAgent>
    where C : HasImpedance, C : HasAttractivenessModel, C : HasDestinationChoiceModel, C : HasModeChoiceModel,
          C : HasChoiceModelModes, C : HasModeAvailabilityModel, C : HasSpawnModeCharacteristics,
          C : HasSpawnDestinationCharacteristics, C : HasReplanningStrategy get() =

        stateMachine<PersonAgent>("PersonsStateMachine") {
            start(StartPerson, ::startPerson) { send ->
                val target = person.schedule.activities().first()
                target.location = person.household.location

                val firstActivity = (block as Agenda).elements[0]
                send(firstActivity(firstActivity), self, firstActivity.startTime)
                //
            }.transitionOn(FirstActivity) { message, send ->
                performingActivity((block as Agenda), message.activity)
            }

            state(PerformingActivity) { send ->
                send(endActivity(), self, activity.endTime)
                //
            }.transitionOn(EndActivity) { message, send ->
                person.schedule.step()
                when (block) {
                    is Agenda -> performingActivity(block as Agenda, agenda.elements[0])
                    is LinkTrip -> startingTrip(block as LinkTrip)
                    null -> finishedPerson()
                    else -> error("Expected next block in schedule to be agenda, trip or null but git: $block")
                }
            }

            transState(StartingTrip) { send ->
                // destination choice
                // TODO Robin last.endlocation is destination?

                // check if this hack is still necessary or can be removed
                if ("home" in (trip.nextAction?.type?.description?.lowercase() ?: "")) {
                    trip.elements.last().endLocation = person.household.location
                }

                if (trip.elements.last().endLocation == StandardLocation.LOCATIONUNKNOWN) {
                    val situation = spawnDestinationCharacteristics(person, time, trip)
                    context(situation, person.random) {
                        trip.elements.last().endLocation = destinationChoiceModel.select()
                    }
                    trip.elements.forEach { it.transportType = MODEUNKOWN }
                }
            }.next { send ->
                // mode choice: either continue using mode resource in person state or choose new mode
                val resource = person.modeResource?.also {
                    it.startTrip(person)
                } ?: modeChoice(send, choiceModelModes, modeAvailability, modeChoiceModel, spawnModeCharacteristics)
                // for new mode choice -> startTrip is called inside modeChoice's synchronizeAll block

                trip.alternateByImpedance(impedance, replanner = replanningStrategy) {
                    taking(resource.mode to destination)
                }

                person.inTransit = true

                if (resource is PoolingResource) {
                    waitingForPickup(drtRide = resource.drtRide!!)
                } else {
                    performLeg(leg = trip.elements[0])
                }
            }

            state(PerformLeg) { send ->
                send(endLeg(), self, leg.endTime)
                //
            }.transitionOn(EndLeg) { message, send ->
                person.location = leg.endLocation
                person.schedule.step()

                when (block) {
                    null -> {
                        person.modeResource = person.modeResource?.endTrip(person)
                        finishedPerson()
                    }

                    is LinkTrip -> performLeg(block as LinkTrip, leg = trip.legs[0])

                    is Agenda -> {
                        person.modeResource = person.modeResource!!.endTrip(person)
                        val agenda = block as Agenda
                        agenda.elements.firstOrNull()?.let {
                            performingActivity(agenda, agenda.elements[0])
                        } ?: finishedPerson()
                    }

                    else -> error(
                        "Cannot process EndLeg: '$message' ins PerformLeg state: $this!" +
                            " Current schedule block should be Agenda or LinkTrip but is of type " +
                            block.agendaBlockDescription(),
                    )
                }
            }

            finState(FinishedPerson)

            // DRT states

            state(WaitingForPickup) {
                // move leg into present
                person.schedule.step()
                println("${person.id.value} waits for pickup by ${drtRide.offer.providerAgent.name} at $time")
            }.transitionOn(PickupByDrt) { message, send ->

                // pedestrian leg finished, pooling leg starts
                println("Provider ${drtRide.offer.providerAgent.name} picks up ${person.id.value} at $time")
                person.location = drtRide.offer.pickupAt
                waitingForDropOff()
            }

            state(WaitingForDropOff) {
                //
                println("${person.id.value} waits for drop off by ${drtRide.offer.providerAgent.name} at $time")
            }.transitionOn(DropOffByDrt) { message, send ->
                println("Provider ${drtRide.offer.providerAgent.name} drops off up ${person.id.value} at $time")
                // pooling leg finished, ped egress leg starts
                person.location = drtRide.offer.dropOffAt
                onDrtEgress()
            }

            state(OnDrtEgress) { send ->
                val arrival = maxOf(time, drtRide.offer.arrivalTimeAtDest)
                send(finishDrtEgress(drtRide), self, arrival)
                //
            }.transitionOn(FinishDrtEgress) { message, send ->
                // "ped egress leg" finished
                person.location = drtRide.offer.destination

                person.schedule.step() // move leg to past
                finishDrtTrip()
            }

            transState(FinishDrtTrip).next { send ->
                person.modeResource = person.modeResource?.endTrip(person)
                when (block) {
                    null -> finishedPerson()

                    is Agenda -> {
                        val agenda = block as Agenda
                        agenda.elements.firstOrNull()?.let {
                            performingActivity(agenda, agenda.elements[0])
                        } ?: finishedPerson()
                    }

                    else -> error(
                        "Expected next block to be null or Agenda in FinishDrtTrip state: $this!" +
                            " Current schedule block should be Agenda or null but is of type " +
                            block.agendaBlockDescription(),
                    )
                }
            }
        }

private fun Representative<out LinkedAction>?.agendaBlockDescription(): String = "${this?.let {
    it::class.simpleName
} ?: "null"}: '$this'"

internal fun StartingTripState.modeChoice( // Add ignore modes for recursive call
    send: Send,
    modes: ChoiceModelModes,
    modeAvailability: ModeAvailabilityModel,
    modeChoiceModel: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,
    spawnModeCharacteristics: GenerateModeCharacteristics<ModeChoiceCharacteristics>,
): ModeResource {
    val providerAvail = modes.options.map {
        modeAvailability.providerAvailability(it, person, time, destination)
    }.filter { it.isAvailable }

    val choices = providerAvail.map { it.mode }
    val providers = providerAvail.flatMap { it.providers!!.toList() }.distinct().toSet()

    val mode = synchronizeAll(providers) {
        val resource = retryModeChoiceOnRideUnavailable(choices, send, modes) { choiceSet ->
            val modeSituation = spawnModeCharacteristics(
                person,
                time,
                origin,
                destination,
                choiceSet,
                null,
            )

            val mcAvail = choices.mapNotNull {
                modeAvailability.resourceAvailability(it, modeSituation)
            }.associateBy { it.mode }

            val mode = context(modeSituation, person.random) {
                modeChoiceModel.select(mcAvail.keys)
            }

            return@retryModeChoiceOnRideUnavailable mcAvail[mode]!!
        }

        person.modeResource = resource
        resource.startTrip(person)
        resource
    }

    return mode
}

internal fun StartingTripState.retryModeChoiceOnRideUnavailable(
    choices: List<Mode>,
    send: Send,
    modes: ChoiceModelModes,
    modeChoiceScope: StartingTripState.(List<Mode>) -> ModeResource,
): ModeResource {
    var mode = modeChoiceScope(choices)

    if (mode is PoolingResource) {
        mode.startTrip(person)

        if (mode.drtRide == null) {
            mode.drtOffer.providerAgent.algorithm.revokeOffer(mode.drtOffer)
            val modesNoDrt = choices - modes.ridePooling
            mode = modeChoiceScope(modesNoDrt)
            // if we re-chose a PoolingResource, we might need to book it too,
            // but usually ridePooling is one option or we assume the next choice is not ridePooling
        } else {
            // TODO since request/booking/revoke not via messages currently,
            // trigger provider state machine in case it went inactive
            send.now(pickupDropOffPersons(), mode.drtOffer.providerAgent)
        }
    }

    return mode
}
