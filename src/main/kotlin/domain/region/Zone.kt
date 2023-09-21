package domain.region

import Builder
import ID
import java.awt.geom.Point2D


interface ZoneData {
    val id: ID
    val name: String
    val centroid: Point2D
    val relief: Double
}

data class MutableZoneData(
    override var id: ID,
    override var name: String,
    override var centroid: Point2D,
    override var relief: Double
): Builder<ZoneData>, ZoneData {
    override fun build() = this
}