package application.config.subconfigs

import com.fasterxml.jackson.annotation.JsonIgnore
import domain.jackson.JSONInitializer
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

/**
 * CSVConfig with bikesharing.
 */
data class BikeSharingConfig(
    override val personCSV: Path,
    override val householdCSV: Path,
    override val activityCSV: Path,
    override val privateCarsCSV: Path,
    override val fixedDestinationCSV: Path,
    override val attractivitiesCSV: Path,
    override val zonesCSV: Path,
    val bikeSharingStationsCSV: Path
) : CoreCSVConfig(
    personCSV,
    householdCSV,
    activityCSV,
    privateCarsCSV,
    fixedDestinationCSV,
    attractivitiesCSV,
    zonesCSV
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
    override fun getNonexistentPaths(): List<Path> {
        if (bikeSharingStationsCSV.exists()) {
            return super.getNonexistentPaths()
        }
        return super.getNonexistentPaths().plusElement(bikeSharingStationsCSV)
    }

    /**
     * Returns new BikeSharinConfig with changed attractivities, bikeSharingStations and zones paths.
     * @param attractivitiesCSV The path to attractivities.csv relative to the new zone repo, or an absolute path.
     * @param bikeSharingStationsCSV The path to bikesharing_stations.csv relative to the new zone repo, or an
     * absolute path.
     * @param zonesCSV The path to zones.csv relative to the new zone repo, or an
     * absolute path.
     * @return A new CSVConfig with the attractivities, bikesharingstations, and zones based on the new zone repo.
     */
    fun overwriteZoneRepo(
        zoneRepo: Path,
        attractivitiesCSV: Path = defaultAttractivitiesCSV,
        bikeSharingStationsCSV: Path = defaultBikeSharingStationsCSV,
        zonesCSV: Path = defaultZonesCSV
    ): BikeSharingConfig {
        return BikeSharingConfig(
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
    override fun overwriteDataRepo(
        dataRepo: Path,
        personCSV: Path,
        householdCSV: Path,
        activityCSV: Path,
        privateCarsCSV: Path,
        fixedDestinationCSV: Path,
    ): BikeSharingConfig {
        return BikeSharingConfig(
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

    companion object : JSONInitializer<BikeSharingConfig> {
        private const val SHARING_PARAM = "bikeSharingStationsCSV"
        private val defaultBikeSharingStationsCSV: Path = Path("bikesharing_stations.csv")

        /**
         * @return all the constructor parameter names, including dataRepo and zoneRepo.
         */
        override fun getParameterNames(): Set<String> {
            return CoreCSVConfig.getParameterNames() + SHARING_PARAM
        }

        /**
         * Constructs a config out of the given params.
         * @throws error If the given params don't contain either 'dataRepo' and 'zoneRepo' or all other fields since
         * no sensible config can be constructed then.
         */
        override fun init(givenParams: Map<String, String>): BikeSharingConfig {
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
                return BikeSharingConfig(
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
                    return BikeSharingConfig(
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
