package domain.region

import Builder
import ID
import Identifiable
import java.awt.geom.Point2D


interface ZoneData: Identifiable {
    val id: ID
    val name: String
    val centroid: Point2D
    val relief: Double

    override fun id() = id
}

class MutableZoneData: Builder<ZoneData>, ZoneData {
    override var id: ID = -1
    override var name: String = ""
    override var centroid: Point2D = Point2D.Double(0.0,0.0)
    override var relief: Double = 0.0
    override fun build() = this
}