package synthesis.fixedDestinations

import domain.data.Zone
import domain.enums.ActivityType
import synthesis.FixedDestinationElements
import synthesis.SurveyInfo
import synthesis.domain.SynthesisPerson
import synthesis.isHigherStudent
import synthesis.isPrimaryStudent
import synthesis.isWorker
import usecases.AttractivenessModel

class AssignStepBuilder<AREA, G>(
    val zones: List<AREA>,
    val attractivenessModel: AttractivenessModel
) {
    /*
     * Everything below here is development code and should be properly assigned to the corresponding packages.
     */

    inner class AssignStep(
        private val activityType: ActivityType,
        private val filter: (SynthesisPerson<out G>) -> Boolean,
        private val assignFunction: SimpleGroupLocator<in G>
    ) {

        fun generateFixedDestinations(target: Collection<SynthesisPerson<out G>>): List<FixedDestinationElements> {
            // TODO test that only valid agents are assigned stuff
            val applicableAgents = target.filter(filter)
            return assignFunction.match(applicableAgents).map {
                FixedDestinationElements(it.targetPerson, activityType, it.assignedLocation)
            }
        }
    }

    val steps: MutableList<AssignStep> = mutableListOf()
    fun forActivity(lambda: AssignStepBuilder<AREA, G>.FixedIn.() -> Unit) {
        val element = FixedIn()
        element.apply(lambda)
        steps.add(AssignStep(element.activityType, element.filter, element.assignmentStrategy))
    }

    inner class FixedIn {
        lateinit var activityType: ActivityType
        lateinit var assignmentStrategy: SimpleGroupLocator<in G>
        lateinit var filter: (SynthesisPerson<out G>) -> Boolean
    }
}

/**
 * Extension functions if the person has the employment as an attribute, in which case the filter condition does not
 * need to be provided externally
 */
fun <AREA, T : SurveyInfo> AssignStepBuilder<AREA, T>.primarySchool(lambda: AssignStepBuilder<AREA, T>.FixedIn.() -> Unit) {
    val element = FixedIn()
    element.lambda()
    steps.add(AssignStep(element.activityType, SynthesisPerson<out T>::isPrimaryStudent, element.assignmentStrategy))
}

fun <AREA, T : SurveyInfo> AssignStepBuilder<AREA, T>.secondarySchool(lambda: AssignStepBuilder<AREA, T>.FixedIn.() -> Unit) {
    val element = FixedIn()
    element.lambda()
    steps.add(AssignStep(element.activityType, SynthesisPerson<out T>::isHigherStudent, element.assignmentStrategy))
}

fun <AREA, T : SurveyInfo> AssignStepBuilder<AREA, T>.work(lambda: AssignStepBuilder<AREA, T>.FixedIn.() -> Unit) {
    val element = FixedIn()
    element.lambda()
    steps.add(AssignStep(element.activityType, SynthesisPerson<out T>::isWorker, element.assignmentStrategy))
}
