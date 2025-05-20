package domain.agent

import Mutable
import datastructure.Action
import datastructure.Schedule
import domain.data.IPerson
import domain.data.Person
import domain.data.PersonId
import domain.enums.Mode
import domain.location.Location
import domain.resources.Subscribable
import modeling.events.Agent
import modeling.events.Event
import utils.random.SeededActor

@Mutable
abstract class PersonAgent(
    final override val id: PersonId,
    override val household: HouseholdAgent,
    seed: Long,
) : SeededActor<Person>(seed), IPerson, Agent<PersonAgent> {

    abstract val memberships: List<Subscribable<PersonAgent>>
    abstract val schedule: Schedule // = Schedule(TrackableModel(BlockModel()))

    var inTransit: Boolean = false
    final override var location: Location = household.location
    final override var nextEvent: Event<PersonAgent>? = null
    final override val entity: PersonAgent by lazy { this }

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
