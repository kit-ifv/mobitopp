package domain.events

import datastructure.ActionBlockVisitor
import datastructure.Activity
import datastructure.Agenda
import datastructure.Leg
import datastructure.LinkTrip
import datastructure.StationaryAction
import datastructure.alternateByImpedance
import domain.agent.PersonAgent
import domain.enums.MODEUNKOWN
import domain.enums.Mode
import domain.location.LOCATIONUNKNOWN
import domain.location.Location
import domain.location.Metrics
import modeling.events.Event
import modeling.models.FixedChoicesModel
import usecases.AttractivenessModel
import usecases.models.DestinationAlternative
import usecases.models.ModeAvailabilityFilter
import usecases.models.ModeChoiceAlternative
import usecases.models.ModeChoiceSituation
import usecases.models.TripChoiceSituation
import utils.concurrent.synchronizeAll
import utils.units.Time

private const val EXPECTED_LEG_BLOCK = "expected leg block"

abstract class ActionBlockEvent(
    val person: PersonAgent,
    priority: Int,
    time: Time,
    protected val behavior: PersonBehavior,
) : Event<PersonAgent>(
    agent = person,
    priority = priority,
    time = time
),
    ActionBlockVisitor<List<Event<*>>> {

    override fun process(entity: PersonAgent): List<Event<*>> {
        entity.location = entity.schedule.step()
//        require(entity.location == entity.locationBySchedule()) {
//            "Error, mismatch in locations precision."
//        }
        val block = entity.schedule.nextBlock()
        return block?.accept(this) ?: emptyList()
    }
}

class InitPersonEvent(
    person: PersonAgent,
    behavior: PersonBehavior,
) : ActionBlockEvent(
    person = person,
    priority = 0,
    time = Time.START,
    behavior = behavior,
) {
    override fun visitTrip(leg: LinkTrip): List<Event<*>> {
        error("Initial block should always be an activity block, but got leg block")
    }

    override fun visitActivityBlock(activity: Agenda): List<Event<*>> {
        val target = person.schedule.activities().first()
        // Setting the location of the first activity in the schedule to be the home zone
        target.location = person.household.location
        return listOf(
            StartActivityEvent(
                person = person,
                activity = activity.elements[0],
                behavior
            )
        )
    }

    override fun process(entity: PersonAgent): List<Event<*>> {
        val block = entity.schedule.nextBlock()
        return block?.accept(this) ?: emptyList()
    }
}

class StartActivityEvent(
    person: PersonAgent,
    activity: StationaryAction,
    behavior: PersonBehavior,
) : ActionBlockEvent(
    person = person,
    priority = 1,
    time = activity.startTime,
    behavior = behavior,

) {
    override fun visitTrip(leg: LinkTrip): List<Event<*>> {
        error("expected activity block")
    }

    override fun visitActivityBlock(activity: Agenda): List<Event<*>> {
        return listOf(
            EndActivityEvent(
                person = person,
                activity = activity.elements[0],
                behavior
            )
        )
    }
}

class EndActivityEvent(
    person: PersonAgent,
    activity: Activity,
    behavior: PersonBehavior,
) : ActionBlockEvent(
    person = person,
    priority = 1,
    time = activity.endTime,
    behavior = behavior,
) {
    override fun visitTrip(leg: LinkTrip): List<Event<*>> {
        return listOf(
            StartTripEvent(
                person = person,
                time = time,
                behavior,
            )
        )
    }

    override fun visitActivityBlock(activity: Agenda): List<Event<*>> {
        return listOf(
            StartActivityEvent(
                person = person,
                activity = activity.elements[0],
                behavior
            )
        )
    }
}

