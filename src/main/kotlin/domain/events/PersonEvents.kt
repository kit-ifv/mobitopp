package domain.events

import datastructure.ActionBlockVisitor
import datastructure.Activity
import datastructure.Agenda
import datastructure.Leg
import datastructure.LinkTrip
import datastructure.StationaryAction
import datastructure.alternateByImpedance
import domain.data.Person
import domain.data.Zone
import domain.enums.MODEUNKOWN
import domain.enums.Mode
import domain.location.LOCATIONUNKNOWN
import domain.location.Location
import domain.location.Metrics
import modeling.events.Event
import modeling.models.ChoiceModel
import usecases.AttractivenessModel
import usecases.choicemodels.ChoiceModelModes
import usecases.choicemodels.LegacyDestinationChoice
import usecases.choicemodels.LegacyModeChoiceModel
import usecases.choicemodels.TripChoiceSituation
import usecases.choicemodels.destinationchoice.parameters.ChoiceModelPurposes
import utils.concurrent.synchronizeAll
import utils.units.Time

private const val EXPECTED_LEG_BLOCK = "expected leg block"

abstract class ActionBlockEvent(
    val person: Person,
    priority: Int,
    time: Time,
    protected val behavior: PersonBehavior,
) : Event<Person>(
    agent = person,
    priority = priority,
    time = time
),
    ActionBlockVisitor<List<Event<*>>> {

    override fun process(entity: Person): List<Event<*>> {
        entity.location = entity.schedule.step()
//        require(entity.location == entity.locationBySchedule()) {
//            "Error, mismatch in locations precision."
//        }
        val block = entity.schedule.nextBlock()
        return block?.accept(this) ?: emptyList()
    }
}

class InitPersonEvent(
    person: Person,
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

    override fun process(entity: Person): List<Event<*>> {
        val block = entity.schedule.nextBlock()
        return block?.accept(this) ?: emptyList()
    }
}

class StartActivityEvent(
    person: Person,
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
    person: Person,
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
    person: Person,
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
            leg.elements.last().endLocation = behavior.destinationChoice.choose(person, time)
            leg.elements.forEach { it.transportType = MODEUNKOWN }
        }

        val origin = leg.origin
        val destination = leg.destination

        val sharedResources = person.sharedResources()
        return synchronizeAll(sharedResources) {
            val mode: Mode = behavior.modeChoice.choose(
                TripChoiceSituation(person, origin, destination, sharedResources),
                time
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

    override fun process(entity: Person): List<Event<*>> {
        val block = entity.schedule.nextBlock()
        return block?.accept(this).orEmpty()
    }
}

class StartLegEvent(
    person: Person,
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
    person: Person,
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
    val destinationChoice: ChoiceModel<Person, Location>,
    val modeChoice: ChoiceModel<TripChoiceSituation, Mode>,
    val impedance: Metrics,
    val scopeDispatcher: ModeScopeDispatcher,
) {
    companion object {
        @Suppress("LongParameterList")
        fun from(
            impedance: Metrics,
            attractivenessModel: AttractivenessModel,
            umlands: (Location) -> Boolean,
            zones: Set<Zone>,
            modes: ChoiceModelModes,
            purposes: ChoiceModelPurposes,
            scopeByMode: Map<Mode, ModeScopeSelector>,
        ): PersonBehavior {
            return PersonBehavior(
                LegacyDestinationChoice(impedance, attractivenessModel, umlands, zones, modes, purposes),
                LegacyModeChoiceModel(attractivenessModel, modes = modes, purposes = purposes, impedance = impedance),
                impedance,
                ModeScopeDispatcher(scopeByMode)
            )
        }
    }
}
