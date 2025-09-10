package application.steps.parser

import NetfileParser
import VisumLocale
import core.modelsteps.LateInit
import core.modelsteps.ModelStep
import core.modelsteps.Warning
import core.modelsteps.validateFileReadAccess
import core.modelsteps.validateScope
import domain.shared.datastructure.LocatableGraph
import domain.simulation.config.DemandSimContext
import units.Hemisphere
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

//    mobitopp.roadNetwork.value = LocatableGraph( //TODO @Robin, why parse outside the model step?
//        parseNetwork(file) { }
//    )
}

interface RoadNetworkContext : DemandSimContext {
    val roadNetwork: LateInit<LocatableGraph>
}

class LoadRoadNetworkStep<C>(
    private val context: C,
    val file: Path,
    val localeLambda: VisumLocale.() -> Unit = {}

) : ModelStep where C : RoadNetworkContext {

    override val name: String = "Load visum road network from ${file.name}"

    override fun execute() {
        context.roadNetwork.value = LocatableGraph(
            NetfileParser(
                file = file,
                locale = VisumLocale(),
                utmZone = 32,
                utmHemisphere = Hemisphere.NORTHERN
            ).parseNetwork(localeLambda)
        )
    }

    override fun verifyInput(): Warning? = validateScope("Validate visum net file: ${file.name}") {
        validateFileReadAccess(file, fileDescription = "Visum Net File containing road network data")
    }

    override fun mockBehavior(): Warning? = validateScope("Mock Visum road network data") {
        // TODO @Robin
    }
}
