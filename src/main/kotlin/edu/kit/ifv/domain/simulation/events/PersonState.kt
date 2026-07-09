package edu.kit.ifv.domain.simulation.events
import edu.kit.ifv.MessageCalled
import edu.kit.ifv.StateCalled
import edu.kit.ifv.application.steps.HasAttractivenessModel
import edu.kit.ifv.application.steps.HasChoiceModelModes
import edu.kit.ifv.application.steps.HasImpedance
import edu.kit.ifv.application.steps.HasModeChoiceModel
import edu.kit.ifv.application.steps.HasMutableDestinationChoiceModel
import edu.kit.ifv.application.steps.HasMutableModeAvailabilityModel
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
import edu.kit.ifv.domain.shared.datastructure.schedule.replanning.ReplanningStrategy
import edu.kit.ifv.domain.shared.enums.MODEUNKOWN
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.agent.DrtOffer
import edu.kit.ifv.domain.simulation.agent.DrtRide
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.agent.PersonMessage
import edu.kit.ifv.domain.simulation.agent.PrivateCarAgent
import edu.kit.ifv.domain.simulation.agent.getBestCar
import edu.kit.ifv.domain.simulation.behavior.AvailabilityModelWithSharing
import edu.kit.ifv.domain.simulation.behavior.BikeSharingConnectionSelector
import edu.kit.ifv.domain.simulation.behavior.DrtAvailabilitySelector
import edu.kit.ifv.domain.simulation.behavior.flatten
import edu.kit.ifv.utils.concurrent.synchronizeAll
import edu.kit.ifv.utils.units.AbsoluteTime

abstract class
PersonState(time: AbsoluteTime, override val agent: PersonAgent, doStep: Boolean) : BaseStateData(time) {

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
class PerformLegState(trip: LinkTrip, val leg: Leg, val afterLegAction: AfterLegAction, state: PersonState) :
    TripState(trip, state, doStep = true)

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
        doStep = true,
    )

@StateCalled("OnDrtEgress", WaitingForDropOffState::class)
class OnDrtEgressState(state: PersonState, val trip: LinkTrip, val drtRide: DrtRide) :
    PersonState(
        state.time,
        state.agent,
        doStep = true,
    )

