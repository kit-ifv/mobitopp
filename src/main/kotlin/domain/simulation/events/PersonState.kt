package domain.simulation.events

import MessageCalled
import StateCalled
import core.statemachine.builder.BaseStateData
import core.statemachine.builder.stateMachine
import discreteChoice.models.FixedChoicesModel
import domain.shared.datastructure.schedule.Agenda
import domain.shared.datastructure.schedule.Leg
import domain.shared.datastructure.schedule.LinkTrip
import domain.shared.datastructure.schedule.LinkedAction
import domain.shared.datastructure.schedule.Representative
import domain.shared.datastructure.schedule.StationaryAction
import domain.shared.datastructure.schedule.Trip
import domain.shared.datastructure.schedule.alternateByImpedance
import domain.shared.enums.MODEUNKOWN
import domain.shared.enums.Mode
import domain.shared.location.LOCATIONUNKNOWN
import domain.shared.location.Location
import domain.simulation.agent.PersonAgent
import domain.simulation.agent.PersonMessage
import domain.simulation.behavior.ModeChoiceSituation
import domain.simulation.behavior.TripChoiceSituation
import domain.synthesis.data.Person
import utils.concurrent.synchronizeAll
import utils.units.AbsoluteTime

abstract class PersonState(time: AbsoluteTime, override val agent: PersonAgent, doStep: Boolean = true) : BaseStateData(time) {

    init {
        if(doStep) {
            person.schedule.step()
        }
    }

    val block: Representative<out LinkedAction>?
        get() = person.schedule.nextBlock()

    val agenda: Agenda
        get() = block as? Agenda ?: error(
            "Expected current block in state ${this::class.simpleName} to be Agenda, but got: $block"
        )

    val trip: LinkTrip
        get() = block as? LinkTrip ?: error(
            "Expected current block in state ${this::class.simpleName} to be LinkTrip, but got: $block"
        )

    val behavior: PersonBehavior
        get() = person.behavior

    val self: PersonAgent
        get() = agent

    val person: PersonAgent
        get() = self
}

// Person Messages
@MessageCalled("FirstActivity", PersonStartState::class)
data class FirstActivityMessage(val activity: StationaryAction): PersonMessage

@MessageCalled("EndActivity", PerformingActivityState::class)
data class EndActivityMessage(val activity: StationaryAction): PersonMessage

@MessageCalled("EndLeg", PerformLegState::class)
data class EndLegMessage(val leg: Leg): PersonMessage


// Person States
@StateCalled("StartPerson")
class PersonStartState(time: AbsoluteTime, agent: PersonAgent) : PersonState(time, agent, doStep = false)

@StateCalled("PerformingActivity", PersonStartState::class, PerformingActivityState::class, PerformLegState::class)
class PerformingActivityState(
    val activity: StationaryAction,
    state: PersonState
) : PersonState(activity.startTime, state.agent) {
    val location: Location
        get() = activity.location
}

abstract class TripState(time: AbsoluteTime, agent: PersonAgent, doStep: Boolean = true): PersonState(time, agent, doStep) {
    val origin: Location
        get() = trip.origin

    val destination : Location
        get() = trip.destination
}

@StateCalled("StartingTrip", PerformingActivityState::class)
class StartingTripState(state: PersonState) : TripState(state.time, state.agent, doStep=false)

@StateCalled("PerformLeg", StartingTripState::class, PerformLegState::class)
class PerformLegState(val leg: Leg, state: PersonState) : TripState(state.time, state.agent)

@StateCalled("FinishedPerson", PerformLegState::class, PerformingActivityState::class)
class FinishedPersonState(state: PersonState) : PersonState(state.time, state.agent)



val personStateMachine = stateMachine<PersonAgent>("PersonsStateMachine") {

    start(StartPerson, ::startPerson) { send ->
        val target = person.schedule.activities().first()
        target.location = person.household.location

        val firstActivity = agenda.elements[0]
        send(firstActivity(firstActivity), self, firstActivity.startTime)
        //
    }.transitionOn(FirstActivity) { message, send ->
        performingActivity(message.activity)
    }

    state(PerformingActivity) { send ->
        send(endActivity(), self, activity.endTime)
        //
    }.transitionOn(EndActivity) { message, send ->
        person.schedule.step()
        when(block) {
            is Agenda -> performingActivity(agenda.elements[0])
            is LinkTrip -> startingTrip()
            null -> finishedPerson()
            else -> error("")
        }
    }

    transState(StartingTripState::class) {
        val leg = trip

        // mode and destination choice
        // TODO Robin last.endlocation is destination?
        if (leg.elements.last().endLocation == LOCATIONUNKNOWN) {
            leg.elements.last().endLocation = behavior.destinationChoice.filterAndSelect(
                leg.elements.last().let {
                    TripChoiceSituation(
                        person,
                        time,
                        it.startLocation,
                        behavior.impedance,
                        person.sharedResources(),
                        behavior.attractivityModel,
                        behavior.availabilityModel
                    )
                }
            )
            leg.elements.forEach { it.transportType = MODEUNKOWN }
        }

    }.next { send ->
        val sharedResources = person.sharedResources()

        synchronizeAll(sharedResources) {
            val mode: Mode = behavior.modeChoice.filterAndSelect(
                ModeChoiceSituation(person, time, origin, destination, behavior.impedance, sharedResources),
            )

            trip.alternateByImpedance(behavior.impedance) {
                taking(mode to destination)
            }

            person.inTransit = true
            performLeg(trip.elements[0])

//TODO
//behavior.scopeDispatcher.pickScope(startLegEvent, mode, person, leg).also {
//  person.inTransit = true
//}
        }
    }

    state(PerformLeg) { send ->
        send(endLeg(), self, leg.endTime)
        //
    }.transitionOn(EndLeg) { message, send ->
        when(block) {
            null -> finishedPerson()
            is LinkTrip -> performLeg(trip.legs[0])
            is Agenda -> agenda.elements.firstOrNull()?.let {
                performingActivity(agenda.elements[0])
            } ?:  finishedPerson()
            else -> error("")
        }
    }

    finState(FinishedPerson)



}
