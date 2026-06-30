package edu.kit.ifv.domain.synthesis.attributes.household
import edu.kit.ifv.domain.shared.location.Location

interface HasLocation {
    val location: Location<*>
}
