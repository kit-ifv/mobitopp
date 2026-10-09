package edu.kit.ifv.domain.simulation.parser.binary

import edu.kit.ifv.binary.ElementWiseBinaryReader
import edu.kit.ifv.domain.shared.data.person.PersonId
import edu.kit.ifv.domain.shared.enums.ActivityType
import edu.kit.ifv.domain.shared.location.parser.LocationUtils.decodeLocation
import edu.kit.ifv.domain.shared.location.zone.Zone
import edu.kit.ifv.domain.shared.location.zone.ZoneId
import edu.kit.ifv.domain.shared.location.zone.attributes.HasRegionType
import edu.kit.ifv.domain.simulation.data.ActivityLocation
import edu.kit.ifv.utils.CodePlan
import java.nio.ByteBuffer

@Suppress("MagicNumber")
class FixedDestinationReader(
//    val personConverter: (PersonId) -> Person?,
    private val activityTypeConverter: CodePlan<ActivityType>,
    val zoneConverter: (ZoneId) -> Zone<HasRegionType>,
) : ElementWiseBinaryReader<ActivityLocation> {

    override fun ByteBuffer.decode(stringLength: Int): ActivityLocation? {
        val personId = PersonId(long)
        val activityType = activityTypeConverter.decode(int)
        val location = decodeLocation(zoneConverter)
        return ActivityLocation(personId, activityType, location)
    }
}
