package usecases.steps.binary

import domain.data.Zone
import domain.data.ZoneId
import domain.location.Location
import domain.location.RoadAccess
import units.GPSCoordinate
import units.share
import java.io.DataInputStream
import java.io.DataOutputStream

/**
 * Since writing and reading are heavily intertwined, they are encapsulated in this object, so that they will always
 * be at the same location, and if someone changes the logic, that they may see that they need to adapt the other
 * method as well. the write calls should be in the same order as the read calls. WriteLong -> WriteDouble -> etc.
 * should meet nextLong -> nextDouble -> etc.
 */
@Suppress("MagicNumber")
object LocationUtils {
    fun writeLocation(location: Location, outputStream: DataOutputStream) {
        outputStream.writeLong(location.zone?.id?.value ?: Long.MIN_VALUE)
        outputStream.writeDouble(location.coordinate.latitudeDegrees)
        outputStream.writeDouble(location.coordinate.longitudeDegrees)
        outputStream.writeLong(location.roadAccess?.roadId ?: Long.MIN_VALUE)
        outputStream.writeDouble(location.roadAccess?.position?.toDouble() ?: 0.5)
    }

    fun readLocation(buffer: TrackingBuffer, converter: (ZoneId) -> Zone?): Location {
        val zoneId = ZoneId(buffer.nextLong) // Reading zone ID
        val coordinate = GPSCoordinate.decimalDegree(
            buffer.nextDouble,
            buffer.nextDouble
        ) // Reading latitude and longitude
        val roadAccess = RoadAccess(buffer.nextLong, buffer.nextDouble.share()) // Reading roadId and position
        return Location(coordinate, converter(zoneId), roadAccess) // Returning a Location object
    }

    fun DataInputStream.decodeLocation(converter: (ZoneId) -> Zone?): Location {
        val zoneId = ZoneId(readLong()) // Reading zone ID
        val coordinate = GPSCoordinate.decimalDegree(
            readDouble(),
            readDouble()
        ) // Reading latitude and longitude
        val roadAccess = RoadAccess(readLong(), readDouble().share()) // Reading roadId and position
        return Location(coordinate, converter(zoneId), roadAccess)
    }

    fun DataOutputStream.encodeLocation(location: Location) {
        writeLong(location.zone?.id?.value ?: Long.MIN_VALUE)
        writeDouble(location.coordinate.latitudeDegrees)
        writeDouble(location.coordinate.longitudeDegrees)
        writeLong(location.roadAccess?.roadId ?: Long.MIN_VALUE)
        writeDouble(location.roadAccess?.position?.toDouble() ?: 0.5)
    }
}
