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
@Suppress("LongParameterList")
data class CoreCSVConfig(
    override val personCSV: Path,
    override val householdCSV: Path,
    override val activityCSV: Path,
    override val privateCarsCSV: Path,
    override val fixedDestinationCSV: Path,
    override val attractivitiesCSV: Path,
    override val zonesCSV: Path,
): BaseCSVFiles {

    /**
     * Creation method based on two directories. All files are expected to reside in either the dataFolder or the
     * zoneFolder.
     *
     * __Default expected structure__ applied when only dataDirectory and zoneDirectory are given:
     *
     * `dataDirectory`: person.csv, household.csv, activity.csv, car.csv, fixedDestination.csv
     *
     * `zoneDirectory`: attractivities.csv, zones.csv
     */
    @Suppress("LongParameterList")
    constructor(
        dataRepo: Path,
        zoneRepo: Path,
        personCSV: Path? = null,
        householdCSV: Path? = null,
        activityCSV: Path? = null,
        privateCarsCSV: Path? = null,
        fixedDestinationCSV: Path? = null,
        attractivitiesCSV: Path? = null,
        zonesCSV: Path? = null,
    ) :
        this(
            personCSV = dataRepo.resolve(personCSV ?: defaultPersonCSV),
            householdCSV = dataRepo.resolve(householdCSV ?: defaultHouseholdCSV),
            activityCSV = dataRepo.resolve(activityCSV ?: defaultActivityCSV),
            privateCarsCSV = dataRepo.resolve(privateCarsCSV ?: defaultPrivateCarsCSV),
            fixedDestinationCSV = dataRepo.resolve(fixedDestinationCSV ?: defaultFixedDestinationCSV),
            attractivitiesCSV = zoneRepo.resolve(attractivitiesCSV ?: defaultAttractivitiesCSV),
            zonesCSV = zoneRepo.resolve(zonesCSV ?: defaultZonesCSV),
        )

    /**
     * Checks whether all paths exist and returns the ones not existing.
     * @return list containing any of the paths this class manages, if they don't exist.
     */
    @JsonIgnore
    override fun getNonexistentPaths(): List<Path> {
        val paths = listOf(
            personCSV,
            householdCSV,
            activityCSV,
            privateCarsCSV,
            fixedDestinationCSV,
            attractivitiesCSV,
            zonesCSV
        )
        return paths.filter { !it.exists() }
    }

    /**
     * Returns new CSVConfig with changed attractivities and zones paths.
     * @param attractivitiesCSV The path to attractivities.csv relative to the new zone repo, or an absolute path.
     * @param zonesCSV The path to zones.csv relative to the new zone repo, or an
     * absolute path.
     * @return A new CSVConfig with the attractivities and zones based on the new zone repo.
     */
    fun overwriteZoneRepo(
        zoneRepo: Path,
        attractivitiesCSV: Path = defaultAttractivitiesCSV,
        zonesCSV: Path = defaultZonesCSV
    ): CoreCSVConfig {
        return CoreCSVConfig(
            personCSV = personCSV,
            householdCSV = householdCSV,
            activityCSV = activityCSV,
            privateCarsCSV = privateCarsCSV,
            fixedDestinationCSV = fixedDestinationCSV,
            attractivitiesCSV = zoneRepo.resolve(attractivitiesCSV),
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
    fun overwriteDataRepo(
        dataRepo: Path,
        personCSV: Path = defaultPersonCSV,
        householdCSV: Path = defaultHouseholdCSV,
        activityCSV: Path = defaultActivityCSV,
        privateCarsCSV: Path = defaultPrivateCarsCSV,
        fixedDestinationCSV: Path = defaultFixedDestinationCSV,
    ): CoreCSVConfig {
        return CoreCSVConfig(
            personCSV = dataRepo.resolve(personCSV),
            householdCSV = dataRepo.resolve(householdCSV),
            activityCSV = dataRepo.resolve(activityCSV),
            privateCarsCSV = dataRepo.resolve(privateCarsCSV),
            fixedDestinationCSV = dataRepo.resolve(fixedDestinationCSV),
            attractivitiesCSV = attractivitiesCSV,
            zonesCSV = zonesCSV
        )
    }

    companion object : JSONInitializer<CoreCSVConfig> {
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
                ZONES_PARAM
            )
        }

        fun Map<String, String>.retrieveAsPath(name: String,): Path? {
            return if (containsKey(name)) { Path(get(name)!!) } else null
        }


        fun allNotNull(vararg paths: Path?): Boolean {
            return paths.all { it != null }
        }

        /**
         * Constructs a config out of the given params.
         * @throws error If the given params don't contain either 'dataRepo' and 'zoneRepo' or all other fields since
         * no sensible config can be constructed then.
         */
        override fun init(givenParams: Map<String, String>): CoreCSVConfig {
            val dataRepo: Path? = givenParams.retrieveAsPath(DATA_REPO_PARAM)
            val zoneRepo: Path? = givenParams.retrieveAsPath(ZONE_REPO_PARAM)
            val personCSV: Path? = givenParams.retrieveAsPath(PERSON_PARAM)
            val householdCSV: Path? = givenParams.retrieveAsPath(HOUSEHOLD_PARAM)
            val activityCSV: Path? = givenParams.retrieveAsPath(ACTIVITY_PARAM)
            val privateCarsCSV: Path? = givenParams.retrieveAsPath(CAR_PARAM)
            val fixedDestinationCSV: Path? = givenParams.retrieveAsPath(DESTINATION_PARAM)
            val attractivitiesCSV: Path? = givenParams.retrieveAsPath(ATTRACTIVITY_PARAM)
            val zonesCSV: Path? = givenParams.retrieveAsPath(ZONES_PARAM)

            if (dataRepo != null && zoneRepo != null) {
                return CoreCSVConfig(
                    dataRepo = dataRepo,
                    zoneRepo = zoneRepo,
                    personCSV = personCSV,
                    householdCSV = householdCSV,
                    activityCSV = activityCSV,
                    privateCarsCSV = privateCarsCSV,
                    fixedDestinationCSV = fixedDestinationCSV,
                    attractivitiesCSV = attractivitiesCSV,
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
                        zonesCSV
                    )
                ) {
                    return CoreCSVConfig(
                        personCSV = personCSV!!,
                        householdCSV = householdCSV!!,
                        activityCSV = activityCSV!!,
                        privateCarsCSV = privateCarsCSV!!,
                        fixedDestinationCSV = fixedDestinationCSV!!,
                        attractivitiesCSV = attractivitiesCSV!!,
                        zonesCSV = zonesCSV!!
                    )
                } else {
                    error("Missing mandatory fields. Either set 'dataRepo' and 'zoneRepo' or all other fields.")
                }
            }
        }
    }
}
