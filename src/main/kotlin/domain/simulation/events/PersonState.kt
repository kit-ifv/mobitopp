package domain.simulation.events

import MessageCalled
import StateCalled
import core.statemachine.builder.BaseStateData
import core.statemachine.builder.stateMachine
import discreteChoice.models.FixedChoicesModel
import domain.shared.behavior.AttractivenessModel
import domain.shared.behavior.ChoiceModelModes
import domain.shared.datastructure.schedule.Agenda
import domain.shared.datastructure.schedule.Leg
import domain.shared.datastructure.schedule.LinkTrip
import domain.shared.datastructure.schedule.LinkedAction
import domain.shared.datastructure.schedule.Representative
import domain.shared.datastructure.schedule.StationaryAction
import domain.shared.datastructure.schedule.alternateByImpedance
import domain.shared.enums.MODEUNKOWN
import domain.shared.enums.Mode
import domain.shared.location.LOCATIONUNKNOWN
import domain.shared.location.Location
import domain.shared.location.Metrics
import domain.simulation.agent.PersonAgent
import domain.simulation.agent.PersonMessage
import domain.simulation.agent.PrivateCarAgent
import domain.simulation.agent.getBestCar
import domain.simulation.agent.locationBySchedule
import domain.simulation.behavior.BikeSharingConnectionSelector
import domain.simulation.behavior.DestinationAlternative
import domain.simulation.behavior.ModeAvailabilityFilter
import domain.simulation.behavior.ModeChoiceAlternative
import domain.simulation.behavior.ModeChoiceSituation
import domain.simulation.behavior.TripChoiceSituation
import utils.concurrent.synchronizeAll
import utils.units.AbsoluteTime

abstract class PersonState(time: AbsoluteTime, override val agent: PersonAgent, doStep: Boolean) : BaseStateData(
    time
) {

    init {
        if (doStep) {
            person.schedule.step()
        }
    }

    val block: Representative<out LinkedAction>?
        get() = person.schedule.nextBlock()

    val behavior: PersonBehavior
        get() = person.behavior

    val impedance: Metrics
        get() = behavior.impedance

    val modeAvailability: ModeAvailabilityFilter
        get() = behavior.availabilityModel

    val modeChoice: FixedChoicesModel<ModeChoiceAlternative, Mode>
        get() = behavior.modeChoice

    val destinationChoice: FixedChoicesModel<DestinationAlternative, Location>
        get() = behavior.destinationChoice

    val modes: ChoiceModelModes
        get() = behavior.choiceModelModes

    val attractivity: AttractivenessModel
        get() = behavior.attractivityModel

    val bikeSharingConnections: BikeSharingConnectionSelector
        get() = behavior.bikeSharingConnectionSelector

    val self: PersonAgent
        get() = agent

    val person: PersonAgent
        get() = self
}

abstract class ActivityState(val agenda: Agenda, time: AbsoluteTime, agent: PersonAgent, doStep: Boolean) : PersonState(
    time,
    agent,
    doStep
) {
    constructor(agenda: Agenda, state: PersonState, doStep: Boolean) : this(agenda, state.time, state.agent, doStep)
}

