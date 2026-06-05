package edu.kit.ifv.application.config.subconfigs
import com.fasterxml.jackson.annotation.JsonIgnore
import edu.kit.ifv.application.config.subconfigs.CoreCSVConfig.Companion.retrieveAsPath
import edu.kit.ifv.domain.jackson.JSONInitializer
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

/**
 * Base csv files with additional bikesharingstations file.
 * On default the bikesharing_stations.csv is expected to be a child of the zoneRepo.
 */
data class BikeSharingConfig(
    val sharingProviderName: String,
    val vehicleCountColumn: String,
    val bikeSharingStationsCSV: Path,
    val coreCSVConfig: CoreCSVConfig,
) : BaseCSVFiles by coreCSVConfig {

    /**
     * Creation method based on two directories. All files are expected to reside in either the .dataFolder or the
     * zoneFolder.
     *
     * __Default expected structure__ applied when only dataDirectory and zoneDirectory are given:
     *
     * `dataDirectory`: person.csv, household.csv, activity.csv, car.csv, fixedDestination.csv
     *
     * `zoneDirectory`: .attractivities.csv, bikesharing_stations.csv, zones.csv
     */
    constructor(
        dataRepo: Path,
        zoneRepo: Path,
        sharingProviderName: String,
        vehicleCountColumn: String,
        personCSV: Path? = null,
        householdCSV: Path? = null,
        activityCSV: Path? = null,
        privateCarsCSV: Path? = null,
        fixedDestinationCSV: Path? = null,
        attractivitiesCSV: Path? = null,
        bikeSharingStationsCSV: Path? = null,
        zonesCSV: Path? = null,
    ) : this(
        sharingProviderName = sharingProviderName,
        vehicleCountColumn = vehicleCountColumn,
        bikeSharingStationsCSV = CoreCSVConfig.existsOrDefault(
            bikeSharingStationsCSV,
            defaultBikeSharingStationsCSV,
            zoneRepo,
        ),
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
        ),
    )

    constructor(
        sharingProviderName: String,
        vehicleCountColumn: String,
        personCSV: Path,
        householdCSV: Path,
        activityCSV: Path,
        privateCarsCSV: Path,
        fixedDestinationCSV: Path,
        attractivitiesCSV: Path,
        bikeSharingStationsCSV: Path,
        zonesCSV: Path,
    ) : this(
        sharingProviderName = sharingProviderName,
        vehicleCountColumn = vehicleCountColumn,
        bikeSharingStationsCSV = bikeSharingStationsCSV,
        coreCSVConfig = CoreCSVConfig(
            personCSV,
            householdCSV,
            activityCSV,
            privateCarsCSV,
            fixedDestinationCSV,
            attractivitiesCSV,
            zonesCSV,
        ),
    )

    constructor(
        sharingProviderName: String,
        vehicleCountColumn: String,
        zoneRepo: Path,
        bikeSharingStationsCSV: Path? = defaultBikeSharingStationsCSV,
        coreCSVConfig: CoreCSVConfig,
    ) : this(
        sharingProviderName = sharingProviderName,
        vehicleCountColumn = vehicleCountColumn,
        bikeSharingStationsCSV = CoreCSVConfig.existsOrDefault(
            bikeSharingStationsCSV,
            defaultBikeSharingStationsCSV,
            zoneRepo,
        ),
        coreCSVConfig,
    )

    /**
     * Checks whether all paths exist and returns the ones not existing.
     * @return list containing any of the paths this class manages, if they do not exist.
     */
    @JsonIgnore
    override fun getNonexistentPaths(): List<Path> {
        if (bikeSharingStationsCSV.exists()) {
            return coreCSVConfig.getNonexistentPaths()
        }
        return coreCSVConfig.getNonexistentPaths().plusElement(bikeSharingStationsCSV)
    }

    /**
     * Returns new BikeSharingConfig with changed .attractivities, bikeSharingStations and zones paths.
     * @param attractivitiesCSV The path to .attractivities.csv relative to the new zone repo, or an absolute path.
     * @param bikeSharingStationsCSV The path to bikesharing_stations.csv relative to the new zone repo, or an
     * absolute path.
     * @param zonesCSV The path to zones.csv relative to the new zone repo, or an
     * absolute path.
     * @return A new BikeSharingConfig with the .attractivities, bikesharingstations, and zones based on the new zone repo.
     */
    fun overwriteZoneRepo(
        zoneRepo: Path,
        attractivitiesCSV: Path = defaultAttractivitiesCSV,
        bikeSharingStationsCSV: Path = defaultBikeSharingStationsCSV,
        zonesCSV: Path = defaultZonesCSV,
    ): BikeSharingConfig = BikeSharingConfig(
        sharingProviderName = sharingProviderName,
        vehicleCountColumn = vehicleCountColumn,
        bikeSharingStationsCSV = zoneRepo.resolve(bikeSharingStationsCSV),
        coreCSVConfig = coreCSVConfig.overwriteZoneRepo(zoneRepo, attractivitiesCSV, zonesCSV),
    )

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
    ): BikeSharingConfig = BikeSharingConfig(
        sharingProviderName = sharingProviderName,
        vehicleCountColumn = vehicleCountColumn,
        bikeSharingStationsCSV = bikeSharingStationsCSV,
        coreCSVConfig = coreCSVConfig.overwriteDataRepo(
            dataRepo,
            personCSV,
            householdCSV,
            activityCSV,
            privateCarsCSV,
            fixedDestinationCSV,
        ),
    )

    companion object : JSONInitializer<BikeSharingConfig> {
        private const val SHARING_PARAM = "bikeSharingStationsCSV"
        private const val PROVIDER_PARAM = "sharingProviderName"
        private const val COUNT_COLUMN_PARAM = "vehicleCountColumn"
        private val defaultBikeSharingStationsCSV: Path = Path("bikesharing_stations.csv")

        /**
         * @return all the constructor parameter names, including dataRepo and zoneRepo.
         */
        override fun getParameterNames(): Set<String> =
            CoreCSVConfig.getParameterNames() + SHARING_PARAM + PROVIDER_PARAM + COUNT_COLUMN_PARAM

        /**
         * Constructs a config out of the given params.
         * @throws error If the given params do not contain either 'dataRepo' and 'zoneRepo' or all other fields since
         * no sensible config can be constructed then.
         */
        override fun init(givenParams: Map<String, String>): BikeSharingConfig {
            val bikeSharingStationsCSV: Path? = givenParams.retrieveAsPath(SHARING_PARAM)
            val providerName = givenParams[PROVIDER_PARAM]
            val countColumn = givenParams[COUNT_COLUMN_PARAM]
            val zoneRepo: Path? = givenParams.retrieveAsPath(CoreCSVConfig.ZONE_REPO_PARAM)
            val core = CoreCSVConfig.init(givenParams)

            if (providerName == null || countColumn == null) {
                error(
                    "Bikesharing can not be used without a provider name and a count column. " +
                        "Please provide both '$PROVIDER_PARAM' and '$COUNT_COLUMN_PARAM' parameters for the " +
                        "BikeSharingConfig. \nOnly received [$providerName] as provider name and [$countColumn] as " +
                        "count column.",
                )
            }
            if (zoneRepo != null) {
                return BikeSharingConfig(
                    sharingProviderName = providerName,
                    vehicleCountColumn = countColumn,
                    zoneRepo,
                    bikeSharingStationsCSV,
                    core,
                )
            } else if (bikeSharingStationsCSV != null) {
                return BikeSharingConfig(
                    sharingProviderName = providerName,
                    vehicleCountColumn = countColumn,
                    bikeSharingStationsCSV,
                    core,
                )
            } else {
                error(
                    "A BikeSharingConfig was being initialized without sufficient information. At least a zoneRepo " +
                        "or a path to a bikeSharingStationsCSV must be specified in the yaml. " +
                        "Neither is given rn. \nzoneRepo=$zoneRepo, bikeSharingStationsCSV=$bikeSharingStationsCSV",
                )
            }
        }
    }
}
