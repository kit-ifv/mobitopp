package domain.shared.location.parser

import domain.shared.location.PointCreator
import domain.shared.location.RoadAccess
import domain.shared.location.StandardLocation
import domain.shared.location.ZonedRoadAccessLocationDTO
import domain.shared.location.zone.Zone
import domain.shared.location.zone.ZoneId
import domain.shared.location.zone.attributes.HasRegionType
import edu.kit.ifv.units.share
import java.io.DataOutputStream
import java.nio.ByteBuffer

/**
 * Since writing and reading are heavily intertwined, they are encapsulated in this object, so that they will always
 * be at the same location, and if someone changes the logic, that they may see that they need to adapt the other
 * method as well. the write calls should be in the same order as the read calls. WriteLong -> WriteDouble -> etc.
 * should meet nextLong -> nextDouble -> etc.
 */
@Suppress("MagicNumber")
object LocationUtils {
    fun ByteBuffer.decodeLocation(converter: (ZoneId) -> Zone<HasRegionType>?): StandardLocation {
        val zoneId = ZoneId(long) // Reading zone ID
        val coordinate = PointCreator.createWGS(
            double,
            double,
        ) // Reading latitude and longitude
        val roadAccess = RoadAccess(long, double.share()) // Reading roadId and position
        return StandardLocation(
            position = coordinate,
            zone = converter(zoneId) ?: run {
                throw NoSuchElementException("No Zone For thing")
            },
            roadAccess = roadAccess,
        )
    }

    fun ByteBuffer.decodeNakedLocation(): ZonedRoadAccessLocationDTO {
        val zoneId = ZoneId(long) // Reading zone ID
        val coordinate = PointCreator.createWGS(
            double,
            double,
        ) // Reading latitude and longitude
        val roadAccess = RoadAccess(long, double.share()) // Reading roadId and position

        return ZonedRoadAccessLocationDTO(zoneId, roadAccess, coordinate)
    }

    /**
     * Extension function for `DataOutputStream` that writes a `Location` object to the output stream.
     * The method serializes the properties of the `Location` object (zone, coordinate, and road access)
     * into the output stream in a specific format:
     * - The `zone.id` is written as a `Long` (or `Long.MIN_VALUE` if `zone.id` is `null`).
     * - The latitude and longitude of the `coordinate` are written as `Double` values.
     * - The `roadAccess.roadId` is written as a `Long` (or `Long.MIN_VALUE` if `roadAccess.roadId` is `null`).
     * - The position of the `roadAccess` is written as a `Double` (with a default value of `0.5` if
     * `roadAccess.position` is `null`).
     *
     * @param location The `Location` object to write to the `DataOutputStream`.
     */
    fun DataOutputStream.encodeLocation(location: ZonedRoadAccessLocationDTO) {
        writeLong(location.zoneId.value)
        writeDouble(location.position.x)
        writeDouble(location.position.y)
        writeLong(location.roadAccess.roadId)
        writeDouble(location.roadAccess.position.toDouble())
    }
}
