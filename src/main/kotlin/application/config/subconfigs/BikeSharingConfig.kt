package application.config.subconfigs

import application.config.subconfigs.CoreCSVConfig.Companion.retrieveAsPath
import com.fasterxml.jackson.annotation.JsonIgnore
import domain.jackson.JSONInitializer
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

/**
 * Base csv files with additional bikesharingstations file.
 * On default the bikesharing_stations.csv is expected to be a child of the zoneRepo.
 */
data class BikeSharingConfig(
    val bikeSharingStationsCSV: Path,
    val coreCSVConfig: CoreCSVConfig
) : BaseCSVFiles by coreCSVConfig {

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
    ) : this(
        bikeSharingStationsCSV = zoneRepo.resolve(bikeSharingStationsCSV ?: defaultBikeSharingStationsCSV),
        coreCSVConfig = CoreCSVConfig(
            dataRepo,
            zoneRepo,
            personCSV,
            householdCSV,
            activityCSV,
            privateCarsCSV,
            fixedDestinationCSV,
            attractivitiesCSV,
            zonesCSV,
        )
    )

    /**
     * Simple constructor requiring every path.
     */
    constructor(
        personCSV: Path,
        householdCSV: Path,
        activityCSV: Path,
        privateCarsCSV: Path,
        fixedDestinationCSV: Path,
        attractivitiesCSV: Path,
        bikeSharingStationsCSV: Path,
        zonesCSV: Path,
    ) : this(
        bikeSharingStationsCSV = bikeSharingStationsCSV,
        coreCSVConfig = CoreCSVConfig(
            personCSV,
            householdCSV,
            activityCSV,
            privateCarsCSV,
            fixedDestinationCSV,
            attractivitiesCSV,
            zonesCSV,
        )
    )

    constructor(
        zoneRepo: Path,
        bikeSharingStationsCSV: Path? = defaultBikeSharingStationsCSV,
        coreCSVConfig: CoreCSVConfig,
    ) : this(
        bikeSharingStationsCSV = zoneRepo.resolve(bikeSharingStationsCSV ?: defaultBikeSharingStationsCSV),
        coreCSVConfig
    )

    /**
     * Checks whether all paths exist and returns the ones not existing.
     * @return list containing any of the paths this class manages, if they don't exist.
     */
    @JsonIgnore
    override fun getNonexistentPaths(): List<Path> {
        if (bikeSharingStationsCSV.exists()) {
            return coreCSVConfig.getNonexistentPaths()
        }
        return coreCSVConfig.getNonexistentPaths().plusElement(bikeSharingStationsCSV)
    }

    /**
     * Returns new BikeSharingConfig with changed attractivities, bikeSharingStations and zones paths.
     * @param attractivitiesCSV The path to attractivities.csv relative to the new zone repo, or an absolute path.
     * @param bikeSharingStationsCSV The path to bikesharing_stations.csv relative to the new zone repo, or an
     * absolute path.
     * @param zonesCSV The path to zones.csv relative to the new zone repo, or an
     * absolute path.
     * @return A new BikeSharingConfig with the attractivities, bikesharingstations, and zones based on the new zone repo.
     */
    fun overwriteZoneRepo(
        zoneRepo: Path,
        attractivitiesCSV: Path = defaultAttractivitiesCSV,
        bikeSharingStationsCSV: Path = defaultBikeSharingStationsCSV,
        zonesCSV: Path = defaultZonesCSV
    ): BikeSharingConfig {
        return BikeSharingConfig(
            bikeSharingStationsCSV = zoneRepo.resolve(bikeSharingStationsCSV),
            coreCSVConfig = coreCSVConfig.overwriteZoneRepo(zoneRepo, attractivitiesCSV, zonesCSV),
        )
    }

    /**
     * Returns new BikeSharingConfig with changed person, household, activity, private_cars and fixed_destination paths.
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
     * @return A new BikeSharingConfig with person, household, activity, cars and fixed_destinations based on the given dataRepo.
     */
    @Suppress("LongParameterList")
    fun overwriteDataRepo(
        dataRepo: Path,
        personCSV: Path,
        householdCSV: Path,
        activityCSV: Path,
        privateCarsCSV: Path,
        fixedDestinationCSV: Path,
    ): BikeSharingConfig {
        return BikeSharingConfig(
            bikeSharingStationsCSV = bikeSharingStationsCSV,
            coreCSVConfig = coreCSVConfig.overwriteDataRepo(
                dataRepo,
                personCSV,
                householdCSV,
                activityCSV,
                privateCarsCSV,
                fixedDestinationCSV,
            )
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
            val bikeSharingStationsCSV: Path? = givenParams.retrieveAsPath(SHARING_PARAM)
            val zoneRepo: Path? = givenParams.retrieveAsPath(ZONE_REPO_PARAM)
            val core = CoreCSVConfig.init(givenParams)

            if (zoneRepo != null) {
                return BikeSharingConfig(
                    zoneRepo,
                    bikeSharingStationsCSV,
                    core,
                )
            } else if (bikeSharingStationsCSV != null) {
                return BikeSharingConfig(
                    bikeSharingStationsCSV,
                    core,
                )
            } else {
                error(
                    "A BikeSharingConfig was being initialized without sufficient information. At least a zoneRepo " +
                        "or a path to a bikeSharingStationsCSV must be specified in the yaml. " +
                        "Neither is given rn. \nzoneRepo=$zoneRepo, bikeSharingStationsCSV=$bikeSharingStationsCSV"
                )
            }
        }
    }
}
