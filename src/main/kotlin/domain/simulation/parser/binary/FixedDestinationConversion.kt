package domain.simulation.parser.binary

import domain.shared.data.person.PersonId
import domain.shared.enums.ActivityType
import domain.shared.location.parser.LocationUtils.decodeLocation
import domain.shared.location.zone.Zone
import domain.shared.location.zone.ZoneId
import domain.shared.location.zone.attributes.HasRegionType
import domain.simulation.data.ActivityLocation
import utils.CodePlan
import utils.binary.BinaryReader
import utils.binary.DefaultBinaryWriter
import java.nio.ByteBuffer

@Suppress("MagicNumber")
class FixedDestinationReader(
//    val personConverter: (PersonId) -> Person?,
    private val activityTypeConverter: CodePlan<ActivityType>,
    val zoneConverter: (ZoneId) -> Zone<HasRegionType>,
) : BinaryReader<ActivityLocation> {

    override fun ByteBuffer.decode(stringLength: Int): ActivityLocation? {
        val personId = PersonId(long)
        val activityType = activityTypeConverter.decode(int)
        val location = decodeLocation(zoneConverter)
        return ActivityLocation(personId, activityType, location)
    }
}

class FixedDestinationWriter : DefaultBinaryWriter<ActivityLocation>()
