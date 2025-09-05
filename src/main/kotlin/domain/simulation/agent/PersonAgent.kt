package domain.simulation.agent

import Mutable
import core.statemachine.Message
import core.statemachine.StateBasedAgent
import core.statemachine.StateMachineFactory
import domain.shared.datastructure.schedule.Action
import domain.shared.datastructure.schedule.Schedule
import domain.shared.enums.Mode
import domain.shared.location.Location
import domain.simulation.events.PersonBehavior
import domain.synthesis.data.DrtProvider
import domain.synthesis.data.IPerson
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import utils.random.SeededActor
import utils.units.AbsoluteTime

interface PersonMessage : Message

// TODO interface SimulationPerson revealing only specific properties to household etc

@Mutable
abstract class PersonAgent(
    final override val id: PersonId,
    override val household: HouseholdAgent,
    stateMachine: StateMachineFactory<PersonAgent>,
    seed: Long,
) : SeededActor<Person>(seed), IPerson, StateBasedAgent<PersonMessage> {

    final override val stateMachine = stateMachine.create(AbsoluteTime.START, this)

    abstract override val sharingMemberships: List<SharingProviderAgent>
    abstract override val drtMemberships: List<DrtProviderAgent>

    abstract val schedule: Schedule // = Schedule(TrackableModel(BlockModel()))

    abstract val behavior: PersonBehavior

    var inTransit: Boolean = false
    var location: Location = household.location
}

fun PersonAgent.lastTransportMode(action: Action): Mode? {
    return schedule.pastLegs().lastOrNull { it < action }?.transportType
}

fun PersonAgent.lastTransportMode(): Mode? {
    return schedule.present?.let { present ->
        schedule.pastLegs().lastOrNull { it < present }?.transportType
    }
}

fun Schedule.location(): Location? {
    return present?.startLocation ?: past.lastOrNull()?.endLocation
}

fun PersonAgent.locationBySchedule() = schedule.location() ?: household.location

fun PersonAgent.getBestCarOrNull(): PrivateCarAgent? {
    return household.cars.filter {
        it.state == PrivateCarAgent.CarState.PARKED &&
            (it.location == location) &&
            (it.keyHolder?.let { kh -> kh == this } ?: true)
    }.maxByOrNull {
        if (it.mainUser == this) 1 else 0
    }
}

fun PersonAgent.getBestCar(): PrivateCarAgent = requireNotNull(this.getBestCarOrNull()) {
    "No vehicle is available for this person $id. " +
        "Household at ${household.location} contains vehicles: " +
        "  ${household.cars.map { "${it.id} ${it.state} ${it.keyHolder?.id} ${it.location}" }}\n\n"
}
