package domain.synthesis.attributes.household

import domain.shared.location.StandardLocation

interface HasMutableLocation : HasLocation {
    override var location: StandardLocation
}