class StartTripEvent(
    person: PersonAgent,
    time: Time,
    behavior: PersonBehavior,
) : ActionBlockEvent(
    person = person,
    priority = 1,
    time = time,
    behavior = behavior,
) {
    override fun visitTrip(leg: LinkTrip): List<Event<*>> {
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

        val origin = leg.origin
        val destination = leg.destination

        val sharedResources = person.sharedResources()
        return synchronizeAll(sharedResources) {
            val mode: Mode = behavior.modeChoice.filterAndSelect(
                ModeChoiceSituation(person, time, origin, destination, behavior.impedance, sharedResources),
            )

            // TODO move this code snippet to the scope dispatcher maybe?

            leg.alternateByImpedance(behavior.impedance) {
                taking(mode to destination)
            }
            val startLegEvent = StartLegEvent(
                person = person,
                leg = leg.elements[0],
                behavior
            )

            behavior.scopeDispatcher.pickScope(startLegEvent, mode, person, leg).also {
                person.inTransit = true
            }
        }
    }

    override fun visitActivityBlock(activity: Agenda): List<Event<*>> {
        error(EXPECTED_LEG_BLOCK)
    }

    override fun process(entity: PersonAgent): List<Event<*>> {
        val block = entity.schedule.nextBlock()
        return block?.accept(this).orEmpty()
    }
}

class StartLegEvent(
    person: PersonAgent,
    leg: Leg,
    factory: PersonBehavior,
) : ActionBlockEvent(
    person = person,
    priority = 1,
    time = leg.startTime,
    behavior = factory,
) {
    override fun visitTrip(leg: LinkTrip): List<Event<*>> {
        return listOf(
            EndLegEvent(
                person = person,
                leg = leg.elements[0],
                behavior,
            )
        )
    }

    override fun visitActivityBlock(activity: Agenda): List<Event<*>> {
        error(EXPECTED_LEG_BLOCK)
    }
}

class EndLegEvent(
    person: PersonAgent,
    leg: Leg,
    factory: PersonBehavior,
) : ActionBlockEvent(
    person = person,
    priority = 1,
    time = leg.endTime,
    behavior = factory,
) {
    override fun visitTrip(leg: LinkTrip): List<Event<*>> {
        return listOf(
            StartLegEvent(
                person = person,
                leg = leg.elements[0],
                behavior
            )
        )
    }

    override fun visitActivityBlock(activity: Agenda): List<Event<*>> {
        person.inTransit = false

        return activity.elements.firstOrNull()?.let {
            listOf(
                StartActivityEvent(
                    person = person,
                    activity = it,
                    behavior
                )
            )
        } ?: emptyList()
    }
}

data class PersonBehavior(
    val destinationChoice: FixedChoicesModel<DestinationAlternative, Location>,
    val modeChoice: FixedChoicesModel<ModeChoiceAlternative, Mode>,
    val impedance: Metrics,
    val scopeDispatcher: ModeScopeDispatcher,
    val attractivityModel: AttractivenessModel,
    val availabilityModel: ModeAvailabilityFilter,
) {
    companion object {
        @Suppress("LongParameterList")
        fun from(
            impedance: Metrics,
            destinationChoice: FixedChoicesModel<DestinationAlternative, Location>,
            modeChoice: FixedChoicesModel<ModeChoiceAlternative, Mode>,
//            umlands: (Location) -> Boolean,
//            zones: Set<Zone>,
//            modes: ChoiceModelModes,
//            purposes: ChoiceModelPurposes,
            scopeByMode: Map<Mode, ModeScopeSelector>,
            attractivenessModel: AttractivenessModel,
            modeAvailability: ModeAvailabilityFilter,
        ): PersonBehavior {
            return PersonBehavior(
                destinationChoice,
                modeChoice,
//                LegacyDestinationChoice(impedance, attractivenessModel, umlands, zones, modes, purposes),
//                LegacyModeChoiceModel(attractivenessModel, modes = modes, purposes = purposes, impedance = impedance),
                impedance,
                ModeScopeDispatcher(scopeByMode),
                attractivenessModel,
                modeAvailability,
            )
        }
    }
}
