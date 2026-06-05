package edu.kit.ifv.domain.simulation.agent
import Mutable
import edu.kit.ifv.core.statemachine.Message
import edu.kit.ifv.core.statemachine.StateBasedAgent
import edu.kit.ifv.core.statemachine.StateMachineFactory
import edu.kit.ifv.domain.shared.data.person.PersonId
import edu.kit.ifv.domain.shared.datastructure.schedule.Schedule
import edu.kit.ifv.domain.shared.datastructure.schedule.action.Action
import edu.kit.ifv.domain.shared.datastructure.schedule.action.MovingAction
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.Location
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.data.person.IPerson
import edu.kit.ifv.domain.simulation.events.PersonBehavior
import edu.kit.ifv.utils.random.StochasticActor
import edu.kit.ifv.utils.units.AbsoluteTime
import kotlin.random.Random

interface PersonMessage : Message

// TODO interface SimulationPerson revealing only specific properties to household etc

@Mutable
abstract class PersonAgent(
    final override val id: PersonId,
    override val household: HouseholdAgent,
    stateMachine: StateMachineFactory<PersonAgent>,
    seed: Long,
) : IPerson,
    StateBasedAgent<PersonMessage>,
    StochasticActor {

    final override val random: Random by lazy { Random(id.value + seed) }

    final override val stateMachine = stateMachine.create(AbsoluteTime.START, this)

    abstract override val sharingMemberships: List<SharingProviderAgent>
    abstract override val drtMemberships: List<DrtProviderAgent>

    abstract val schedule: Schedule // = Schedule(TrackableModel(BlockModel()))

    abstract val behavior: PersonBehavior

    var inTransit: Boolean = false
    var location: StandardLocation = household.location
}

fun PersonAgent.lastTransportMode(action: Action? = null): Mode? = schedule.pastLegs().lastOrNull {
    action?.let { act -> it < act } ?: true
}?.transportType

fun PersonAgent.lastTransportModeDeprecated(): Mode? = schedule.past.filter { it is MovingAction }.map {
    (it as MovingAction).transportType
}.lastOrNull()

fun Schedule.location(): Location<*>? = present?.startLocation ?: past.lastOrNull()?.endLocation

fun PersonAgent.locationBySchedule() = schedule.location() ?: household.location

fun PersonAgent.getBestCarOrNull(): PrivateCarAgent? = household.cars.filter {
    it.state == PrivateCarAgent.CarState.PARKED &&
        (it.location == this.location) &&
        (it.keyHolder?.let { kh -> kh == this } ?: true)
}.maxByOrNull {
    if (it.mainUser == this) 1 else 0
}

fun PersonAgent.getBestCar(): PrivateCarAgent = requireNotNull(this.getBestCarOrNull()) {
    "No vehicle is available for this person $id. " +
        "Household at ${household.location} contains vehicles: " +
        "  ${household.cars.map { "${it.id} ${it.state} ${it.keyHolder?.id} ${it.location}" }}\n\n"
}
