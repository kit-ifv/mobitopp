package application.steps.parser

import NetfileParser
import VisumLocale
import application.steps.HasMutableRoadNetwork
import core.modelsteps.Context
import core.modelsteps.steps.modelStep
import core.modelsteps.validation.validateFileReadAccess
import domain.shared.datastructure.LocatableGraph
import edu.kit.ifv.units.Hemisphere
import java.nio.file.Path
import kotlin.io.path.name

fun HasMutableRoadNetwork.loadVisumNetwork(
    file: Path,
    localeLambda: VisumLocale.() -> Unit = {}
) = modelStep(
    "Load visum road network from ${file.name}",
    listOf({ validateLoadVisumNetwork(file) }),
) {
    val locale = VisumLocale()
    locale.localeLambda()

    roadNetwork = LocatableGraph(
        NetfileParser(
            file = file,
            locale = locale,
            utmZone = 32,
            utmHemisphere = Hemisphere.NORTHERN //TODO why fixed?
        ).parseNetwork {}
    )
}

private fun Context.validateLoadVisumNetwork(file: Path): Boolean =
    validateFileReadAccess(file, fileDescription = "Visum Net File containing road network data")
