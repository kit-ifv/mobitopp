package domain.synthesis.behavior.fixedDestinations

import domain.shared.behavior.AttractivenessModel
import domain.shared.enums.ActivityType
import domain.synthesis.SynthesisHousehold
import domain.synthesis.attributes.household.MinimumHouseholdAttributes
import domain.synthesis.attributes.person.HasEmployment
import domain.synthesis.attributes.person.MinimumPersonAttributes
import domain.synthesis.attributes.person.isPrimaryStudent
import domain.synthesis.attributes.person.isSecondaryStudent
import domain.synthesis.attributes.person.isTertiaryStudent
import domain.synthesis.attributes.person.isWorker
import domain.synthesis.behavior.MinimalistPerson
import domain.synthesis.results.FixedDestinationElements

/**
 * This class provides the syntax to build the fixed destinations of agents.
 */
class AssignFixedDestinationBuilder<AREA, S : MinimumHouseholdAttributes, T : MinimumPersonAttributes>(
    val attractivenessModel: AttractivenessModel,
) {

    /**
     * Represents a step in the assignment process, where individuals (agents) are matched with destinations
     * based on a given filter and assignment logic. This class is responsible for generating the list of fixed
     * destination elements for a given target group.
     *
     * @param activityType The type of activity for which the agents are assigned a location.
     * @param filter A predicate function that filters the target agents based on some condition.
     * @param assignFunction A function that maps the filtered agents to their respective destinations.
     */
    inner class FixedLocationAssignmentStep(
        private val activityType: ActivityType,
        private val filter: (MinimalistPerson<T>) -> Boolean,
        private val assignFunction: SimpleGroupLocator<in T>,
    ) {

        fun generateFixedDestinations(target: Collection<SynthesisHousehold<S, T>>): List<FixedDestinationElements> {
            // TODO test that only valid agents are assigned stuff
            val applicableAgents = target.flatMap { it.members }.filter(filter)
            return applicableAgents.zip(assignFunction.match(applicableAgents)) { person, location ->
                FixedDestinationElements(person, activityType, location)
            }
        }
    }

    val steps: MutableList<FixedLocationAssignmentStep> = mutableListOf()
    fun forActivity(lambda: AssignFixedDestinationBuilder<AREA, S, T>.FixedLocationConfig.() -> Unit) {
        val element = FixedLocationConfig()
        element.apply(lambda)
        steps.add(FixedLocationAssignmentStep(element.activityType, element.filter, element.assignmentStrategy))
    }

    /**
     * A config object that allows the assignment of the attributes of [FixedLocationAssignmentStep]. This class exists
     * so that other configurations (either by .apply{} or some builder call lambda() with a receiver object) can be
     * configured with little code effort and just being able to write activity = Act.X filter = {::isWorker} or similar
     */
    inner class FixedLocationConfig {
        lateinit var activityType: ActivityType
        lateinit var assignmentStrategy: SimpleGroupLocator<in T>
        lateinit var filter: (MinimalistPerson<T>) -> Boolean
    }
}

/**
 * Extension functions if the person has the employment as an attribute, in which case the filter condition does not
 * need to be provided externally
 */
fun <AREA, S : MinimumHouseholdAttributes, T> AssignFixedDestinationBuilder<AREA, S, T>.primarySchool(
    lambda: AssignFixedDestinationBuilder<AREA, S, T>.FixedLocationConfig.() -> Unit,
) where T : HasEmployment, T : MinimumPersonAttributes {
    val element = FixedLocationConfig()
    element.lambda()
    steps.add(
        FixedLocationAssignmentStep(
            element.activityType,
            MinimalistPerson<T>::isPrimaryStudent,
            element.assignmentStrategy,
        )
    )
}

fun <AREA, S : MinimumHouseholdAttributes, T> AssignFixedDestinationBuilder<AREA, S, T>.secondarySchool(
    lambda: AssignFixedDestinationBuilder<AREA, S, T>.FixedLocationConfig.() -> Unit,
) where T : HasEmployment, T : MinimumPersonAttributes {
    val element = FixedLocationConfig()
    element.lambda()
    steps.add(
        FixedLocationAssignmentStep(
            element.activityType,
            MinimalistPerson<T>::isSecondaryStudent,
            element.assignmentStrategy,
        )
    )
}

fun <AREA, S : MinimumHouseholdAttributes, T> AssignFixedDestinationBuilder<AREA, S, T>.tertiarySchool(
    lambda: AssignFixedDestinationBuilder<AREA, S, T>.FixedLocationConfig.() -> Unit,
) where T : HasEmployment, T : MinimumPersonAttributes {
    val element = FixedLocationConfig()
    element.lambda()
    steps.add(
        FixedLocationAssignmentStep(
            element.activityType,
            MinimalistPerson<T>::isTertiaryStudent,
            element.assignmentStrategy,
        )
    )
}

fun <AREA, S : MinimumHouseholdAttributes, T> AssignFixedDestinationBuilder<AREA, S, T>.work(
    lambda: AssignFixedDestinationBuilder<AREA, S, T>.FixedLocationConfig.() -> Unit,
) where T : HasEmployment, T : MinimumPersonAttributes {
    val element = FixedLocationConfig()
    element.lambda()
    steps.add(
        FixedLocationAssignmentStep(
            element.activityType,
            MinimalistPerson<T>::isWorker,
            element.assignmentStrategy
        )
    )
}
