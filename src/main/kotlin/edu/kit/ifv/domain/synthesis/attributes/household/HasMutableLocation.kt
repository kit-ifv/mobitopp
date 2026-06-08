package edu.kit.ifv.domain.synthesis.attributes.household
import edu.kit.ifv.domain.shared.location.StandardLocation

interface HasMutableLocation : HasLocation {
    override var location: StandardLocation
}
