package synthesis.fixedDestinations

import domain.data.Zone
import domain.data.ZoneId
import domain.enums.ActivityType
import domain.location.Location
import modeling.discreteChoice.GlobalRandomizer
import synthesis.SynthesisPerson
import synthesis.randomCoordinate
import utils.collections.select
import java.nio.file.Path
import kotlin.io.path.Path


fun interface DetermineLocationInZone {
    fun getLocation(zone: Zone): Location
}

/**
 * Assign a location randomly in a radius of 100 meters around the zone centroid
 */
object DebugZoneAssigner : DetermineLocationInZone {
    override fun getLocation(zone: Zone): Location {
        return Location(zone.centroid.coordinate.randomCoordinate(100.0, GlobalRandomizer), zone, null)
    }

}

class CommuterMatrix(
    private val translator: BiMap<ZoneId, CommunityNumber>,
    commuterInfo: List<CommuterInfo>,
    private val zoneMapping: Map<ZoneId, Zone>,
    private val zoneLocationAssigner: DetermineLocationInZone
) : LocationFinder {

    private val commuterTargets: Map<CommunityNumber, Map<CommunityNumber, Double>> =
        commuterInfo.groupBy { it.origin }
            .mapValues { targets ->
                targets.value.filter { it.amount > 0 }.associate { it.destination to it.amount.toDouble() }
            }


    override fun find(person: SynthesisPerson, activityType: ActivityType): Location {
        //TODO remove !! replace with accurate error message.
        val communityTarget = translator.forwardMap[person.homeLocation.zone!!.id]

        val destination = commuterTargets[communityTarget]!!.select(GlobalRandomizer.nextDouble())

        val destinationZoneIds = translator.backwardMap[destination]!!
        return zoneLocationAssigner.getLocation(zoneMapping[destinationZoneIds.random()]!!)
    }
    companion object {
        fun parse(
            mappingFile: Path = Path("src/test/resources/synthesis/zone-to-community.csv"),
            commuterFile: Path = Path("src/test/resources/synthesis/commuters-rastatt.csv"),
            zoneMapping: Map<ZoneId, Zone>,
            converter: DetermineLocationInZone = DebugZoneAssigner
        ): CommuterMatrix {
            return CommuterMatrix(
                readZoneToCommunity(mappingFile),
                readCommuters(commuterFile).toList(),
                zoneMapping,
                converter
            )
        }
    }



}