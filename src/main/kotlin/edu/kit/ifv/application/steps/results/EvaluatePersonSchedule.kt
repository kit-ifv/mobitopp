@file:Suppress("TooManyFunctions")

package edu.kit.ifv.application.steps.results
import edu.kit.ifv.application.steps.HasHouseholdRepo
import edu.kit.ifv.application.steps.HasPersonAgentRepo
import edu.kit.ifv.core.results.plots.RGB
import edu.kit.ifv.core.results.plots.modeStringColor
import edu.kit.ifv.domain.shared.datastructure.schedule.action.LinkedActivity
import edu.kit.ifv.domain.shared.datastructure.schedule.action.LinkedLeg
import edu.kit.ifv.domain.shared.datastructure.schedule.action.MovingAction
import edu.kit.ifv.domain.shared.datastructure.schedule.action.StationaryAction
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.simulation.agent.PersonAgent
import edu.kit.ifv.domain.simulation.data.household.Household
import edu.kit.ifv.units.kilometers
import kotlin.time.Duration.Companion.minutes

fun HasPersonAgentRepo<*, PersonAgent>.persons(): List<PersonAgent> = personAgentRepository.elements.toList()

fun HasHouseholdRepo<*, Household>.households(): List<Household> = householdRepository.elements.toList()

data class PersonLeg(val person: PersonAgent, val leg: MovingAction, val purpose: ActivityType?)

fun HasPersonAgentRepo<*, PersonAgent>.personLegs(): List<PersonLeg> = persons().legs()

fun List<PersonAgent>.legs(): List<PersonLeg> = this.flatMap { person ->

    val result = mutableListOf<PersonLeg>()
    var lastPurpose: ActivityType? = null

    person.schedule.past.reversed().forEach { action ->
        when (action) {
            is StationaryAction -> {
                lastPurpose = action.type
            }

            is MovingAction -> {
                result += if (lastPurpose == null) {
                    PersonLeg(person, action, null)
                } else {
                    PersonLeg(person, action, lastPurpose)
                }
            }

            else -> error(
                "Cannot process $action of type ${action::class.simpleName} while creating PersonLegs," +
                    " expected MovingAction or StationaryAction",
            )
        }
    }

    result.reversed()
}

fun LinkedLeg.nextActivity(): LinkedActivity? = this.next?.let {
    when (it) {
        is LinkedLeg -> it.nextActivity()
        is LinkedActivity -> it
        else -> null
    }
}

fun PersonLeg.duration(impedance: Impedance) = try {
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

fun PersonLeg.distance(impedance: Impedance) = try {
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

fun Int.ageGroup(intervals: List<Pair<Int, Int>>) = intervals.find { it.first <= this && this < it.second }?.let {
    "[${it.first},${it.second})"
} ?: "UNDEFINED"

fun modeColor(mode: Mode): RGB = modeStringColor(mode.toString().lowercase())
