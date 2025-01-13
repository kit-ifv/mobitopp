package synthesis.fixedDestinations

import domain.data.Zone
import domain.enums.ActivityType
import domain.location.FlightDistance
import domain.location.Location
import synthesis.FixedDestinationElements
import synthesis.SurveyInfo
import synthesis.domain.SynthesisPerson
import synthesis.isHigherStudent
import synthesis.isPrimaryStudent
import synthesis.isWorker
import usecases.AttractivenessModel
import kotlin.io.path.Path

class AssignStepBuilder<G>(
    val zones: List<Zone>,
    val attractivenessModel: AttractivenessModel
) {
    /*
     * Everything below here is development code and should be properly assigned to the corresponding packages.
     */

    inner class AssignStep(
        private val activityType: ActivityType,
        private val filter: (SynthesisPerson<out G>) -> Boolean,
        private val assignFunction: GroupLocationFinder<in G>
    ) {
        fun run(target: Collection<SynthesisPerson<out G>>): Map<SynthesisPerson<*>, Pair<ActivityType, Location>> {
            val applicableAgents = target.filter(filter)
            println("The simulation has: ${applicableAgents.size} agents required for $activityType")
            return assignFunction.find(applicableAgents, activityType).associate { it.first to Pair(activityType, it.second) }
        }

        fun runOther(target: Collection<SynthesisPerson<out G>>): List<FixedDestinationElements> {

            val applicableAgents = target.filter(filter)
            println("The simulation has: ${applicableAgents.size} agents required for $activityType")
            return assignFunction.find(target, activityType).map {
                FixedDestinationElements(it.first, activityType, it.second) }
        }

    }

    val steps: MutableList<AssignStep> = mutableListOf()

    inner class FixedIn {
        lateinit var activityType: ActivityType
        lateinit var assignmentStrategy: GroupLocationFinder<in G>
        lateinit var filter : (SynthesisPerson<out G>) -> Boolean
        var strategy = GREEDY_BY_DISTANCE
        var locationInZone = DebugZoneAssigner

        inner class StepIN {
            var communityMapping = Path("src/test/resources/synthesis/zone-to-community.csv")
            var commuterFile = Path("src/test/resources/synthesis/commuters-rastatt.csv")
            fun generate(): CommuterMatrix {
                return CommuterMatrix.parse(
                    mappingFile = communityMapping,
                    commuterFile = commuterFile,
                    zoneMapping = zones.associateBy { it.id })
            }
        }



        fun distanceBasedCommunity(lambda: StepIN.() -> Unit): MetricCommAssign {
            val commuterMatrix = StepIN().apply(lambda).generate()
            return MetricCommAssign(commuterMatrix, FlightDistance(), attractivenessModel, strategy, locationInZone)
        }

    }



}

/**
 * Extension functions if the person has attribute
 *
 */
fun <T: SurveyInfo> AssignStepBuilder<T>.primarySchool(lambda: AssignStepBuilder<T>.FixedIn.() -> Unit) {
    val element = FixedIn()
    element.lambda()
    steps.add(AssignStep(element.activityType, SynthesisPerson<out T>::isPrimaryStudent, element.assignmentStrategy))
}

fun <T: SurveyInfo> AssignStepBuilder<T>.secondarySchool(lambda: AssignStepBuilder<T>.FixedIn.() -> Unit) {
    val element = FixedIn()
    element.lambda()
    steps.add(AssignStep(element.activityType, SynthesisPerson<out T>::isHigherStudent, element.assignmentStrategy))
}

fun <T: SurveyInfo> AssignStepBuilder<T>.work(lambda: AssignStepBuilder<T>.FixedIn.() -> Unit) {
    val element = FixedIn()
    element.lambda()
    steps.add(AssignStep(element.activityType, SynthesisPerson<out T>::isWorker, element.assignmentStrategy))
}