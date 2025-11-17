package application.config.subconfigs


import com.fasterxml.jackson.annotation.JsonCreator
import java.nio.file.Path
import kotlin.io.path.Path
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
     *
     * __Default expected structure__ applied when only dataDirectory and zoneDirectory are given:
     *
     * `dataDirectory`: person.csv, household.csv, activity.csv, car.csv, fixedDestination.csv
     *
     * `zoneDirectory`: attractivities.csv, bikesharing_stations.csv, zones.csv
     */
    @JsonCreator
    constructor(
        dataDirectory: Path,
        zoneDirectory: Path,
        personCSV: Path = Path("person.csv"),
        householdCSV: Path = Path("household.csv"),
        activityCSV: Path = Path("activity.csv"),
        privateCarsCSV: Path = Path("car.csv"),
        fixedDestinationCSV: Path = Path("fixedDestination.csv"),
        attractivitiesCSV: Path = Path("attractivities.csv"),
        bikeSharingStationsCSV: Path = Path("bikesharing_stations.csv"),
        zonesCSV: Path = Path("zones.csv"),
        ) :
            this(
                personCSV = dataDirectory.resolve(personCSV),
                householdCSV = dataDirectory.resolve(householdCSV),
                activityCSV = dataDirectory.resolve(activityCSV),
                privateCarsCSV = dataDirectory.resolve(privateCarsCSV),
                fixedDestinationCSV = dataDirectory.resolve(fixedDestinationCSV),
                attractivitiesCSV = zoneDirectory.resolve(attractivitiesCSV),
                bikeSharingStationsCSV = zoneDirectory.resolve(bikeSharingStationsCSV),
                zonesCSV = zoneDirectory.resolve(zonesCSV)
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