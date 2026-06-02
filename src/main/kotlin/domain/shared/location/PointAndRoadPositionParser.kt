package domain.shared.location

import edu.kit.ifv.units.share
import org.locationtech.jts.geom.Point

fun interface PointAndRoadPositionParser : PointParser {
    fun parse(string: String): Pair<Point, RoadAccess>

    override fun parsePoint(string: String): Point = parse(string).first

    companion object {
        /**
         * Handles parsing both a point and the road position from the legacy mobitopp format.
         */
        val parseWGS = PointAndRoadPositionParser { string ->
            val res = string.removeSurrounding(prefix = "(", suffix = ")").split(":", ",").map { it.trim() }
            require(res.size == 4) {
                "Cannot parse '$this' as RoadPosition: expected format LONG:LAT,ROAD_ID,ROAD_POS"
            }
            val point = PointCreator.createWGS(
                res[0].toDouble(),
                res[1].toDouble(),
            )
            val roadAccess = RoadAccess(
                roadId = res[2].toLongOrNull() ?: Long.MIN_VALUE,
                position = (res[3].toDoubleOrNull() ?: 0.5).share(),
            )
            point to roadAccess
        }
    }
}
