package usecases.steps

import VisumLocale
import domain.roadnetwork.LocatableGraph
import modeling.steps.Context
import modeling.steps.ModelExecution
import modeling.steps.ModelStep
import modeling.validation.Warning
import modeling.validation.validateFileReadAccess
import parseNetwork
import java.nio.file.Path

fun <S, C> S.loadVisumNetwork(
    file: Path,
    localeLambda: VisumLocale.() -> Unit = {}
) where S : ModelExecution<C>, C : Context, C : RoadNetworkContext {
    addStep(
        LoadRoadNetworkStep(
            context,
            file,
            localeLambda
        )
    )
    context.roadNetwork.value = LocatableGraph(
        parseNetwork(file) {
        }
    )
}

private class LoadRoadNetworkStep<C>(
    private val context: C,
    val file: Path,
    val localeLambda: VisumLocale.() -> Unit = {}
) : ModelStep where C : Context, C : RoadNetworkContext {
    override val name: String = "Load visum road network"

    override fun execute() {
        context.roadNetwork.value = LocatableGraph(parseNetwork(file, localeLambda))
    }

    override fun validate(): Warning? {
        return validateFileReadAccess(file.toFile())
    }
}
