package edu.kit.ifv.domain.synthesis.behavior.fixeddestinations.bandwidth
import edu.kit.ifv.domain.shared.behavior.AttractivenessModel
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.StandardLocation

/**
 * Contains all relevant information for the discrete choice within the [domain.synthesis.behavior.fixeddestinations.BandwidthLocator] to select a proper target.
 */
@Suppress("MagicNumber") // The small attractiveness as default seems to cause issues.
data class LocationAlternative(val attractivenessModel: AttractivenessModel, val activityType: ActivityType) {
    /**
     * We can extrapolate the attractiveness by simply evaluating the location.
     */
    fun attractiveness(location: StandardLocation) = location.zoneId.let {
        attractivenessModel.attractivenessFor(it, activityType)
    }
}
