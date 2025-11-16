package application.config.subconfigs

import java.nio.file.Path
import kotlin.io.path.exists


/**
 * This data class contains all paths to csv files for a short term simulation. It handles the
 * default structure and possible creation methods.
 */
data class CSVConfig(
    val personCSV: Path,
    val householdCSV: Path,
    val activityCSV: Path,
    val privateCarsCSV: Path,
    val fixedDestinationCSV: Path,
    val attractivitiesCSV: Path,
    val bikeSharingStationsCSV: Path,
    val zonesCSV: Path,
) {

    /**
     * Creation method based on two directories. All files are expected to reside in either the dataFolder or the
     * zoneFolder.
     */
    constructor(
        dataDirectory: Path,
        zoneDirectory: Path,
        personCSV: Path = dataDirectory.resolve("person.csv"),
        householdCSV: Path = dataDirectory.resolve("household.csv"),
        activityCSV: Path = dataDirectory.resolve("activity.csv"),
        privateCarsCSV: Path = dataDirectory.resolve("car.csv"),
        fixedDestinationCSV: Path = dataDirectory.resolve("fixedDestination.csv"),
        attractivitiesCSV: Path = zoneDirectory.resolve("attractivities.csv"),
        bikeSharingStationsCSV: Path = zoneDirectory.resolve("bikesharing_stations.csv"),
        zonesCSV: Path = zoneDirectory.resolve("zones.csv"),
        ) :
            this(
                personCSV = personCSV,
                householdCSV = householdCSV,
                activityCSV = activityCSV,
                privateCarsCSV = privateCarsCSV,
                fixedDestinationCSV = fixedDestinationCSV,
                attractivitiesCSV = attractivitiesCSV,
                bikeSharingStationsCSV = bikeSharingStationsCSV,
                zonesCSV = zonesCSV
            )

    /**
     * Checks whether all paths exist and returns the ones not existing.
     * @return list containing any of the paths this class manages, if they don't exist.
     */
    fun getNonexistentPaths(): List<Path> {
        val paths = listOf(personCSV, householdCSV, activityCSV, privateCarsCSV, fixedDestinationCSV,
            attractivitiesCSV, bikeSharingStationsCSV, zonesCSV)
        return paths.filter { !it.exists() }
    }
}