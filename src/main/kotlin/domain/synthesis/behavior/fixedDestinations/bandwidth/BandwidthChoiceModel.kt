package domain.synthesis.behavior.fixeddestinations.bandwidth

import domain.shared.location.StandardLocation
import edu.kit.ifv.mobitopp.discretechoice.structure.RuleBasedStructure
import edu.kit.ifv.mobitopp.discretechoice.utilityassignment.openMultinomialLogit
import edu.kit.ifv.units.Distance
import edu.kit.ifv.units.DistanceUnit
import utils.WithMetric
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
