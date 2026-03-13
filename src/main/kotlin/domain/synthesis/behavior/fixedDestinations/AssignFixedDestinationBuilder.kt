package domain.synthesis.behavior.fixedDestinations

import domain.shared.behavior.AttractivenessModel
import domain.shared.enums.ActivityType
import domain.shared.location.Location
import domain.shared.location.ZonedRoadAccessLocation
import domain.synthesis.behavior.SurveyInfo
import domain.synthesis.behavior.domain.SynthesisPerson
import domain.synthesis.behavior.isPrimaryStudent
import domain.synthesis.behavior.isSecondaryStudent
import domain.synthesis.behavior.isTertiaryStudent
import domain.synthesis.behavior.isWorker
import domain.synthesis.results.FixedDestinationElements

/**
 * This class provides the syntax to build the fixed destinations of agents.
 */
class AssignFixedDestinationBuilder<AREA, G>(
    val attractivenessModel: AttractivenessModel
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
        private val filter: (SynthesisPerson<out G>) -> Boolean,
        private val assignFunction: SimpleGroupLocator<in G>,
    ) {

        fun generateFixedDestinations(target: Collection<SynthesisPerson<out G>>): List<FixedDestinationElements> {
            // TODO test that only valid agents are assigned stuff
            val applicableAgents = target.filter(filter)
            return assignFunction.match(applicableAgents).map {
                FixedDestinationElements(it.targetPerson, activityType, it.assignedLocation)
            }
        }
    }

    val steps: MutableList<FixedLocationAssignmentStep> = mutableListOf()
    fun forActivity(lambda: AssignFixedDestinationBuilder<AREA, G>.FixedLocationConfig.() -> Unit) {
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
        lateinit var assignmentStrategy: SimpleGroupLocator<in G>
        lateinit var filter: (SynthesisPerson<out G>) -> Boolean
    }
}

/**
 * Extension functions if the person has the employment as an attribute, in which case the filter condition does not
 * need to be provided externally
 */
fun <AREA, T : SurveyInfo> AssignFixedDestinationBuilder<AREA, T>.primarySchool(
    lambda: AssignFixedDestinationBuilder<AREA, T>.FixedLocationConfig.() -> Unit
) {
    val element = FixedLocationConfig()
    element.lambda()
    steps.add(
        FixedLocationAssignmentStep(
            element.activityType,
            SynthesisPerson<out T>::isPrimaryStudent,
            element.assignmentStrategy,
        )
    )
}

fun <AREA, T : SurveyInfo> AssignFixedDestinationBuilder<AREA, T>.secondarySchool(
    lambda: AssignFixedDestinationBuilder<AREA, T>.FixedLocationConfig.() -> Unit
) {
    val element = FixedLocationConfig()
    element.lambda()
    steps.add(
        FixedLocationAssignmentStep(
            element.activityType,
            SynthesisPerson<out T>::isSecondaryStudent,
            element.assignmentStrategy,
        )
    )
}

fun <AREA, T : SurveyInfo> AssignFixedDestinationBuilder<AREA, T>.tertiarySchool(
    lambda: AssignFixedDestinationBuilder<AREA, T>.FixedLocationConfig.() -> Unit
) {
    val element = FixedLocationConfig()
    element.lambda()
    steps.add(
        FixedLocationAssignmentStep(
            element.activityType,
            SynthesisPerson<out T>::isTertiaryStudent,
            element.assignmentStrategy,
        )
    )
}

fun <AREA, T : SurveyInfo> AssignFixedDestinationBuilder<AREA, T>.work(
    lambda: AssignFixedDestinationBuilder<AREA, T>.FixedLocationConfig.() -> Unit
) {
    val element = FixedLocationConfig()
    element.lambda()
    steps.add(
        FixedLocationAssignmentStep(
            element.activityType,
            SynthesisPerson<out T>::isWorker,
            element.assignmentStrategy
        )
    )
}
