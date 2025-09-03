@file:Suppress("TooManyFunctions")

package domain.simulation.results

import core.modelsteps.LateInit
import core.modelsteps.Repository
import core.results.plots.RGB
import core.results.plots.modeStringColor
import domain.shared.datastructure.schedule.LinkedActivity
import domain.shared.datastructure.schedule.LinkedLeg
import domain.shared.datastructure.schedule.MovingAction
import domain.shared.enums.ActivityType
import domain.shared.enums.Mode
import domain.shared.location.Metrics
import domain.simulation.agent.PersonAgent
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.PersonId
import units.kilometers
import java.util.TreeMap
import kotlin.time.Duration.Companion.minutes

interface AgentResultsContext {
    val personAgents: Repository<PersonAgent, PersonId>
    val householdRepository: Repository<Household, HouseholdId>
    val impedance: LateInit<Metrics>
}

val AgentResultsContext.persons: List<PersonAgent>
    get() = personAgents.elements.toList()

val AgentResultsContext.households: List<Household>
    get() = householdRepository.elements.toList()

data class PersonLeg(val person: PersonAgent, val leg: MovingAction, val purpose: ActivityType?)

val AgentResultsContext.personLegs: List<PersonLeg>
    get() = persons.flatMap { person ->
        val purposes = person.schedule.pastActivities().associate {
            it.startTime to it.type
        }

        val lookup = TreeMap(purposes)
        person.schedule.pastLegs().map { leg ->
            PersonLeg(person, leg, lookup.ceilingEntry(leg.endTime)?.value)
        }
    }

fun LinkedLeg.nextActivity(): LinkedActivity? = this.next?.let {
    when (it) {
        is LinkedLeg -> it.nextActivity()
        is LinkedActivity -> it
        else -> null
    }
}

fun PersonLeg.duration(impedance: Metrics) = try {
    impedance.duration(leg.startLocation, leg.endLocation, leg.transportType, leg.startTime)
} catch (_: IllegalArgumentException) {
//    println("Warning: error while computing distance:\n" +
//            " - from: ${leg.startLocation}\n" +
//            " - to: ${leg.endLocation}\n" +
//            " - mode: ${leg.transportType}\n" +
//            "${e.message}"
//    )
    0.minutes
}

fun PersonLeg.distance(impedance: Metrics) = try {
    impedance.distance(leg.startLocation, leg.endLocation, leg.transportType)
} catch (_: java.lang.IllegalArgumentException) {
//    println("Warning: error while computing distance:\n" +
//            " - from: ${leg.startLocation}\n" +
//            " - to: ${leg.endLocation}\n" +
//            " - mode: ${leg.transportType}\n" +
//            "${e.message}"
//    )
    0.kilometers
}

@Suppress("MagicNumber")
private val ageGroups = listOf(
    0 to 10,
    10 to 20,
    20 to 30,
    30 to 40,
    40 to 50,
    50 to 60,
    60 to 70,
    70 to 80,
    80 to 90,
    90 to 100,
    100 to 120,
)

fun Int.ageGroup() = this.ageGroup(ageGroups)

fun Int.ageGroup(intervals: List<Pair<Int, Int>>) =
    intervals.find { it.first <= this && this < it.second }?.let {
        "[${it.first},${it.second})"
    } ?: "UNDEFINED"

fun modeColor(mode: Mode): RGB = modeStringColor(mode.toString().lowercase())
