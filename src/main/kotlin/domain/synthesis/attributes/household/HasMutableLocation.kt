package domain.synthesis.attributes.household

import domain.shared.location.Location

interface HasMutableLocation: HasLocation {
    override var location: Location
}