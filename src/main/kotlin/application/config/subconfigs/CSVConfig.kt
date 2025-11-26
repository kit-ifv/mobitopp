package application.config.subconfigs

import com.fasterxml.jackson.annotation.JsonIgnore
import domain.jackson.JSONInitializer
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
    constructor(
        dataRepo: Path,
        zoneRepo: Path,
        personCSV: Path? = null,
        householdCSV: Path? = null,
        activityCSV: Path? = null,
        privateCarsCSV: Path? = null,
        fixedDestinationCSV: Path? = null,
        attractivitiesCSV: Path? = null,
        bikeSharingStationsCSV: Path? = null,
        zonesCSV: Path? = null,
    ) :
        this(
            personCSV = dataRepo.resolve(personCSV ?: defaultPersonCSV),
            householdCSV = dataRepo.resolve(householdCSV ?: defaultHouseholdCSV),
            activityCSV = dataRepo.resolve(activityCSV ?: defaultActivityCSV),
            privateCarsCSV = dataRepo.resolve(privateCarsCSV ?: defaultPrivateCarsCSV),
            fixedDestinationCSV = dataRepo.resolve(fixedDestinationCSV ?: defaultFixedDestinationCSV),
            attractivitiesCSV = zoneRepo.resolve(attractivitiesCSV ?: defaultAttractivitiesCSV),
            bikeSharingStationsCSV = zoneRepo.resolve(bikeSharingStationsCSV ?: defaultBikeSharingStationsCSV),
            zonesCSV = zoneRepo.resolve(zonesCSV ?: defaultZonesCSV),
        )

    /**
     * Checks whether all paths exist and returns the ones not existing.
     * @return list containing any of the paths this class manages, if they don't exist.
     */
    @JsonIgnore
    fun getNonexistentPaths(): List<Path> {
        val paths = listOf(
            personCSV,
            householdCSV,
            activityCSV,
            privateCarsCSV,
            fixedDestinationCSV,
            attractivitiesCSV,
            bikeSharingStationsCSV,
            zonesCSV
        )
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
    fun overrideZoneRepo(
        zoneRepo: Path,
        attractivitiesCSV: Path = defaultAttractivitiesCSV,
        bikeSharingStationsCSV: Path = defaultBikeSharingStationsCSV,
        zonesCSV: Path = defaultZonesCSV
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
    @Suppress("LongParameterList")
    fun overrideDataRepo(
        dataRepo: Path,
        personCSV: Path = defaultPersonCSV,
        householdCSV: Path = defaultHouseholdCSV,
        activityCSV: Path = defaultActivityCSV,
        privateCarsCSV: Path = defaultPrivateCarsCSV,
        fixedDestinationCSV: Path = defaultFixedDestinationCSV,
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

    companion object : JSONInitializer<CSVConfig> {
        private const val DATA_REPO_PARAM = "dataRepo"
        private const val ZONE_REPO_PARAM = "zoneRepo"
        private const val PERSON_PARAM = "personCSV"
        private const val HOUSEHOLD_PARAM = "householdCSV"
        private const val ACTIVITY_PARAM = "activityCSV"
        private const val CAR_PARAM = "privateCarsCSV"
        private const val DESTINATION_PARAM = "fixedDestinationCSV"
        private const val ATTRACTIVITY_PARAM = "attractivitiesCSV"
        private const val SHARING_PARAM = "bikeSharingStationsCSV"
        private const val ZONES_PARAM = "zonesCSV"

        private val defaultPersonCSV: Path = Path("person.csv")
        private val defaultHouseholdCSV: Path = Path("household.csv")
        private val defaultActivityCSV: Path = Path("activity.csv")
        private val defaultPrivateCarsCSV: Path = Path("car.csv")
        private val defaultFixedDestinationCSV: Path = Path("fixedDestination.csv")
        private val defaultAttractivitiesCSV: Path = Path("attractivities.csv")
        private val defaultBikeSharingStationsCSV: Path = Path("bikesharing_stations.csv")
        private val defaultZonesCSV: Path = Path("zones.csv")

        /**
         * @return all the constructor parameter names, including dataRepo and zoneRepo.
         */
        override fun getParameterNames(): Set<String> {
            return setOf(
                DATA_REPO_PARAM,
                ZONE_REPO_PARAM,
                PERSON_PARAM,
                HOUSEHOLD_PARAM,
                ACTIVITY_PARAM,
                CAR_PARAM,
                DESTINATION_PARAM,
                ATTRACTIVITY_PARAM,
                SHARING_PARAM,
                ZONES_PARAM
            )
        }

        private fun Map<String, String>.retrieveAsPath(name: String,): Path? {
            return if (containsKey(name)) { Path(get(name)!!) } else null
        }

        private fun allNotNull(vararg paths: Path?): Boolean {
            return paths.all { it != null }
        }

        /**
         * Constructs a config out of the given params.
         * @throws error If the given params don't contain either 'dataRepo' and 'zoneRepo' or all other fields since
         * no sensible config can be constructed then.
         */
        override fun init(givenParams: Map<String, String>): CSVConfig {
            val dataRepo: Path? = givenParams.retrieveAsPath(DATA_REPO_PARAM)
            val zoneRepo: Path? = givenParams.retrieveAsPath(ZONE_REPO_PARAM)
            val personCSV: Path? = givenParams.retrieveAsPath(PERSON_PARAM)
            val householdCSV: Path? = givenParams.retrieveAsPath(HOUSEHOLD_PARAM)
            val activityCSV: Path? = givenParams.retrieveAsPath(ACTIVITY_PARAM)
            val privateCarsCSV: Path? = givenParams.retrieveAsPath(CAR_PARAM)
            val fixedDestinationCSV: Path? = givenParams.retrieveAsPath(DESTINATION_PARAM)
            val attractivitiesCSV: Path? = givenParams.retrieveAsPath(ATTRACTIVITY_PARAM)
            val bikeSharingStationsCSV: Path? = givenParams.retrieveAsPath(SHARING_PARAM)
            val zonesCSV: Path? = givenParams.retrieveAsPath(ZONES_PARAM)

            if (dataRepo != null && zoneRepo != null) {
                return CSVConfig(
                    dataRepo = dataRepo,
                    zoneRepo = zoneRepo,
                    personCSV = personCSV,
                    householdCSV = householdCSV,
                    activityCSV = activityCSV,
                    privateCarsCSV = privateCarsCSV,
                    fixedDestinationCSV = fixedDestinationCSV,
                    attractivitiesCSV = attractivitiesCSV,
                    bikeSharingStationsCSV = bikeSharingStationsCSV,
                    zonesCSV = zonesCSV
                )
            } else {
                if (allNotNull(
                        personCSV,
                        householdCSV,
                        activityCSV,
                        privateCarsCSV,
                        fixedDestinationCSV,
                        attractivitiesCSV,
                        bikeSharingStationsCSV,
                        zonesCSV
                    )
                ) {
                    return CSVConfig(
                        personCSV = personCSV!!,
                        householdCSV = householdCSV!!,
                        activityCSV = activityCSV!!,
                        privateCarsCSV = privateCarsCSV!!,
                        fixedDestinationCSV = fixedDestinationCSV!!,
                        attractivitiesCSV = attractivitiesCSV!!,
                        bikeSharingStationsCSV = bikeSharingStationsCSV!!,
                        zonesCSV = zonesCSV!!
                    )
                } else {
                    error("Missing mandatory fields. Either set 'dataRepo' and 'zoneRepo' or all other fields.")
                }
            }
        }
    }
}
