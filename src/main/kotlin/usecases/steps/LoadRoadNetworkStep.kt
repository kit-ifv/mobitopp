package usecases.steps

import VisumLocale
import domain.roadnetwork.LocatableGraph
import modeling.steps.Context
import modeling.steps.LateInit
import modeling.steps.ModelStep
import modeling.validation.Warning
import modeling.validation.validateFileReadAccess
import modeling.validation.validateScope
import parseNetwork
import java.nio.file.Path
import kotlin.io.path.name

fun RoadNetworkContext.loadVisumNetwork(
    file: Path,
    localeLambda: VisumLocale.() -> Unit = {}
) = runStep {
    LoadRoadNetworkStep(
        this,
        file,
        localeLambda
    )

//    context.roadNetwork.value = LocatableGraph( //TODO @Robin, why parse outside the model step?
//        parseNetwork(file) { }
//    )
}

interface RoadNetworkContext : Context {
    val roadNetwork: LateInit<LocatableGraph>
}

class LoadRoadNetworkStep<C>(
    private val context: C,
    val file: Path,
    val localeLambda: VisumLocale.() -> Unit = {}

) : ModelStep where C : RoadNetworkContext {

    override val name: String = "Load visum road network from ${file.name}"

    override fun execute() {
        context.roadNetwork.value = LocatableGraph(parseNetwork(file, localeLambda))
    }

    override fun verifyInput(): Warning? = validateScope("Validate visum net file: ${file.name}") {
        validateFileReadAccess(file.toFile(), fileDescription = "Visum Net File containing road network data")
    }

    override fun mockBehavior(): Warning? = validateScope("Mock Visum road network data") {
        // TODO @Robin
    }
}