@StateCalled("FinishDrtTrip", OnDrtEgressState::class)
class FinishDrtTripState(state: PersonState, val trip: LinkTrip, val drtRide: DrtRide) :
    PersonState(
        state.time,
        state.agent,
        doStep = true,
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

fun <C> C.personStateMachine(): StateMachineFactory<PersonAgent>
where C : HasImpedance, C : HasAttractivenessModel, C : HasMutableDestinationChoiceModel, C : HasModeChoiceModel,
      C : HasChoiceModelModes, C : HasMutableModeAvailabilityModel, C : HasSpawnModeCharacteristics, C : HasSpawnDestinationCharacteristics,
      C : HasReplanningStrategy =

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
            context(choiceModelModes, modeAvailability) {
                // mode choice

                // TODO add version of ModeAvailabilityFilter with fixed global choice set
                val (choices, sharedResources) = context(person, time, destination) {
                    choiceModelModes.options.map { modeAvailability.providerAvailability(it) }
                }.flatten()

                synchronizeAll(sharedResources.distinct().toSet()) {
                    val (mode, drtRide) = modeChoiceDrtWrapper(choices, send) { choiceSet, drtOffer ->
                        val modeSituation = spawnModeCharacteristics(
                            person,
                            time,
                            origin,
                            destination,
                            choiceSet,
                            drtOffer,
                        )

                        val modeResult = context(modeSituation, person.random) {
                            val mcAvail = choices.filter {
                                context(impedance) {
                                    modeAvailability.resourceAvailability(it)
                                }
                            }
                            modeChoiceModel.select(mcAvail.toSet())
                        }
                        modeResult
                    }

                    trip.alternateByImpedance(impedance, replanner = replanningStrategy) {
                        taking(mode to destination)
                    }

                    person.inTransit = true

                    // TODO after leg action is temporary hack until nested state machine is possible
                    val noAction = AfterLegAction { a, t -> Unit }
                    context(impedance, replanningStrategy, modeAvailability) {
                        when (mode) {
                            choiceModelModes.car -> startingCarTrip()
                            choiceModelModes.bikeSharing -> startingBikeSharingTrip()
                            choiceModelModes.ridePooling -> startingRidePoolingTrip(drtRide!!)
                            else -> performLeg(leg = trip.elements[0], afterLegAction = noAction)
                        }
                    }
                }
            }
        }

        state(PerformLeg) { send ->
            send(endLeg(), self, leg.endTime)
            //
        }.transitionOn(EndLeg) { message, send ->
            person.location = leg.endLocation
            person.schedule.step()
            afterLegAction.execute(person, time)

            when (block) {
                null -> finishedPerson()

                is LinkTrip -> performLeg(block as LinkTrip, leg = trip.legs[0])

                is Agenda -> {
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
            //
        }.transitionOn(PickupByDrt) { message, send ->
            // pedestrian leg finished, pooling leg starts
            person.location = trip.elements[0].endLocation
            person.schedule.step()
            waitingForDropOff()
        }

        state(WaitingForDropOff) {
            //
        }.transitionOn(DropOffByDrt) { message, send ->
            // pooling leg finished, ped egress leg starts
            person.location = trip.elements[0].endLocation
            person.schedule.step()
            onDrtEgress()
        }

        state(OnDrtEgress) { send ->
            val arrival = maxOf(time, drtRide.offer.arrivalTimeAtDest)
            send(finishDrtEgress(drtRide), self, arrival)
            //
        }.transitionOn(FinishDrtEgress) { message, send ->
            // ped egress leg finished
            person.location = trip.elements[0].endLocation
            person.schedule.step()
            finishDrtTrip()
        }

        transState(FinishDrtTrip).next { send ->

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

fun StartingTripState.startingCarTrip(): PerformLegState {
    val car = person.getBestCar()

    car.state = PrivateCarAgent.CarState.IN_USE
    car.addDriver(person)
    car.keyHolder = person

    var returned = false
    val checkEndOfCarTrip = AfterLegAction { a, t ->
        if (a.location == destination && !returned) {
            car.location = destination
            car.removeDriver()
            car.state = PrivateCarAgent.CarState.PARKED
            if (destination == a.household.location) { // TODO check
                car.keyHolder = null
                returned = true
            }
        }
    }

    return performLeg(leg = trip.elements[0], afterLegAction = checkEndOfCarTrip)
}

context(
    bikeSharingConnections: BikeSharingConnectionSelector,
    impedance: Impedance,
    replanningStrategy: ReplanningStrategy,
    modes: ChoiceModelModes,
    modeAvailability: AvailabilityModelWithSharing
)
fun StartingTripState.startingBikeSharingTrip(): PerformLegState {
    val maybeBikesharing = modeAvailability.findConnection(person, destination)

    if (maybeBikesharing == null) {
        println("NOOO")
    }

    val (startStation, endStation) = requireNotNull(maybeBikesharing) {
        "How did you manage to select bikesharing if no connection available?\n" +
            " - check availability model: ${modeAvailability::class.simpleName}\n" + // TODO: Is there really a need of modeAvailability here ??
            " - check connection model: ${bikeSharingConnections::class.simpleName}"
    }

    trip.alternateByImpedance(impedance, replanner = replanningStrategy) {
        taking(modes.pedestrian to startStation.location)
        taking(modes.bikeSharing to endStation.location)
        taking(modes.pedestrian to destination)
    }

    val vehicle = startStation.takeAny()

    var returned = false
    val checkBikeReturn = AfterLegAction { a, t ->
        if (a.location == endStation.location && !returned) {
            synchronized(endStation) {
                vehicle.returnTo(endStation)
            }
            returned = true
        }
    }

    return performLeg(leg = trip.elements[0], afterLegAction = checkBikeReturn)
}

context(impedance: Impedance, replanningStrategy: ReplanningStrategy, modes: ChoiceModelModes)
fun StartingTripState.startingRidePoolingTrip(drtRide: DrtRide): WaitingForPickupState {
    trip.alternateByImpedance(impedance, replanner = replanningStrategy) {
        taking(modes.pedestrian to drtRide.offer.pickupAt)
        taking(modes.ridePooling to drtRide.offer.dropOffAt)
        taking(modes.pedestrian to destination)
    }

    return waitingForPickup(drtRide = drtRide)
}

context(drtAvailabilitySelector: DrtAvailabilitySelector, modes: ChoiceModelModes)
internal fun StartingTripState.modeChoiceDrtWrapper(
    choices: List<Mode>,
    send: Send,
    modeChoiceScope: StartingTripState.(List<Mode>, DrtOffer?) -> Mode,
): Pair<Mode, DrtRide?> {
    val drtOffers = takeIf { modes.ridePooling in choices }?.let {
        context(person, time, destination) {
            drtAvailabilitySelector.findDrtOffers()
        }
    } ?: emptyList()

    val drtOffer = drtOffers.minByOrNull { it.totalDuration }

    var mode = modeChoiceScope(choices, drtOffer)

    var drtRide: DrtRide? = null
    if (mode == modes.ridePooling) {
        drtOffer?.also {
            drtRide = it.providerAgent.algorithm.bookRide(it)
        }
        drtOffers.filter { it != drtOffer }.forEach {
            it.providerAgent.algorithm.revokeOffer(it)
        }

        if (drtRide == null) {
            val modesNoDrt = choices - modes.ridePooling
            mode = modeChoiceScope(modesNoDrt, null)
        } else {
            // TODO since request/booking/revoke not via messages currently,
            // trigger provider state machine in case it went inactive
            send.now(pickupDropOffPersons(), drtRide.offer.providerAgent)
        }
    } else {
        drtOffers.forEach {
            it.providerAgent.algorithm.revokeOffer(it)
        }
    }

    return mode to drtRide
}
