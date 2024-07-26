package domain.events

import datastructure.ActionBlockVisitor
import datastructure.Activity
import datastructure.Agenda
import datastructure.Leg
import datastructure.LinkTrip
import datastructure.StationaryAction
import domain.data.Person
import domain.data.Zone
import domain.enums.MODEUNKOWN
import domain.enums.Mode
import domain.location.LOCATIONUNKNOWN
import domain.location.Location
import domain.location.Metrics
import domain.location.ZoneLocation
import modeling.events.Event
import modeling.models.ChoiceModel
import usecases.AttractivenessModel
import usecases.choicemodels.LegacyDestinationChoice
import usecases.choicemodels.LegacyModeChoiceModel
import utils.CodePlan
import utils.units.AbsoluteTime
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
        entity.schedule.step()
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
                behavior
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
        if (leg.elements.last().endLocation == LOCATIONUNKNOWN) {
            leg.elements.last().endLocation = behavior.destinationChoice.choose(person, time)
            leg.elements.forEach { it.transportType = MODEUNKOWN }

            // update travel times!
        }
        val mode = behavior.modeChoice.choose(person, time)

        val duration = behavior.impedance.duration(
            from = leg.elements.first().startLocation,
            to = leg.elements.last().endLocation,
            mode = mode,
            time = time
        )

        // Alternates the leg block to a monomodal trip
        leg.alternate {
            +Leg.fromDuration(
                previousAction?.endTime ?: person.schedule.lastAction()?.endTime?: AbsoluteTime.START,
                duration = duration,
                startLocation = originals.first().startLocation,
                endLocation = originals.last().endLocation,
                mode = mode
            )
        }

        return listOf(
            StartLegEvent(
                person = person,
                leg = leg.elements[0],
                behavior
            )
        )
    }

    override fun visitActivityBlock(activity: Agenda): List<Event<*>> {
        error(EXPECTED_LEG_BLOCK)
    }

    override fun process(entity: Person): List<Event<*>> {
        val block = entity.schedule.nextBlock()
        return block?.accept(this) ?: emptyList()
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
    val destinationChoice: ChoiceModel<Person, ZoneLocation>,
    val modeChoice: ChoiceModel<Person, Mode>,
    val impedance: Metrics,
) {
    companion object {
        fun from(
            impedance: Metrics,
            attractivenessModel: AttractivenessModel,
            umlands: (Location) -> Boolean,
            zones: Set<Zone>,
            modes: CodePlan<Mode>
        ): PersonBehavior {
            return PersonBehavior(
                LegacyDestinationChoice(impedance, attractivenessModel, umlands, zones, modes),
                LegacyModeChoiceModel(attractivenessModel, modes = modes, impedance = impedance),
                impedance,
            )
        }
    }
}
