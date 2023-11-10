package domain.region

import Buildable
import ID
import Identifiable
import newId
import java.awt.geom.Point2D



class Zone(
    val name: String,
    val centroid: Point2D,
    val relief: Double
): Identifiable<Zone> {
    override val id: ID<Zone> = this.newId()
}
