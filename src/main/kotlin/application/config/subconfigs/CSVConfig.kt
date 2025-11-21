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
        dataRepo: Path,
        zoneRepo: Path,
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
                personCSV = dataRepo.resolve(personCSV),
                householdCSV = dataRepo.resolve(householdCSV),
                activityCSV = dataRepo.resolve(activityCSV),
                privateCarsCSV = dataRepo.resolve(privateCarsCSV),
                fixedDestinationCSV = dataRepo.resolve(fixedDestinationCSV),
                attractivitiesCSV = zoneRepo.resolve(attractivitiesCSV),
                bikeSharingStationsCSV = zoneRepo.resolve(bikeSharingStationsCSV),
                zonesCSV = zoneRepo.resolve(zonesCSV)
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

    /**
     * Returns new CSVConfig with changed attractivities, bikeSharingStations and zones paths.
     * @param attractivitiesCSV The path to attractivities.csv relative to the new zone repo, or an absolute path.
     * @param bikeSharingStationsCSV The path to bikesharing_stations.csv relative to the new zone repo, or an
     * absolute path.
     * @param zonesCSV The path to zones.csv relative to the new zone repo, or an
     * absolute path.
     * @return A new CSVConfig with the attractivities, bikesharingstations, and zones based on the new zone repo.
     */
    fun overrideZoneRepo(zoneRepo: Path,
                         attractivitiesCSV: Path = Path("attractivities.csv"),
                         bikeSharingStationsCSV: Path = Path("bikesharing_stations.csv"),
                         zonesCSV: Path = Path("zones.csv")
    ): CSVConfig {
        return CSVConfig(
            personCSV = personCSV,
            householdCSV = householdCSV,
            activityCSV = activityCSV,
            privateCarsCSV = privateCarsCSV,
            fixedDestinationCSV = fixedDestinationCSV,
            attractivitiesCSV = zoneRepo.resolve(attractivitiesCSV),
            bikeSharingStationsCSV = zoneRepo.resolve(bikeSharingStationsCSV),
            zonesCSV = zoneRepo.resolve(zonesCSV)
        )
    }

    /**
     * Returns new CSVConfig with changed person, household, activity, private_cars and fixed_destination paths.
     * @param personCSV The path to person.csv relative to the new dataRepo, or an
     * absolute path.
     * @param householdCSV The path to household.csv relative to the new dataRepo, or an
     * absolute path.
     * @param activityCSV The path to activity.csv relative to the new dataRepo, or an
     * absolute path.
     * @param privateCarsCSV The path to car.csv relative to the new dataRepo, or an
     * absolute path.
     * @param fixedDestinationCSV The path to fixedDestination.csv relative to the new dataRepo, or an
     * absolute path.
     * @return A new CSVConfig person, household, activity, cars and fixed_destinations based on the given dataRepo.
     */
    fun overrideDataRepo(dataRepo: Path,
                         personCSV: Path = Path("person.csv"),
                         householdCSV: Path = Path("household.csv"),
                         activityCSV: Path = Path("activity.csv"),
                         privateCarsCSV: Path = Path("car.csv"),
                         fixedDestinationCSV: Path = Path("fixedDestination.csv")
    ): CSVConfig {
        return CSVConfig(
            personCSV = dataRepo.resolve(personCSV),
            householdCSV = dataRepo.resolve(householdCSV),
            activityCSV = dataRepo.resolve(activityCSV),
            privateCarsCSV = dataRepo.resolve(privateCarsCSV),
            fixedDestinationCSV = dataRepo.resolve(fixedDestinationCSV),
            attractivitiesCSV = attractivitiesCSV,
            bikeSharingStationsCSV = bikeSharingStationsCSV,
            zonesCSV = zonesCSV
        )
    }
}