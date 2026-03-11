package domain.synthesis.parser.binary

import domain.shared.location.LocationOld
import domain.shared.location.RoadAccess
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import edu.kit.ifv.units.WGS84Coordinate
import edu.kit.ifv.units.share
import java.io.DataInputStream
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
    /**
     * Extension function, that reads a location from a [DataInputStream].
     */
    @Deprecated("Should be used with a bytebuffer instead.")
    fun DataInputStream.decodeLocation(converter: (ZoneId) -> Zone?): LocationOld {
        val zoneId = ZoneId(readLong()) // Reading zone ID
        val coordinate = WGS84Coordinate.decimalDegree(
            readDouble(),
            readDouble()
        ) // Reading latitude and longitude
        val roadAccess = RoadAccess(readLong(), readDouble().share()) // Reading roadId and position
        return LocationOld(coordinate, converter(zoneId), roadAccess)
    }
    fun ByteBuffer.decodeLocation(converter: (ZoneId) -> Zone?): LocationOld {
        val zoneId = ZoneId(long) // Reading zone ID
        val coordinate = WGS84Coordinate.decimalDegree(
            double,
            double
        ) // Reading latitude and longitude
        val roadAccess = RoadAccess(long, double.share()) // Reading roadId and position
        return LocationOld(coordinate, converter(zoneId), roadAccess)
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
    fun DataOutputStream.encodeLocation(location: LocationOld) {
        writeLong(location.zone?.id?.value ?: Long.MIN_VALUE)
        writeDouble(location.coordinate.y)
        writeDouble(location.coordinate.x)
        writeLong(location.roadAccess?.roadId ?: Long.MIN_VALUE)
        writeDouble(location.roadAccess?.position?.toDouble() ?: 0.5)
    }
}
