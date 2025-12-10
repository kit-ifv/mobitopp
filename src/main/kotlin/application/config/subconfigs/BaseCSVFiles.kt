package application.config.subconfigs

import java.nio.file.Path
import kotlin.io.path.Path

val defaultPersonCSV: Path = Path("person.csv")
val defaultHouseholdCSV: Path = Path("household.csv")
val defaultActivityCSV: Path = Path("activity.csv")
val defaultPrivateCarsCSV: Path = Path("car.csv")
val defaultFixedDestinationCSV: Path = Path("fixedDestination.csv")
val defaultAttractivitiesCSV: Path = Path("attractivities.csv")
val defaultZonesCSV: Path = Path("zones.csv")

/**
 * Base paths for a short term simulation.
 */
interface BaseCSVFiles {
    val personCSV: Path
    val householdCSV: Path
    val activityCSV: Path
    val privateCarsCSV: Path
    val fixedDestinationCSV: Path
    val attractivitiesCSV: Path
    val zonesCSV: Path

    fun getNonexistentPaths(): List<Path>
}
