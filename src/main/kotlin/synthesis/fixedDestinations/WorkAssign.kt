package synthesis.fixedDestinations

import domain.data.Zone
import domain.data.ZoneId
import domain.enums.ActivityType
import domain.enums.LegacyActivityType
import domain.location.Location
import modeling.discreteChoice.GlobalRandomizer
import synthesis.SurveyPerson
import synthesis.randomCoordinate
import utils.collections.select
import java.io.File
import kotlin.io.path.Path



data class FixedLocationOutput(
    val person: SurveyPerson,
    val location: Location,
    val activityType: ActivityType
)

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
    private val commuterInfo: List<CommuterInfo>,
    private val zoneMapping: Map<ZoneId, Zone>,
    private val zoneLocationAssigner: DetermineLocationInZone
) : LocationFinder {

    private val commuterTargets: Map<CommunityNumber, Map<CommunityNumber, Double>> =
        commuterInfo.groupBy { it.origin }
            .mapValues { targets ->
                targets.value.filter { it.amount > 0 }.associate { it.destination to it.amount.toDouble() }
            }

//    override fun assignWorkLocation(person: SurveyPerson, home: Location): FixedLocationOutput {
//        val communityTarget = translator.forwardMap[home.zone!!.id]
//
//        val destination = commuterTargets[communityTarget]!!.select(GlobalRandomizer.nextDouble())
//
//        val destinationZoneIds = translator.backwardMap[destination]!!
//
//        // TODO pass an attractiveness model instead of randomly picking a zone
//        // TODO pass interface to determine the activity type
//        return FixedLocationOutput(
//            person,
//            zoneLocationAssigner.getLocation(zoneMapping[destinationZoneIds.random()]!!),
//            LegacyActivityType.WORK
//        )
//    }
    override fun find(person: SurveyPerson, home: Location, activityType: ActivityType): Location {
        val communityTarget = translator.forwardMap[home.zone!!.id]

        val destination = commuterTargets[communityTarget]!!.select(GlobalRandomizer.nextDouble())

        val destinationZoneIds = translator.backwardMap[destination]!!
        return zoneLocationAssigner.getLocation(zoneMapping[destinationZoneIds.random()]!!)
    }
    companion object {
        fun parse(
            mappingFile: File = Path("src/test/resources/synthesis/zone-to-community.csv").toFile(),
            commuterFile: File = Path("src/test/resources/synthesis/commuters-rastatt.csv").toFile(),
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