abstract class TripState(val trip: LinkTrip, time: AbsoluteTime, agent: PersonAgent, doStep: Boolean) : PersonState(
    time,
    agent,
    doStep
) {
    constructor(trip: LinkTrip, state: PersonState, doStep: Boolean) : this(trip, state.time, state.agent, doStep)

    val origin: Location
        get() = trip.origin

    val destination: Location
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

@StateCalled("PerformingActivity", PersonStartState::class, PerformingActivityState::class, PerformLegState::class)
class PerformingActivityState(
    agenda: Agenda,
    val activity: StationaryAction,
    state: PersonState
) : ActivityState(agenda, activity.startTime, state.agent, doStep = true) {
    val location: Location
        get() = activity.location
}

@StateCalled("StartingTrip", PerformingActivityState::class)
class StartingTripState(trip: LinkTrip, state: PersonState) : TripState(trip, state, doStep = false)

@StateCalled(
    "PerformLeg",
    StartingTripState::class,
    StartingCarTripState::class,
    StartingBikeSharingTripState::class,
    PerformLegState::class
)
class PerformLegState(trip: LinkTrip, val leg: Leg, val afterLegAction: AfterLegAction, state: PersonState) :
    TripState(trip, state, doStep = true)

@StateCalled("StartingCarTrip", StartingTripState::class)
class StartingCarTripState(trip: LinkTrip, state: PersonState) : TripState(trip, state, doStep = false)

@StateCalled("StartingBikeSharingTrip", StartingTripState::class)
class StartingBikeSharingTripState(trip: LinkTrip, state: PersonState) : TripState(trip, state, doStep = false)

@StateCalled("FinishedPerson", PerformLegState::class, PerformingActivityState::class)
class FinishedPersonState(state: PersonState) : PersonState(state.time, state.agent, doStep = false)

val personStateMachine = stateMachine<PersonAgent>("PersonsStateMachine") {

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

    transState(StartingTrip) {

        // mode and destination choice
        // TODO Robin last.endlocation is destination?
        if (trip.elements.last().endLocation == LOCATIONUNKNOWN) {
            trip.elements.last().endLocation = destinationChoice.filterAndSelect(
                trip.elements.last().let {
                    TripChoiceSituation(
                        person,
                        time,
                        it.startLocation,
                        impedance,
                        attractivity,
                        modeAvailability
                    )
                }
            )
            trip.elements.forEach { it.transportType = MODEUNKOWN }
        }
    }.next { send ->
        val (_, sharedResources) = modeAvailability.situativeAvailability(person)

        println(sharedResources)
        synchronizeAll(sharedResources) {
            val mode: Mode = modeChoice.filterAndSelect(
                ModeChoiceSituation(person, time, origin, destination, impedance),
            )

            trip.alternateByImpedance(impedance) {
                taking(mode to destination)
            }

            person.inTransit = true

            val noAction = AfterLegAction { a, t -> Unit }
            when (mode) {
                modes.car -> startingCarTrip()
                modes.bikeSharing -> startingBikeSharingTrip()
                else -> performLeg(leg = trip.elements[0], afterLegAction = noAction)
            }
        }

    }

    transState(StartingCarTrip).next {
        val car = person.getBestCar()

        car.keyHolder = person
        car.addDriver(person)
        car.state = PrivateCarAgent.CarState.IN_USE

        var returned = false
        val checkEndOfCarTrip = AfterLegAction { a, t ->
            if (a.locationBySchedule() == destination && !returned) {
                car.location = a.location
                car.removeDriver()
                car.state = PrivateCarAgent.CarState.PARKED
                if (a.locationBySchedule() == a.household.location) { // TODO check
                    car.keyHolder = null
                    returned = true
                }
            }
        }

        performLeg(leg = trip.elements[0], afterLegAction = checkEndOfCarTrip)
    }

    transState(StartingBikeSharingTrip).next { send ->
        val maybeBikesharing = bikeSharingConnections.findConnection(
            ModeChoiceAlternative(person, time, origin, destination, modes.bikeSharing, impedance),
        )
        val (startStation, endStation) = requireNotNull(maybeBikesharing) {
            "How did you manage to select bikesharing if no connection available?\n" +
                " - check availability model: ${modeAvailability::class.simpleName}\n" +
                " - check connection model: ${bikeSharingConnections::class.simpleName}"
        }

        trip.alternateByImpedance(impedance) {
            taking(modes.pedestrian to startStation.location)
            taking(modes.bikeSharing to endStation.location)
            taking(modes.pedestrian to destination)
        }

        val vehicle = startStation.takeAny()

        var returned = false
        val checkBikeReturn = AfterLegAction { a, t ->
            if (a.location == endStation.location && !returned) {
                vehicle.returnTo(endStation)
                returned = true
            }
        }

        performLeg(leg = trip.elements[0], afterLegAction = checkBikeReturn)
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
                    "${block?.let {it::class.simpleName} ?: "null"}: '$block'"
            )
        }
    }

    finState(FinishedPerson)
}
