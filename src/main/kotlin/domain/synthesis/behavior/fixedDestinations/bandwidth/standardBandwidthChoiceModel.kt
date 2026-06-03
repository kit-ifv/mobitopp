package domain.synthesis.behavior.fixeddestinations.bandwidth

import core.datastructure.kdtree.WithMetric
import domain.shared.location.StandardLocation
import domain.synthesis.behavior.fixeddestinations.bandwidth.LocationAlternative
import edu.kit.ifv.mobitopp.discretechoice.structure.RuleBasedStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.openMultinomialLogit
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.DistanceUnit
import kotlin.math.ln
import kotlin.math.pow

val standardBandwidthChoiceModel = RuleBasedStructure<
    WithMetric<StandardLocation, Distance>,
    LocationAlternative,
    BandwidthParameters,
    > {
    ruleForAll { option, characteristics ->
        val (loc, distance) = option
        ln(characteristics.attractiveness(loc).value) /
            (bDistance * distance.toDouble(DistanceUnit.KILOMETERS).pow(aDistance))
    }
}.openMultinomialLogit("DefaultBandwidthLocationSelector")
