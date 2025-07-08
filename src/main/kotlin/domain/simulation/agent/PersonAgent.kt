package domain.simulation.agent

import Mutable
import core.events.Subscribable
import core.statemachine.Message
import core.statemachine.StateBasedAgent
import core.statemachine.StateMachine
import domain.shared.datastructure.schedule.Action
import domain.shared.datastructure.schedule.Schedule
import domain.shared.enums.Mode
import domain.shared.location.Location
import domain.simulation.events.PersonBehavior
import domain.synthesis.data.IPerson
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import utils.random.SeededActor

interface PersonMessage : Message

// TODO interface SimulationPerson revealing only specific properties to household etc

@Mutable
abstract class PersonAgent(
    final override val id: PersonId,
    override val household: HouseholdAgent,
    override val stateMachine: StateMachine,
    seed: Long,
) : SeededActor<Person>(seed), IPerson, StateBasedAgent<PersonMessage> {

    abstract override val sharingMemberships: List<SharingProviderAgent>
    abstract val memberships: List<Subscribable<PersonAgent>>
    abstract val schedule: Schedule // = Schedule(TrackableModel(BlockModel()))

    abstract val behavior: PersonBehavior

    var inTransit: Boolean = false
    final var location: Location = household.location

    fun sharedResources() = memberships.flatMap { it.availableResourcesFor(this) }.toSet()
}

fun PersonAgent.lastTransportMode(action: Action): Mode? {
    return schedule.pastLegs().lastOrNull { it < action }?.transportType
}

fun Schedule.location(): Location? {
    return present?.startLocation ?: past.lastOrNull()?.endLocation
}

fun PersonAgent.locationBySchedule() = schedule.location() ?: household.location

fun PersonAgent.getBestCar(): PrivateCarAgent? {
    return household.cars.filter {
        it.state == PrivateCarAgent.CarState.PARKED &&
            (it.location == location) &&
            (it.keyHolder?.let { kh -> kh == this } ?: true)
    }.maxByOrNull {
        if (it.mainUser == this) 1 else 0
    }
}
